package org.test.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.test.config.AppProperties;

import javax.annotation.PostConstruct;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.sql.DriverManager.getConnection;
import static org.test.config.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsertsHelper {

    private final AppProperties appProperties;

    private static void generateInsertStatements(Connection conn, String tableName, PrintWriter printWriter)
            throws Exception {
        String sql = String.format(SELECT_FROM, tableName);
        log.debug("Executing {}", sql);

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();
            int[] columnTypes = new int[numColumns];
            String columnNames = "";
            int count = 0;

            for (int i = 0; i < numColumns; i++) {
                columnTypes[i] = rsmd.getColumnType(i + 1);
                if (i != 0) {
                    columnNames += ",";
                }
                columnNames += rsmd.getColumnName(i + 1);
            }

            while (rs.next()) {
                count++;
                convert(tableName, rs, numColumns, columnTypes, columnNames, printWriter);
            }

            if (count > 0) {
                printWriter.println();
            }
        }
    }

    private static void convert(String tableName,
                                ResultSet rs,
                                int numColumns,
                                int[] columnTypes,
                                String columnNames,
                                PrintWriter printWriter) throws SQLException {
        Date date = null;
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
                    date = rs.getDate(i + 1);
                case Types.TIME:
                    if (date == null) date = rs.getTime(i + 1);
                case Types.TIMESTAMP:
                    if (date == null) date = rs.getTimestamp(i + 1);

                    if (date == null) {
                        columnValues += "null";
                    } else {
                        columnValues += String.format("TO_DATE('%s', 'YYYY/MM/DD HH24:MI:SS')", dateFormat.format(date));
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

        printWriter.print(String.format(INSERT_INTO,
                tableName,
                columnNames,
                columnValues));
    }

    @PostConstruct
    @SneakyThrows
    public void init() {
        Class.forName(JDBC_DRIVER);
    }

    public void generateStandard(OutputStream outputStream) {
        generate(outputStream, appProperties);
    }

    public void generateCustom(OutputStream outputStream, AppProperties data) {
        AppProperties properties = data.merge(appProperties);
        generate(outputStream, properties);
    }

    @SneakyThrows
    private void generate(OutputStream outputStream, AppProperties appProperties) {
        log.debug("Prepare to export data for {}, {}", appProperties.url, appProperties.username);
        try {
            try (Connection conn = getConnection(appProperties.url, appProperties.username, appProperties.password)) {
                log.info("Prepare to dump data");
                List<String> tables = loadTableNames(conn);
                log.info("Loaded information about {} tables", tables.size());
                try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
                    for (String tableName : tables) {
                        generateInsertStatements(conn, tableName, printWriter);
                    }
                }
                log.info("Data was successfully exported");
            }
        } catch (Exception e) {
            log.error("ERROR: ", e);
            e.printStackTrace(new PrintWriter(outputStream));
        }
    }

    private List<String> loadTableNames(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(QUERY); ResultSet rs = stmt.executeQuery()) {
            List<String> output = new ArrayList<>();

            while (rs.next()) {
                output.add(rs.getString(TABLE_NAME));
            }

            return output;
        }
    }

}
