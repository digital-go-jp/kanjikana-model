package jp.go.digital.kanjikana.core.executor;

import jp.go.digital.kanjikana.core.engine.ai.generate.SearchResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class OutputMakerTest {

    @Test
    void keepsGenerationProbabilityInTheZeroToOneScale() throws Exception {
        Output output = OutputMaker.exec(List.of(new SearchResult("タナカ", 0.5)), OutputMaker.GenKey.KANA);

        assertThat(output.result.getAdditionalProperties().get(OutputMaker.ADDITIONAL_KEY_PROB), equalTo(List.of(50)));
    }
}
