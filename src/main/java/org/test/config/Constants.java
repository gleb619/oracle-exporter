package org.test.config;

import java.text.SimpleDateFormat;

public class Constants {

    public static final String BYTES = "bytes";
    public static final String APPLICATION_X_SQL_CHARSET_UTF_8 = "application/x-sql;charset=UTF-8";
    public static final String DEFAULT_PORT = "1521";
    public static final String START_REQUEST = "start-request";
    public static final String ID = "request-id";
    public static final String TABLE_NAME = "table_name";
    public static final String SELECT_FROM = "SELECT * FROM \"%s\"";
    public static final String INSERT_INTO = "INSERT INTO %s (%s) values (%s);";
    public static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    public static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public static final String QUERY = "" +
            "SELECT DISTINCT table_name " +
            "FROM user_tables " +
            "WHERE INSTR(table_name, 'X_') <> 1 " +
            "      AND INSTR(table_name, '$') = 0 " +
            "      AND NOT table_name IN (SELECT view_name FROM user_views) " +
            "      AND NOT table_name IN (SELECT mview_name FROM user_mviews) " +
            "ORDER BY table_name";

    private Constants() {
        //
    }

}
