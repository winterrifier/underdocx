/*
MIT License

Copyright (c) 2024 Gerald Winter

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package de.underdocx.common.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.underdocx.environment.UnderdocxEnv;
import de.underdocx.environment.UnderdocxExecutionException;
import de.underdocx.tools.common.Convenience;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class JsonCodec implements Codec<JsonNode> {

    private final boolean isSimplifiedSyntaxAllowed;
    private final boolean pretty;
    private final boolean exchangeQuotes;
    private ObjectMapper mapper;

    public JsonCodec() {
        this(false, false, false);
    }

    public JsonCodec(boolean pretty, boolean isSimplifiedSyntaxAllowed, boolean exchangeQuotes) {
        this.pretty = pretty;
        this.isSimplifiedSyntaxAllowed = isSimplifiedSyntaxAllowed;
        this.exchangeQuotes = exchangeQuotes;
        this.mapper = createMapper();
    }

    @Override
    public Optional<JsonNode> parse(String string) {
        return Convenience.buildOptional(w -> {
            try {
                w.value = mapper.readTree(checkQuotes(string));
            } catch (JsonProcessingException e) {
                UnderdocxEnv.getInstance().logger.warn(e);
            }
        });
    }

    private ObjectMapper createMapper() {
        return Convenience.also(new ObjectMapper(), mapper -> {
            if (isSimplifiedSyntaxAllowed) {
                mapper.enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature());
                mapper.enable(JsonReadFeature.ALLOW_JAVA_COMMENTS.mappedFeature());
                mapper.enable(JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature());
                mapper.enable(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature());
                mapper.enable(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature());
                mapper.enable(JsonReadFeature.ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature());
            }
        });
    }

    private String checkQuotes(String string) {
        if (exchangeQuotes) {
            string = string
                    .replace('\u201C', '\"')
                    .replace('\u201D', '\"')
                    .replace('\u201E', '\"')
                    .replace('\u201F', '\"');
        }
        return string;
    }

    @Override
    public String getTextContent(JsonNode data) {
        try {
            if (pretty) {
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            } else {
                return mapper.writeValueAsString(data);
            }
        } catch (JsonProcessingException e) {
            throw new UnderdocxExecutionException(e);
        }
    }


    public Map<String, Object> getAsMap(JsonNode data) {
        return mapper.convertValue(data, Map.class);
    }

    public Optional<Map<String, Object>> getAsMap(String data) {
        return Convenience.buildOptional(w -> parse(data).ifPresent(json -> w.value = getAsMap(json)));
    }

    public Optional<Map<String, Object>> getAsMap(InputStream is) {
        String content = null;
        try {
            content = IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            UnderdocxEnv.getInstance().logger.warn(e);
            return Optional.empty();
        }
        return getAsMap(content);
    }

    public Optional<String> convertMapToJsonString(Object mapStructure) {
        try {
            JsonNode tree = mapper.valueToTree(mapStructure);
            return Optional.of(getTextContent(tree));
        } catch (Exception e) {
            UnderdocxEnv.getInstance().logger.warn(e);
            return Optional.empty();
        }
    }
}
