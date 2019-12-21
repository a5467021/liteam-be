package com.liteam.common;

/**
 * 全局代理所有的运行时异常
 *
 */
public class ChatRuntimeException extends RuntimeException{

    private static final long serialVersionUID = -5627321342995459918L;

    public ChatRuntimeException(){
        super();
    }

    public ChatRuntimeException(String message){
        super(message);
    }

    public ChatRuntimeException(Throwable cause){
        super(cause);
    }

    protected ChatRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ChatRuntimeException(String message, Throwable cause){
        super(message, cause);
    }
}
