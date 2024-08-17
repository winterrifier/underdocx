package de.underdocx.tools;

import de.underdocx.AbstractTest;
import de.underdocx.tools.tree.Nodes;
import de.underdocx.tools.tree.TreeWalker;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.assertj.core.api.Assertions.assertThat;


public class TreeWalkerTest extends AbstractTest {

    private void assertEquals(TreeWalker.VisitState state, String nodeName, Boolean isBegin) {
        assertThat(state.getNode().getNodeName()).isEqualTo(nodeName);
        assertThat(state.isBeginVisit()).isEqualTo(isBegin);
    }

    @Test
    public void testTreeWalker() {
        String xml = "<a><b></b><c><d></d></c></a>";
        Document doc = readXML(xml);
        Node a = Nodes.findFirstDescendantNode(doc, "a").get();
        TreeWalker walker = new TreeWalker(a, a);
        assertThat(walker.hasNext()).isTrue();
        assertEquals(walker.next(), "a", true);
        assertThat(walker.hasNext()).isTrue();
        assertEquals(walker.next(), "b", true);
        assertThat(walker.hasNext()).isTrue();
        assertEquals(walker.next(), "b", false);
        assertThat(walker.hasNext()).isTrue();
        assertEquals(walker.next(), "c", true);
        assertThat(walker.hasNext()).isTrue();
        assertEquals(walker.next(), "d", true);
        assertThat(walker.hasNext()).isTrue();
        assertEquals(walker.next(), "d", false);
        assertThat(walker.hasNext()).isTrue();
        assertEquals(walker.next(), "c", false);
        assertThat(walker.hasNext()).isTrue();
        assertEquals(walker.next(), "a", false);
        assertThat(walker.hasNext()).isFalse();
    }
}
