package com.example.webchat.websocket;

import java.time.Instant;

public record Message(User user, String comment, Action action, Instant timestamp) {
}
