package ru.pchelicam.services;

import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBCDataSource {

    private static final PGSimpleDataSource dataSource;

    static {
        dataSource = new PGSimpleDataSource();
        dataSource.setDatabaseName("fiasdb");
        dataSource.setCurrentSchema("fias");
        dataSource.setUser("postgres");
        dataSource.setPassword("postgres");
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
