package com.example.cakeapp.model;

public class DeleteProductResponse {
    private boolean success;
    private String message;

    // Getters và setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
