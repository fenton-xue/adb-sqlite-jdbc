package com.adbsqlite;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class AdbSqliteResultSet implements ResultSet {

    private final List<String> columns;
    private final List<String[]> rows;
    private final String nullMarker;
    private int cursor = -1;       // -1 = before first
    private boolean closed;
    private boolean lastWasNull;
    private final ResultSetMetaData metaData;

    public AdbSqliteResultSet(List<String> columns, List<String[]> rows, String nullMarker) {
        this(columns, rows, nullMarker, null);
    }

    public AdbSqliteResultSet(List<String> columns, List<String[]> rows, String nullMarker, List<Integer> columnTypes) {
        this.columns = columns;
        this.rows = rows;
        this.nullMarker = nullMarker;
        this.metaData = new AdbSqliteResultSetMetaData(columns, columnTypes);
    }

    @Override
    public boolean next() throws SQLException {
        ensureOpen();
        cursor++;
        return cursor < rows.size();
    }

    @Override
    public void close() throws SQLException {
        closed = true;
    }

    @Override
    public boolean isClosed() { return closed; }

    @Override
    public boolean wasNull() { return lastWasNull; }

    // ---- column value getters ----

    @Override
    public String getString(int columnIndex) throws SQLException {
        String val = getValue(columnIndex);
        lastWasNull = (val == null);
        return val;
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        return getString(findColumn(columnLabel));
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        String s = getString(columnIndex);
        return (s == null) ? 0 : Integer.parseInt(s);
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return getInt(findColumn(columnLabel));
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        String s = getString(columnIndex);
        return (s == null) ? 0 : Long.parseLong(s);
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return getLong(findColumn(columnLabel));
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        String s = getString(columnIndex);
        return (s == null) ? 0.0 : Double.parseDouble(s);
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return getDouble(findColumn(columnLabel));
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        String s = getString(columnIndex);
        return s != null && !"0".equals(s) && !"false".equalsIgnoreCase(s);
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean(findColumn(columnLabel));
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return getString(columnIndex);
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return getString(columnLabel);
    }

    // ---- metadata ----

    @Override
    public ResultSetMetaData getMetaData() { return metaData; }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).equalsIgnoreCase(columnLabel)) return i + 1;
        }
        throw new SQLException("Column not found: " + columnLabel);
    }

    // ---- internals ----

    private String getValue(int columnIndex) throws SQLException {
        ensureOpen();
        if (cursor < 0 || cursor >= rows.size())
            throw new SQLException("No current row");
        String[] row = rows.get(cursor);
        if (columnIndex < 1 || columnIndex > row.length)
            throw new SQLException("Column index out of range: " + columnIndex);
        String val = row[columnIndex - 1];
        if (nullMarker.equals(val)) return null;
        return val;
    }

    private void ensureOpen() throws SQLException {
        if (closed) throw new SQLException("ResultSet is closed");
    }

    // ---- unsupported stubs (required by interface) ----

    @Override public boolean isBeforeFirst() throws SQLException { return cursor < 0; }
    @Override public boolean isAfterLast() throws SQLException { return cursor >= rows.size(); }
    @Override public boolean isFirst() throws SQLException { return cursor == 0; }
    @Override public boolean isLast() throws SQLException { return cursor == rows.size() - 1; }
    @Override public void beforeFirst() throws SQLException { cursor = -1; }
    @Override public void afterLast() throws SQLException { cursor = rows.size(); }
    @Override public boolean first() throws SQLException { cursor = 0; return rows.size() > 0; }
    @Override public boolean last() throws SQLException { cursor = rows.size() - 1; return rows.size() > 0; }
    @Override public void moveToCurrentRow() throws SQLException { }
    @Override public void moveToInsertRow() throws SQLException { throw uoe(); }
    @Override public void cancelRowUpdates() throws SQLException { }
    @Override public void deleteRow() throws SQLException { throw uoe(); }
    @Override public void insertRow() throws SQLException { throw uoe(); }
    @Override public void updateRow() throws SQLException { throw uoe(); }
    @Override public void refreshRow() throws SQLException { }
    @Override public int getRow() { return (cursor >= 0 && cursor < rows.size()) ? cursor + 1 : 0; }
    @Override public boolean absolute(int row) throws SQLException { throw uoe(); }
    @Override public boolean relative(int r) throws SQLException { throw uoe(); }
    @Override public boolean previous() throws SQLException { throw uoe(); }
    @Override public void setFetchDirection(int d) throws SQLException { ensureOpen(); }
    @Override public int getFetchDirection() throws SQLException { return FETCH_FORWARD; }
    @Override public void setFetchSize(int rows) throws SQLException { ensureOpen(); }
    @Override public int getFetchSize() throws SQLException { return 1; }
    @Override public int getType() throws SQLException { return TYPE_FORWARD_ONLY; }
    @Override public int getConcurrency() throws SQLException { return CONCUR_READ_ONLY; }
    @Override public boolean rowUpdated() throws SQLException { return false; }
    @Override public boolean rowInserted() throws SQLException { return false; }
    @Override public boolean rowDeleted() throws SQLException { return false; }
    @Override public void updateNull(int i) throws SQLException { throw uoe(); }
    @Override public void updateNull(String l) throws SQLException { throw uoe(); }
    @Override public void updateBoolean(int i, boolean x) throws SQLException { throw uoe(); }
    @Override public void updateBoolean(String l, boolean x) throws SQLException { throw uoe(); }
    @Override public void updateByte(int i, byte x) throws SQLException { throw uoe(); }
    @Override public void updateByte(String l, byte x) throws SQLException { throw uoe(); }
    @Override public void updateShort(int i, short x) throws SQLException { throw uoe(); }
    @Override public void updateShort(String l, short x) throws SQLException { throw uoe(); }
    @Override public void updateInt(int i, int x) throws SQLException { throw uoe(); }
    @Override public void updateInt(String l, int x) throws SQLException { throw uoe(); }
    @Override public void updateLong(int i, long x) throws SQLException { throw uoe(); }
    @Override public void updateLong(String l, long x) throws SQLException { throw uoe(); }
    @Override public void updateFloat(int i, float x) throws SQLException { throw uoe(); }
    @Override public void updateFloat(String l, float x) throws SQLException { throw uoe(); }
    @Override public void updateDouble(int i, double x) throws SQLException { throw uoe(); }
    @Override public void updateDouble(String l, double x) throws SQLException { throw uoe(); }
    @Override public void updateBigDecimal(int i, BigDecimal x) throws SQLException { throw uoe(); }
    @Override public void updateBigDecimal(String l, BigDecimal x) throws SQLException { throw uoe(); }
    @Override public void updateString(int i, String x) throws SQLException { throw uoe(); }
    @Override public void updateString(String l, String x) throws SQLException { throw uoe(); }
    @Override public void updateBytes(int i, byte[] x) throws SQLException { throw uoe(); }
    @Override public void updateBytes(String l, byte[] x) throws SQLException { throw uoe(); }
    @Override public void updateDate(int i, Date x) throws SQLException { throw uoe(); }
    @Override public void updateDate(String l, Date x) throws SQLException { throw uoe(); }
    @Override public void updateTime(int i, Time x) throws SQLException { throw uoe(); }
    @Override public void updateTime(String l, Time x) throws SQLException { throw uoe(); }
    @Override public void updateTimestamp(int i, Timestamp x) throws SQLException { throw uoe(); }
    @Override public void updateTimestamp(String l, Timestamp x) throws SQLException { throw uoe(); }
    @Override public void updateAsciiStream(int i, InputStream x) throws SQLException { throw uoe(); }
    @Override public void updateAsciiStream(String l, InputStream x) throws SQLException { throw uoe(); }
    @Override public void updateAsciiStream(int i, InputStream x, int len) throws SQLException { throw uoe(); }
    @Override public void updateAsciiStream(String l, InputStream x, int len) throws SQLException { throw uoe(); }
    @Override public void updateAsciiStream(int i, InputStream x, long len) throws SQLException { throw uoe(); }
    @Override public void updateAsciiStream(String l, InputStream x, long len) throws SQLException { throw uoe(); }
    @Override public void updateBinaryStream(int i, InputStream x, int len) throws SQLException { throw uoe(); }
    @Override public void updateBinaryStream(String l, InputStream x, int len) throws SQLException { throw uoe(); }
    @Override public void updateCharacterStream(int i, Reader x, int len) throws SQLException { throw uoe(); }
    @Override public void updateCharacterStream(String l, Reader x, int len) throws SQLException { throw uoe(); }
    @Override public void updateObject(int i, Object x) throws SQLException { throw uoe(); }
    @Override public void updateObject(String l, Object x) throws SQLException { throw uoe(); }
    @Override public void updateObject(int i, Object x, int scale) throws SQLException { throw uoe(); }
    @Override public void updateObject(String l, Object x, int scale) throws SQLException { throw uoe(); }
    @Override public void updateArray(int i, Array x) throws SQLException { throw uoe(); }
    @Override public void updateArray(String l, Array x) throws SQLException { throw uoe(); }
    @Override public Statement getStatement() throws SQLException { return null; }
    @Override public <T> T getObject(int i, Class<T> type) throws SQLException { throw uoe(); }
    @Override public <T> T getObject(String label, Class<T> type) throws SQLException { throw uoe(); }
    @Override public Object getObject(String label, Map<String, Class<?>> map) throws SQLException { throw uoe(); }
    @Override public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException { throw uoe(); }

    // result set meta data for driver
    @Override public String getCursorName() throws SQLException { throw uoe(); }
    @Override public int getHoldability() throws SQLException { throw uoe(); }
    @Override public SQLWarning getWarnings() throws SQLException { return null; }
    @Override public void clearWarnings() throws SQLException {}

    // ---- stubs that are just fallbacks ----
    @Override public String getNString(int i) throws SQLException { return getString(i); }
    @Override public String getNString(String label) throws SQLException { return getString(label); }
    @Override public byte getByte(int i) throws SQLException { return (byte) getInt(i); }
    @Override public short getShort(int i) throws SQLException { return (short) getInt(i); }
    @Override public float getFloat(int i) throws SQLException { return (float) getDouble(i); }
    @Override public byte[] getBytes(int i) throws SQLException { throw uoe(); }
    @Override public Date getDate(int i) throws SQLException { throw uoe(); }
    @Override public Time getTime(int i) throws SQLException { throw uoe(); }
    @Override public Timestamp getTimestamp(int i) throws SQLException { throw uoe(); }
    @Override public InputStream getAsciiStream(int i) throws SQLException { throw uoe(); }
    @Override public InputStream getUnicodeStream(int i) throws SQLException { throw uoe(); }
    @Override public InputStream getBinaryStream(int i) throws SQLException { throw uoe(); }
    @Override public Reader getCharacterStream(int i) throws SQLException { throw uoe(); }
    @Override public Reader getNCharacterStream(int i) throws SQLException { throw uoe(); }
    @Override public BigDecimal getBigDecimal(int i) throws SQLException { throw uoe(); }
    @Override public BigDecimal getBigDecimal(int i, int scale) throws SQLException { throw uoe(); }
    @Override public BigDecimal getBigDecimal(String l) throws SQLException { throw uoe(); }
    @Override public BigDecimal getBigDecimal(String l, int scale) throws SQLException { throw uoe(); }
    @Override public byte getByte(String l) throws SQLException { return getByte(findColumn(l)); }
    @Override public short getShort(String l) throws SQLException { return getShort(findColumn(l)); }
    @Override public float getFloat(String l) throws SQLException { return getFloat(findColumn(l)); }
    @Override public byte[] getBytes(String l) throws SQLException { throw uoe(); }
    @Override public Date getDate(String l) throws SQLException { throw uoe(); }
    @Override public Time getTime(String l) throws SQLException { throw uoe(); }
    @Override public Timestamp getTimestamp(String l) throws SQLException { throw uoe(); }
    @Override public InputStream getAsciiStream(String l) throws SQLException { throw uoe(); }
    @Override public InputStream getUnicodeStream(String l) throws SQLException { throw uoe(); }
    @Override public InputStream getBinaryStream(String l) throws SQLException { throw uoe(); }
    @Override public Reader getCharacterStream(String l) throws SQLException { throw uoe(); }
    @Override public Reader getNCharacterStream(String l) throws SQLException { throw uoe(); }
    @Override public Date getDate(int i, Calendar cal) throws SQLException { throw uoe(); }
    @Override public Date getDate(String l, Calendar cal) throws SQLException { throw uoe(); }
    @Override public Time getTime(int i, Calendar cal) throws SQLException { throw uoe(); }
    @Override public Time getTime(String l, Calendar cal) throws SQLException { throw uoe(); }
    @Override public Timestamp getTimestamp(int i, Calendar cal) throws SQLException { throw uoe(); }
    @Override public Timestamp getTimestamp(String l, Calendar cal) throws SQLException { throw uoe(); }
    @Override public URL getURL(int i) throws SQLException { throw uoe(); }
    @Override public URL getURL(String l) throws SQLException { throw uoe(); }
    @Override public Ref getRef(int i) throws SQLException { throw uoe(); }
    @Override public Ref getRef(String l) throws SQLException { throw uoe(); }
    @Override public Blob getBlob(int i) throws SQLException { throw uoe(); }
    @Override public Blob getBlob(String l) throws SQLException { throw uoe(); }
    @Override public Clob getClob(int i) throws SQLException { throw uoe(); }
    @Override public Clob getClob(String l) throws SQLException { throw uoe(); }
    @Override public Array getArray(int i) throws SQLException { throw uoe(); }
    @Override public Array getArray(String l) throws SQLException { throw uoe(); }
    @Override public NClob getNClob(int i) throws SQLException { throw uoe(); }
    @Override public NClob getNClob(String l) throws SQLException { throw uoe(); }
    @Override public SQLXML getSQLXML(int i) throws SQLException { throw uoe(); }
    @Override public SQLXML getSQLXML(String l) throws SQLException { throw uoe(); }
    @Override public RowId getRowId(int i) throws SQLException { throw uoe(); }
    @Override public RowId getRowId(String l) throws SQLException { throw uoe(); }
    @Override public <T> T unwrap(Class<T> iface) throws SQLException { throw uoe(); }
    @Override public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
    @Override public void updateRef(int i, Ref x) throws SQLException { throw uoe(); }
    @Override public void updateRef(String l, Ref x) throws SQLException { throw uoe(); }
    @Override public void updateBlob(int i, Blob x) throws SQLException { throw uoe(); }
    @Override public void updateBlob(String l, Blob x) throws SQLException { throw uoe(); }
    @Override public void updateBlob(int i, InputStream x) throws SQLException { throw uoe(); }
    @Override public void updateBlob(String l, InputStream x) throws SQLException { throw uoe(); }
    @Override public void updateBlob(int i, InputStream x, long len) throws SQLException { throw uoe(); }
    @Override public void updateBlob(String l, InputStream x, long len) throws SQLException { throw uoe(); }
    @Override public void updateClob(int i, Clob x) throws SQLException { throw uoe(); }
    @Override public void updateClob(String l, Clob x) throws SQLException { throw uoe(); }
    @Override public void updateClob(int i, Reader x) throws SQLException { throw uoe(); }
    @Override public void updateClob(String l, Reader x) throws SQLException { throw uoe(); }
    @Override public void updateClob(int i, Reader x, long len) throws SQLException { throw uoe(); }
    @Override public void updateClob(String l, Reader x, long len) throws SQLException { throw uoe(); }
    @Override public void updateNClob(int i, NClob x) throws SQLException { throw uoe(); }
    @Override public void updateNClob(String l, NClob x) throws SQLException { throw uoe(); }
    @Override public void updateNClob(int i, Reader x) throws SQLException { throw uoe(); }
    @Override public void updateNClob(String l, Reader x) throws SQLException { throw uoe(); }
    @Override public void updateNClob(int i, Reader x, long len) throws SQLException { throw uoe(); }
    @Override public void updateNClob(String l, Reader x, long len) throws SQLException { throw uoe(); }
    @Override public void updateNString(int i, String x) throws SQLException { throw uoe(); }
    @Override public void updateNString(String l, String x) throws SQLException { throw uoe(); }
    @Override public void updateNCharacterStream(int i, Reader x) throws SQLException { throw uoe(); }
    @Override public void updateNCharacterStream(String l, Reader x) throws SQLException { throw uoe(); }
    @Override public void updateNCharacterStream(int i, Reader x, long len) throws SQLException { throw uoe(); }
    @Override public void updateNCharacterStream(String l, Reader x, long len) throws SQLException { throw uoe(); }
    @Override public void updateBinaryStream(int i, InputStream x) throws SQLException { throw uoe(); }
    @Override public void updateBinaryStream(String l, InputStream x) throws SQLException { throw uoe(); }
    @Override public void updateBinaryStream(int i, InputStream x, long len) throws SQLException { throw uoe(); }
    @Override public void updateBinaryStream(String l, InputStream x, long len) throws SQLException { throw uoe(); }
    @Override public void updateCharacterStream(int i, Reader x) throws SQLException { throw uoe(); }
    @Override public void updateCharacterStream(String l, Reader x) throws SQLException { throw uoe(); }
    @Override public void updateCharacterStream(int i, Reader x, long len) throws SQLException { throw uoe(); }
    @Override public void updateCharacterStream(String l, Reader x, long len) throws SQLException { throw uoe(); }
    @Override public void updateRowId(int i, RowId x) throws SQLException { throw uoe(); }
    @Override public void updateRowId(String l, RowId x) throws SQLException { throw uoe(); }
    @Override public void updateSQLXML(int i, SQLXML x) throws SQLException { throw uoe(); }
    @Override public void updateSQLXML(String l, SQLXML x) throws SQLException { throw uoe(); }

    private SQLException uoe() { return new SQLFeatureNotSupportedException(); }
}
