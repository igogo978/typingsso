/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.sso.typing.service;

import app.sso.typing.model.User;
import app.sso.typing.model.user.Titles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author igogo
 */
@Service
public class UpdateMssql {

    // Declare the JDBC objects.  
    Connection conn;
    PreparedStatement pstmt;
    ResultSet rs;

    private final Logger logger = LoggerFactory.getLogger(UpdateMssql.class);

    public void updateStudMssql(String typingid, String typingpasswd, User user) throws ClassNotFoundException, SQLException {
        int year = Calendar.getInstance().get(Calendar.YEAR);

        logger.info("connect mssql");
//        String connectionUrl = "jdbc:sqlserver://163.17.63.98:1433;"
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

//        //test description, 測試新增資料
//        if (typingid.equals("064757-504-nFf2I")) {
//            if (rs.next()) {
//                logger.info(String.format("刪掉測試帳號:%s", typingid));
//                sql = "DELETE dbo.users where userid=?";
//                pstmt = conn.prepareStatement(sql);
//                pstmt.setString(1, typingid);
//                result = pstmt.executeUpdate();
//            }
//        }
        if (rs.next()) {
            logger.info("update data:" + typingid);
            sql = "UPDATE dbo.users SET pwd=?, pfrom=?, pname=?, game_year=?, kind=?, pgrade=?, pclassno=? where userid=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, typingpasswd);  //pwd
            pstmt.setString(2, user.getSchoolname());  //pfrom
            pstmt.setString(3, user.getUsername()); //pname
            pstmt.setString(4, String.format("%d", year));  //game_year

            pstmt.setString(5, "p"); //表示練習組 kind
            pstmt.setString(6, user.getClassinfo().get(0).getGrade()); //pgrade
            pstmt.setString(7, user.getClassinfo().get(0).getClassno()); //pclassno
            pstmt.setString(8, typingid);

            result = pstmt.executeUpdate();

        } else {
            logger.info("no records, insert data: " + typingid);

            sql = "INSERT INTO dbo.users (userid, pwd, pfrom, pname, game_year, pgrade, pclassno, kind) VALUES(?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, typingid);
            pstmt.setString(2, typingpasswd);
            pstmt.setString(3, user.getSchoolname());
            pstmt.setString(4, user.getUsername());
            pstmt.setString(5, String.format("%d", year));
            pstmt.setString(6, user.getClassinfo().get(0).getGrade()); //pgrade
            pstmt.setString(7, user.getClassinfo().get(0).getClassno()); //pclassno
            pstmt.setString(8, "p");
            result = pstmt.executeUpdate();
        }

        try {
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.info(ex.getMessage());
        }
    }

    public void updateTeacherMssql(String typingid, String typingpasswd, User user) throws ClassNotFoundException, SQLException {
        int year = Calendar.getInstance().get(Calendar.YEAR);

        String userTitles = user.getTitles().stream()
                .flatMap(titles -> titles.getTitles().stream())
                .collect(Collectors.joining("-"));

        logger.info(String.format("%s-%s", user.getSchoolname(), userTitles));
        String pfrom = String.format("%s-%s", user.getSchoolname(), userTitles);
        logger.info("connect mssql");

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
            logger.info("update data:" + typingid);
            sql = "UPDATE dbo.users SET pwd=?, pfrom=?, pname=?, game_year=?, kind=? where userid=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, typingpasswd);  //pwd
            pstmt.setString(2, pfrom);  //pfrom
            pstmt.setString(3, user.getUsername()); //pname
            pstmt.setString(4, String.format("%d", year));  //game_year

            pstmt.setString(5, "p"); //表示練習組 kind
            pstmt.setString(6, typingid);

            result = pstmt.executeUpdate();

        } else {
            logger.info("no records, insert data: " + typingid);

            sql = "INSERT INTO dbo.users (userid, pwd, pfrom, pname, game_year, kind) VALUES(?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, typingid);
            pstmt.setString(2, typingpasswd);
            pstmt.setString(3, pfrom);
            pstmt.setString(4, user.getUsername());
            pstmt.setString(5, String.format("%d", year));

            pstmt.setString(6, "p");
            result = pstmt.executeUpdate();
        }

        try {
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.info(ex.getMessage());
        }
    }

}
