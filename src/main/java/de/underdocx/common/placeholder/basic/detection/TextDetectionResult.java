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

package de.underdocx.common.placeholder.basic.detection;

import de.underdocx.common.placeholder.basic.textnodeinterpreter.TextNodeInterpreter;
import de.underdocx.tools.tree.nodepath.TextNodePath;
import org.w3c.dom.Node;

public class TextDetectionResult {

    public final TextArea area;
    public final TextDetectionResultType result;

    public TextDetectionResult(TextDetectionResultType result, TextArea area) {
        this.result = result;
        this.area = area;
    }

    public enum TextDetectionResultType {
        CONTAINS_TEXT, CONTAINS_START, NO_DETECTION
    }

    public static class TextArea {

        public final TextNodePath path;
        public final TextNodePath.TextRange range;
        public final TextNodeInterpreter interpreter;

        public TextArea(TextNodePath nodes, TextNodePath.TextRange range, TextNodeInterpreter interpreter) {
            this.path = nodes;
            this.range = range;
            this.interpreter = interpreter;
        }

        public Node getBeginNode() {
            return path.getNodes().get(range.begin.nodeIndex);
        }


        public Node getEndNode() {
            return path.getNodes().get(range.end.nodeIndex);
        }

        public boolean isSelfContained() {
            return path.getNodes().size() == 1 && range.begin.nodeIndex == 0 && range.end.nodeIndex == 0 &&
                    range.begin.charIndex == 0 && range.end.charIndex == interpreter.getText(path.getNodes().get(0)).length() - 1;
        }

    }


}




