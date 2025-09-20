package com.xiangxi.message.client;

import java.io.Serial;

public class ClientException extends Exception{
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * UUID of the request, it will be empty if request is not fulfilled.
     */
    private String requestId;

    /**
     * Error code, When API returns a failure, it must have an error code.
     */
    private String errorCode;

    public  ClientException(String message, Throwable e){
        super(message, e);
    }

    public ClientException(String message){
        super(message, null);
    }

    public ClientException(String message, String requestId) {
        this(message, requestId, "");
    }

    public ClientException(String message, String requestId, String errorCode) {
        super(message);
        this.requestId = requestId;
        this.errorCode = errorCode;
    }

    public String getRequestId() {
        return requestId;
    }

    /**
     * Get error code
     *
     * @return A string represents error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    public String toString() {
        String msg = "[ClientException]"
                + "code: "
                + this.getErrorCode()
                + " message:"
                + this.getMessage()
                + " requestId:"
                + this.getRequestId();
        if (getCause() != null) {
            msg += " cause:" + getCause().toString();
        }
        return msg;
    }

}
