package dev.sample.auth;

import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthUserDao userDao;

    public AuthService(AuthUserDao userDao) {
        this.userDao = userDao;
    }

    public User login(String loginId, String password) throws Exception {
        var rec = userDao.findByLoginId(loginId);
        if (rec == null) return null;                 // 아이디 없음
        if (!password.equals(rec.password)) return null; // 비번 불일치 (지금은 평문 비교 방식 유지)
        return new User(rec.userId, rec.loginId, rec.name);
    }
}