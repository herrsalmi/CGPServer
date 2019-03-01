package edu.sdet.dao;

import edu.sdet.entities.Word;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * project FunctionalAnalysisClient
 * Created by ayyoub on 4/11/18.
 */
public class WordDaoSqlite implements WordDao {

    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    public void insertWord(Word word) {
        String sql = "INSERT INTO frequencies(lemm,count) VALUES(?,?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, word.getLemm());
            pstmt.setDouble(2, word.getCount());
            pstmt.executeUpdate();
            LOGGER.info("Inserted " + word);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Word getWord(String lemm) {
        String sql = "SELECT lemm, count FROM frequencies WHERE lemm=?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lemm);
            ResultSet rs = pstmt.executeQuery();
            // loop through the result set
            rs.next();
            return new Word(rs.getString("lemm"), rs.getInt("count"));

        } catch (SQLException e) {
            // log
        }
        return new Word(lemm, 0);
    }

    private Connection connect() {
        String url = "jdbc:sqlite:frequency.sqlite";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS frequencies (\n"
                + " lemm text PRIMARY KEY,\n"
                + " count integer\n"
                + ");";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
