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

package de.underdocx.modelengine;

import com.fasterxml.jackson.databind.JsonNode;
import de.underdocx.AbstractTest;
import de.underdocx.common.codec.JsonCodec;
import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.enginelayers.modelengine.model.simple.MapModelNode;
import de.underdocx.tools.common.Convenience;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleModelTest extends AbstractTest {

    @Test
    public void testJsonRead() {
        String jsonString = """
                {
                  "a": "Hello World",
                  "b": ["Item1", "Item2"],
                  "c": {
                    "d": "Jon Doe"
                  }
                }                
                """;
        ModelNode model = new MapModelNode(jsonString);
        String newJsonString = model.toString();
        Convenience.also(new JsonCodec(), codec -> {
            JsonNode parsed = codec.parse(newJsonString).get();
            String toCheck = codec.getTextContent(parsed);
            Assertions.assertThat(toCheck).isEqualTo("{\"a\":\"Hello World\",\"b\":[\"Item1\",\"Item2\"],\"c\":{\"d\":\"Jon Doe\"}}");
        });
    }
}
