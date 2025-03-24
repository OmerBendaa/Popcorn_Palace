package com.att.tdp.popcorn_palace.model;

import lombok.Data;

@Data

public class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}