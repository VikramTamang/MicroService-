package com.project.user.dto;

public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserDto user;

    public AuthResponse() {}

    public AuthResponse(String token, String tokenType, Long expiresIn, UserDto user) {
        this.token = token;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final AuthResponse resp = new AuthResponse();
        public Builder token(String token) { resp.setToken(token); return this; }
        public Builder tokenType(String tokenType) { resp.setTokenType(tokenType); return this; }
        public Builder expiresIn(Long expiresIn) { resp.setExpiresIn(expiresIn); return this; }
        public Builder user(UserDto user) { resp.setUser(user); return this; }
        public AuthResponse build() { return resp; }
    }
}
