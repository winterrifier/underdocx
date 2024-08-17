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

package de.underdocx.common.placeholder.basic.textnodeinterpreter;

import de.underdocx.tools.common.Convenience;
import de.underdocx.tools.odf.OdfNodeType;
import de.underdocx.tools.tree.Nodes;
import org.w3c.dom.Node;

import java.util.Arrays;

public abstract class AbstractOdtTextNodeInterpreter implements TextNodeInterpreter {

    protected final String spanElementName = getNodeName(OdfNodeType.ELEMENT_SPAN);
    protected final String tabElementName = getNodeName(OdfNodeType.ELEMENT_TAB);
    protected final String lineBreakElementName = getNodeName(OdfNodeType.ELEMENT_LINEBREAK);
    protected final String aElementName = getNodeName(OdfNodeType.ELEMENT_A);
    protected final String sElementName = getNodeName(OdfNodeType.ELEMENT_SPACE);
    protected final String pElementName = getNodeName(OdfNodeType.ELEMENT_PARAGRAPH);
    protected final String cAttributeName = getNodeName(OdfNodeType.ATTRIBUTE_SPACE_COUNT);

    protected String getNodeName(OdfNodeType name) {
        return name.getElementName();
    }

    protected boolean isOneOf(Node node, String... names) {
        return Arrays.asList(names).contains(node.getNodeName());
    }

    protected boolean is(Node node, String name) {
        return name.equals(node.getNodeName());
    }


    @Override
    public String getText(Node node) {
        if (node.getNodeType() == Node.TEXT_NODE) return node.getNodeValue();
        if (isOneOf(node, spanElementName, aElementName, pElementName)) return "";
        if (is(node, tabElementName)) return "\t";
        if (is(node, lineBreakElementName)) return System.lineSeparator();
        if (is(node, sElementName)) return Convenience.buildString(buffer -> {
            for (int i = 0; i < getC(node); i++) buffer.append(" ");
        });
        return null;
    }

    private int getC(Node node) {
        try {
            return Integer.valueOf(Nodes.attributes(node).getOrDefault(cAttributeName, "0"));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean isPlainTextType(Node node) {
        return node.getNodeType() == Node.TEXT_NODE || isOneOf(node, sElementName, tabElementName, lineBreakElementName);
    }

    @Override
    public boolean isPlainTextContainerType(Node node) {
        return isOneOf(node, sElementName, tabElementName, lineBreakElementName);
    }

    @Override
    public boolean isPartialTextContainerType(Node node) {
        return is(node, pElementName);
    }

    @Override
    public boolean isTextRelatedType(Node node) {
        return isPlainTextContainerType(node) || isPlainTextType(node) || isPartialTextContainerType(node);
    }
}
