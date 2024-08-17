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

import de.underdocx.common.placeholder.basic.detection.TextDetectionResult;
import de.underdocx.common.placeholder.basic.detection.TextDetector;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.TextNodeInterpreter;
import de.underdocx.tools.tree.Enumerator;
import de.underdocx.tools.tree.TreeWalker;
import de.underdocx.tools.tree.nodepath.TextNodePath;
import de.underdocx.tools.tree.nodepath.TreeNodeCollector;
import org.w3c.dom.Node;

import java.util.List;

import static de.underdocx.tools.common.Convenience.also;
import static de.underdocx.tools.common.Convenience.buildList;

public class AbstractFullPathExtractor extends AbstractExtractor {

    public AbstractFullPathExtractor(TextDetector detector, TextNodeInterpreter interpreter) {
        super(detector, interpreter);
    }

    @Override
    public List<Node> extractNodes(Node tree) {
        return buildList(result -> {
            Node start = tree;
            while (start != null) {
                TextNodePath fullPath = new TextNodePath(also(new TreeNodeCollector(start, tree), Enumerator::collect).getNodes(), interpreter);
                TextDetectionResult.TextArea area = detector.detect(fullPath).area;
                if (area != null) {
                    Node foundNode = getIfEncapsulated(area).orElse(Encapsulator.encapsulate(area));
                    result.add(foundNode);
                    start = TreeWalker.findNextNode(foundNode, tree, true);
                } else {
                    start = null;
                }
            }
        });
    }
}
