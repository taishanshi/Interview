package com.stone.interview.hotfix;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 运行时把补丁挂到「编译期插进去的」changeQuickRedirect 上。
 *
 * 为什么反射：源码里没有这个字段，Javassist 才加上去，写成 HotfixTarget.changeQuickRedirect 编不过。
 * 正式环境会 DexClassLoader 加载下发的 dex；演示用当前 ClassLoader 加载 APK 里的 Patch 类，调用路径一样。
 */
public final class HotfixManager {

    private static final String TAG = "Hotfix";
    private static final String FIELD = "changeQuickRedirect";

    private HotfixManager() {
    }

    public static boolean hasHook(Class<?> originClass) {
        try {
            originClass.getDeclaredField(FIELD);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    public static void loadAndApply(Class<?> originClass, String patchClassName, ClassLoader patchLoader)
            throws Exception {
        Class<?> patchClass = Class.forName(patchClassName, true, patchLoader);
        apply(originClass, new ReflectRedirect(originClass.getName(), patchClass));
        Log.i(TAG, "applied " + patchClassName + " -> " + originClass.getName());
    }

    public static void apply(Class<?> originClass, ChangeQuickRedirect redirect) throws Exception {
        // static 字段，target 传 null；set 之后该类每个已插桩方法下次调用都会先问 isSupport
        Field field = originClass.getDeclaredField(FIELD);
        field.setAccessible(true);
        field.set(null, redirect);
    }

    public static void clear(Class<?> originClass) throws Exception {
        Field field = originClass.getDeclaredField(FIELD);
        field.setAccessible(true);
        field.set(null, null);
        Log.i(TAG, "cleared " + originClass.getName());
    }

    public static final class ReflectRedirect implements ChangeQuickRedirect {
        private final String originName;
        private final Method invoke;

        private final Method supported;

        ReflectRedirect(String originName, Class<?> patchClass) throws NoSuchMethodException {
            this.originName = originName;
            this.invoke = patchClass.getDeclaredMethod("invoke", String.class, Object[].class);
            this.invoke.setAccessible(true);
            Method support = null;
            try {
                support = patchClass.getDeclaredMethod("isSupport", String.class);
                support.setAccessible(true);
            } catch (NoSuchMethodException ignored) {
                // 补丁未提供 isSupport 时，按类名匹配
            }
            this.supported = support;
        }

        @Override
        public boolean isSupport(String methodId, Object[] paramsArray) {
            if (methodId == null || !methodId.startsWith(originName + ".")) {
                return false;
            }
            if (supported == null) {
                return true;
            }
            try {
                return Boolean.TRUE.equals(supported.invoke(null, methodId));
            } catch (Throwable t) {
                return false;
            }
        }

        @Override
        public Object accessDispatch(String methodId, Object[] paramsArray) {
            try {
                // 反射调补丁静态方法；生产环境 patchClass 来自 DexClassLoader
                return invoke.invoke(null, methodId, paramsArray);
            } catch (Throwable t) {
                throw new RuntimeException("patch invoke failed: " + methodId, t);
            }
        }
    }
}
