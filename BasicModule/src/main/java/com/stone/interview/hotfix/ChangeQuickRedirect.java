package com.stone.interview.hotfix;

/**
 * 美团 Robust 同款入口。编译期给每个类插入静态字段 changeQuickRedirect，
 * 运行时把本接口的实现赋上去；为 null 则全部走原方法。
 */
public interface ChangeQuickRedirect {

    boolean isSupport(String methodId, Object[] paramsArray);

    Object accessDispatch(String methodId, Object[] paramsArray);
}
