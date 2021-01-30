package core;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
public class DatabaseManager {
    private static DatabaseManager instance;
    private DatabaseManager() {
    }
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private static final String JDBC_URL = "jdbc:mysql://192.168.100.122:3307/cloudia";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";
    private static int max; // 連接池中最大Connection數目
    private HikariDataSource ds;
    public void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
             e.printStackTrace();
            return;
        }
        connect();
    }

    public void connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(JDBC_URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(3);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        ds = new HikariDataSource(config);
    }

    public synchronized Connection getConnection()
            throws SQLException {
        return ds.getConnection();
    }

    public void insertBatch(String... sqls) throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement();
        for (String sql : sqls) {
            stmt.addBatch(sql); // SQL
        }
        stmt.executeBatch();
        conn.commit();
        stmt.close();
        conn.close();
    }

    public void insert(String sql) throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
        conn.close();
    }

    public boolean isInit() {
        return ds != null;
    }

    public void close() {
        ds.close();
        ds = null;
    }
}
