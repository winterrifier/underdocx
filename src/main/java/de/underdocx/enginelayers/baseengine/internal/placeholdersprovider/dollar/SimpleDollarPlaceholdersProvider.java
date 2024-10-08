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

package de.underdocx.enginelayers.baseengine.internal.placeholdersprovider.dollar;

import de.underdocx.common.codec.Codec;
import de.underdocx.common.doc.odf.OdfContainer;
import de.underdocx.common.placeholder.EncapsulatedNodesExtractor;
import de.underdocx.common.placeholder.TextualPlaceholderToolkit;
import de.underdocx.common.placeholder.basic.extraction.RegexExtractor;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.TextNodeInterpreter;
import de.underdocx.enginelayers.baseengine.internal.placeholdersprovider.AbstractTextualPlaceholdersProvider;
import de.underdocx.tools.common.Regex;
import org.odftoolkit.odfdom.doc.OdfDocument;

import java.util.Optional;

public class SimpleDollarPlaceholdersProvider<C extends OdfContainer<D>, D extends OdfDocument> extends AbstractTextualPlaceholdersProvider<C, String, D> {

    public static final Regex regex = new Regex("\\$\\w+");
    public static final EncapsulatedNodesExtractor defaultExtractor = createExtractor(new OdfTextNodeInterpreter());
    public static final Codec<String> codec = new Codec<>() {
        @Override
        public Optional<String> parse(String string) {
            if (string.startsWith("$"))
                return Optional.of(string.substring(1));
            else return Optional.empty();
        }

        @Override
        public String getTextContent(String data) {
            return "$" + data;
        }
    };

    public SimpleDollarPlaceholdersProvider(C doc) {
        super(doc, new TextualPlaceholderToolkit<>(defaultExtractor, codec));
    }

    public SimpleDollarPlaceholdersProvider(C doc, TextNodeInterpreter interpreter) {
        super(doc, new TextualPlaceholderToolkit<>(createExtractor(interpreter), codec));
    }

    public static EncapsulatedNodesExtractor createExtractor(TextNodeInterpreter interpreter) {
        return new RegexExtractor(regex, interpreter);
    }
}
