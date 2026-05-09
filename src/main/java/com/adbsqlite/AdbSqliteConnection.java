package com.adbsqlite;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class AdbSqliteConnection implements Connection {

    private final String device;
    private final String dbPath;
    private final String url;
    private boolean closed;
    private ResultSet lastResultSet;
    private final DatabaseMetaData metaData;

    AdbSqliteConnection(String url, String device, String dbPath) throws SQLException {
        this.url = url;
        this.device = device;
        this.dbPath = dbPath;
        this.metaData = new AdbSqliteDatabaseMetaData(this);
        // 连接时校验 ADB 和 Root
        validateConnection();
    }

    private void validateConnection() throws SQLException {
        // 用 AdbSqliteStatement.runAdb 静态方法执行验证
        AdbSqliteStatement s = new AdbSqliteStatement(this);
        try {
            // 校验 root
            s.runAdb(device, "su -c 'echo ROOT_OK'");
        } catch (SQLException e) {
            throw new SQLException("ADB 连接或 Root 权限检查失败，请确认: \n"
                    + "  1. adb devices 能看到设备\n"
                    + "  2. 设备已 Root\n"
                    + "  3. 目标设备地址: " + device, e);
        }
    }

    @Override
    public Statement createStatement() throws SQLException {
        ensureOpen();
        return new AdbSqliteStatement(this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        ensureOpen();
        return new AdbSqlitePreparedStatement(this, sql);
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        if (closed) return false;
        try {
            AdbSqliteStatement s = new AdbSqliteStatement(this);
            s.runAdb(device, "su -c 'echo OK'");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void close() { closed = true; }

    @Override
    public boolean isClosed() { return closed; }

    @Override
    public DatabaseMetaData getMetaData() { return metaData; }

    @Override
    public String getCatalog() { return null; }

    @Override
    public void setCatalog(String catalog) {}

    @Override
    public String getSchema() { return null; }

    @Override
    public void setSchema(String schema) {}

    @Override
    public int getTransactionIsolation() { return TRANSACTION_NONE; }

    @Override
    public void setTransactionIsolation(int level) {}

    @Override
    public SQLWarning getWarnings() { return null; }

    @Override
    public void clearWarnings() {}

    @Override
    public Map<String, Class<?>> getTypeMap() { return null; }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) {}

    @Override
    public int getHoldability() { return ResultSet.CLOSE_CURSORS_AT_COMMIT; }

    @Override
    public void setHoldability(int holdability) {}

    @Override
    public boolean getAutoCommit() { return true; }

    @Override
    public void setAutoCommit(boolean autoCommit) {}

    @Override
    public void commit() {}

    @Override
    public void rollback() {}

    @Override
    public boolean isReadOnly() { return false; }

    @Override
    public void setReadOnly(boolean readOnly) {}

    @Override
    public void setClientInfo(String name, String value) {}

    @Override
    public void setClientInfo(Properties properties) {}

    @Override
    public String getClientInfo(String name) { return null; }

    @Override
    public Properties getClientInfo() { return new Properties(); }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) {}

    @Override
    public int getNetworkTimeout() { return 0; }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw uoe();
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw uoe();
    }

    @Override
    public Blob createBlob() throws SQLException { throw uoe(); }
    @Override
    public Clob createClob() throws SQLException { throw uoe(); }
    @Override
    public NClob createNClob() throws SQLException { throw uoe(); }
    @Override
    public SQLXML createSQLXML() throws SQLException { throw uoe(); }
    @Override
    public Savepoint setSavepoint() throws SQLException { throw uoe(); }
    @Override
    public Savepoint setSavepoint(String name) throws SQLException { throw uoe(); }
    @Override
    public void rollback(Savepoint savepoint) throws SQLException { throw uoe(); }
    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException { throw uoe(); }
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return createStatement();
    }
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return createStatement();
    }
    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return prepareStatement(sql);
    }
    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return prepareStatement(sql);
    }
    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return prepareStatement(sql);
    }
    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return prepareStatement(sql);
    }
    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return prepareStatement(sql);
    }
    @Override
    public CallableStatement prepareCall(String sql) throws SQLException { throw uoe(); }
    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { throw uoe(); }
    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { throw uoe(); }
    @Override
    public String nativeSQL(String sql) throws SQLException { return sql; }
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException { throw uoe(); }
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }

    // ---- package-level accessors ----

    String getDevice() { return device; }
    String getDbPath() { return dbPath; }

    void setLastResultSet(ResultSet rs) { this.lastResultSet = rs; }
    ResultSet getLastResultSet() { return lastResultSet; }

    @Override
    public void abort(Executor executor) throws SQLException {
        close();
    }

    private void ensureOpen() throws SQLException {
        if (closed) throw new SQLException("Connection is closed");
    }

    private SQLException uoe() { return new SQLFeatureNotSupportedException(); }
}
