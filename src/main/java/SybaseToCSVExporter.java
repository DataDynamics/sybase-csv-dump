import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SybaseToCSVExporter {

    public static void main(String[] args) {
        Map<String, String> params = parseArgs(args);

        String jdbcUrl = params.get("jdbc-url");
        String username = params.get("username");
        String password = params.get("password");
        String database = params.get("database");
        String sql = params.get("sql");
        String table = params.get("table");
        String delimiter = params.getOrDefault("delimiter", ",");
        String output = params.get("output");

        if (jdbcUrl == null || username == null || password == null || (database == null || table == null) ? (sql == null ? true : false) : false || output == null) {
            System.err.println("Missing required arguments.");
            printUsage();
            System.exit(1);
        }

        String query = sql == null ? String.format("SELECT * FROM %s..%s", database, table) : sql;

        try (
                Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                BufferedWriter writer = new BufferedWriter(new FileWriter(output));
        ) {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            // Write header
            for (int i = 1; i <= columnCount; i++) {
                writer.write(meta.getColumnName(i));
                if (i < columnCount) writer.write(delimiter);
            }
            writer.newLine();

            // Write rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String val = rs.getString(i);
                    if (val != null) {
                        // Escape delimiter and quote if necessary
                        val = val.replace("\"", "\"\"");
                        if (val.contains(delimiter) || val.contains("\"")) {
                            val = "\"" + val + "\"";
                        }
                    }
                    writer.write(val != null ? val : "");
                    if (i < columnCount) writer.write(delimiter);
                }
                writer.newLine();
            }

            System.out.println("CSV export completed: " + output);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (String arg : args) {
            if (arg.startsWith("--") && arg.contains("=")) {
                String[] parts = arg.substring(2).split("=", 2);
                map.put(parts[0], parts[1]);
            }
        }
        return map;
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java SybaseToCSVExporter --jdbc-url=JDBC_URL --username=USER --password=PASS --database=DB --table=TABLE --delimiter=',' --output=FILE.csv");
    }
}
