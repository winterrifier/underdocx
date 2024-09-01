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

import de.underdocx.AbstractOdtTest;
import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.enginelayers.defaultodtengine.DefaultODTEngine;
import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.enginelayers.modelengine.model.simple.LeafModelNode;
import de.underdocx.enginelayers.modelengine.model.simple.ReflectionModelNode;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ReflectionModelTest extends AbstractOdtTest {

    @Test
    public void testReflectionModel() {
        ModelNode node = new ReflectionModelNode(new TestClassA());
        assertThat(node.getProperty("a").getValue()).isEqualTo("Hallo");
        assertThat(node.getProperty("b").getProperty("c").getProperty(0).getValue()).isEqualTo("Item1");
        assertThat(node.getProperty("b").getProperty("c").getProperty(1).getValue()).isEqualTo("Item2");
        assertThat(node.getProperty("b").getProperty("d").getValue()).isEqualTo(true);
        assertThat(node.getProperty("b").getProperty("getNull").getValue()).isNull();
    }

    private class TestClassA {
        public String a = "Hallo";
        public TestClassB b = new TestClassB();
    }

    private class TestClassB {
        public List<String> getC() {
            return Arrays.asList("Item1", "Item2");
        }

        public boolean isD = true;

        public String getNull() {
            return null;
        }

    }

    @Test
    public void testReflectionModelEngine() {
        OdtContainer doc = new OdtContainer("${String @value:\"b.c[0]\"}");
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.setModel(new TestClassA());
        show(doc);
        engine.run();
        show(doc);
        assertContains(doc, "Item1");
        assertNoPlaceholders(doc);
    }

    @Test
    public void testResolve() {
        OdtContainer doc = new OdtContainer("" +
                "${String @value:\"b.x\"}      \n" +
                "${String @value:\"b.c[0]\"}   \n" +
                "${String @value:\"b.c[0]\"}   \n");
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.setModel(new TestClassA(),
                (object, name) -> name.equals("x")
                        ? java.util.Optional.of(new LeafModelNode("42"))
                        : Optional.empty());
        show(doc);
        engine.run();
        show(doc);
        assertContains(doc, "Item1");
        assertContains(doc, "42");
        assertNoPlaceholders(doc);
    }
}
