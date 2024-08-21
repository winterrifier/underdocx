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

package de.underdocx.parameterengine;

import de.underdocx.AbstractTest;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import de.underdocx.enginelayers.parameterengine.internal.ParametersPlaceholderCodec;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParametersPlaceholderCodecTest extends AbstractTest {

    @Test
    public void testSingleKey() {
        ParametersPlaceholderCodec codec = new ParametersPlaceholderCodec();
        ParametersPlaceholderData parsed = codec.parse("${ Test }").get();
        assertThat(parsed.getKey()).isEqualTo("Test");
        assertThat(parsed.getJson()).isNull();
        String text = codec.getTextContent(parsed);
        assertThat(text).isEqualTo("${Test}");

    }

    @Test
    public void testKeyAndJson() {
        ParametersPlaceholderCodec codec = new ParametersPlaceholderCodec();
        ParametersPlaceholderData parsed = codec.parse("${Test    abc :\"DEF\"}").get();
        assertThat(parsed.getKey()).isEqualTo("Test");
        assertThat(parsed.getStringAttribute("abc").get()).isEqualTo("DEF");
        String text = codec.getTextContent(parsed);
        assertThat(text).isEqualTo("${Test \"abc\":\"DEF\"}");
    }

    @Test
    public void testAddAttr() {
        ParametersPlaceholderCodec codec = new ParametersPlaceholderCodec();
        ParametersPlaceholderData parsed = codec.parse("${Test abc:\"DEF\"}").get();
        parsed.addStringAttribute("id", "42");
        String text = codec.getTextContent(parsed);
        assertThat(text).isEqualTo("${Test \"abc\":\"DEF\",\"id\":\"42\"}");
    }

}
