package org.xoanross.starwars.backend.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.xoanross.starwars.backend.exception.model.ClientException;
import org.xoanross.starwars.backend.exception.response.GenericResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<GenericResponse> handleException(Exception ex) {
        String errorId = UUID.randomUUID().toString();

        log.error("Error ID: {} - Exception: ", errorId, ex);

        GenericResponse response = new GenericResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unknown error occurred. Please contact support. Error ID: " + errorId
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler({ClientException.class})
    public ResponseEntity<GenericResponse> handleException(ClientException ex) {
        GenericResponse response = new GenericResponse(
                LocalDateTime.now(),
                ex.getStatusCode(),
                ex.getMessage()
        );

        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }
}
