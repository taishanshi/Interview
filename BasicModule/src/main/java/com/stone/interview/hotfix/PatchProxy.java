package com.stone.interview.hotfix;

import android.util.Log;

import com.stone.interview.hotfix.ChangeQuickRedirect;

/**
 * 插桩字节码调用的桥：isSupport 做 bool 判断，accessDispatch 才真正进补丁。
 *
 * 为什么在 app 而不是插件里：插件只在编译电脑上跑，这个类必须打进 APK，手机上才能调用。
 * 为什么不要插桩本类：isSupport 里如果再走插桩，会再次调用 isSupport，死递归。
 */
public final class PatchProxy {

    private static final String TAG = "Hotfix";

    private PatchProxy() {
    }

    /**
     * 插桩代码每个方法开头都会先调这里。
     * 返回 false：继续执行原方法体（没补丁、补丁不覆盖此方法、或补丁抛错）。
     * 返回 true：插桩代码接着调 accessDispatch，然后直接 return，原方法体不再执行。
     */
    public static boolean isSupport(
            Object[] args,
            Object thisObj,
            ChangeQuickRedirect changeQuickRedirect,
            boolean isStatic,
            String methodId
    ) {
        if (changeQuickRedirect == null) {
            return false; // 未 loadAndApply，热修复关闭
        }
        try {
            return changeQuickRedirect.isSupport(methodId, pack(args, thisObj, isStatic));
        } catch (Throwable t) {
            // 补丁自己崩了也不能让业务方法崩溃，退回原代码
            Log.w(TAG, "isSupport failed " + methodId, t);
            return false;
        }
    }

    public static Object accessDispatch(
            Object[] args,
            Object thisObj,
            ChangeQuickRedirect changeQuickRedirect,
            boolean isStatic,
            String methodId
    ) {
        Log.i(TAG, "dispatch " + methodId);
        return changeQuickRedirect.accessDispatch(methodId, pack(args, thisObj, isStatic));
    }

    /**
     * 实例方法把 this 放进 [0]，后面才是实参；静态方法没有 this。
     * 补丁里取参数要从下标 1 开始（见 HotfixTargetPatch.add）。
     */
    private static Object[] pack(Object[] args, Object thisObj, boolean isStatic) {
        if (isStatic) {
            return args == null ? new Object[0] : args;
        }
        int len = args == null ? 0 : args.length;
        Object[] packed = new Object[len + 1];
        packed[0] = thisObj;
        if (len > 0) {
            System.arraycopy(args, 0, packed, 1, len);
        }
        return packed;
    }
}
