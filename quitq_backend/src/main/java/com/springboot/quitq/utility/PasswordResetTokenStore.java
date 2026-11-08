package com.springboot.quitq.utility;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PasswordResetTokenStore {

    private static final long TTL_MS = 15 * 60 * 1000;

    private final Map<String, Entry> tokens = new ConcurrentHashMap<>();

    private record Entry(String username, long expiresAt) {
    }

    public String create(String username) {
        cleanup();
        String token = UUID.randomUUID().toString().replace("-", "");
        tokens.put(token, new Entry(username, System.currentTimeMillis() + TTL_MS));
        return token;
    }

    public String consume(String token) {
        if (token == null || token.isBlank())
            return null;

        Entry entry = tokens.remove(token);
        if (entry == null)
            return null;

        if (entry.expiresAt() < System.currentTimeMillis())
            return null;

        return entry.username();
    }

    private void cleanup() {
        long now = System.currentTimeMillis();
        tokens.entrySet().removeIf(e -> e.getValue().expiresAt() < now);
    }
}
