package com.nisvschoolug;

public class FeedbackModel {
    String email, message;

    public FeedbackModel(){}
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FeedbackModel(String email, String message) {
        this.email = email;
        this.message = message;
    }
}
