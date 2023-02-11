package org.sijinghua.rpc.proxy.api.callback;

public interface AsyncRpcCallback {
    /**
     * 成功后的回调方法
     */
    void onSuccess(Object result);

    /**
     * 异常的回调方法
     */
    void onException(Exception e);
}
