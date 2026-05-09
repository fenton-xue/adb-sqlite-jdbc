package com.adbsqlite;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdbSqliteStatement implements Statement {

    private static final String NULL_MARKER = "__ADB_NULL__";
    private static final int TIMEOUT_SEC = 30;

    private final AdbSqliteConnection conn;
    private boolean closed;
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
        String finalSql = replaceParams(sql);
        List<String> lines = exec(finalSql);
        // 解析 CSV: 第一行列名，后续为数据
        if (lines.isEmpty()) return emptyResultSet();
        List<String> columns = parseCsvLine(lines.get(0));
        List<String[]> rows = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] row = parseCsvLine(lines.get(i)).toArray(new String[0]);
            rows.add(row);
        }
        return new AdbSqliteResultSet(columns, rows, NULL_MARKER);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        ensureOpen();
        String finalSql = replaceParams(sql);
        exec(finalSql);
        return 0; // sqlite3 不返回影响行数
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
    public int getUpdateCount() { return -1; }

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
        int[] results = new int[batch.size()];
        for (int i = 0; i < batch.size(); i++) {
            execute(batch.get(i));
            results[i] = SUCCESS_NO_INFO;
        }
        batch.clear();
        return results;
    }

    // ---- 核心: ADB 命令执行（用 base64 编码传递 SQL，全平台兼容）----
    //     base64 仅含字母数字和 +/=，无任何 shell 特殊字符

    private List<String> exec(String sql) throws SQLException {
        String b64 = Base64.getEncoder().encodeToString(sql.getBytes(StandardCharsets.UTF_8));
        String cmd = String.format(
                "su -c 'echo \"%s\" | base64 -d | sqlite3 -header -csv -nullvalue %s %s 2>&1'",
                b64, NULL_MARKER, conn.getDbPath());
        System.out.println("[ADB] " + cmd);
        System.out.println("[SQL] " + sql);
        return runAdb(conn.getDevice(), cmd);
    }

    List<String> runAdb(String device, String shellCmd) throws SQLException {
        List<String> result = new ArrayList<>();
        try {
            ProcessBuilder pb = new ProcessBuilder("adb", "-s", device, "shell", shellCmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();

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

            // 检查 sqlite3 错误
            if (!result.isEmpty() && result.get(0).startsWith("Error:")) {
                throw new SQLException("SQLite 错误: " + String.join("\n", result));
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

    // ---- 参数替换 ----

    private String replaceParams(String sql) {
        if (params.isEmpty()) return sql;
        String result = sql;
        for (int i = 0; i < params.size(); i++) {
            Object p = params.get(i);
            if (p == null) {
                result = result.replaceFirst("\\?", "NULL");
            } else if (p instanceof Number) {
                result = result.replaceFirst("\\?", p.toString());
            } else {
                // 字符串: 用单引号包裹，转义内部单引号
                String escaped = p.toString().replace("'", "''");
                result = result.replaceFirst("\\?", "'" + escaped + "'");
            }
        }
        return result;
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
