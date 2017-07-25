package ru.ilonich.igps.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApplicationException {
    private static final String EXCEPTION_COMMON_NOT_FOUND = "exception.common.notFound";

    //  http://stackoverflow.com/a/22358422/548473
    public NotFoundException(String arg) {
        super(EXCEPTION_COMMON_NOT_FOUND, HttpStatus.UNPROCESSABLE_ENTITY, arg);
    }

    public NotFoundException(String code, HttpStatus httpStatus, String arg) {
        super(code, httpStatus, arg);
    }
}
