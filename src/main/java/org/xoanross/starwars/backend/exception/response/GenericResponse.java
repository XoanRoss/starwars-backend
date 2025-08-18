package org.xoanross.starwars.backend.exception.response;

import java.time.LocalDateTime;

public record GenericResponse(LocalDateTime timestamp, int code, String message) {
}
