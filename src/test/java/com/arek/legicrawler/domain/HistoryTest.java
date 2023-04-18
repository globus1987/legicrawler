package com.arek.legicrawler.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.arek.legicrawler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(History.class);
        History history1 = new History();
        history1.setId("id1");
        History history2 = new History();
        history2.setId(history1.getId());
        assertThat(history1).isEqualTo(history2);
        history2.setId("id2");
        assertThat(history1).isNotEqualTo(history2);
        history1.setId(null);
        assertThat(history1).isNotEqualTo(history2);
    }
}
