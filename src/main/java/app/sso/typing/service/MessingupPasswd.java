package app.sso.typing.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;


@Service
public class MessingupPasswd {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Declare the JDBC objects.
//    Connection conn;
//    PreparedStatement pstmt;
//    ResultSet rs;

    @Async
    public void execute(String typingid, String passwd) throws InterruptedException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        Random random = new Random();
        Integer waitingSeconds = random.ints(10000, 20000).findFirst().getAsInt();
        logger.info("waiting seconds to mess up user's password: " + String.valueOf(waitingSeconds / 1000));
        Thread.sleep(waitingSeconds);
//        logger.info(String.format("update user %s passwd", typingid));


//        String passwd = String.format("%s", Base64.getEncoder().encodeToString(String.valueOf(waitingSeconds).getBytes(StandardCharsets.UTF_8)).substring(0, 8));
//
        logger.info("GENERATE an random passwd:" + passwd);

        String connectionUrl = "jdbc:sqlserver://163.17.39.33:1433;"
                + "databaseName=type_db;user=game2;password=pwdpwd";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        conn = DriverManager.getConnection(connectionUrl);


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


//validate passwd
//        sql = "SELECT * FROM dbo.users where userid=?";
//        pstmt = conn.prepareStatement(sql);
//        pstmt.setString(1, typingid);  //typingid
//        rs = pstmt.executeQuery();
//        while (rs.next()) {
//            logger.info("query update passwd value:" + rs.getString("pwd"));
//        }

//        try {
//            pstmt.close();
//            conn.close();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            logger.info(ex.getMessage());
//        }
