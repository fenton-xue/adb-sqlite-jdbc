package com.adbsqlite;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdbSqliteStatementCsvUnitTest {

    @Test
    void parseCsvRecordsKeepsQuotedNewlinesInSameRecord() throws Exception {
        String csv = "id,sql\n"
                + "1,\"CREATE TABLE demo (\n"
                + "  id INTEGER,\n"
                + "  name TEXT\n"
                + ")\"\n"
                + "2,\"CREATE TABLE other (id INTEGER)\"\n";

        List<List<String>> records = AdbSqliteStatement.parseCsvRecords(csv);

        assertEquals(3, records.size());
        assertEquals(Arrays.asList("id", "sql"), records.get(0));
        assertEquals("1", records.get(1).get(0));
        assertEquals("CREATE TABLE demo (\n  id INTEGER,\n  name TEXT\n)", records.get(1).get(1));
        assertEquals("2", records.get(2).get(0));
        assertEquals("CREATE TABLE other (id INTEGER)", records.get(2).get(1));
    }

    @Test
    void parseCsvRecordsHandlesCommasQuotesAndEmptyFields() throws Exception {
        String csv = "id,text,empty\n"
                + "1,\"hello, world\",\n"
                + "2,\"a \"\"quoted\"\" value\",tail\n";

        List<List<String>> records = AdbSqliteStatement.parseCsvRecords(csv);

        assertEquals(3, records.size());
        assertEquals(Arrays.asList("id", "text", "empty"), records.get(0));
        assertEquals(Arrays.asList("1", "hello, world", ""), records.get(1));
        assertEquals(Arrays.asList("2", "a \"quoted\" value", "tail"), records.get(2));
    }
}
