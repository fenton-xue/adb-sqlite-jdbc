package com.adbsqlite;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

class AdbSqliteResultSetMetaData implements ResultSetMetaData {

    private final List<String> columns;

    AdbSqliteResultSetMetaData(List<String> columns) {
        this.columns = columns;
    }

    @Override
    public int getColumnCount() { return columns.size(); }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return columns.get(check(column));
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return columns.get(check(column));
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        check(column);
        return "TEXT";
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        check(column);
        return Types.VARCHAR;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        check(column);
        return "";
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        check(column);
        return "";
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        check(column);
        return "";
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        check(column);
        return 256;
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        check(column);
        return 0;
    }

    @Override
    public int getScale(int column) throws SQLException {
        check(column);
        return 0;
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        check(column);
        return false;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        check(column);
        return true;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        check(column);
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        check(column);
        return true;
    }

    @Override
    public int isNullable(int column) throws SQLException {
        check(column);
        return columnNullable;
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        check(column);
        return false;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        check(column);
        return true;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        check(column);
        return false;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        check(column);
        return true;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        check(column);
        return "java.lang.String";
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException { throw new SQLException("not supported"); }
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }

    private int check(int column) throws SQLException {
        if (column < 1 || column > columns.size())
            throw new SQLException("Column index out of range: " + column);
        return column - 1;
    }
}
