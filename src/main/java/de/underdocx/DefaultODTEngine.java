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

package de.underdocx;

import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.enginelayers.baseengine.BaseEngine;
import de.underdocx.enginelayers.baseengine.internal.commands.SimpleReplaceFunctionCommand;
import de.underdocx.enginelayers.baseengine.internal.placeholdersprovider.SimpleDollarPlaceholdersProvider;
import de.underdocx.tools.common.Regex;
import org.odftoolkit.odfdom.doc.OdfTextDocument;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public class DefaultODTEngine implements Runnable {

    private static final SimpleDollarPlaceholdersProvider<OdtContainer, OdfTextDocument> simpleDollar = new SimpleDollarPlaceholdersProvider<>();

    private BaseEngine<OdtContainer, OdfTextDocument> engine;

    public DefaultODTEngine(OdtContainer doc) {
        this.engine = new BaseEngine<>(doc);
    }

    public void registerSimpleDollarReplaceFunction(Function<String, Optional<String>> replaceFunction) {
        engine.registerCommandHandler(simpleDollar, new SimpleReplaceFunctionCommand<>(replaceFunction));
    }

    public void registerSimpleDollarReplacement(String variableName, String replacement) {
        this.registerSimpleDollarReplacement(Pattern.compile(Pattern.quote(variableName)), replacement);
    }

    public void registerSimpleDollarReplacement(Pattern pattern, String replacement) {
        this.registerSimpleDollarReplaceFunction(variableName ->
                new Regex(pattern).matches(variableName) ? Optional.of(replacement) : Optional.empty()
        );
    }


    @Override
    public void run() {
        engine.run();
    }
}
