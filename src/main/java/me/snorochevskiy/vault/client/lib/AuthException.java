package me.snorochevskiy.vault.client.lib;

public class AuthException extends RuntimeException {
    public AuthException(String resource) {
        super("No permissions for resource: " + resource);
    }
}
