package de.underdocx;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.AbstractOdfTextNodeInterpreter;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.TextNodeInterpreter;
import de.underdocx.environment.UnderdocxEnv;
import de.underdocx.tools.odf.OdfNodeType;
import de.underdocx.tools.tree.Nodes;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.incubator.search.TextNavigation;
import org.odftoolkit.odfdom.incubator.search.TextSelection;
import org.w3c.dom.Node;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

import static de.underdocx.tools.common.Convenience.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AbstractOdtTest extends AbstractTest {

    /*

      -- Detection --

     */

    public boolean isIsLibreOfficeInstalled() {
        return UnderdocxEnv.getInstance().libreOfficeExecutable != null;
    }

    protected Optional<TextSelection> search(OdfTextDocument doc, String text) {
        return search(doc, Pattern.compile(Pattern.quote(text)));
    }

    protected Optional<TextSelection> search(OdfTextDocument doc, Pattern pattern) {
        TextNavigation searcher = new TextNavigation(pattern, doc);
        return searcher.hasNext() ? Optional.of(searcher.next()) : Optional.empty();
    }

    protected boolean containsText(OdfTextDocument doc, String text) {
        return search(doc, text).isPresent();
    }

    protected boolean containsText(OdtContainer doc, String text) {
        return containsText(doc.getDocument(), text);
    }

    protected Optional<Integer> getOrder(OdfTextDocument doc, String text1, String text2) {
        return buildOptional(result -> {
            if (containsText(doc, text1) && containsText(doc, text2)) {
                Pattern regex = Pattern.compile(Pattern.quote(text1) + " | " + Pattern.quote(text2));
                Optional<TextSelection> searchResult = search(doc, regex);
                searchResult.ifPresent(selection -> result.value = selection.getText().equals(text1) ? -1 : 1);
            }
        });
    }

    protected Optional<Integer> getOrder(OdtContainer doc, String text1, String text2) {
        return getOrder(doc.getDocument(), text1, text2);
    }


    /*

      -- Assertions

     */


    protected void assertContains(OdtContainer doc, String text) {
        assertThat(containsText(doc, text)).isTrue();
    }

    protected void assertContains(OdfTextDocument doc, String text) {
        assertThat(containsText(doc, text)).isTrue();
    }

    protected void assertNotContains(OdtContainer doc, String text) {
        assertThat(containsText(doc, text)).isFalse();
    }

    protected void assertNotContains(OdfTextDocument doc, String text) {
        assertThat(containsText(doc, text)).isFalse();
    }

    protected void assertFirst(OdtContainer doc, String first, String second) {
        assertFirst(doc.getDocument(), first, second);
    }

    protected void assertFirst(OdfTextDocument doc, String first, String second) {
        assertThat(getOrder(doc, first, second).get()).isLessThan(0);
    }

    protected void assertOrder(OdfTextDocument doc, String... texts) {
        for (int i = 0; i < texts.length - 2; i++) {
            assertFirst(doc, texts[i], texts[i + 1]);
        }
    }

    protected void assertOrder(OdtContainer doc, String... texts) {
        assertOrder(doc, texts);
    }

    protected void assertNoPlaceholders(OdtContainer doc) {
        assertThat(search(doc.getDocument(), Pattern.compile("\\$\\{.*\\}")).isPresent()).isFalse();
        assertThat(search(doc.getDocument(), Pattern.compile("\\$\\.*")).isPresent()).isFalse();
    }

    /*

    -- Creation

     */


    protected static TextNodeInterpreter testTextNodeInterpreter = new AbstractOdfTextNodeInterpreter() {

        @Override
        protected String getNodeName(OdfNodeType name) {
            return name.getPureName();
        }

        @Override
        public Node createTextContainer(Node parent) {
            return also(parent.getOwnerDocument().createElement("span"), span -> parent.appendChild(span));
        }

        @Override
        public void setNodeText(Node node, String text) {
            Nodes.setNodeText(node, text);
        }
    };

    protected void show(DocContainer<?> doc) {
        File tmp = createTmpFile(doc.getFileExtension());
        try {
            doc.save(tmp);
            Desktop.getDesktop().open(tmp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
