package com.aftersports.aftersports.infra.security;

import com.aftersports.aftersports.domain.model.User;
import com.aftersports.aftersports.infra.config.JwtProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
    private static final String HMAC_ALG = "HmacSHA256";

    private final JwtProperties properties;
    private final ObjectMapper objectMapper;
    private byte[] secretBytes;

    public JwtService(JwtProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void init() {
        if (properties.getSecret() == null || properties.getSecret().length() < 32) {
            throw new IllegalStateException("JWT secret must be defined and at least 32 characters");
        }
        this.secretBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plus(Duration.ofMinutes(properties.getExpirationMinutes()));
        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        JwtPayload payload = new JwtPayload(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                exp.getEpochSecond()
        );
        try {
            String headerPart = BASE64_URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(header));
            String payloadPart = BASE64_URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(payload));
            String content = headerPart + "." + payloadPart;
            String signature = BASE64_URL_ENCODER.encodeToString(sign(content));
            return content + "." + signature;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to generate token", e);
        }
    }

    public JwtPayload parseAndValidate(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token is required");
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid token format");
        }
        String content = parts[0] + "." + parts[1];
        byte[] expectedSig = sign(content);
        byte[] providedSig = BASE64_URL_DECODER.decode(parts[2]);
        if (!MessageDigest.isEqual(expectedSig, providedSig)) {
            throw new IllegalArgumentException("Invalid token signature");
        }
        try {
            JwtPayload payload = objectMapper.readValue(BASE64_URL_DECODER.decode(parts[1]), JwtPayload.class);
            if (payload.exp() < Instant.now().getEpochSecond()) {
                throw new IllegalArgumentException("Token expired");
            }
            return payload;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token payload");
        }
    }

    private byte[] sign(String content) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALG);
            mac.init(new SecretKeySpec(secretBytes, HMAC_ALG));
            return mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to sign token", e);
        }
    }

    public record JwtPayload(Long sub, String email, String name, String role, long exp) { }
}
