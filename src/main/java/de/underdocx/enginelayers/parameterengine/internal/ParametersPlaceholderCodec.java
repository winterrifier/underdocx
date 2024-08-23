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

package de.underdocx.enginelayers.parameterengine.internal;

import de.underdocx.common.codec.Codec;
import de.underdocx.common.codec.JsonCodec;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

import java.util.Optional;

import static de.underdocx.tools.common.Convenience.buildOptional;

public class ParametersPlaceholderCodec implements Codec<ParametersPlaceholderData> {

    public final static JsonCodec JSON_CODEC = new JsonCodec(false, true, true);
    public final static ParametersPlaceholderCodec INSTANCE = new ParametersPlaceholderCodec();

    @Override
    public Optional<ParametersPlaceholderData> parse(String string) {
        return buildOptional(w -> {
            if (string.startsWith("${") && string.endsWith("}") && string.length() > 3) {
                String trimmedContent = string.substring(2, string.length() - 1).trim();
                int spaceIndex = trimmedContent.indexOf(' ');
                if (spaceIndex >= 0) {
                    final String key = trimmedContent.substring(0, spaceIndex).trim();
                    final String jsonString = "{" + trimmedContent.substring(spaceIndex).trim() + "}";
                    JSON_CODEC.parse(jsonString).ifPresent(json -> w.value = new ParametersPlaceholderData.Simple(key, json));
                } else {
                    w.value = new ParametersPlaceholderData.Simple(trimmedContent, null);
                }
            }
        });
    }

    @Override
    public String getTextContent(ParametersPlaceholderData data) {
        if (data.getJson() != null) {
            String jsonStr = JSON_CODEC.getTextContent(data.getJson());
            String paramsJson = jsonStr.substring(1, jsonStr.length() - 1);
            return "${" + data.getKey() + " " + paramsJson + "}";
        } else {
            return "${" + data.getKey() + "}";
        }
    }
}
