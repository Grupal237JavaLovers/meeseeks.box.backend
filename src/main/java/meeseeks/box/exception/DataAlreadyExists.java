package meeseeks.box.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class DataAlreadyExists extends RuntimeException {

    public DataAlreadyExists() {
        super();
    }

    public DataAlreadyExists(final String message,
                             final Throwable cause,
                             final boolean enableSuppression,
                             final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DataAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAlreadyExists(String message) {
        super(message);
    }

    public DataAlreadyExists(Throwable cause) {
        super(cause);
    }
}
