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

package de.underdocx.commands.string;

import de.underdocx.AbstractOdtTest;
import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.enginelayers.defaultodtengine.DefaultODTEngine;
import de.underdocx.enginelayers.modelengine.model.simple.MapModelNode;
import org.junit.jupiter.api.Test;

public class StringTest extends AbstractOdtTest {

    @Test
    public void testModeString() {
        String jsonString = """
                {
                  "a":{
                    "b":["Test0", "Test1"]
                  },
                  "c":{
                    "d":["Test2", "Test3"]
                  }
                }
                """;
        String documentStr = "" +
                "A ${String @value:\"a.b[0]\"} A       \n" +
                "${Model value:\"a.b[0]\"}             \n" +
                "B ${String} B                         \n" +
                "C ${String @value:\"^c.d[1]\"} C      \n" +
                "D ${String @value:\"^no\"} D          \n";
        OdtContainer doc = new OdtContainer(documentStr);
        show(doc);
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.setModel(new MapModelNode(jsonString));
        engine.run();
        show(doc);
        assertContains(doc, "A Test0 A");
        assertContains(doc, "B Test0 B");
        assertContains(doc, "C Test3 C");
        assertContains(doc, "D  D");
        assertNoPlaceholders(doc);
    }
}
