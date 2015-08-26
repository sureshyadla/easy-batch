/*
 *  The MIT License
 *
 *   Copyright (c) 2015, Mahmoud Ben Hassine (mahmoud@benhassine.fr)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */

package org.easybatch.core.filter;

import org.easybatch.core.api.RecordFilter;
import org.easybatch.core.api.Report;
import org.easybatch.core.impl.EngineBuilder;
import org.easybatch.core.mapper.GenericRecordMapper;
import org.easybatch.core.processor.RecordCollector;
import org.easybatch.core.reader.StringRecordReader;
import org.easybatch.core.record.StringRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easybatch.core.util.Utils.LINE_SEPARATOR;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link EmptyRecordFilter}.
 *
 * @author Mahmoud Ben Hassine (mahmoud@benhassine.fr)
 */
@RunWith(MockitoJUnitRunner.class)
public class EmptyRecordFilterTest {

    @Mock
    private StringRecord stringRecord;

    private RecordFilter recordFilter;

    @Before
    public void setUp() throws Exception {
        recordFilter = new EmptyRecordFilter();
    }

    @Test
    public void testFilterEmptyRecord() throws Exception {
        when(stringRecord.getPayload()).thenReturn("");
        assertThat(recordFilter.filterRecord(stringRecord)).isTrue();
    }

    @Test
    public void testFilterNonEmptyRecord() throws Exception {
        when(stringRecord.getPayload()).thenReturn("foo");
        assertThat(recordFilter.filterRecord(stringRecord)).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void integrationTest() throws Exception {
        String dataSource = "foo" + LINE_SEPARATOR + "" + LINE_SEPARATOR + "bar" + LINE_SEPARATOR + "" + LINE_SEPARATOR;

        Report report = EngineBuilder.aNewEngine()
                .reader(new StringRecordReader(dataSource))
                .filter(new EmptyRecordFilter())
                .mapper(new GenericRecordMapper())
                .processor(new RecordCollector())
                .build().call();

        assertThat(report).isNotNull();
        assertThat(report.getTotalRecords()).isEqualTo(4);
        assertThat(report.getFilteredRecordsCount()).isEqualTo(2);
        assertThat(report.getSuccessRecordsCount()).isEqualTo(2);

        List<String> records = (List<String>) report.getBatchResult();
        assertThat(records).hasSize(2).containsExactly("foo", "bar");
    }
}