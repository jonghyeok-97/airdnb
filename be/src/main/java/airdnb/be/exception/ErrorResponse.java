package airdnb.be.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        HttpStatus status,
        String field,
        String message
) {}
