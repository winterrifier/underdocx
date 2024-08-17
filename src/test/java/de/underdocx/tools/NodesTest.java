package de.underdocx.tools;

import de.underdocx.AbstractTest;
import de.underdocx.tools.tree.Nodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class NodesTest extends AbstractTest {

    private String xmlStr = "<root><a><b><c></c><d></d><e></e></b><f></f></a></root>";

    private Document xml1 = null;
    private Document xml2 = null;

    @BeforeEach
    public void createXMLDocuments() {
        xml1 = readXML(xmlStr);
        xml2 = readXML(xmlStr);
    }

    @Test
    public void testGetAncestors() {
        Node d = Nodes.findFirstDescendantNode(xml1, "d").get();
        Node a = Nodes.findDescendantNodes(xml1, "a").get(0);
        List<Node> path = Nodes.getAncestors(d, a);
        assertThat(path.size()).isEqualTo(3);
        assertThat(namesOf(path)).isEqualTo(listOf("d", "b", "a"));
    }

    @Test
    public void testImport() {
        Node targetParent = Nodes.findFirstDescendantNode(xml2, "f").get();
        Node toImport = Nodes.findFirstDescendantNode(xml1, "b").get();
        Node imported = Nodes.importNode(targetParent, toImport);
        assertEqualTrees(imported, readXML("<b><c></c><d></d><e></e></b>").getFirstChild());
        assertEqualTrees(
                xml2.getFirstChild(),
                readXML("<root><a><b><c></c><d></d><e></e></b><f><b><c></c><d></d><e></e></b></f></a></root>").getFirstChild());
    }

    @Test
    public void hallo() {
        System.out.println("hallo ich bin johanna");
    }


}
