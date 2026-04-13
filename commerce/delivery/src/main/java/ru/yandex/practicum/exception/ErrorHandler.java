package ru.yandex.practicum.exception;

import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exceptions.NoDeliveryFoundException;
import ru.yandex.practicum.messages.Message;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handelNoDeliveryFound(final NoDeliveryFoundException exception) {
        log.warn(Message.HANDLER_NOT_FOUND_DELIVERY, exception.getMessage());
        return getError(exception, HttpStatus.NOT_FOUND, "Not Found Delivery");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleFeignException(final FeignException exception) {
        log.error(Message.HANDLER_FEIGN_EXCEPTION, exception.getMessage());
        return getError(exception, HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException exception) {
        log.warn(Message.HANDLER_ILLEGAL_ARGUMENT_EXCEPTION, exception.getMessage());
        return getError(exception, HttpStatus.BAD_REQUEST, "Illegal Argument");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalStateException(final IllegalStateException exception) {
        log.warn(Message.HANDLER_ILLEGAL_STATE_EXCEPTION, exception.getMessage());
        return getError(exception, HttpStatus.BAD_REQUEST, "Illegal Argument");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException exception) {
        log.warn(Message.HANDLER_CONSTRAINT_VIOLATION_EXCEPTION, exception.getMessage());
        return getError(exception, HttpStatus.BAD_REQUEST, "Bad Request");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        log.warn(Message.HANDLER_METHOD_NOT_VALID_EXCEPTION, exception.getMessage());
        return getError(exception, HttpStatus.BAD_REQUEST, "Bad Request");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerError(final Exception exception) {
        log.error(Message.HANDLER_ERROR, exception.getMessage());
        return getError(exception, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    private ErrorResponse getError(Exception e, HttpStatus httpStatus, String message) {
        return new ErrorResponse(
                e.getCause(),
                e.getStackTrace(),
                httpStatus,
                e.getMessage(),
                message,
                e.getSuppressed(),
                e.getLocalizedMessage()
        );
    }
}
