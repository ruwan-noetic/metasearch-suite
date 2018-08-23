package com.noetic.exception;

/**
 * Created by dineshcaldera on 3/8/16.
 */
public class TransformerException extends Exception {

    public TransformerException() {
        super();
    }

    public TransformerException(String message) {
        super(message);
    }

    public TransformerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransformerException(Throwable cause) {
        super(cause);
    }

    protected TransformerException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


}
