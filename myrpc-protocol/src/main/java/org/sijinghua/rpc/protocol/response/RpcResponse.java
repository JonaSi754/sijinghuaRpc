package org.sijinghua.rpc.protocol.response;

import org.sijinghua.rpc.protocol.base.RpcMessage;

public class RpcResponse extends RpcMessage {
    private static final long serialVersionUID = 425335064405584525L;

    private String error;

    private Object result;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return  result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
