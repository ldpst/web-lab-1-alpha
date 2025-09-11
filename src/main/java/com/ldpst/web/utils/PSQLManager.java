package com.ldpst.web.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.logging.Logger;

public class PSQLManager {
    private String url = "jdbc:postgresql://shoots-db:5432/shoots";
    private String user = "postgres";
    private String password = "passsword";

    private String createTableSQL = """
            CREATE TABLE IF NOT EXISTS shoots (
                id SERIAL PRIMARY KEY,
                x TEXT,
                y TEXT,
                r TEXT,
                duration TEXT,
                date TEXT,
                hit BOOLEAN
            );
            """;

    private Connection conn;

    public PSQLManager() {
        try {
            conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addShoot(Map<String, BigDecimal> body, String duration, LocalDateTime date, boolean hit) {
        if (conn == null) {
            System.err.println("Database connection is not initialized.");
            return;
        }

        if (body == null || body.get("x") == null || body.get("y") == null || body.get("r") == null) {
            System.err.println("Invalid body: missing x, y, or r values.");
            return;
        }

        if (date == null) {
            date = LocalDateTime.now();
        }

        String insertSQL = "INSERT INTO shoots(x,y,r,duration,date,hit) VALUES(?,?,?,?,?,?);";

        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, body.get("x").toString());
            pstmt.setString(2, body.get("y").toString());
            pstmt.setString(3, body.get("r").toString());
            pstmt.setString(4, duration != null ? duration : "");

            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            pstmt.setString(5, formatter.format(java.sql.Timestamp.valueOf(date)));

            pstmt.setBoolean(6, hit);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to insert shoot into database:");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in addShoot:");
            e.printStackTrace();
        }
    }


    public void clear() {
        String truncateSQL = "TRUNCATE TABLE shoots RESTART IDENTITY;";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(truncateSQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getShoots() {
        String selectSQL = "SELECT * FROM shoots;";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeQuery(selectSQL);

            ObjectMapper mapper = new ObjectMapper();
            ArrayNode array = mapper.createArrayNode();

            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                ObjectNode row = mapper.createObjectNode();
                row.put("x", rs.getString(2));
                row.put("y", rs.getString(3));
                row.put("r", rs.getString(4));
                row.put("duration", rs.getString(5));
                row.put("date", rs.getString(6));
                row.put("check", rs.getBoolean(7));
                array.add(row);
            }

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(array);


        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
