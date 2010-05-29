/*
 * Copyright Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.comparison.impl.DefaultDataSetComparator;
import org.unitils.dataset.comparison.impl.TableContentRetriever;
import org.unitils.dataset.comparison.impl.TableContents;
import org.unitils.dataset.comparison.model.ColumnDifference;
import org.unitils.dataset.comparison.model.DataSetComparison;
import org.unitils.dataset.comparison.model.RowComparison;
import org.unitils.dataset.comparison.model.TableComparison;
import org.unitils.dataset.core.database.Column;
import org.unitils.dataset.core.database.Row;
import org.unitils.dataset.core.database.Value;
import org.unitils.dataset.core.dataset.DataSetRow;
import org.unitils.dataset.core.dataset.DataSetSettings;
import org.unitils.dataset.core.dataset.DataSetValue;
import org.unitils.dataset.core.impl.DataSetRowProcessor;
import org.unitils.dataset.database.DatabaseMetaData;
import org.unitils.dataset.rowsource.DataSetRowSource;
import org.unitils.mock.Mock;

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.INTEGER;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDataSetComparatorRowsTest extends UnitilsJUnit4 {

    /* Tested object */
    private DefaultDataSetComparator defaultDataSetComparator = new DefaultDataSetComparator();

    protected Mock<DatabaseMetaData> database;
    protected Mock<DataSetRowProcessor> dataSetRowProcessor;
    protected Mock<TableContentRetriever> tableContentRetriever;
    protected Mock<TableContents> tableContents;
    protected Mock<DataSetRowSource> dataSetRowSource;

    protected List<String> emptyVariables = new ArrayList<String>();

    protected Row actualRow1;
    protected Row actualRow2;
    protected Row actualRow3;

    @Before
    public void initialize() throws Exception {
        defaultDataSetComparator.init(dataSetRowProcessor.getMock(), tableContentRetriever.getMock(), database.getMock());

        tableContentRetriever.onceReturns(tableContents).getTableContents(null, null, null);

        actualRow1 = new Row("1", "schema.table");
        actualRow1.addValue(new Value(11, false, new Column("column1", INTEGER, true)));
        actualRow1.addValue(new Value(12, false, new Column("column2", INTEGER, true)));
        actualRow1.addValue(new Value(13, false, new Column("column3", INTEGER, true)));

        actualRow2 = new Row("2", "schema.table");
        actualRow2.addValue(new Value(21, false, new Column("column1", INTEGER, true)));
        actualRow2.addValue(new Value(22, false, new Column("column2", INTEGER, true)));
        actualRow2.addValue(new Value(23, false, new Column("column3", INTEGER, true)));

        actualRow3 = new Row("3", "schema.table");
        actualRow3.addValue(new Value(31, false, new Column("column1", INTEGER, true)));
        actualRow3.addValue(new Value(32, false, new Column("column2", INTEGER, true)));
        actualRow3.addValue(new Value(33, false, new Column("column3", INTEGER, true)));
    }


    @Test
    public void allRowsAreMatches() throws Exception {
        setExpectedRow(createExpectedRow(createColumn("column1", 11), createColumn("column2", 12)));
        setExpectedRow(createExpectedRow(createColumn("column1", 21), createColumn("column2", 22), createColumn("column3", 23)));
        setActualRow(actualRow1);
        setActualRow(actualRow2);
        setActualRow(actualRow3);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertTrue(dataSetComparison.isMatch());
        assertRowMatch(true, "1", dataSetComparison);
        assertRowMatch(true, "2", dataSetComparison);
        assertRowMatch(false, "3", dataSetComparison);
    }

    @Test
    public void firstRowIsAMatch() throws Exception {
        setExpectedRow(createExpectedRow(createColumn("column1", 11)));
        setActualRow(actualRow1);
        setActualRow(actualRow2);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertTrue(dataSetComparison.isMatch());
        assertRowMatch(true, "1", dataSetComparison);
    }

    @Test
    public void lastRowIsAMatch() throws Exception {
        setExpectedRow(createExpectedRow(createColumn("column1", 21)));
        setActualRow(actualRow1);
        setActualRow(actualRow2);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertTrue(dataSetComparison.isMatch());
        assertRowMatch(true, "2", dataSetComparison);
    }

    @Test
    public void missingRow() throws Exception {
        setExpectedRow(createExpectedRow(createColumn("column1", 11)));
        setExpectedRow(createExpectedRow(createColumn("column1", 21)));
        setActualRow(actualRow1);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertNrOfMissingRows(1, dataSetComparison);
    }

    @Test
    public void difference() throws Exception {
        setExpectedRow(createExpectedRow(createColumn("column1", 777), createColumn("column2", 888), createColumn("column3", 999)));
        setActualRow(actualRow1);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertColumnDifference(0, "column1", 777, 11, dataSetComparison);
        assertColumnDifference(1, "column2", 888, 12, dataSetComparison);
        assertColumnDifference(2, "column3", 999, 13, dataSetComparison);
    }


    @Test
    public void allRowsHaveDifferences() throws Exception {
        Row expectedRow1 = createExpectedRow(createColumn("column1", 999), createColumn("column2", 999));
        Row expectedRow2 = createExpectedRow(createColumn("column1", 999), createColumn("column2", 999), createColumn("column3", 999));
        setExpectedRow(expectedRow1);
        setExpectedRow(expectedRow2);
        setActualRow(actualRow1);
        setActualRow(actualRow2);
        setActualRow(actualRow3);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertFalse(dataSetComparison.isMatch());
        assertBestRowComparison(expectedRow1, actualRow1, dataSetComparison);
        assertBestRowComparison(expectedRow2, actualRow1, dataSetComparison);
    }

    @Test
    public void firstRowIsBetterMatch() throws Exception {
        Row expectedRow1 = createExpectedRow(createColumn("column1", 11), createColumn("column2", 999));
        setExpectedRow(expectedRow1);
        setActualRow(actualRow1);
        setActualRow(actualRow2);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertFalse(dataSetComparison.isMatch());
        assertBestRowComparison(expectedRow1, actualRow1, dataSetComparison);
    }

    @Test
    public void lastRowIsBetterMatch() throws Exception {
        Row expectedRow1 = createExpectedRow(createColumn("column1", 999), createColumn("column2", 22));
        setExpectedRow(expectedRow1);
        setActualRow(actualRow1);
        setActualRow(actualRow2);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertFalse(dataSetComparison.isMatch());
        assertBestRowComparison(expectedRow1, actualRow2, dataSetComparison);
    }

    @Test
    public void expectedNoMoreRecordsInDatabase() throws Exception {
        setExpectedRow(createExpectedRow(createColumn("column1", 11)));
        setExpectedRow(createExpectedRow(createColumn("column1", 21)));
        setEmptyExpectedRow();
        setActualRow(actualRow1);
        setActualRow(actualRow2);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertTrue(dataSetComparison.isMatch());
        assertIsExpectedNoMoreRecordsButFoundMore(false, dataSetComparison);
    }

    @Test
    public void expectedNoMoreRecordsInDatabaseButFoundMore() throws Exception {
        setExpectedRow(createExpectedRow(createColumn("column1", 11)));
        setExpectedRow(createExpectedRow(createColumn("column1", 21)));
        setEmptyExpectedRow();
        setActualRow(actualRow1);
        setActualRow(actualRow2);
        setActualRow(actualRow3);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertFalse(dataSetComparison.isMatch());
        assertIsExpectedNoMoreRecordsButFoundMore(true, dataSetComparison);
    }

    @Test
    public void noRowsInDataSet() throws Exception {
        setActualRow(actualRow1);
        setActualRow(actualRow2);

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertTrue(dataSetComparison.isMatch());
    }

    @Test
    public void noRowsInTable() throws Exception {
        setExpectedRow(createExpectedRow(createColumn("column1", 11)));

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertFalse(dataSetComparison.isMatch());
    }

    @Test
    public void noRowsInTableAndOnlyEmptyElementInDataSet() throws Exception {
        setEmptyExpectedRow();

        DataSetComparison dataSetComparison = defaultDataSetComparator.compare(dataSetRowSource.getMock(), emptyVariables);
        assertTrue(dataSetComparison.isMatch());
    }


    private Value createColumn(String name, Object value) {
        return new Value(value, false, new Column(name, INTEGER, true));
    }

    private Row createExpectedRow(Value... values) {
        Row expectedRow = new Row("schema.table");
        for (Value value : values) {
            expectedRow.addValue(value);
        }
        return expectedRow;
    }

    private void setExpectedRow(Row row) throws Exception {
        dataSetRowSource.onceReturns(createDataSetRow()).getNextDataSetRow();
        dataSetRowProcessor.onceReturns(row).process(null, emptyVariables, null);
    }

    private void setEmptyExpectedRow() throws Exception {
        dataSetRowSource.onceReturns(createEmptyDataSetRow()).getNextDataSetRow();
        dataSetRowProcessor.onceReturns(createExpectedRow()).process(null, emptyVariables, null);
    }

    private void setActualRow(Row row) throws Exception {
        tableContents.onceReturns(row).getRow();
    }


    private void assertNrOfMissingRows(int expectedNrOfMissingRows, DataSetComparison dataSetComparison) {
        TableComparison tableComparison = dataSetComparison.getTableComparisons().get(0);
        int nrOfMissingRows = tableComparison.getMissingRows().size();
        assertEquals("Found different nr of missing rows", expectedNrOfMissingRows, nrOfMissingRows);
    }

    private void assertRowMatch(boolean match, String rowIdentifier, DataSetComparison dataSetComparison) {
        TableComparison tableComparison = dataSetComparison.getTableComparisons().get(0);
        boolean result = tableComparison.isMatchingRow(rowIdentifier);
        assertEquals(match, result);
    }

    private void assertColumnDifference(int index, String columnName, Object expectedValue, Object actualValue, DataSetComparison dataSetComparison) {
        TableComparison tableComparison = dataSetComparison.getTableComparisons().get(0);
        RowComparison bestRowComparison = tableComparison.getBestRowComparisons().get(0);
        ColumnDifference columnDifference1 = bestRowComparison.getColumnDifferences().get(index);
        assertEquals(columnName, columnDifference1.getColumnName());
        assertEquals(expectedValue, columnDifference1.getExpectedValue());
        assertEquals(actualValue, columnDifference1.getActualValue());
    }

    private void assertBestRowComparison(Row expectedRow, Row actualRow, DataSetComparison dataSetComparison) {
        TableComparison tableComparison = dataSetComparison.getTableComparisons().get(0);
        RowComparison bestRowComparison = tableComparison.getBestRowComparison(expectedRow);
        assertSame(expectedRow, bestRowComparison.getExpectedRow());
        assertSame(actualRow, bestRowComparison.getActualRow());
    }

    private void assertIsExpectedNoMoreRecordsButFoundMore(boolean expectedValue, DataSetComparison dataSetComparison) {
        TableComparison tableComparison = dataSetComparison.getTableComparisons().get(0);
        assertEquals(expectedValue, tableComparison.isExpectedNoMoreRecordsButFoundMore());
    }


    private DataSetRow createDataSetRow() {
        DataSetSettings dataSetSettings = new DataSetSettings('=', '$', false);
        DataSetRow dataSetRow = new DataSetRow("schema", "table", null, false, dataSetSettings);
        dataSetRow.addDataSetValue(new DataSetValue("column", "value"));
        return dataSetRow;
    }

    private DataSetRow createEmptyDataSetRow() {
        DataSetSettings dataSetSettings = new DataSetSettings('=', '$', false);
        return new DataSetRow("schema", "table", null, false, dataSetSettings);
    }

}