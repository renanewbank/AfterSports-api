package com.aftersports.aftersports.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /**
     * Chave secreta usada na assinatura HMAC. Definida via env (JWT_KEY) em produção.
     */
    private String secret = "dev-secret-key-change-me-please-1234567890";

    /**
     * Tempo de expiração do token em minutos.
     */
    private long expirationMinutes = 240;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMinutes() {
        return expirationMinutes;
    }

    public void setExpirationMinutes(long expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }
}
