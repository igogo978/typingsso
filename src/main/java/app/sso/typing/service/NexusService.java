package app.sso.typing.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

@Service
public class NexusService {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    OidcClient oidcClient;

    public String getRandomPasswd(Integer passwdLength) {
        //帐号登入时取随机密码用
        return oidcClient.getState().toString().substring(0, passwdLength);
    }


    public String getRandomPasswd(String timestamp) {
        String passwd = String.format("%s%s", oidcClient.getState().toString().substring(4, 8), timestamp.substring(timestamp.length() - 4, timestamp.length()));
        logger.info("GENERATE RANDOM PASSWD:" + passwd);
        return passwd;

    }


    @Async
    public void messingupPasswd(String typingid, String passwd) throws InterruptedException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        Random random = new Random();
        Integer waitingSeconds = random.ints(10000, 15000).findFirst().getAsInt();
        logger.info("waiting seconds to mess up user's password: " + String.valueOf(waitingSeconds / 1000));
        Thread.sleep(waitingSeconds);

        String connectionUrl = "jdbc:sqlserver://163.17.39.33:1433;"
                + "databaseName=type_db;user=game2;password=pwdpwd";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        String sql = "UPDATE dbo.users SET pwd=? where userid=?";
        try (Connection conn = DriverManager.getConnection(connectionUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            pstmt.setString(1, passwd);  //pwd
            pstmt.setString(2, typingid);  //pfrom


            try {

                Integer rs = pstmt.executeUpdate();
                logger.info(String.format("MESSING UP USER %s PASSWD AND RETURN VALUES: %s", typingid, rs));


            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                pstmt.close();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }


    }
}
