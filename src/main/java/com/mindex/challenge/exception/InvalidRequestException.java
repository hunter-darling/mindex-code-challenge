package com.mindex.challenge.exception;

public class InvalidRequestException extends RuntimeException {
  public InvalidRequestException(String message) {
    super(message);
  }
}
