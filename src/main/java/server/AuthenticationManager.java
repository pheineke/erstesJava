package main.java.server;

import java.util.HashMap;

public class AuthenticationManager {
    private HashMap<String, String> usersAndPasswords = new HashMap<>();
    private HashMap<String, Boolean> loggedInUsers = new HashMap<>();

    public boolean authenticate(String username, String password) {
        String storedPassword = usersAndPasswords.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    public void register(String username, String password) {
        if (!usersAndPasswords.containsKey(username)) {
            usersAndPasswords.put(username, password);
        }
    }

    public boolean isLoggedIn(String usersocket) {
        return loggedInUsers.getOrDefault(usersocket, false);
    }

    public void login(String usersocket) {
        loggedInUsers.put(usersocket, true);
    }

    public void logout(String usersocket) {
        loggedInUsers.remove(usersocket);
    }
}
