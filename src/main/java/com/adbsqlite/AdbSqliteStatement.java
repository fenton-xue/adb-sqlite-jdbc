package com.adbsqlite;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdbSqliteStatement implements Statement {

    static final String NULL_MARKER = "__ADB_NULL__";
    private static final String UPDATE_COUNT_COLUMN = "__adb_update_count__";
    private static final int TIMEOUT_SEC = 30;

    private final AdbSqliteConnection conn;
    private boolean closed;
    private int updateCount = -1;
    private final List<Object> params = new ArrayList<>();

    AdbSqliteStatement(AdbSqliteConnection conn) {
        this.conn = conn;
    }

    // ---- parameter setter (简陋 PreparedStatement) ----

    public void setParam(int index, Object value) {
        while (params.size() <= index) params.add(null);
        params.set(index, value);
    }

    public void setString(int index, String value) { setParam(index, value); }
    public void setInt(int index, int value) { setParam(index, value); }
    public void setLong(int index, long value) { setParam(index, value); }
    public void setDouble(int index, double value) { setParam(index, value); }
    public void setBoolean(int index, boolean value) { setParam(index, value ? 1 : 0); }
    public void setObject(int index, Object value) { setParam(index, value); }

    public void clearParams() { params.clear(); }

    // ---- execute methods ----

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        ensureOpen();
        updateCount = -1;
        String finalSql = replaceParams(sql);
        List<String> lines = exec(finalSql);
        if (lines.isEmpty()) return emptyResultSet();
        List<List<String>> records = parseCsvRecords(String.join("\n", lines));
        if (records.isEmpty()) return emptyResultSet();
        // 解析 CSV: 第一条记录是列名，后续为数据
        List<String> columns = records.get(0);
        List<String[]> rows = new ArrayList<>();
        for (int i = 1; i < records.size(); i++) {
            List<String> fields = records.get(i);
            if (fields.size() > columns.size()) {
                throw new SQLException("CSV record has " + fields.size()
                        + " fields, expected " + columns.size() + " at record " + (i + 1));
            }
            String[] row = new String[columns.size()];
            for (int j = 0; j < columns.size(); j++) {
                row[j] = j < fields.size() ? fields.get(j) : NULL_MARKER;
            }
            rows.add(row);
        }
        // 尝试解析列类型（仅对 SELECT FROM 单表查询有效）
        List<Integer> columnTypes = resolveColumnTypes(finalSql, columns);
        return new AdbSqliteResultSet(columns, rows, NULL_MARKER, columnTypes);
    }

    private List<Integer> resolveColumnTypes(String sql, List<String> columns) {
        String tableName = extractTableName(sql);
        if (tableName == null) return null;
        try {
            String safeName = tableName.replace("'", "''");
            List<String> pragmaLines = exec("PRAGMA table_info('" + safeName + "')");
            if (pragmaLines.isEmpty()) return null;
            List<String> ph = parseCsvLine(pragmaLines.get(0));
            int colNameIdx = ph.indexOf("name");
            int typeIdx = ph.indexOf("type");
            Integer[] types = new Integer[columns.size()];
            for (int i = 1; i < pragmaLines.size(); i++) {
                List<String> pf = parseCsvLine(pragmaLines.get(i));
                String colName = pf.get(colNameIdx);
                String sqliteType = pf.get(typeIdx);
                int jdbcType = AdbSqliteDatabaseMetaData.sqliteTypeToJdbcType(sqliteType);
                for (int j = 0; j < columns.size(); j++) {
                    if (columns.get(j).equalsIgnoreCase(colName)) {
                        types[j] = jdbcType;
                    }
                }
            }
            for (int i = 0; i < types.length; i++) {
                if (types[i] == null) types[i] = Types.VARCHAR;
            }
            return java.util.Arrays.asList(types);
        } catch (Exception e) {
            return null;
        }
    }

    private static String extractTableName(String sql) {
        String upper = sql.toUpperCase().trim();
        int fromIdx = upper.indexOf("FROM ");
        if (fromIdx < 0) return null;
        String after = sql.substring(fromIdx + 5).trim();
        if (after.isEmpty()) return null;
        // 跳过可能的括号、引号
        char first = after.charAt(0);
        if (first == '(') return null; // 子查询，不支持
        int end = 0;
        for (int i = 0; i < after.length(); i++) {
            char c = after.charAt(i);
            if (Character.isWhitespace(c) || c == ',' || c == ';' || c == ')'
                    || c == 'W' /* WHERE */ || c == 'G' /* GROUP */ || c == 'O' /* ORDER */
                    || c == 'L' /* LIMIT */ || c == 'J' /* JOIN */ || c == 'I' /* INNER */ || c == 'L' /* LEFT */) {
                end = i;
                break;
            }
            end = i + 1;
        }
        String tableName = after.substring(0, end);
        // 去除可能的引号
        if ((tableName.startsWith("\"") && tableName.endsWith("\""))
                || (tableName.startsWith("`") && tableName.endsWith("`"))
                || (tableName.startsWith("'") && tableName.endsWith("'"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
        }
        if (tableName.isEmpty()) return null;
        return tableName;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        ensureOpen();
        String finalSql = replaceParams(sql);
        List<String> lines = exec(appendChangesQuery(finalSql));
        updateCount = parseUpdateCount(lines);
        return updateCount;
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        ensureOpen();
        String trimmed = replaceParams(sql).trim().toUpperCase();
        if (trimmed.startsWith("SELECT") || trimmed.startsWith("PRAGMA")
                || trimmed.startsWith("EXPLAIN")) {
            // 存到 conn 的 lastResultSet 中供 getResultSet 获取
            conn.setLastResultSet(executeQuery(sql));
            return true;
        } else {
            executeUpdate(sql);
            conn.setLastResultSet(null);
            return false;
        }
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return conn.getLastResultSet();
    }

    @Override
    public int getUpdateCount() { return updateCount; }

    @Override
    public long getLargeUpdateCount() { return updateCount; }

    @Override
    public long executeLargeUpdate(String sql) throws SQLException {
        return executeUpdate(sql);
    }

    @Override
    public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return executeLargeUpdate(sql);
    }

    @Override
    public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return executeLargeUpdate(sql);
    }

    @Override
    public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
        return executeLargeUpdate(sql);
    }

    @Override
    public boolean getMoreResults() { return false; }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    @Override
    public void close() { closed = true; }

    @Override
    public boolean isClosed() { return closed; }

    @Override
    public Connection getConnection() { return conn; }

    // ---- batch ----

    private final List<String> batch = new ArrayList<>();

    @Override
    public void addBatch(String sql) { batch.add(sql); }

    @Override
    public void clearBatch() { batch.clear(); }

    @Override
    public int[] executeBatch() throws SQLException {
        ensureOpen();
        int[] results = new int[batch.size()];
        for (int i = 0; i < batch.size(); i++) {
            results[i] = executeUpdate(batch.get(i));
        }
        batch.clear();
        return results;
    }

    @Override
    public long[] executeLargeBatch() throws SQLException {
        ensureOpen();
        long[] results = new long[batch.size()];
        for (int i = 0; i < batch.size(); i++) {
            results[i] = executeLargeUpdate(batch.get(i));
        }
        batch.clear();
        return results;
    }

    // ---- 核心: ADB 命令执行 ----

    List<String> exec(String sql) throws SQLException {
        String sqliteCmd = String.format(
                "sqlite3 -header -csv -nullvalue %s %s 2>&1",
                NULL_MARKER, conn.getDbPath());
        String cmd = conn.isRoot() ? "su -c '" + sqliteCmd + "'" : sqliteCmd;
        System.out.println("[ADB] " + cmd);
        System.out.println("[SQL] " + sql);
        return runAdb(conn.getDevice(), cmd, sql);
    }

    List<String> runAdb(String device, String shellCmd) throws SQLException {
        return runAdb(device, shellCmd, null);
    }

    List<String> runAdb(String device, String shellCmd, String stdin) throws SQLException {
        List<String> result = new ArrayList<>();
        try {
            ProcessBuilder pb = new ProcessBuilder("adb", "-s", device, "shell", shellCmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            if (stdin != null) {
                try (Writer writer = new OutputStreamWriter(p.getOutputStream(), StandardCharsets.UTF_8)) {
                    writer.write(stdin);
                }
            }

            // 读取输出
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.add(line);
                }
            }

            boolean finished = p.waitFor(TIMEOUT_SEC, TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                throw new SQLException("ADB 命令超时（" + TIMEOUT_SEC + "秒）");
            }

            int exitCode = p.exitValue();
            // 检查 sqlite3 错误
            if (!result.isEmpty() && result.get(0).startsWith("Error:")) {
                throw new SQLException("SQLite 错误: " + String.join("\n", result));
            }
            if (exitCode != 0) {
                String output = result.isEmpty() ? "" : ": " + String.join("\n", result);
                throw new SQLException("ADB/SQLite 命令失败（exit " + exitCode + "）" + output);
            }
            return result;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("ADB 执行失败: " + e.getMessage(), e);
        }
    }

    // ---- CSV 解析 ----

    static List<String> parseCsvLine(String line) {
        if (line == null || line.isEmpty()) return Collections.singletonList("");
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inquote = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inquote) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        sb.append('"');
                        i++;
                    } else {
                        inquote = false;
                    }
                } else {
                    sb.append(c);
                }
            } else {
                if (c == '"') {
                    inquote = true;
                } else if (c == ',') {
                    fields.add(sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }
            }
        }
        fields.add(sb.toString());
        return fields;
    }

    static List<List<String>> parseCsvRecords(String csv) throws SQLException {
        List<List<String>> records = new ArrayList<>();
        if (csv == null || csv.isEmpty()) return records;

        List<String> record = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inquote = false;
        boolean recordHasContent = false;

        for (int i = 0; i < csv.length(); i++) {
            char c = csv.charAt(i);
            if (inquote) {
                if (c == '"') {
                    if (i + 1 < csv.length() && csv.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        inquote = false;
                    }
                } else {
                    field.append(c);
                }
            } else {
                if (c == '"') {
                    inquote = true;
                    recordHasContent = true;
                } else if (c == ',') {
                    record.add(field.toString());
                    field.setLength(0);
                    recordHasContent = true;
                } else if (c == '\n' || c == '\r') {
                    if (c == '\r' && i + 1 < csv.length() && csv.charAt(i + 1) == '\n') {
                        i++;
                    }
                    record.add(field.toString());
                    field.setLength(0);
                    records.add(record);
                    record = new ArrayList<>();
                    recordHasContent = false;
                } else {
                    field.append(c);
                    recordHasContent = true;
                }
            }
        }

        if (inquote) {
            throw new SQLException("Unterminated quoted CSV field");
        }
        if (recordHasContent || field.length() > 0 || !record.isEmpty()) {
            record.add(field.toString());
            records.add(record);
        }
        return records;
    }

    private static String appendChangesQuery(String sql) {
        String trimmed = sql.trim();
        if (trimmed.endsWith(";")) {
            return trimmed + "\nSELECT changes() AS " + UPDATE_COUNT_COLUMN;
        }
        return trimmed + ";\nSELECT changes() AS " + UPDATE_COUNT_COLUMN;
    }

    static int parseUpdateCount(List<String> lines) throws SQLException {
        if (lines == null || lines.isEmpty()) return 0;
        throwIfSqliteError(lines);

        List<List<String>> records = parseCsvRecords(String.join("\n", lines));
        for (int i = 0; i < records.size() - 1; i++) {
            List<String> header = records.get(i);
            for (int j = 0; j < header.size(); j++) {
                if (UPDATE_COUNT_COLUMN.equalsIgnoreCase(header.get(j))) {
                    List<String> values = records.get(i + 1);
                    if (j >= values.size()) {
                        throw new SQLException("Missing SQLite update count value");
                    }
                    try {
                        return Integer.parseInt(values.get(j));
                    } catch (NumberFormatException e) {
                        throw new SQLException("Invalid SQLite update count: " + values.get(j), e);
                    }
                }
            }
        }

        List<String> lastRecord = records.get(records.size() - 1);
        if (lastRecord.size() == 1) {
            try {
                return Integer.parseInt(lastRecord.get(0));
            } catch (NumberFormatException ignored) {
                // Fall through to a descriptive error below.
            }
        }
        throw new SQLException("Unable to determine SQLite update count: " + String.join("\n", lines));
    }

    private static void throwIfSqliteError(List<String> lines) throws SQLException {
        for (String line : lines) {
            if (line == null) continue;
            String trimmed = line.trim();
            if (trimmed.startsWith("Error:") || trimmed.startsWith("Runtime error")) {
                throw new SQLException("SQLite 错误: " + String.join("\n", lines));
            }
        }
    }

    // ---- 参数替换 ----

    private String replaceParams(String sql) {
        if (params.isEmpty()) return sql;
        return bindParams(sql, params);
    }

    static String bindParams(String sql, List<Object> params) {
        StringBuilder result = new StringBuilder(sql.length() + params.size() * 8);
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        int paramIndex = 0;

        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);

            if (inSingleQuote) {
                result.append(c);
                if (c == '\'') {
                    if (i + 1 < sql.length() && sql.charAt(i + 1) == '\'') {
                        result.append(sql.charAt(i + 1));
                        i++;
                    } else {
                        inSingleQuote = false;
                    }
                }
                continue;
            }

            if (inDoubleQuote) {
                result.append(c);
                if (c == '"') {
                    if (i + 1 < sql.length() && sql.charAt(i + 1) == '"') {
                        result.append(sql.charAt(i + 1));
                        i++;
                    } else {
                        inDoubleQuote = false;
                    }
                }
                continue;
            }

            if (c == '\'') {
                inSingleQuote = true;
                result.append(c);
            } else if (c == '"') {
                inDoubleQuote = true;
                result.append(c);
            } else if (c == '?' && paramIndex < params.size()) {
                result.append(toSqlLiteral(params.get(paramIndex++)));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    static String toSqlLiteral(Object value) {
        if (value == null) return "NULL";
        if (value instanceof Number) return value.toString();
        String escaped = value.toString().replace("'", "''");
        return "'" + escaped + "'";
    }

    private ResultSet emptyResultSet() throws SQLException {
        return new AdbSqliteResultSet(
                Collections.emptyList(),
                Collections.emptyList(),
                NULL_MARKER);
    }

    private void ensureOpen() throws SQLException {
        if (closed) throw new SQLException("Statement is closed");
        if (conn.isClosed()) throw new SQLException("Connection is closed");
    }

    // ---- stubs ----

    @Override public int getMaxFieldSize() { return 0; }
    @Override public void setMaxFieldSize(int max) {}
    @Override public int getMaxRows() { return 0; }
    @Override public void setMaxRows(int max) {}
    @Override public void setEscapeProcessing(boolean enable) {}
    @Override public int getQueryTimeout() { return TIMEOUT_SEC; }
    @Override public void setQueryTimeout(int s) {}
    @Override public void cancel() {}
    @Override public SQLWarning getWarnings() { return null; }
    @Override public void clearWarnings() {}
    @Override public void setCursorName(String name) {}
    @Override public int getResultSetConcurrency() { return ResultSet.CONCUR_READ_ONLY; }
    @Override public int getResultSetType() { return ResultSet.TYPE_FORWARD_ONLY; }
    @Override public void setFetchDirection(int d) {}
    @Override public int getFetchDirection() { return ResultSet.FETCH_FORWARD; }
    @Override public void setFetchSize(int rows) {}
    @Override public int getFetchSize() { return 1; }
    @Override public int getResultSetHoldability() { return ResultSet.CLOSE_CURSORS_AT_COMMIT; }
    @Override public boolean isPoolable() { return false; }
    @Override public void setPoolable(boolean p) {}
    @Override public void closeOnCompletion() {}
    @Override public boolean isCloseOnCompletion() { return false; }
    @Override public <T> T unwrap(Class<T> iface) throws SQLException { throw new SQLException("not supported"); }
    @Override public boolean isWrapperFor(Class<?> iface) { return false; }

    // ---- getGeneratedKeys (返回空) ----

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return emptyResultSet();
    }

    // ---- execute(String, int...) stubs ----

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return executeUpdate(sql);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return executeUpdate(sql);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return executeUpdate(sql);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return execute(sql);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return execute(sql);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return execute(sql);
    }
}
