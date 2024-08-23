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

package de.underdocx.enginelayers.parameterengine.internal;

import de.underdocx.common.placeholder.basic.detection.TextDetectionResult;
import de.underdocx.common.placeholder.basic.detection.TextDetector;
import de.underdocx.tools.common.Pair;
import de.underdocx.tools.tree.nodepath.TextNodePath;

import java.util.StringTokenizer;

public class ParametersDetector implements TextDetector {

    public static final ParametersDetector INSTANCE = new ParametersDetector();

    private DetectionState state;
    private int counter = 0;
    private int startIndex = 0;
    private final StringBuilder readBuffer = new StringBuilder();

    private void init() {
        state = DetectionState.IDLE;
        counter = 0;
        startIndex = 0;
        readBuffer.delete(0, readBuffer.length());
    }

    private enum DetectionState {
        IDLE,
        DOLLAR_DETECTED,
        READING_CONTENT,
        ENDED
    }


    @Override
    public TextDetectionResult detect(TextNodePath path) {
        String text = path.fetchTextContent();
        Pair<Integer, Integer> indicies = parse(text);
        TextDetectionResult.TextDetectionResultType type = createResultType(indicies);
        return (type == TextDetectionResult.TextDetectionResultType.CONTAINS_TEXT)
                ? new TextDetectionResult(type, createArea(path, indicies))
                : new TextDetectionResult(type, null);
    }

    private TextDetectionResult.TextArea createArea(TextNodePath path, Pair<Integer, Integer> indices) {
        if (path == null || indices == null || indices.left == null || indices.right == null) return null;
        return new TextDetectionResult.TextArea(
                path,
                new TextNodePath.TextRange(
                        path.getTextPointer(indices.left),
                        path.getTextPointer(indices.right)),
                path.getTextNodeInterpreter());
    }

    private TextDetectionResult.TextDetectionResultType createResultType(Pair<Integer, Integer> indices) {
        return (indices == null) ? TextDetectionResult.TextDetectionResultType.NO_DETECTION
                : (indices.right == null ? TextDetectionResult.TextDetectionResultType.CONTAINS_START
                : TextDetectionResult.TextDetectionResultType.CONTAINS_TEXT);
    }

    private Pair<Integer, Integer> parse(String text) {
        init();
        StringTokenizer t = new StringTokenizer(text, "${}", true);
        while (t.hasMoreTokens() && state != DetectionState.ENDED) {
            String token = t.nextToken();
            switch (token) {
                case "$" -> parseDollar();
                case "{" -> parseOpen();
                case "}" -> parseClose();
                default -> parseContent(token);
            }
        }
        return switch (state) {
            case IDLE -> null;
            case DOLLAR_DETECTED, READING_CONTENT -> new Pair<>(startIndex, null);
            case ENDED -> new Pair<>(startIndex, readBuffer.length() - 1);
        };
    }

    private void parseDollar() {
        readBuffer.append("$");
        switch (state) {
            case IDLE, DOLLAR_DETECTED -> {
                state = DetectionState.DOLLAR_DETECTED;
                startIndex = readBuffer.length() - 1;
            }
        }
    }

    private void parseOpen() {
        readBuffer.append("{");
        switch (state) {
            case DOLLAR_DETECTED -> {
                state = DetectionState.READING_CONTENT;
                counter = 0;
            }
            case READING_CONTENT -> counter++;
        }
    }

    private void parseClose() {
        readBuffer.append("}");
        switch (state) {
            case DOLLAR_DETECTED -> state = DetectionState.IDLE;
            case READING_CONTENT -> {
                if (counter == 0) {
                    state = DetectionState.ENDED;
                } else {
                    counter--;
                }
            }
        }
    }

    private void parseContent(String s) {
        readBuffer.append(s);
        state = (state == DetectionState.DOLLAR_DETECTED) ? DetectionState.IDLE : state;
    }


}
