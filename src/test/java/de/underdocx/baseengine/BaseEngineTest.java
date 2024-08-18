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

package de.underdocx.baseengine;

import de.underdocx.AbstractOdtTest;
import de.underdocx.DefaultODTEngine;
import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.common.placeholder.EncapsulatedNodesExtractor;
import de.underdocx.enginelayers.baseengine.BaseEngine;
import de.underdocx.enginelayers.baseengine.internal.commands.SimpleReplaceFunctionCommand;
import de.underdocx.enginelayers.baseengine.internal.placeholdersprovider.dollar.SimpleDollarPlaceholdersProvider;
import de.underdocx.tools.tree.Nodes;
import de.underdocx.tools.tree.TreePrinter;
import org.junit.jupiter.api.Test;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseEngineTest extends AbstractOdtTest {

    @Test
    public void testHelloWorldDefaultODTEngine() {
        OdtContainer doc = new OdtContainer("Hello $name");
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.registerSimpleDollarReplacement("name", "World");
        engine.run();
        assertContains(doc, "Hello World");
        assertNotContains(doc, "$");
    }

    @Test
    public void testHelloWorldBaseEngine() {
        OdtContainer doc = new OdtContainer("Hello $name");
        BaseEngine<OdtContainer, OdfTextDocument> baseEngine = new BaseEngine<>(doc);
        baseEngine.registerCommandHandler(
                new SimpleDollarPlaceholdersProvider(doc),
                new SimpleReplaceFunctionCommand<>(foundString -> Optional.ofNullable(foundString.equals("name") ? "World" : null))
        );
        baseEngine.run();
        assertContains(doc, "Hello World");
        assertNotContains(doc, "$");
    }

    @Test
    public void testSimpleDollarPlaceholderProviderEncapsulated() {
        OdtContainer doc = new OdtContainer("Hello $name");
        SimpleDollarPlaceholdersProvider<OdtContainer, OdfTextDocument> provider = new SimpleDollarPlaceholdersProvider<>(doc);
        Node node = provider.getPlaceholders().next();
        assertThat(node.getFirstChild().getNodeValue()).isEqualTo("$name");
    }

    @Test
    public void testSimpleDollarExtractor() {
        Document xml = readXML("<root><p>Hallo $name :-)</p></root>");
        Node paragraph = Nodes.findFirstDescendantNode(xml, "p").get();
        EncapsulatedNodesExtractor extractor = SimpleDollarPlaceholdersProvider.createExtractor(testTextNodeInterpreter);
        List<Node> nodes = extractor.extractNodes(paragraph);
        assertThat(nodes.get(0).getFirstChild().getNodeValue()).isEqualTo("$name");
        String xmlTree = new TreePrinter(Nodes.findFirstDescendantNode(xml, "root").get(), true).toString();
        assertThat(xmlTree).isEqualTo("<root><p>Hallo <span>$name</span> :-)</p></root>");
    }

    @Test
    public void testSimpleImage() throws Exception {
        OdtContainer doc = new OdtContainer(getResource("ContainsImagePlaceholder.odt"));
        assertThat(Nodes.findFirstDescendantNode(doc.getContentDom(), node ->
                (Nodes.attributes(node).getOrDefault("draw:name", "").startsWith("$")
                )).isPresent());
        String imageURI = createTmpUri(getResource("image3.jpg"), "jpg");
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.registerSimpleDollarImageReplacement("image", imageURI, true);
        engine.run();
        assertThat(Nodes.findFirstDescendantNode(doc.getContentDom(), node ->
                (Nodes.attributes(node).getOrDefault("draw:name", "").startsWith("$")
                )).isEmpty());
        //show(doc);

    }
}
