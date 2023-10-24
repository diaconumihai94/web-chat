package com.example.webchat.service;

import com.example.webchat.websocket.User;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class OnlineUsersService {

    private final Set<User> onlineUsers = new LinkedHashSet<>();

    public Set<User> getOnlineUsers() {
        return onlineUsers;
    }

    public void addOnlineUser(User user) {
        onlineUsers.add(user);
    }

    public void removeOnlineUser(User user) {
        onlineUsers.remove(user);
    }
}
