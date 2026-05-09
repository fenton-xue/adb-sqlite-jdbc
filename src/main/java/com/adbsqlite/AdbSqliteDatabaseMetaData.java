package com.adbsqlite;

import java.sql.*;

class AdbSqliteDatabaseMetaData implements DatabaseMetaData {

    private final AdbSqliteConnection conn;

    AdbSqliteDatabaseMetaData(AdbSqliteConnection conn) {
        this.conn = conn;
    }

    @Override public String getDatabaseProductName() { return "SQLite"; }
    @Override public String getDatabaseProductVersion() { return "3.x"; }
    @Override public String getDriverName() { return "ADB SQLite JDBC Driver"; }
    @Override public String getDriverVersion() { return "1.0.0"; }
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
    @Override public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException { return emptyRs(); }
    @Override public ResultSet getSchemas() throws SQLException { return emptyRs(); }
    @Override public ResultSet getCatalogs() throws SQLException { return emptyRs(); }
    @Override public ResultSet getTableTypes() throws SQLException { return emptyRs(); }
    @Override public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException { return emptyRs(); }
    @Override public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException { return emptyRs(); }
    @Override public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException { return emptyRs(); }
    @Override public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException { return emptyRs(); }
    @Override public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException { return emptyRs(); }
    @Override public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException { return emptyRs(); }
    @Override public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException { return emptyRs(); }
    @Override public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException { return emptyRs(); }
    @Override public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException { return emptyRs(); }
    @Override public ResultSet getTypeInfo() throws SQLException { return emptyRs(); }
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

    private ResultSet emptyRs() throws SQLException {
        return new AdbSqliteResultSet(java.util.Collections.emptyList(), java.util.Collections.emptyList(), "__ADB_NULL__");
    }
}
