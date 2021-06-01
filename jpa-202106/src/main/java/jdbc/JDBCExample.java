package jdbc;

import entitymanager.domain.Member;

import java.sql.*;

public class JDBCExample {
    private static final String URL = "jdbc:h2:~/test";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";
    private static final String DRIVER_CLASS_NAME = "org.h2.Driver";

    public static void main(String[] args) {
        JDBCExample app = new JDBCExample();

        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        app.createMemberTable();
        app.addMember(1L, "user1");
        app.addMember(2L, "user2");

        Member member1 = app.findMember(1L);
        Member member2 = app.findMember(1L);
        System.out.println(member1 == member2);
    }

    private void createMemberTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE MEMBERS (")
                .append("member_id INTEGER not NULL, ")
                .append("member_name VARCHAR(128), ")
                .append("PRIMARY KEY (member_id)")
                .append(")");
        String sql = sb.toString();


        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement()) {
            int result = statement.executeUpdate(sql);
            System.out.println("result: " + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMember(Long id, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO MEMBERS ")
                .append("(member_id, member_name) ")
                .append("VALUES (?,?)");
        String sql = sb.toString();

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(Math.toIntExact(id), name);
            System.out.println("Inserted Members");
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
    }

    private Member findMember(Long memberId) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT MEMBER_ID, MEMBER_NAME FROM MEMBERS WHERE MEMBER_ID =");
        String sql = sb.toString();

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql + memberId)) {
            if (resultSet.next()) {
                Long id = resultSet.getLong("member_id");
                String name = resultSet.getString("member_name");
                System.out.println("id: " + id + " , name: " + name);
                return new Member(id, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
