package nextstep.publics.member;

import nextstep.framework.auth.AuthorizationLevel;

public class Member {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private AuthorizationLevel auth;

    public Member(Long id, String username, String password, String name, String phone, String auth) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.auth = AuthorizationLevel.getAuthorizationFromString(auth);
    }

    public Member(String username, String password, String name, String phone) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public AuthorizationLevel getAuth() {
        return auth;
    }

    public boolean checkWrongPassword(String password) {
        return !this.password.equals(password);
    }
}
