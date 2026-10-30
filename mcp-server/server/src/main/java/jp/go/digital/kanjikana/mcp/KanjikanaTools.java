package jp.go.digital.kanjikana.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.go.digital.kanjikana.core.Resources;
import jp.go.digital.kanjikana.core.executor.Output;
import jp.go.digital.kanjikana.core.executor.OutputMaker;
import jp.go.digital.kanjikana.core.executor.generate.Kana2Kanji;
import jp.go.digital.kanjikana.core.executor.generate.Kanji2Kana;
import jp.go.digital.kanjikana.core.executor.match.KanjiKanaMatch;
import jp.go.digital.kanjikana.core.executor.match.strategy.StrategyIF;
import jp.go.digital.kanjikana.core.executor.match.strategy.impl.StrategyBasic;
import jp.go.digital.kanjikana.core.executor.match.strategy.impl.StrategyEnsemble;
import jp.go.digital.kanjikana.core.executor.match.strategy.impl.StrategyOnlyAi;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KanjikanaTools {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @McpTool(name = "match_name_detail", description = "詳細モデルで漢字氏名とカナ氏名を照合します。", annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public String matchNameDetail(
            @McpToolParam(description = "漢字氏名") String kanji,
            @McpToolParam(description = "カナ氏名") String kana,
            @McpToolParam(description = "判定メモも返す場合はtrue", required = false) Boolean debug) {
        return match(kanji, kana, debug, StrategyEnsemble::newInstance);
    }

    @McpTool(name = "match_name_simple", description = "簡易モデルで漢字氏名とカナ氏名を照合します。", annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public String matchNameSimple(
            @McpToolParam(description = "漢字氏名") String kanji,
            @McpToolParam(description = "カナ氏名") String kana,
            @McpToolParam(description = "判定メモも返す場合はtrue", required = false) Boolean debug) {
        return match(kanji, kana, debug, StrategyBasic::newInstance);
    }

    @McpTool(name = "match_name_ai", description = "AIモデルで漢字氏名とカナ氏名を照合します。", annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public String matchNameAi(
            @McpToolParam(description = "漢字氏名") String kanji,
            @McpToolParam(description = "カナ氏名") String kana,
            @McpToolParam(description = "判定メモも返す場合はtrue", required = false) Boolean debug) {
        return match(kanji, kana, debug, StrategyOnlyAi::newInstance);
    }

    @McpTool(name = "generate_kana_candidates", description = "漢字氏名からカナ氏名の候補を生成します。", annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public String generateKanaCandidates(
            @McpToolParam(description = "漢字氏名") String kanji,
            @McpToolParam(description = "返す候補数（1-10）。省略時は5", required = false) Integer max) {
        return serialize(() -> new Kanji2Kana().exec(kanji, maxCandidates(max)));
    }

    @McpTool(name = "generate_kanji_candidates", description = "カナ氏名から漢字氏名の候補を生成します。", annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public String generateKanjiCandidates(
            @McpToolParam(description = "カナ氏名") String kana,
            @McpToolParam(description = "返す候補数（1-10）。省略時は5", required = false) Integer max) {
        return serialize(() -> new Kana2Kanji().exec(kana, maxCandidates(max)));
    }

    @McpTool(name = "get_model_version", description = "コアモデルのバージョンを返します。", annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true, openWorldHint = false))
    public String getModelVersion() {
        return serialize(() -> Map.of("version", Resources.getProperty(Resources.PropKey.VERSION)));
    }

    private String match(String kanji, String kana, Boolean debug, StrategyFactory strategyFactory) {
        return serialize(() -> {
            Output output = new KanjiKanaMatch(strategyFactory.create()).exec(kanji, kana);
            if (!Boolean.TRUE.equals(debug)) {
                output.result.getAdditionalProperties().remove(OutputMaker.ADDITIONAL_KEY_NOTES);
            }
            return output;
        });
    }

    private String serialize(ToolOperation operation) {
        try {
            return objectMapper.writeValueAsString(operation.execute());
        } catch (Exception exception) {
            throw new IllegalStateException("漢字カナモデルの処理に失敗しました。", exception);
        }
    }

    private int maxCandidates(Integer max) {
        int value = max == null ? 5 : max;
        if (value < 1 || value > 10) {
            throw new IllegalArgumentException("maxは1から10の整数で指定してください。");
        }
        return value;
    }

    @FunctionalInterface
    private interface ToolOperation {
        Object execute() throws Exception;
    }

    @FunctionalInterface
    private interface StrategyFactory {
        StrategyIF create() throws Exception;
    }
}