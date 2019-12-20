/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.sso.typing.service;

import app.sso.typing.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Calendar;
import java.util.stream.Collectors;

/**
 * @author igogo
 */
@Service
public class UpdateMssql {


    private final Logger logger = LoggerFactory.getLogger(UpdateMssql.class);
    ObjectMapper mapper = new ObjectMapper();

    int year = Calendar.getInstance().get(Calendar.YEAR);
    String connectionUrl = "jdbc:sqlserver://163.17.39.33:1433;"
            + "databaseName=type_db;user=game2;password=pwdpwd";

    public boolean isUserExist(String userid) throws ClassNotFoundException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String sql = "SELECT COUNT(*) FROM dbo.users where userid=?";

        try (Connection conn = DriverManager.getConnection(connectionUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {
            pstmt.setString(1, userid);
            try (ResultSet rs = pstmt.executeQuery();) {
                rs.next();
//                logger.info("count for user:" + String.valueOf(rs.getInt(1)));

                if (rs.getInt(1) == 0) {
                    return false;
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return true;
    }


    public void updateStudentMssql(String userid, String userpasswd, User user) throws ClassNotFoundException, SQLException, JsonProcessingException {

//        if (userid.equals("064643-601-096100766")) {
//            deleteUser(userid);
//        }

        if (isUserExist(userid)) {
            updateStudent(userid, userpasswd, user);

        } else {
            addStudent(userid, userpasswd, user);
        }


    }

    private void updateStudent(String userid, String userpasswd, User user) throws ClassNotFoundException, JsonProcessingException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        logger.info(String.format("UPDATE data: %s,%s", user.getUsername(), userid));
        String sql = "UPDATE dbo.users SET pwd=?, pfrom=?, pname=?, game_year=?, kind=?, pgrade=?, pclassno=? where userid=?";


        try (Connection conn = DriverManager.getConnection(connectionUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            pstmt.setString(1, userpasswd);  //pwd
            pstmt.setString(2, user.getSchoolname());  //pfrom
            pstmt.setString(3, user.getUsername()); //pname
            pstmt.setString(4, String.format("%d", year));  //game_year

            pstmt.setString(5, "p"); //表示練習組 kind
            pstmt.setString(6, user.getClassinfo().get(0).getGrade()); //pgrade
            pstmt.setString(7, user.getClassinfo().get(0).getClassno()); //pclassno
            pstmt.setString(8, userid);


            try {

                Integer rs = pstmt.executeUpdate();
                logger.info("UPDATE USER AND RETUREN VALUE: " + mapper.writeValueAsString(rs));


            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                pstmt.close();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }


    }


    private void addStudent(String userid, String userpasswd, User user) throws ClassNotFoundException, JsonProcessingException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        logger.info(String.format("UPDATE Student data: %s,%s", user.getUsername(), userid));
        String sql = "INSERT INTO dbo.users (userid, pwd, pfrom, pname, game_year, pgrade, pclassno, kind) VALUES(?,?,?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(connectionUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            logger.info(String.format("ADD data: %s,%s", user.getUsername(), userid));

            pstmt.setString(1, userid);
            pstmt.setString(2, userpasswd);
            pstmt.setString(3, user.getSchoolname());
            pstmt.setString(4, user.getUsername());
            pstmt.setString(5, String.format("%d", year));
            pstmt.setString(6, user.getClassinfo().get(0).getGrade()); //pgrade
            pstmt.setString(7, user.getClassinfo().get(0).getClassno()); //pclassno
            pstmt.setString(8, "p");


            try {

                Integer rs = pstmt.executeUpdate();
                logger.info("INSERT INTO DB AND RETUREN VALUE: " + mapper.writeValueAsString(rs));


            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                pstmt.close();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }


    }


    private void updateTeacher(String userid, String userpasswd, User user) throws ClassNotFoundException, JsonProcessingException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        String userTitles = user.getTitles().stream()
                .flatMap(titles -> titles.getTitles().stream())
                .collect(Collectors.joining("-"));


        String pfrom = String.format("%s-%s", user.getSchoolname(), userTitles);
        String sql = "UPDATE dbo.users SET pwd=?, pfrom=?, pname=?, game_year=?, kind=? where userid=?";

        try (Connection conn = DriverManager.getConnection(connectionUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            pstmt.setString(1, userpasswd);  //pwd
            pstmt.setString(2, pfrom);  //pfrom
            pstmt.setString(3, user.getUsername()); //pname
            pstmt.setString(4, String.format("%d", year));  //game_year

            pstmt.setString(5, "p"); //表示練習組 kind
            pstmt.setString(6, userid);


            try {

                Integer rs = pstmt.executeUpdate();
                logger.info("UPDATE DB AND RETUREN VALUE: " + mapper.writeValueAsString(rs));


            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                pstmt.close();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }


    }


    private void addTeacher(String userid, String userpasswd, User user) throws ClassNotFoundException, JsonProcessingException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        logger.info(String.format("ADD data: %s,%s", user.getUsername(), userid));

        String sql = "INSERT INTO dbo.users (userid, pwd, pfrom, pname, game_year, kind) VALUES(?,?,?,?,?,?)";


        String userTitles = user.getTitles().stream()
                .flatMap(titles -> titles.getTitles().stream())
                .collect(Collectors.joining("-"));


        String pfrom = String.format("%s-%s", user.getSchoolname(), userTitles);

        try (Connection conn = DriverManager.getConnection(connectionUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {


            pstmt.setString(1, userid);
            pstmt.setString(2, userpasswd);
            pstmt.setString(3, pfrom);
            pstmt.setString(4, user.getUsername());
            pstmt.setString(5, String.format("%d", year));
            pstmt.setString(6, "p");

            try {

                Integer rs = pstmt.executeUpdate();
                logger.info("INSERT INTO DB AND RETUREN VALUE: " + mapper.writeValueAsString(rs));


            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                pstmt.close();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }


    }


    public void updateTeacherMssql(String userid, String userpasswd, User user) throws ClassNotFoundException, SQLException, JsonProcessingException {

//        if (userid.equals("igogo")) {
//            deleteUser(userid);
//
//        }

        // Declare the JDBC objects.
        Connection conn;
        PreparedStatement pstmt;
        ResultSet rs;

//        int year = Calendar.getInstance().get(Calendar.YEAR);

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
        pstmt.setString(1, userid);
        int result;
        rs = pstmt.executeQuery();

        if (isUserExist(userid)) {
            updateTeacher(userid, userpasswd, user);


        } else {
            addTeacher(userid, userpasswd, user);

        }


        try {
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.info(ex.getMessage());
        }
    }


    private void deleteUser(String userid) throws ClassNotFoundException, JsonProcessingException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        logger.info(String.format("DELETE USER: %s", userid));

        String sql = "DELETE dbo.users where userid=?";


        try (Connection conn = DriverManager.getConnection(connectionUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            pstmt.setString(1, userid);

            try {

                Integer rs = pstmt.executeUpdate();
                logger.info("DELETE USER AND RERUTN VALUES: " + mapper.writeValueAsString(rs));


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



