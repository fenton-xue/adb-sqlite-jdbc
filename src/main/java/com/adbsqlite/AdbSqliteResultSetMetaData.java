package com.adbsqlite;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

class AdbSqliteResultSetMetaData implements ResultSetMetaData {

    private final List<String> columns;
    private final List<Integer> columnTypes;

    private static final Map<Integer, String> TYPE_NAMES = new HashMap<>();
    static {
        TYPE_NAMES.put(Types.INTEGER, "INTEGER");
        TYPE_NAMES.put(Types.DOUBLE, "REAL");
        TYPE_NAMES.put(Types.VARCHAR, "TEXT");
        TYPE_NAMES.put(Types.BLOB, "BLOB");
    }

    AdbSqliteResultSetMetaData(List<String> columns) {
        this(columns, null);
    }

    AdbSqliteResultSetMetaData(List<String> columns, List<Integer> columnTypes) {
        this.columns = columns;
        this.columnTypes = columnTypes;
    }

    private int getColType(int column) throws SQLException {
        if (columnTypes != null && column - 1 < columnTypes.size()) {
            return columnTypes.get(column - 1);
        }
        return Types.VARCHAR;
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
        String name = TYPE_NAMES.get(getColType(column));
        return name != null ? name : "TEXT";
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        check(column);
        return getColType(column);
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
        int type = getColType(check(column));
        if (type == Types.INTEGER) return 10;
        if (type == Types.DOUBLE) return 15;
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
        int type = getColType(check(column));
        return type == Types.INTEGER || type == Types.DOUBLE;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        check(column);
        return true;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        int type = getColType(check(column));
        switch (type) {
            case Types.INTEGER: return "java.lang.Long";
            case Types.DOUBLE: return "java.lang.Double";
            case Types.BLOB: return "[B";
            default: return "java.lang.String";
        }
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
