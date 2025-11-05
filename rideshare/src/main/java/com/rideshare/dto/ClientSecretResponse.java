package com.rideshare.dto;

public class ClientSecretResponse {
    private String clientSecret;

    public ClientSecretResponse(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}