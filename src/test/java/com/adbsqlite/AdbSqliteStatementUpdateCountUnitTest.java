package com.adbsqlite;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdbSqliteStatementUpdateCountUnitTest {

    @Test
    void executeUpdateReturnsSqliteChangesCount() throws Exception {
        StubStatement stmt = new StubStatement(Arrays.asList("__adb_update_count__", "1"));

        int count = stmt.executeUpdate("UPDATE sample_report_main SET sample_finish_date = NULL WHERE id = 'id-1'");

        assertEquals(1, count);
        assertEquals(1, stmt.getUpdateCount());
        assertTrue(stmt.executedSql.contains("SELECT changes() AS __adb_update_count__"));
    }

    @Test
    void executeUpdateParsesChangesAfterOtherCsvOutput() throws Exception {
        StubStatement stmt = new StubStatement(Arrays.asList(
                "id",
                "id-1",
                "__adb_update_count__",
                "1"));

        int count = stmt.executeUpdate("UPDATE sample_report_main SET sample_finish_date = NULL WHERE id = 'id-1'");

        assertEquals(1, count);
    }

    @Test
    void executeLargeUpdateUsesSqliteChangesCount() throws Exception {
        StubStatement stmt = new StubStatement(Arrays.asList("__adb_update_count__", "1"));

        long count = stmt.executeLargeUpdate("UPDATE sample_report_main SET sample_finish_date = NULL WHERE id = 'id-1'");

        assertEquals(1L, count);
        assertEquals(1L, stmt.getLargeUpdateCount());
    }

    @Test
    void preparedExecuteLargeUpdateUsesSqliteChangesCount() throws Exception {
        StubPreparedStatement stmt = new StubPreparedStatement(
                "UPDATE sample_report_main SET sample_finish_date = ? WHERE id = ?",
                Arrays.asList("__adb_update_count__", "1"));
        stmt.setNull(1, java.sql.Types.VARCHAR);
        stmt.setString(2, "id-1");

        long count = stmt.executeLargeUpdate();

        assertEquals(1L, count);
        assertTrue(stmt.executedSql.contains("sample_finish_date = NULL"));
        assertTrue(stmt.executedSql.contains("id = 'id-1'"));
    }

    @Test
    void executeBatchReturnsSqliteChangesCount() throws Exception {
        StubStatement stmt = new StubStatement(Arrays.asList("__adb_update_count__", "1"));
        stmt.addBatch("UPDATE sample_report_main SET sample_finish_date = NULL WHERE id = 'id-1'");

        int[] counts = stmt.executeBatch();

        assertArrayEquals(new int[]{1}, counts);
        assertEquals(1, stmt.getUpdateCount());
    }

    @Test
    void executeLargeBatchReturnsSqliteChangesCount() throws Exception {
        StubStatement stmt = new StubStatement(Arrays.asList("__adb_update_count__", "1"));
        stmt.addBatch("UPDATE sample_report_main SET sample_finish_date = NULL WHERE id = 'id-1'");

        long[] counts = stmt.executeLargeBatch();

        assertArrayEquals(new long[]{1L}, counts);
        assertEquals(1L, stmt.getLargeUpdateCount());
    }

    @Test
    void preparedExecuteBatchReturnsSqliteChangesCount() throws Exception {
        StubPreparedStatement stmt = new StubPreparedStatement(
                "UPDATE sample_report_main SET sample_finish_date = ? WHERE id = ?",
                Arrays.asList("__adb_update_count__", "1"));
        stmt.setNull(1, java.sql.Types.VARCHAR);
        stmt.setString(2, "id-1");
        stmt.addBatch();

        int[] counts = stmt.executeBatch();

        assertArrayEquals(new int[]{1}, counts);
        assertTrue(stmt.executedSql.contains("sample_finish_date = NULL"));
        assertTrue(stmt.executedSql.contains("id = 'id-1'"));
    }

    @Test
    void preparedStatementDoesNotReplaceQuestionMarksInsideParameterValues() throws Exception {
        String frontUrl = "https://example.test/front.jpg?x=1&sig=front";
        String smallUrl = "https://example.test/front_small.jpg?x=2&sig=small";
        StubPreparedStatement stmt = new StubPreparedStatement(
                "UPDATE sample_report_main SET sample_finish_date = ? WHERE id = ? "
                        + "AND pic_front_uri = ? AND pic_front_uri_small = ?",
                Arrays.asList("__adb_update_count__", "1"));
        stmt.setString(1, "2026-06-30T00:00:00.000Z");
        stmt.setString(2, "id-1");
        stmt.setString(3, frontUrl);
        stmt.setString(4, smallUrl);

        stmt.executeUpdate();

        assertTrue(stmt.executedSql.contains("pic_front_uri = '" + frontUrl + "'"));
        assertTrue(stmt.executedSql.contains("pic_front_uri_small = '" + smallUrl + "'"));
    }

    @Test
    void executeUpdateThrowsWhenSqliteErrorAppearsWithChangesOutput() throws Exception {
        StubStatement stmt = new StubStatement(Arrays.asList(
                "__adb_update_count__",
                "0",
                "Error: near line 1: UNIQUE constraint failed: sample_report_main.user_id, sample_report_main.spod_id, sample_report_main.is_deleted"));

        SQLException error = assertThrows(SQLException.class,
                () -> stmt.executeUpdate("INSERT INTO sample_report_main (id) VALUES ('id-1')"));

        assertTrue(error.getMessage().contains("UNIQUE constraint failed"));
    }

    @Test
    void execSendsLongSqlThroughStdinInsteadOfShellCommand() throws Exception {
        CapturingStatement stmt = new CapturingStatement(connection("device-1", "/data/data/pkg/test.db", true));
        char[] chars = new char[5000];
        Arrays.fill(chars, 'x');
        String sql = "SELECT '" + new String(chars) + "' AS big_value";

        stmt.exec(sql);

        assertEquals(sql, stmt.stdin);
        assertTrue(stmt.shellCmd.length() < 256);
        assertTrue(stmt.shellCmd.contains("sqlite3"));
        assertTrue(!stmt.shellCmd.contains("base64"));
    }

    @Test
    void execUsesPlainSqliteWhenRootDisabled() throws Exception {
        CapturingStatement stmt = new CapturingStatement(connection("device-1", "/data/data/pkg/test.db", false));

        stmt.exec("SELECT 1");

        assertEquals("device-1", stmt.device);
        assertEquals("SELECT 1", stmt.stdin);
        assertTrue(stmt.shellCmd.startsWith("sqlite3 "));
        assertFalse(stmt.shellCmd.contains("su -c"));
    }

    @Test
    void execUsesSuByDefaultWhenRootEnabled() throws Exception {
        CapturingStatement stmt = new CapturingStatement(connection("device-1", "/data/data/pkg/test.db", true));

        stmt.exec("SELECT 1");

        assertTrue(stmt.shellCmd.startsWith("su -c "));
        assertTrue(stmt.shellCmd.contains("sqlite3"));
    }

    @Test
    void parseUrlDefaultsToRootMode() throws Exception {
        AdbSqliteDriver.UrlInfo info = AdbSqliteDriver.parseUrl(
                "jdbc:adb:sqlite://127.0.0.1:16384?package=com.giga.qc&db=databases/qcapp_localSQLite.db");

        assertEquals(Boolean.TRUE, readField(info, "root"));
    }

    @Test
    void parseUrlAcceptsRootFalse() throws Exception {
        AdbSqliteDriver.UrlInfo info = AdbSqliteDriver.parseUrl(
                "jdbc:adb:sqlite://127.0.0.1:16384?package=com.giga.qc&db=databases/qcapp_localSQLite.db&root=false");

        assertEquals(Boolean.FALSE, readField(info, "root"));
    }

    private static class StubStatement extends AdbSqliteStatement {
        private final List<String> lines;
        private String executedSql;

        StubStatement(List<String> lines) throws Exception {
            super(uninitializedConnection());
            this.lines = lines;
        }

        @Override
        List<String> exec(String sql) {
            executedSql = sql;
            return lines;
        }
    }

    private static class StubPreparedStatement extends AdbSqlitePreparedStatement {
        private final List<String> lines;
        private String executedSql;

        StubPreparedStatement(String sql, List<String> lines) throws Exception {
            super(uninitializedConnection(), sql);
            this.lines = lines;
        }

        @Override
        List<String> exec(String sql) {
            executedSql = sql;
            return lines;
        }
    }

    private static class CapturingStatement extends AdbSqliteStatement {
        private String device;
        private String shellCmd;
        private String stdin;

        CapturingStatement(AdbSqliteConnection conn) {
            super(conn);
        }

        @Override
        List<String> runAdb(String device, String shellCmd, String stdin) {
            this.device = device;
            this.shellCmd = shellCmd;
            this.stdin = stdin;
            return Arrays.asList("ok");
        }
    }

    private static AdbSqliteConnection connection(String device, String dbPath, boolean root) throws Exception {
        AdbSqliteConnection conn = uninitializedConnection();
        setField(conn, "device", device);
        setField(conn, "dbPath", dbPath);
        setField(conn, "root", root);
        return conn;
    }

    private static Object readField(Object target, String name) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static AdbSqliteConnection uninitializedConnection() throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Object unsafe = unsafeField.get(null);
        Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
        return (AdbSqliteConnection) allocateInstance.invoke(unsafe, AdbSqliteConnection.class);
    }
}
