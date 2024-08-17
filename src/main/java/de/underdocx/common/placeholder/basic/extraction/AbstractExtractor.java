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

package de.underdocx.common.placeholder.basic.extraction;

import de.underdocx.common.placeholder.EncapsulatedNodesExtractor;
import de.underdocx.common.placeholder.basic.detection.TextDetectionResult;
import de.underdocx.common.placeholder.basic.detection.TextDetector;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.TextNodeInterpreter;
import de.underdocx.tools.tree.nodepath.TextNodePath;
import org.w3c.dom.Node;

import java.util.Optional;

import static de.underdocx.tools.common.Convenience.also;

public abstract class AbstractExtractor implements EncapsulatedNodesExtractor {

    protected final TextDetector detector;
    protected final TextNodeInterpreter interpreter;

    public AbstractExtractor(TextDetector detector, TextNodeInterpreter interpreter) {
        this.detector = detector;
        this.interpreter = interpreter;
    }

    @Override
    public TextNodeInterpreter getTextNodeInterpreter() {
        return interpreter;
    }

    protected Optional<Node> getIfEncapsulated(TextDetectionResult.TextArea extraction) {
        Node parent = extraction.getBeginNode().getParentNode();
        return extraction.isSelfContained() && parent.getChildNodes().getLength() == 1
                && interpreter.isPlainTextContainerType(parent) ? Optional.of(parent) : Optional.empty();
    }

    @Override
    public boolean isEncapsulatedNode(Node node) {
        if (node.getChildNodes().getLength() == 1 && interpreter.isPlainTextContainerType(node) && node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
            Node textNode = node.getFirstChild();
            TextNodePath textNodePath = also(new TextNodePath(interpreter), path -> path.addNode(textNode));
            TextDetectionResult detection = detector.detect(textNodePath);
            return detection.result == TextDetectionResult.TextDetectionResultType.CONTAINS_TEXT && detection.area.isSelfContained();
        }
        return false;
    }
}
