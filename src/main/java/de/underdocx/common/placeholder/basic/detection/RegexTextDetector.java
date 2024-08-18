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

import de.underdocx.tools.common.Pair;
import de.underdocx.tools.common.Regex;
import de.underdocx.tools.tree.nodepath.TextNodePath;

import java.util.Optional;
import java.util.regex.Pattern;

public class RegexTextDetector implements TextDetector {

    private final Regex regex;

    public RegexTextDetector(Pattern regex) {
        this(new Regex(regex));
    }

    public RegexTextDetector(Regex regEx) {
        this.regex = regEx;
    }

    @Override
    public TextDetectionResult detect(TextNodePath path) {
        String text = path.fetchTextContent();
        if (text != null) {
            Optional<Pair<Integer, Integer>> match = regex.findFirst(text);
            if (match.isPresent()) {
                return new TextDetectionResult(TextDetectionResult.TextDetectionResultType.CONTAINS_TEXT,
                        new TextDetectionResult.TextArea(
                                path,
                                new TextNodePath.TextRange(
                                        path.getTextPointer(match.get().left),
                                        path.getTextPointer(match.get().right)
                                ),
                                path.getTextNodeInterpreter()
                        )
                );
            }
        }
        return new TextDetectionResult(TextDetectionResult.TextDetectionResultType.NO_DETECTION, null);
    }
}
