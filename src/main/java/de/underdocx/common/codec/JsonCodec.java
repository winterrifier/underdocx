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

import java.util.Optional;

public class JsonCodec implements Codec<JsonNode> {

    private final boolean isSimplifiedSyntaxAllowed;
    private final boolean pretty;
    private final boolean exchangeQuotes;

    public JsonCodec() {
        pretty = false;
        isSimplifiedSyntaxAllowed = false;
        exchangeQuotes = false;
    }

    public JsonCodec(boolean pretty, boolean isSimplifiedSyntaxAllowed, boolean exchangeQuotes) {
        this.pretty = pretty;
        this.isSimplifiedSyntaxAllowed = isSimplifiedSyntaxAllowed;
        this.exchangeQuotes = exchangeQuotes;
    }

    @Override
    public Optional<JsonNode> parse(String string) {
        return Convenience.buildOptional(w -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                if (isSimplifiedSyntaxAllowed) {
                    mapper.enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature());
                    mapper.enable(JsonReadFeature.ALLOW_JAVA_COMMENTS.mappedFeature());
                    mapper.enable(JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature());
                    mapper.enable(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature());
                    mapper.enable(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature());
                    mapper.enable(JsonReadFeature.ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature());
                }
                JsonNode node = mapper.readTree(checkQuotes(string));
                w.value = node;
            } catch (JsonProcessingException e) {
                UnderdocxEnv.getInstance().logger.warn(e);
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
                return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(data);
            } else {
                return new ObjectMapper().writeValueAsString(data);
            }
        } catch (JsonProcessingException e) {
            throw new UnderdocxExecutionException(e);
        }
    }


}
