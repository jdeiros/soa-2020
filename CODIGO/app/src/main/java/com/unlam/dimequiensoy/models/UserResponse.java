package com.unlam.dimequiensoy.models;

public class UserResponse {
    private Boolean success;
    private String env;
    private String token;
    private String token_refresh;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken_refresh() {
        return token_refresh;
    }

    public void setToken_refresh(String token_refresh) {
        this.token_refresh = token_refresh;
    }
}
