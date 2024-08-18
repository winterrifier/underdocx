package de.underdocx;

import de.underdocx.tools.tree.TreeWalker;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static de.underdocx.tools.common.Convenience.buildList;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractTest {

    /*

        --- load and create files ---

     */
    protected InputStream getResource(String name) {
        return this.getClass().getResourceAsStream(name);
    }

    protected String getResourceAsString(String name) {
        try {
            return new String(getResource(name).readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected File createTmpFile(String prefix, String extension) {
        try {
            return Files.createTempFile(prefix, "." + extension).toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected File createTmpFile(String extension) {
        return createTmpFile("tmp_", extension);
    }

    protected String createTmpUri(InputStream is, String extension) {
        return createTmpUri(is, extension, 1000 * 60);
    }

    protected String createTmpUri(InputStream is, String extension, long lifetime) {
        try {
            File file = createTmpFile(extension);
            file.deleteOnExit();
            FileUtils.copyInputStreamToFile(is, file);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    file.delete();
                }
            }, lifetime);
            return file.toURI().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected File createFileInTempDir(String fileName) {
        String tmpDir = System.getProperty("java.io.tmpdir");
        return new File(tmpDir + "/" + fileName);
    }

    /*

        --- XML---

     */

    protected Document readXML(String xmlContent) {
        InputStream is = new ByteArrayInputStream(xmlContent.getBytes());
        return readXML(is);
    }

    protected Document readXML(InputStream is) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String getXMLString(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute("indent-number", "4");
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            Writer writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected List<String> namesOf(Collection<Node> nodes) {
        return buildList(list -> nodes.forEach(element -> list.add(element.getNodeName())));
    }

    protected <T> List<T> listOf(T... elements) {
        return Arrays.asList(elements);
    }

    protected void assertEqualTrees(Node tree1, Node tree2) {
        TreeWalker walker1 = new TreeWalker(tree1, tree1);
        TreeWalker walker2 = new TreeWalker(tree2, tree2);
        while (walker1.hasNext()) {
            assertThat(walker2.hasNext());
            TreeWalker.VisitState state1 = walker1.next();
            TreeWalker.VisitState state2 = walker2.next();
            assertThat(state1.getNode().getNodeName()).isEqualTo(state2.getNode().getNodeName());
            assertThat(state1.isBeginVisit()).isEqualTo(state2.isBeginVisit());
        }
        assertThat(walker2.hasNext()).isFalse();
    }


}
