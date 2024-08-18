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

package de.underdocx.enginelayers.baseengine.internal.placeholdersprovider;

import de.underdocx.common.doc.odf.OdfContainer;
import de.underdocx.common.placeholder.EncapsulatedNodesExtractor;
import de.underdocx.common.placeholder.PlaceholderCodec;
import de.underdocx.common.placeholder.TextualPlaceholderToolkit;
import de.underdocx.common.placeholder.basic.extraction.RegexExtractor;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.TextNodeInterpreter;
import de.underdocx.tools.common.Regex;
import org.odftoolkit.odfdom.doc.OdfDocument;

public class SimpleDollarPlaceholdersProvider<C extends OdfContainer<D>, D extends OdfDocument> extends AbstractTextualPlaceholdersProvider<C, String, D> {

    public static final Regex regex = new Regex("\\$\\w+");
    public static final EncapsulatedNodesExtractor defaultExtractor = createExtractor(new OdfTextNodeInterpreter());
    public static final PlaceholderCodec<String> codec = new PlaceholderCodec<>() {
        @Override
        public String parse(String string) {
            return string.substring(1);
        }

        @Override
        public String getTextContent(String data) {
            return "$" + data;
        }
    };

    public SimpleDollarPlaceholdersProvider() {
        super(new TextualPlaceholderToolkit<>(defaultExtractor, codec));
    }

    public SimpleDollarPlaceholdersProvider(TextNodeInterpreter interpreter) {
        super(new TextualPlaceholderToolkit<>(createExtractor(interpreter), codec));
    }

    public static EncapsulatedNodesExtractor createExtractor(TextNodeInterpreter interpreter) {
        return new RegexExtractor(regex, interpreter);
    }
}
