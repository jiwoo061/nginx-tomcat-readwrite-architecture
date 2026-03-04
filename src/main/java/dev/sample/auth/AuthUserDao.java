package dev.sample.auth;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class AuthUserDao {
    private final DataSource ds;

    public AuthUserDao(DataSource ds) {
        this.ds = ds;
    }

    public UserRecord findByLoginId(String loginId) throws SQLException {
        String sql = "SELECT user_id, login_id, password, name FROM users WHERE login_id = ?";

        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, loginId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new UserRecord(
                        rs.getLong("user_id"),
                        rs.getString("login_id"),
                        rs.getString("password"),
                        rs.getString("name")
                );
            }
        }
    }

    public static class UserRecord {
        public final long userId;
        public final String loginId;
        public final String password;
        public final String name;

        public UserRecord(long userId, String loginId, String password, String name) {
            this.userId = userId;
            this.loginId = loginId;
            this.password = password;
            this.name = name;
        }
    }
}