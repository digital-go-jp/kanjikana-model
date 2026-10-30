package jp.go.digital.kanjikana.core.engine.dict;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

class DictProbsTest {

    @Test
    void convertsConfiguredFrequencyRangeToProbability() {
        assertThat(DictProbs.calcStatisticsProbs(4), equalTo(0.0));
        assertThat(DictProbs.calcStatisticsProbs(103), equalTo(1.0));
        assertThat(DictProbs.calcStatisticsProbs(5), closeTo(1.0 / 99.0, 1e-12));
    }
}
