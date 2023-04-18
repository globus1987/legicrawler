package com.arek.legicrawler.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.arek.legicrawler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HistoryDataTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HistoryData.class);
        HistoryData historyData1 = new HistoryData();
        historyData1.setId("id1");
        HistoryData historyData2 = new HistoryData();
        historyData2.setId(historyData1.getId());
        assertThat(historyData1).isEqualTo(historyData2);
        historyData2.setId("id2");
        assertThat(historyData1).isNotEqualTo(historyData2);
        historyData1.setId(null);
        assertThat(historyData1).isNotEqualTo(historyData2);
    }
}
