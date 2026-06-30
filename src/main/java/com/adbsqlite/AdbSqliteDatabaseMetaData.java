package com.adbsqlite;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class AdbSqliteDatabaseMetaData implements DatabaseMetaData {

    private final AdbSqliteConnection conn;

    AdbSqliteDatabaseMetaData(AdbSqliteConnection conn) {
        this.conn = conn;
    }

    @Override public String getDatabaseProductName() { return "SQLite"; }
    @Override public String getDatabaseProductVersion() { return "3.x"; }
    @Override public String getDriverName() { return "ADB SQLite JDBC Driver"; }
    @Override public String getDriverVersion() { return "1.0.10"; }
    @Override public int getDriverMajorVersion() { return 1; }
    @Override public int getDriverMinorVersion() { return 0; }
    @Override public String getURL() { return conn.toString(); }
    @Override public String getUserName() { return ""; }
    @Override public boolean isReadOnly() { return false; }
    @Override public boolean allProceduresAreCallable() { return false; }
    @Override public boolean allTablesAreSelectable() { return true; }
    @Override public boolean usesLocalFiles() { return false; }
    @Override public boolean usesLocalFilePerTable() { return false; }
    @Override public boolean supportsMixedCaseIdentifiers() { return false; }
    @Override public boolean storesUpperCaseIdentifiers() { return false; }
    @Override public boolean storesLowerCaseIdentifiers() { return false; }
    @Override public boolean storesMixedCaseIdentifiers() { return true; }
    @Override public boolean supportsMixedCaseQuotedIdentifiers() { return false; }
    @Override public boolean storesUpperCaseQuotedIdentifiers() { return false; }
    @Override public boolean storesLowerCaseQuotedIdentifiers() { return false; }
    @Override public boolean storesMixedCaseQuotedIdentifiers() { return true; }
    @Override public String getIdentifierQuoteString() { return "\""; }
    @Override public String getSQLKeywords() { return ""; }
    @Override public String getNumericFunctions() { return ""; }
    @Override public String getStringFunctions() { return ""; }
    @Override public String getSystemFunctions() { return ""; }
    @Override public String getTimeDateFunctions() { return ""; }
    @Override public String getSearchStringEscape() { return null; }
    @Override public String getExtraNameCharacters() { return ""; }
    @Override public boolean supportsAlterTableWithAddColumn() { return true; }
    @Override public boolean supportsAlterTableWithDropColumn() { return true; }
    @Override public boolean supportsColumnAliasing() { return true; }
    @Override public boolean nullPlusNonNullIsNull() { return true; }
    @Override public boolean supportsConvert() { return false; }
    @Override public boolean supportsConvert(int fromType, int toType) { return false; }
    @Override public boolean supportsTableCorrelationNames() { return false; }
    @Override public boolean supportsDifferentTableCorrelationNames() { return false; }
    @Override public boolean supportsExpressionsInOrderBy() { return true; }
    @Override public boolean supportsOrderByUnrelated() { return true; }
    @Override public boolean supportsGroupBy() { return true; }
    @Override public boolean supportsGroupByUnrelated() { return true; }
    @Override public boolean supportsGroupByBeyondSelect() { return true; }
    @Override public boolean supportsLikeEscapeClause() { return true; }
    @Override public boolean supportsMultipleResultSets() { return false; }
    @Override public boolean supportsMultipleTransactions() { return true; }
    @Override public boolean supportsNonNullableColumns() { return true; }
    @Override public boolean supportsMinimumSQLGrammar() { return true; }
    @Override public boolean supportsCoreSQLGrammar() { return true; }
    @Override public boolean supportsExtendedSQLGrammar() { return false; }
    @Override public boolean supportsANSI92EntryLevelSQL() { return true; }
    @Override public boolean supportsANSI92IntermediateSQL() { return false; }
    @Override public boolean supportsANSI92FullSQL() { return false; }
    @Override public boolean supportsIntegrityEnhancementFacility() { return false; }
    @Override public boolean supportsOuterJoins() { return true; }
    @Override public boolean supportsFullOuterJoins() { return true; }
    @Override public boolean supportsLimitedOuterJoins() { return true; }
    @Override public int getMaxColumnNameLength() { return 0; }
    @Override public int getMaxColumnsInGroupBy() { return 0; }
    @Override public int getMaxColumnsInSelect() { return 0; }
    @Override public int getMaxConnections() { return 0; }
    @Override public int getMaxTableNameLength() { return 0; }
    @Override public int getMaxTablesInSelect() { return 0; }
    @Override public int getMaxStatementLength() { return 0; }
    @Override public int getMaxColumnsInTable() { return 0; }
    @Override public int getMaxRowSize() { return 0; }
    @Override public boolean doesMaxRowSizeIncludeBlobs() { return false; }
    @Override public int getMaxCursorNameLength() { return 0; }
    @Override public int getMaxIndexLength() { return 0; }
    @Override public int getMaxSchemaNameLength() { return 0; }
    @Override public int getMaxProcedureNameLength() { return 0; }
    @Override public int getMaxCatalogNameLength() { return 0; }
    @Override public boolean isCatalogAtStart() { return true; }
    @Override public String getCatalogSeparator() { return ""; }
    @Override public boolean supportsSchemasInDataManipulation() { return false; }
    @Override public boolean supportsSchemasInProcedureCalls() { return false; }
    @Override public boolean supportsSchemasInTableDefinitions() { return false; }
    @Override public boolean supportsSchemasInIndexDefinitions() { return false; }
    @Override public boolean supportsSchemasInPrivilegeDefinitions() { return false; }
    @Override public boolean supportsCatalogsInDataManipulation() { return false; }
    @Override public boolean supportsCatalogsInProcedureCalls() { return false; }
    @Override public boolean supportsCatalogsInTableDefinitions() { return false; }
    @Override public boolean supportsCatalogsInIndexDefinitions() { return false; }
    @Override public boolean supportsCatalogsInPrivilegeDefinitions() { return false; }
    @Override public boolean supportsPositionedDelete() { return false; }
    @Override public boolean supportsPositionedUpdate() { return false; }
    @Override public boolean supportsSelectForUpdate() { return false; }
    @Override public boolean supportsStoredProcedures() { return false; }
    @Override public boolean supportsSubqueriesInComparisons() { return true; }
    @Override public boolean supportsSubqueriesInExists() { return true; }
    @Override public boolean supportsSubqueriesInIns() { return true; }
    @Override public boolean supportsSubqueriesInQuantifieds() { return false; }
    @Override public boolean supportsCorrelatedSubqueries() { return true; }
    @Override public boolean supportsUnion() { return true; }
    @Override public boolean supportsUnionAll() { return true; }
    @Override public boolean supportsOpenCursorsAcrossCommit() { return false; }
    @Override public boolean supportsOpenCursorsAcrossRollback() { return false; }
    @Override public boolean supportsOpenStatementsAcrossCommit() { return false; }
    @Override public boolean supportsOpenStatementsAcrossRollback() { return false; }
    @Override public int getMaxBinaryLiteralLength() { return 0; }
    @Override public int getMaxCharLiteralLength() { return 0; }
    @Override public boolean supportsDataDefinitionAndDataManipulationTransactions() { return true; }
    @Override public boolean supportsDataManipulationTransactionsOnly() { return false; }
    @Override public boolean dataDefinitionCausesTransactionCommit() { return false; }
    @Override public boolean dataDefinitionIgnoredInTransactions() { return false; }
    @Override public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException { return emptyRs(); }
    @Override public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException { return emptyRs(); }
    @Override
    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        try {
            List<String> lines = execMetaQuery("SELECT name, type FROM sqlite_master WHERE type IN ('table','view') ORDER BY name");
            List<String> cols = Arrays.asList("TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "TABLE_TYPE",
                    "REMARKS", "TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "SELF_REFERENCING_COL_NAME", "REF_GENERATION");
            List<String[]> rows = new ArrayList<>();

            if (!lines.isEmpty()) {
                List<String> headerFields = AdbSqliteStatement.parseCsvLine(lines.get(0));
                int nameIdx = headerFields.indexOf("name");
                int typeIdx = headerFields.indexOf("type");
                for (int i = 1; i < lines.size(); i++) {
                    List<String> fields = AdbSqliteStatement.parseCsvLine(lines.get(i));
                    if (fields.size() < 2) continue;
                    String tableName = fields.get(nameIdx);
                    String tableType = fields.get(typeIdx).toUpperCase();
                    if (!tableNameMatches(tableName, tableNamePattern)) continue;
                    if (types != null) {
                        boolean accepted = false;
                        for (String t : types) {
                            if (t != null && t.toUpperCase().equals(tableType)) { accepted = true; break; }
                        }
                        if (!accepted) continue;
                    }
                    rows.add(new String[]{null, null, tableName, tableType, "",
                            null, null, null, null, null});
                }
            }
            return new AdbSqliteResultSet(cols, rows, AdbSqliteStatement.NULL_MARKER);
        } catch (SQLException e) {
            return emptyRs();
        }
    }
    @Override public ResultSet getSchemas() throws SQLException { return emptyRs(); }
    @Override public ResultSet getCatalogs() throws SQLException { return emptyRs(); }
    @Override public ResultSet getTableTypes() throws SQLException { return emptyRs(); }
    @Override
    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        try {
            List<String> cols = Arrays.asList(
                    "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "DATA_TYPE",
                    "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "NUM_PREC_RADIX",
                    "NULLABLE", "REMARKS", "COLUMN_DEF", "SQL_DATA_TYPE", "SQL_DATETIME_SUB",
                    "CHAR_OCTET_LENGTH", "ORDINAL_POSITION", "IS_NULLABLE", "SCOPE_CATALOG",
                    "SCOPE_SCHEMA", "SCOPE_TABLE", "SOURCE_DATA_TYPE", "IS_AUTOINCREMENT", "IS_GENERATEDCOLUMN");
            List<String[]> rows = new ArrayList<>();

            List<String> tableLines = execMetaQuery("SELECT name FROM sqlite_master WHERE type IN ('table','view') ORDER BY name");
            if (tableLines.isEmpty()) return new AdbSqliteResultSet(cols, rows, AdbSqliteStatement.NULL_MARKER);

            List<String> headerFields = AdbSqliteStatement.parseCsvLine(tableLines.get(0));
            int tableNameIdx = headerFields.indexOf("name");
            for (int i = 1; i < tableLines.size(); i++) {
                List<String> fields = AdbSqliteStatement.parseCsvLine(tableLines.get(i));
                String tableName = fields.get(tableNameIdx);
                if (!tableNameMatches(tableName, tableNamePattern)) continue;
                try {
                    String sqlSafeName = tableName.replace("'", "''");
                    List<String> pragmaLines = execMetaQuery("PRAGMA table_info('" + sqlSafeName + "')");
                    if (pragmaLines.isEmpty()) continue;
                    List<String> ph = AdbSqliteStatement.parseCsvLine(pragmaLines.get(0));
                    int cidIdx = ph.indexOf("cid");
                    int colNameIdx = ph.indexOf("name");
                    int typeIdx = ph.indexOf("type");
                    int notnullIdx = ph.indexOf("notnull");
                    int dfltIdx = ph.indexOf("dflt_value");
                    int pkIdx = ph.indexOf("pk");
                    for (int j = 1; j < pragmaLines.size(); j++) {
                        List<String> pf = AdbSqliteStatement.parseCsvLine(pragmaLines.get(j));
                        String colName = pf.get(colNameIdx);
                        if (!tableNameMatches(colName, columnNamePattern)) continue;
                        String sqliteType = pf.get(typeIdx);
                        int jdbcType = sqliteTypeToJdbcType(sqliteType);
                        boolean notNull = "1".equals(pf.get(notnullIdx));
                        int nullable = notNull ? columnNoNulls : columnNullable;
                        String isNullable = notNull ? "NO" : "YES";
                        String dfltVal = pf.size() > dfltIdx ? pf.get(dfltIdx) : null;
                        if (dfltVal == null || dfltVal.isEmpty() || dfltVal.equals(AdbSqliteStatement.NULL_MARKER))
                            dfltVal = null;
                        int pk = pf.size() > pkIdx ? Integer.parseInt(pf.get(pkIdx)) : 0;
                        String autoIncr = pk > 0 ? "YES" : "NO";

                        rows.add(new String[]{
                                null, null, tableName, colName, String.valueOf(jdbcType),
                                sqliteType, "0", null, null, "10",
                                String.valueOf(nullable), "", dfltVal, null, null,
                                null, String.valueOf(Integer.parseInt(pf.get(cidIdx)) + 1), isNullable, null,
                                null, null, null, autoIncr, "NO"
                        });
                    }
                } catch (Exception e) {
                    // Skip tables that cause PRAGMA errors
                    continue;
                }
            }
            return new AdbSqliteResultSet(cols, rows, AdbSqliteStatement.NULL_MARKER);
        } catch (SQLException e) {
            return emptyRs();
        }
    }
    @Override public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException { return emptyRs(); }
    @Override public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException { return emptyRs(); }
    @Override public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException { return emptyRs(); }
    @Override public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException { return emptyRs(); }
    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        try {
            List<String> cols = Arrays.asList("TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME",
                    "COLUMN_NAME", "KEY_SEQ", "PK_NAME");
            List<String[]> rows = new ArrayList<>();
            if (table == null) return new AdbSqliteResultSet(cols, rows, AdbSqliteStatement.NULL_MARKER);

            String sqlSafeName = table.replace("'", "''");
            List<String> pragmaLines = execMetaQuery("PRAGMA table_info('" + sqlSafeName + "')");
            if (pragmaLines.isEmpty()) return new AdbSqliteResultSet(cols, rows, AdbSqliteStatement.NULL_MARKER);

            List<String> ph = AdbSqliteStatement.parseCsvLine(pragmaLines.get(0));
            int colNameIdx = ph.indexOf("name");
            int pkIdx = ph.indexOf("pk");
            for (int i = 1; i < pragmaLines.size(); i++) {
                List<String> pf = AdbSqliteStatement.parseCsvLine(pragmaLines.get(i));
                int pk = Integer.parseInt(pf.get(pkIdx));
                if (pk == 0) continue;
                rows.add(new String[]{null, null, table, pf.get(colNameIdx),
                        String.valueOf(pk), "PRIMARY"});
            }
            return new AdbSqliteResultSet(cols, rows, AdbSqliteStatement.NULL_MARKER);
        } catch (SQLException e) {
            return emptyRs();
        }
    }
    @Override public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException { return emptyRs(); }
    @Override public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException { return emptyRs(); }
    @Override public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException { return emptyRs(); }
    @Override
    public ResultSet getTypeInfo() throws SQLException {
        List<String> cols = Arrays.asList("TYPE_NAME", "DATA_TYPE", "PRECISION", "LITERAL_PREFIX",
                "LITERAL_SUFFIX", "CREATE_PARAMS", "NULLABLE", "CASE_SENSITIVE",
                "SEARCHABLE", "UNSIGNED_ATTRIBUTE", "FIXED_PREC_SCALE", "AUTO_INCREMENT",
                "LOCAL_TYPE_NAME", "MINIMUM_SCALE", "MAXIMUM_SCALE", "SQL_DATA_TYPE",
                "SQL_DATETIME_SUB", "NUM_PREC_RADIX");
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"INTEGER", String.valueOf(Types.INTEGER), "10", null, null, null,
                String.valueOf(typeNullable), "false", "3", "false", "false", "false",
                "INTEGER", "0", "0", null, null, "10"});
        rows.add(new String[]{"TEXT", String.valueOf(Types.VARCHAR), "0", "'", "'", "length",
                String.valueOf(typeNullable), "true", "3", null, "false", "false",
                "TEXT", "0", "0", null, null, null});
        rows.add(new String[]{"REAL", String.valueOf(Types.DOUBLE), "10", null, null, null,
                String.valueOf(typeNullable), "false", "3", "false", "false", "false",
                "REAL", "0", "0", null, null, "10"});
        rows.add(new String[]{"BLOB", String.valueOf(Types.BLOB), "0", null, null, null,
                String.valueOf(typeNullable), "false", "3", null, "false", "false",
                "BLOB", "0", "0", null, null, null});
        return new AdbSqliteResultSet(cols, rows, AdbSqliteStatement.NULL_MARKER);
    }
    @Override public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException { return emptyRs(); }
    @Override public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException { return emptyRs(); }
    @Override public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException { return emptyRs(); }
    @Override public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException { return emptyRs(); }
    @Override public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException { return emptyRs(); }
    @Override public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException { return emptyRs(); }
    @Override public ResultSet getClientInfoProperties() throws SQLException { return emptyRs(); }
    @Override public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException { return emptyRs(); }
    @Override public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException { return emptyRs(); }
    @Override public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException { return emptyRs(); }
    @Override public boolean generatedKeyAlwaysReturned() { return false; }
    @Override public long getMaxLogicalLobSize() { return 0; }
    @Override public boolean supportsRefCursors() { return false; }
    @Override public Connection getConnection() { return conn; }
    @Override public boolean supportsStoredFunctionsUsingCallSyntax() { return false; }
    @Override public boolean autoCommitFailureClosesAllResultSets() { return false; }
    @Override public RowIdLifetime getRowIdLifetime() { return RowIdLifetime.ROWID_UNSUPPORTED; }
    @Override public <T> T unwrap(Class<T> iface) throws SQLException { throw new SQLException("not supported"); }
    @Override public boolean isWrapperFor(Class<?> iface) { return false; }
    @Override public boolean supportsStatementPooling() { return false; }
    @Override public boolean locatorsUpdateCopy() { return false; }
    @Override public int getSQLStateType() { return sqlStateSQL99; }
    @Override public int getJDBCMinorVersion() { return 0; }
    @Override public int getJDBCMajorVersion() { return 4; }
    @Override public int getDatabaseMinorVersion() { return 0; }
    @Override public int getDatabaseMajorVersion() { return 3; }
    @Override public int getResultSetHoldability() { return ResultSet.CLOSE_CURSORS_AT_COMMIT; }
    @Override public boolean supportsResultSetHoldability(int h) { return h == ResultSet.CLOSE_CURSORS_AT_COMMIT; }
    @Override public boolean supportsGetGeneratedKeys() { return false; }
    @Override public boolean supportsBatchUpdates() { return true; }
    @Override public boolean supportsSavepoints() { return false; }
    @Override public boolean supportsNamedParameters() { return false; }
    @Override public boolean supportsMultipleOpenResults() { return false; }
    @Override public boolean supportsResultSetType(int t) { return t == ResultSet.TYPE_FORWARD_ONLY; }
    @Override public boolean supportsResultSetConcurrency(int t, int c) { return c == ResultSet.CONCUR_READ_ONLY; }
    @Override public boolean ownUpdatesAreVisible(int t) { return false; }
    @Override public boolean ownDeletesAreVisible(int t) { return false; }
    @Override public boolean ownInsertsAreVisible(int t) { return false; }
    @Override public boolean othersUpdatesAreVisible(int t) { return false; }
    @Override public boolean othersDeletesAreVisible(int t) { return false; }
    @Override public boolean othersInsertsAreVisible(int t) { return false; }
    @Override public boolean updatesAreDetected(int t) { return false; }
    @Override public boolean deletesAreDetected(int t) { return false; }
    @Override public boolean insertsAreDetected(int t) { return false; }
    @Override public boolean supportsTransactionIsolationLevel(int level) { return false; }
    @Override public boolean supportsTransactions() { return false; }
    @Override public int getDefaultTransactionIsolation() { return Connection.TRANSACTION_NONE; }
    @Override public boolean nullsAreSortedAtEnd() { return true; }
    @Override public boolean nullsAreSortedAtStart() { return false; }
    @Override public boolean nullsAreSortedHigh() { return true; }
    @Override public boolean nullsAreSortedLow() { return false; }
    @Override public int getMaxColumnsInIndex() { return 0; }
    @Override public int getMaxColumnsInOrderBy() { return 0; }
    @Override public int getMaxUserNameLength() { return 0; }
    @Override public int getMaxStatements() { return 0; }
    @Override public String getSchemaTerm() { return "schema"; }
    @Override public String getCatalogTerm() { return "catalog"; }
    @Override public String getProcedureTerm() { return "procedure"; }

    // ---- helpers ----

    private List<String> execMetaQuery(String sql) throws SQLException {
        AdbSqliteStatement stmt = new AdbSqliteStatement(conn);
        return stmt.exec(sql);
    }

    static boolean tableNameMatches(String tableName, String pattern) {
        if (pattern == null) return true;
        StringBuilder regex = new StringBuilder();
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '%') regex.append(".*");
            else if (c == '_') regex.append('.');
            else if (Character.isLetterOrDigit(c) || c == ' ') regex.append(c);
            else regex.append("\\").append(c);
        }
        return tableName.matches(regex.toString());
    }

    static int sqliteTypeToJdbcType(String typeName) {
        if (typeName == null || typeName.isEmpty()) return Types.VARCHAR;
        String upper = typeName.toUpperCase().trim();
        if (upper.equals("INTEGER") || upper.equals("INT")) return Types.INTEGER;
        if (upper.equals("REAL") || upper.equals("FLOAT") || upper.equals("DOUBLE") || upper.startsWith("NUMERIC")) return Types.DOUBLE;
        if (upper.equals("BLOB")) return Types.BLOB;
        if (upper.equals("TEXT") || upper.startsWith("CHAR") || upper.startsWith("VARCHAR") || upper.startsWith("CLOB")) return Types.VARCHAR;
        return Types.VARCHAR;
    }

    private ResultSet emptyRs() throws SQLException {
        return new AdbSqliteResultSet(java.util.Collections.emptyList(), java.util.Collections.emptyList(), "__ADB_NULL__");
    }
}
