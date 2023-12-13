package com.gigaspaces.internal.server.space.mvcc.exception;

public class MVCCGenerationInternalRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -7959722857217558503L;

    public MVCCGenerationInternalRuntimeException() {
        super();
    }

    public MVCCGenerationInternalRuntimeException(String message) {
        super(message);
    }

    public MVCCGenerationInternalRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MVCCGenerationInternalRuntimeException(Throwable cause) {
        super(cause);
    }

    protected MVCCGenerationInternalRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
