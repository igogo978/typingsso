/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.sso.typing.service;

import app.sso.typing.model.Typingscores;
import app.sso.typing.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author igogo
 */
@Service
public class TypingMssqlService {


    private final Logger logger = LoggerFactory.getLogger(TypingMssqlService.class);
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

    public void getTablenames() throws ClassNotFoundException, SQLException {
        String connectionUrl = "jdbc:sqlserver://163.17.39.33:1433;"
                + "databaseName=type_db;user=game2;password=pwdpwd";
        ResultSetMetaData rsmd = null;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        try (Connection conn = DriverManager.getConnection(connectionUrl);) {
            DatabaseMetaData dbmd = conn.getMetaData();
            String[] types = {"TABLE"};
            ResultSet rs = dbmd.getTables(null, null, "%", types);


            while (rs.next()) {
                logger.info("table name:" + rs.getString("TABLE_NAME"));
            }
        }

    }

    public String getTypingArticleSubject(String id) throws ClassNotFoundException, SQLException {
        String subject = "";
        String connectionUrl = "jdbc:sqlserver://163.17.39.33:1433;"
                + "databaseName=type_db;user=game2;password=pwdpwd";
//        String sql = "SELECT * FROM typing WHERE userid LIKE ? ORDER BY id DESC ";
        String sql = "SELECT * FROM typewrite WHERE id = ?";

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");


        try (Connection conn = DriverManager.getConnection(connectionUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
//                    logger.info("id: " + rs.getString("id"));
//                    logger.info("lang: " + rs.getString("lang"));
//                    logger.info("subject: " + rs.getString("subject"));
                    subject = rs.getString("subject");

                }


            }
        }
        return subject;
    }





    public List<Typingscores> getStudentsTypingScores(String schoolid) throws ClassNotFoundException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String connectionUrl = "jdbc:sqlserver://163.17.39.33:1433;"
                + "databaseName=type_db;user=game2;password=pwdpwd";
        String sql = "SELECT TOP 300 * FROM typing WHERE userid LIKE ? ORDER BY id DESC ";
//        String sql = "SELECT TOP 5 * FROM typing ORDER BY id DESC ";

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        List<Typingscores> typingscoresList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(connectionUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + schoolid + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Typingscores typingscores = new Typingscores();
                    typingscores.setId(rs.getString("id"));
                    typingscores.setScores(rs.getString("score"));


                    String lang = "";
                    if (rs.getString("lang").equals("1")) {
                        lang = "中";
                    } else {
                        lang = "英";
                    }
                    typingscores.setLang(lang);
                    typingscores.setPosttime(rs.getString("posttime"));
                    typingscores.setGame_year(rs.getString("game_year"));
                    typingscores.setUserid(rs.getString("userid")); //login id
                    typingscores.setRightcount(rs.getString("rightcount") + "字");
                    typingscores.setWrongcount(rs.getString("wrongcount") + "字");
                    typingscores.setMyname(rs.getString("myname"));
                    typingscores.setTypingsubject(getTypingArticleSubject(rs.getString("typeid")));
                    typingscores.setSchoolname(rs.getString("schoolname"));
//                    logger.info(rs.getString("posttime").substring(0,16));
                    typingscores.setPosttime(rs.getString("posttime").substring(0,16));
                    typingscores.setIp(rs.getString("ip"));

                    typingscores.setClassname(typingscores.getUserid().split("-")[1]);
                    String typingspeed = String.format("%s 字/分", rs.getInt("rightcount") / 10.0);
                    typingscores.setTypingspeed(typingspeed);

                    float accuracy = rs.getInt("rightcount") / (float) (rs.getInt("rightcount") + rs.getInt("wrongcount")) * 100;
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(1);
//                    logger.info("accuracy: " + df.format(accuracy));
                    typingscores.setAccuracy(String.valueOf(df.format(accuracy)) + "%");
//                    logger.info(mapper.writeValueAsString(typingscores));
                    typingscoresList.add(typingscores);
                }


            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return typingscoresList;
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


//typing column name
//                rsmd = rs.getMetaData();
//                logger.info("column name 1:" + rsmd.getColumnName(1)); //id
//                logger.info("column name 2:" + rsmd.getColumnName(2)); //score
//                logger.info("column name 3:" + rsmd.getColumnName(3)); //lang
//                logger.info("column name 4:" + rsmd.getColumnName(4)); //posttime
//                logger.info("column name 5:" + rsmd.getColumnName(5)); //ip
//                logger.info("column name 6:" + rsmd.getColumnName(6)); //userid
//                logger.info("column name 7:" + rsmd.getColumnName(7)); //timer
//                logger.info("column name 8:" + rsmd.getColumnName(8)); //rightcount
//                logger.info("column name 9:" + rsmd.getColumnName(9)); //wrongcount
//                logger.info("column name 10:" + rsmd.getColumnName(10)); //schoolname
//                logger.info("column name 11:" + rsmd.getColumnName(11)); //gradename
//                logger.info("column name 12:" + rsmd.getColumnName(12)); //classname
//                logger.info("column name 13:" + rsmd.getColumnName(13)); //sn
//                logger.info("column name 14:" + rsmd.getColumnName(14)); //typeid
//                logger.info("column name 15:" + rsmd.getColumnName(15)); //myname
//                logger.info("column name 16:" + rsmd.getColumnName(16)); //teachername
//                logger.info("column name 17:" + rsmd.getColumnName(17)); //deleted
//                logger.info("column name 18:" + rsmd.getColumnName(18)); //lastword
//                logger.info("column name 19:" + rsmd.getColumnName(19)); //notype
//                logger.info("column name 20:" + rsmd.getColumnName(20)); //kind
//                logger.info("column name 21:" + rsmd.getColumnName(21)); //edus
//                logger.info("column name 22:" + rsmd.getColumnName(22)); //game_year
//                logger.info("column name 23:" + rsmd.getColumnName(23)); //grade
//                logger.info("column name 24:" + rsmd.getColumnName(24)); //check_code
//                logger.info("column name 25:" + rsmd.getColumnName(25)); //tea_title
//                logger.info("column name 26:" + rsmd.getColumnName(26)); //input


//    public void getAllTypingArticles() throws ClassNotFoundException, SQLException {
////        typewrite column count:10
//        String connectionUrl = "jdbc:sqlserver://163.17.39.33:1433;"
//                + "databaseName=type_db;user=game2;password=pwdpwd";
////        String sql = "SELECT * FROM typing WHERE userid LIKE ? ORDER BY id DESC ";
//        String sql = "SELECT * FROM typewrite";
//
//        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//
//
//        try (Connection conn = DriverManager.getConnection(connectionUrl);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            try (ResultSet rs = pstmt.executeQuery()) {
//
//                while (rs.next()) {
////                    logger.info("id: " + rs.getString("id"));
////                    logger.info("lang: " + rs.getString("lang"));
////                    logger.info("subject: " + rs.getString("subject"));
////                    logger.info("content: "+ rs.getString("content"));
////                    logger.info("inputerid: "+ rs.getString("inputerid"));
//                }
//
//
//            }
//        }
//
//    }

//    public void getTypingArticlesColumnnames() throws ClassNotFoundException, SQLException {
////        typewrite column count:10
//        String connectionUrl = "jdbc:sqlserver://163.17.39.33:1433;"
//                + "databaseName=type_db;user=game2;password=pwdpwd";
////        String sql = "SELECT * FROM typing WHERE userid LIKE ? ORDER BY id DESC ";
//        String sql = "SELECT TOP 1 * FROM typewrite";
//
//        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//
//
//        try (Connection conn = DriverManager.getConnection(connectionUrl);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            try (ResultSet rs = pstmt.executeQuery()) {
//                ResultSetMetaData rsmd = rs.getMetaData();
//
////                logger.info("type write column length:"+ rsmd.getColumnCount());
////                logger.info("type write column name 1:"+ rsmd.getColumnName(1));
////                logger.info("type write column name 1:"+ rsmd.getColumnName(2));
////                logger.info("type write column name 1:"+ rsmd.getColumnName(3));
////                logger.info("type write column name 1:"+ rsmd.getColumnName(4));
////                logger.info("type write column name 1:"+ rsmd.getColumnName(5));
////                logger.info("type write column name 1:"+ rsmd.getColumnName(6));
////                logger.info("type write column name 1:"+ rsmd.getColumnName(7));
////                logger.info("type write column name 1:"+ rsmd.getColumnName(8));
////                logger.info("type write column name 1:"+ rsmd.getColumnName(9));
////                logger.info("type write column name 1:"+ rsmd.getColumnName(10));
//            }
//        }
//
//    }