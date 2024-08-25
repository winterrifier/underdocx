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

package de.underdocx.enginelayers.modelengine.modelpath.parser;

import de.underdocx.common.codec.Codec;
import de.underdocx.enginelayers.modelengine.modelpath.ModelPath;
import de.underdocx.enginelayers.modelengine.modelpath.elements.*;
import de.underdocx.tools.common.Convenience;
import de.underdocx.tools.common.Wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

public class ModelPathCodec implements Codec<ModelPath> {

    @Override
    public Optional<ModelPath> parse(String string) {
        try {
            return Optional.of(new ModelPath(ModelPathParser.parse(string)));
        } catch (ModelPathParseException e) {
            return Optional.empty();
        }
    }

    @Override
    public String getTextContent(ModelPath data) {
        return Convenience.buildString(builder -> {
            Wrapper<String> delim = new Wrapper<>("");
            data.getElements().forEach(element -> {
                builder.append(delim);
                builder.append(element.toString());
                delim.value = ".";
            });
        });
    }

    private static class ModelPathParser {

        private enum State {
            NORMAL,
            INDEX_STARTED,
            INDEX_ENDED;
        }

        private final String toParse;
        private String lastString = null;
        private final List<ModelPathElement> result = new ArrayList<>();
        private State state = State.NORMAL;


        private ModelPathParser(String toParse) {
            this.toParse = toParse;
        }

        public static List<ModelPathElement> parse(String toParse) throws ModelPathParseException {
            return new ModelPathParser(toParse).parse();
        }

        private List<ModelPathElement> parse() throws ModelPathParseException {
            StringTokenizer t = new StringTokenizer(toParse, "<^[].", true);
            while (t.hasMoreTokens()) {
                String token = t.nextToken();
                switch (token) {
                    case "<" -> parseBack();
                    case "^" -> parseRoot();
                    case "[" -> parseBeginIndex();
                    case "]" -> parseEndIndex();
                    case "." -> parseSeparator();
                    default -> parseToken(token);
                }
            }
            storeProperty();
            return result;
        }

        private void storeProperty() {
            if (lastString != null) {
                result.add(new PropertyModelPathElement(lastString));
                lastString = null;
            }
        }

        public void storeIndex() throws ModelPathParseException {
            if (lastString != null) {
                try {
                    result.add(new IndexModelPathElement(Integer.parseInt(lastString)));
                    lastString = null;
                } catch (Exception e) {
                    throw new ModelPathParseException("Expected index number", e);
                }
            }
        }

        private void parseBack() throws ModelPathParseException {
            switch (state) {
                case NORMAL, INDEX_ENDED -> {
                    storeProperty();
                    result.add(new BackModelPathElement());
                    state = State.NORMAL;
                }
                default -> throw new ModelPathParseException("Unexpected back character");
            }
        }

        private void parseRoot() throws ModelPathParseException {
            switch (state) {
                case NORMAL, INDEX_ENDED -> {
                    storeProperty();
                    result.add(new RootModelPathElement());
                    state = State.NORMAL;
                }
                default -> throw new ModelPathParseException("Unexpected root character");
            }
        }

        private void parseBeginIndex() throws ModelPathParseException {
            switch (state) {
                case NORMAL, INDEX_ENDED -> {
                    storeProperty();
                    state = State.INDEX_STARTED;
                }
                default -> throw new ModelPathParseException("Unexpected index start");
            }
        }

        private void parseEndIndex() throws ModelPathParseException {
            switch (state) {
                case INDEX_STARTED -> {
                    storeIndex();
                    state = State.INDEX_ENDED;
                }
                default -> throw new ModelPathParseException("Unexpected index end");
            }
        }

        private void parseSeparator() throws ModelPathParseException {
            switch (state) {
                case NORMAL, INDEX_ENDED -> {
                    storeProperty();
                    state = State.NORMAL;
                }
                default -> throw new ModelPathParseException("Unexpected separator");
            }
        }

        private void parseToken(String token) {
            lastString = token;
        }
    }
}
