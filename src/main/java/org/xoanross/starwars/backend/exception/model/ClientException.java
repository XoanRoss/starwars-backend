package org.xoanross.starwars.backend.exception.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ClientException extends RuntimeException {
    private final int statusCode;
    private final String message;
}
