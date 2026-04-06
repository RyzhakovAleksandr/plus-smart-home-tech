package ru.yandex.practicum.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {
    Throwable cause;
    StackTraceElement[] stackTrace;
    HttpStatus status;
    String message;
    String userMessage;
    Throwable[] suppressed;
    String localizedMessage;
}
