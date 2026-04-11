package com.taskmanagement.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton class to manage SQLite database connection
 * Automatically initializes schema on first use
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:data/taskmanagement.db";
    private static final String DRIVER = "org.sqlite.JDBC";

    private DatabaseConnection() {
        try {
            // Load SQLite JDBC driver
            Class.forName(DRIVER);

            // Ensure data directory exists
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdir();
            }

            // Create connection
            this.connection = DriverManager.getConnection(DB_URL);

            // Enable foreign keys
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }

            // Initialize schema if tables don't exist
            initializeSchema();

            // Apply forward-only migrations for existing databases
            applyMigrations();

            System.out.println("Database connection established at: " + new File(DB_URL.replace("jdbc:sqlite:", "")).getAbsolutePath());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found. Add org.xerial:sqlite-jdbc to dependencies.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish database connection", e);
        }
    }

    /**
     * Gets the singleton instance of DatabaseConnection
     * @return the database connection instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Gets the active database connection
     * @return the Connection object
     */
    public Connection getConnection() {
        if (connection == null) {
            throw new RuntimeException("Database connection is not available");
        }
        return createCloseSafeProxy(connection);
    }

    /**
     * Initializes database schema by reading and executing schema.sql
     */
    private void initializeSchema() {
        try {
            String schema = readSchemaFromFile();
            if (schema != null && !schema.trim().isEmpty()) {
                executeSchema(schema);
                System.out.println("Database schema initialized successfully");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }

    /**
     * Reads schema.sql file from classpath resources
     * @return the schema SQL as a string
     */
    private String readSchemaFromFile() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("schema.sql")) {
            if (inputStream == null) {
                throw new RuntimeException("schema.sql not found in resources");
            }

            StringBuilder schema = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip comments and empty lines
                    if (!line.trim().startsWith("--") && !line.trim().isEmpty()) {
                        schema.append(line).append("\n");
                    }
                }
            }

            return schema.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read schema.sql", e);
        }
    }

    /**
     * Executes the schema SQL statements
     * @param schemaSql the SQL schema to execute
     */
    private void executeSchema(String schemaSql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Split by semicolon to execute individual statements
            String[] statements = schemaSql.split(";");
            for (String sql : statements) {
                String trimmedSql = sql.trim();
                if (!trimmedSql.isEmpty()) {
                    statement.execute(trimmedSql);
                }
            }
        }
    }

    private void applyMigrations() throws SQLException {
        addColumnIfMissing("tasks", "parent_task_id", "VARCHAR(36)");
    }

    private void addColumnIfMissing(String tableName, String columnName, String columnDefinition) throws SQLException {
        String sql = "PRAGMA table_info(" + tableName + ")";

        try (Statement statement = connection.createStatement();
             java.sql.ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                String existingColumn = rs.getString("name");
                if (existingColumn != null && existingColumn.equalsIgnoreCase(columnName)) {
                    return;
                }
            }
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition);
        }
    }

    /**
     * Closes the database connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close database connection", e);
        }
    }

    /**
     * Checks if the connection is still active
     * @return true if connection is active, false otherwise
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    private Connection createCloseSafeProxy(Connection delegate) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if ("close".equals(method.getName()) && method.getParameterCount() == 0) {
                    return null;
                }
                return method.invoke(delegate, args);
            }
        };

        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                handler
        );
    }
}