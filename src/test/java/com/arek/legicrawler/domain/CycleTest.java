package com.arek.legicrawler.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.arek.legicrawler.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CycleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cycle.class);
        Cycle cycle1 = new Cycle();
        cycle1.setId("id1");
        Cycle cycle2 = new Cycle();
        cycle2.setId(cycle1.getId());
        assertThat(cycle1).isEqualTo(cycle2);
        cycle2.setId("id2");
        assertThat(cycle1).isNotEqualTo(cycle2);
        cycle1.setId(null);
        assertThat(cycle1).isNotEqualTo(cycle2);
    }
}
