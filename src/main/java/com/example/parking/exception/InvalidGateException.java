package com.example.parking.exception;

public class InvalidGateException extends RuntimeException {
    public InvalidGateException(Long gateId) {
        super("Invalid gate number: " + gateId);
    }
}