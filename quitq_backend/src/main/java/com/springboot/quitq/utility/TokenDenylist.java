package com.springboot.quitq.utility;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenDenylist {

    private final Set<String> deniedTokens = ConcurrentHashMap.newKeySet();

    public void deny(String token) {
        deniedTokens.add(token);
    }

    public boolean isDenied(String token) {
        return token != null && deniedTokens.contains(token);
    }

}
