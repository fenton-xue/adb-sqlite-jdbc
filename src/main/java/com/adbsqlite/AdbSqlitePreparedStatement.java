package com.adbsqlite;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdbSqlitePreparedStatement extends AdbSqliteStatement implements PreparedStatement {

    private final String sql;
    private final List<Object> params = new ArrayList<>();

    AdbSqlitePreparedStatement(AdbSqliteConnection conn, String sql) {
        super(conn);
        this.sql = sql;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return super.executeQuery(buildSql());
    }

    @Override
    public int executeUpdate() throws SQLException {
        return super.executeUpdate(buildSql());
    }

    @Override
    public long executeLargeUpdate() throws SQLException {
        return super.executeLargeUpdate(buildSql());
    }

    @Override
    public boolean execute() throws SQLException {
        return super.execute(buildSql());
    }

    @Override
    public void addBatch() throws SQLException {
        super.addBatch(buildSql());
    }

    private String buildSql() {
        return bindParams(sql, params);
    }

    // ---- parameter setters ----

    private void set(int index, Object value) {
        while (params.size() <= index) params.add(null);
        params.set(index, value);
    }

    @Override public void setNull(int i, int sqlType) { set(i - 1, null); }
    @Override public void setNull(int i, int sqlType, String typeName) { set(i - 1, null); }
    @Override public void setString(int i, String x) { set(i - 1, x); }
    @Override public void setInt(int i, int x) { set(i - 1, x); }
    @Override public void setLong(int i, long x) { set(i - 1, x); }
    @Override public void setDouble(int i, double x) { set(i - 1, x); }
    @Override public void setBoolean(int i, boolean x) { set(i - 1, x ? 1 : 0); }
    @Override public void setShort(int i, short x) { set(i - 1, (int) x); }
    @Override public void setByte(int i, byte x) { set(i - 1, (int) x); }
    @Override public void setFloat(int i, float x) { set(i - 1, (double) x); }
    @Override public void setObject(int i, Object x) { set(i - 1, x); }

    @Override
    public void setObject(int i, Object x, int targetSqlType) { set(i - 1, x); }

    @Override
    public void setObject(int i, Object x, int targetSqlType, int scaleOrLength) { set(i - 1, x); }

    @Override public void setBigDecimal(int i, BigDecimal x) { set(i - 1, x); }
    @Override public void setBytes(int i, byte[] x) { set(i - 1, x); }
    @Override public void setDate(int i, Date x) { set(i - 1, x != null ? x.toString() : null); }
    @Override public void setDate(int i, Date x, Calendar cal) { set(i - 1, x); }
    @Override public void setTime(int i, Time x) { set(i - 1, x != null ? x.toString() : null); }
    @Override public void setTime(int i, Time x, Calendar cal) { set(i - 1, x); }
    @Override public void setTimestamp(int i, Timestamp x) { set(i - 1, x != null ? x.toString() : null); }
    @Override public void setTimestamp(int i, Timestamp x, Calendar cal) { set(i - 1, x); }

    @Override public void clearParameters() { params.clear(); }

    @Override public ParameterMetaData getParameterMetaData() { throw new UnsupportedOperationException(); }

    // ---- binary stubs ----
    @Override public void setAsciiStream(int i, InputStream x) { throw new UnsupportedOperationException(); }
    @Override public void setAsciiStream(int i, InputStream x, int length) { throw new UnsupportedOperationException(); }
    @Override public void setAsciiStream(int i, InputStream x, long length) { throw new UnsupportedOperationException(); }
    @Override public void setBinaryStream(int i, InputStream x) { throw new UnsupportedOperationException(); }
    @Override public void setBinaryStream(int i, InputStream x, int length) { throw new UnsupportedOperationException(); }
    @Override public void setBinaryStream(int i, InputStream x, long length) { throw new UnsupportedOperationException(); }
    @Override public void setBlob(int i, Blob x) { throw new UnsupportedOperationException(); }
    @Override public void setBlob(int i, InputStream x) { throw new UnsupportedOperationException(); }
    @Override public void setBlob(int i, InputStream x, long length) { throw new UnsupportedOperationException(); }
    @Override public void setCharacterStream(int i, Reader x) { throw new UnsupportedOperationException(); }
    @Override public void setCharacterStream(int i, Reader x, int length) { throw new UnsupportedOperationException(); }
    @Override public void setCharacterStream(int i, Reader x, long length) { throw new UnsupportedOperationException(); }
    @Override public void setClob(int i, Clob x) { throw new UnsupportedOperationException(); }
    @Override public void setClob(int i, Reader x) { throw new UnsupportedOperationException(); }
    @Override public void setClob(int i, Reader x, long length) { throw new UnsupportedOperationException(); }
    @Override public void setNCharacterStream(int i, Reader x) { throw new UnsupportedOperationException(); }
    @Override public void setNCharacterStream(int i, Reader x, long length) { throw new UnsupportedOperationException(); }
    @Override public void setNClob(int i, NClob x) { throw new UnsupportedOperationException(); }
    @Override public void setNClob(int i, Reader x) { throw new UnsupportedOperationException(); }
    @Override public void setNClob(int i, Reader x, long length) { throw new UnsupportedOperationException(); }
    @Override public void setNString(int i, String x) { set(i - 1, x); }
    @Override public void setRef(int i, Ref x) { throw new UnsupportedOperationException(); }
    @Override public void setRowId(int i, RowId x) { throw new UnsupportedOperationException(); }
    @Override public void setSQLXML(int i, SQLXML x) { throw new UnsupportedOperationException(); }
    @Override public void setURL(int i, URL x) { throw new UnsupportedOperationException(); }
    @Override public void setUnicodeStream(int i, InputStream x, int length) { throw new UnsupportedOperationException(); }
    @Override public void setArray(int i, Array x) { throw new UnsupportedOperationException(); }

    @Override
    public ResultSetMetaData getMetaData() { return null; }
}
