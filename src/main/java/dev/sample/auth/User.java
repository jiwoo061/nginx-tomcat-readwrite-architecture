package dev.sample.auth;

public class User {
    private long userId;
    private String loginId;
    private String name;

    public User(long userId, String loginId, String name) {
        this.userId = userId;
        this.loginId = loginId;
        this.name = name;
    }

    public long getUserId() { return userId; }
    public String getLoginId() { return loginId; }
    public String getName() { return name; }
}
