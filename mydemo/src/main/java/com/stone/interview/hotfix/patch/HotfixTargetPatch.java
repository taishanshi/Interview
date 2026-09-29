package com.stone.interview.hotfix.patch;

import android.util.Log;

/**
 * 模拟临时加载的补丁。生产环境在独立 dex 里，这里打进 APK 方便演示反射调用。
 */
public final class HotfixTargetPatch {

    private static final String TAG = "Hotfix";

    private HotfixTargetPatch() {
    }

    public static boolean isSupport(String methodId) {
        return methodId != null
                && (methodId.contains(".getMessage:") || methodId.contains(".add:"));
    }

    public static Object invoke(String methodId, Object[] paramsArray) {
        Log.i(TAG, "patch invoke " + methodId);
        // 实例方法的 paramsArray[0] 是 this，参数从 1 开始
        if (methodId.contains(".getMessage:")) {
            return "热修复后的代码";
        }
        if (methodId.contains(".add:")) {
            int a = (Integer) paramsArray[1];
            int b = (Integer) paramsArray[2];
            return a + b + 100;
        }
        throw new UnsupportedOperationException(methodId);
    }
}
