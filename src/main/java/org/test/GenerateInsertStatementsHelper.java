package org.test;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.test.config.AppProperties;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateInsertStatementsHelper {

    private final AppProperties appProperties;

    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
//    private static final String JDBC_URL = "jdbc:oracle:thin:@192.168.233.131:1521:XE";
//    private static String JDBC_USER = "DMS";
//    private static String JDBC_PASSWD = "DMS";

    private static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public static final String QUERY = "" +
            "SELECT DISTINCT table_name " +
            "FROM user_tables " +
            "WHERE INSTR(table_name, 'X_') <> 1 " +
            "      AND INSTR(table_name, '$') = 0 " +
            "      AND NOT table_name IN (SELECT view_name FROM user_views) " +
            "      AND NOT table_name IN (SELECT mview_name FROM user_mviews) " +
            "ORDER BY table_name";

    @SneakyThrows
    public void generate(OutputStream outputStream) {

        try {
//        if (args.length < 1) {
//            usage();
//            System.exit(1);
//        }

            int i = 0;
//        String tableName = args[i];
//        if (tableName.contains("/")) { // username/password provided
//            String[] uid_pass = args[0].split("/");
//            if ((uid_pass.length != 2) || (args.length < 2)) {
//                usage();
//                System.exit(1);
//            }
//            JDBC_USER = uid_pass[0];
//            JDBC_PASSWD = uid_pass[1];
//            i++;
//            tableName = args[i];
//        }
//
//        if ("-f".equals(tableName)) {
//            tableName = null;
//            if (args.length < (i + 2)) {
//                usage();
//                System.exit(1);
//            }
//            fileName = args[i + 1];
//        }

            Class.forName(JDBC_DRIVER);
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(appProperties.url, appProperties.username, appProperties.password);

                List<String> tables = loadTableNames(conn);
                PrintWriter printWriter = new PrintWriter(outputStream);
                for (String tableName : tables) {
                    generateInsertStatements(conn, tableName, printWriter);
                }

                printWriter.close();

    //            if (tableName != null) {
    //            }
    //            else {
    //                PrintWriter p = new PrintWriter(new FileWriter("insert_all.sql"));
    //                p.println("spool insert_all.log");
    //
    //                BufferedReader r = new BufferedReader(new FileReader(fileName));
    //                tableName = r.readLine();
    //                while (tableName != null) {
    //                    p.println(String.format("@%s_insert.sql", tableName));
    //                    generateInsertStatements(conn, tableName, outputStream);
    //                    tableName = r.readLine();
    //                }
    //                r.close();
    //
    //                p.println("spool off");
    //                p.close();
    //            }
            } finally {
                if (conn != null) conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace(new PrintWriter(outputStream));
            throw e;
        }
    }

    private List<String> loadTableNames(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(QUERY);
        ResultSet rs = stmt.executeQuery();
        List<String> output = new ArrayList<>();

        while (rs.next()) {
            output.add(rs.getString("table_name"));
        }

        return output;
    }

    private static void generateInsertStatements(Connection conn, String tableName, PrintWriter printWriter)
            throws Exception {
        log.info("Generating Insert statements for: table={}", tableName);
        String sql = String.format("SELECT * FROM \"%s\"", tableName);
        Statement stmt = conn.createStatement();
        ResultSet rs = null;
        log.info("Executing {}", sql);
        rs = stmt.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();
        int numColumns = rsmd.getColumnCount();
        int[] columnTypes = new int[numColumns];
        String columnNames = "";
        for (int i = 0; i < numColumns; i++) {
            columnTypes[i] = rsmd.getColumnType(i + 1);
            if (i != 0) {
                columnNames += ",";
            }
            columnNames += rsmd.getColumnName(i + 1);
        }

        java.util.Date d = null;
//        PrintWriter p = new PrintWriter(new FileWriter(tableName + "_insert.sql"));

        printWriter.println("set sqlt off");
        printWriter.println("set sqlblanklines on");
        printWriter.println("set define off");
        while (rs.next()) {
            d = convert(tableName, rs, numColumns, columnTypes, columnNames, d, printWriter);
        }
    }

    private static java.util.Date convert(String tableName, ResultSet rs, int numColumns, int[] columnTypes, String columnNames, Date d, PrintWriter printWriter) throws SQLException {
        String columnValues = "";
        for (int i = 0; i < numColumns; i++) {
            if (i != 0) {
                columnValues += ",";
            }

            switch (columnTypes[i]) {
                case Types.BIGINT:
                case Types.BIT:
                case Types.BOOLEAN:
                case Types.DECIMAL:
                case Types.DOUBLE:
                case Types.FLOAT:
                case Types.INTEGER:
                case Types.SMALLINT:
                case Types.TINYINT:
                    String v = rs.getString(i + 1);
                    columnValues += v;
                    break;

                case Types.DATE:
                    d = rs.getDate(i + 1);
                case Types.TIME:
                    if (d == null) d = rs.getTime(i + 1);
                case Types.TIMESTAMP:
                    if (d == null) d = rs.getTimestamp(i + 1);

                    if (d == null) {
                        columnValues += "null";
                    } else {
                        columnValues += "TO_DATE('"
                                + dateFormat.format(d)
                                + "', 'YYYY/MM/DD HH24:MI:SS')";
                    }
                    break;

                default:
                    v = rs.getString(i + 1);
                    if (v != null) {
                        columnValues += "'" + v.replaceAll("'", "''") + "'";
                    } else {
                        columnValues += "null";
                    }
                    break;
            }
        }

        printWriter.println(String.format("INSERT INTO %s (%s) values (%s)\n/",
                tableName,
                columnNames,
                columnValues));

        return d;
    }

}
