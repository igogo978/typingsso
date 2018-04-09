package app.sso.typing.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.Instant;
import java.util.Base64;


@Service
public class RandomMssqlPasswd {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Declare the JDBC objects.
    Connection conn;
    PreparedStatement pstmt;
    ResultSet rs;

    @Async
    public void execute(String typingid) throws InterruptedException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {

        Thread.sleep(15000);
        logger.info("make user's password chaos");

        Instant instant = Instant.now();
        long timestamp = instant.getEpochSecond();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String passwd = String.valueOf(timestamp);
        byte[] hash = digest.digest(passwd.getBytes(StandardCharsets.UTF_8));
        String encoded = Base64.getEncoder().encodeToString(hash).substring(0, 8);

        logger.info("generate an unknown passwd:" + encoded);

        String connectionUrl = "jdbc:sqlserver://163.17.39.33:1433;"
                + "databaseName=type_db;user=game2;password=pwdpwd";
        String sql;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        conn = DriverManager.getConnection(connectionUrl);


        sql = "SELECT * FROM dbo.users where userid=?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, typingid);
        int result;
        rs = pstmt.executeQuery();


        if (rs.next()) {
            logger.info(String.format("update user %s passwd", typingid));
            sql = "UPDATE dbo.users SET pwd=? where userid=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, encoded);  //pwd
            pstmt.setString(2, typingid);  //typingid

            result = pstmt.executeUpdate();

        }

        //validate passwd
//        sql = "SELECT * FROM dbo.users where userid=?";
//        pstmt = conn.prepareStatement(sql);
//        pstmt.setString(1, typingid);  //typingid
//        rs = pstmt.executeQuery();
//        while (rs.next()) {
//            logger.info("query update passwd value:" + rs.getString("pwd"));
//        }

        try {
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.info(ex.getMessage());
        }

        logger.info("random user's passwd ok.");


    }
}
