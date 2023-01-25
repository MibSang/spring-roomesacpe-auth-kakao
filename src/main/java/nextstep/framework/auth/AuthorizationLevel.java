package nextstep.framework.auth;

public enum AuthorizationLevel {
    USER("USER"),
    ADMIN("ADMIN");

    private final String authLevelString;

    AuthorizationLevel(String authLevelString) {
        this.authLevelString = authLevelString;
    }

    public static AuthorizationLevel getAuthorizationFromString(String authLevel) {
        if (authLevel.equals("ADMIN")) {
            return AuthorizationLevel.ADMIN;
        }
        if (authLevel.equals("USER")) {
            return AuthorizationLevel.USER;
        }
        return null;
    }

    public String value() {
        return authLevelString;
    }

    public boolean canAccessTo(AuthorizationLevel compareAuth) {
        return this == AuthorizationLevel.ADMIN || this == compareAuth;
    }
}
