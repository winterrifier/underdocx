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
import de.underdocx.enginelayers.baseengine.CommandHandler;
import de.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import de.underdocx.enginelayers.baseengine.internal.commands.SimpleDollarImageReplaceCommand;
import de.underdocx.enginelayers.baseengine.internal.commands.SimpleReplaceFunctionCommand;
import de.underdocx.enginelayers.baseengine.internal.placeholdersprovider.dollar.SimpleDollarPlaceholdersProvider;
import de.underdocx.enginelayers.baseengine.internal.placeholdersprovider.dollar.image.SimpleDollarImagePlaceholdersProvider;
import de.underdocx.environment.UnderdocxExecutionException;
import de.underdocx.tools.common.Regex;
import org.odftoolkit.odfdom.doc.OdfTextDocument;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public class DefaultODTEngine implements Runnable {

    private final SimpleDollarImagePlaceholdersProvider simpleDollarImage;
    private final SimpleDollarPlaceholdersProvider<OdtContainer, OdfTextDocument> simpleDollar;

    private final BaseEngine<OdtContainer, OdfTextDocument> engine;

    public DefaultODTEngine(OdtContainer doc) {
        this.engine = new BaseEngine<>(doc);
        simpleDollar = new SimpleDollarPlaceholdersProvider<>(doc);
        simpleDollarImage = new SimpleDollarImagePlaceholdersProvider(doc);
    }

    public <P> void registerCommandHandler(PlaceholdersProvider<OdtContainer, P, OdfTextDocument> provider, CommandHandler<OdtContainer, P, OdfTextDocument> commandHandler) {
        this.engine.registerCommandHandler(provider, commandHandler);
    }

    public void registerSimpleDollarReplaceFunction(Function<String, Optional<String>> replaceFunction) {
        registerCommandHandler(simpleDollar, new SimpleReplaceFunctionCommand<>(replaceFunction));
    }

    public void registerSimpleDollarReplacement(String variableName, String replacement) {
        this.registerSimpleDollarReplacement(Pattern.compile(Pattern.quote(variableName)), replacement);
    }

    public void registerSimpleDollarReplacement(Pattern pattern, String replacement) {
        this.registerSimpleDollarReplaceFunction(variableName ->
                new Regex(pattern).matches(variableName) ? Optional.of(replacement) : Optional.empty()
        );
    }

    public void registerSimpleDollarImageReplaceFunction(Function<String, Optional<URL>> replaceFunction, boolean keepWidth) {
        registerCommandHandler(simpleDollarImage, new SimpleDollarImageReplaceCommand<>(replaceFunction, keepWidth));
    }

    public void registerSimpleDollarImageReplacement(String variableName, String imageURL, boolean keepWidth) {
        this.registerSimpleDollarImageReplacement(Pattern.compile(Pattern.quote(variableName)), imageURL, keepWidth);
    }

    public void registerSimpleDollarImageReplacement(Pattern pattern, String imageURL, boolean keepWidth) {
        this.registerSimpleDollarImageReplaceFunction(variableName ->
                {
                    try {
                        return new Regex(pattern).matches(variableName) ? Optional.of(new URL(imageURL)) : Optional.empty();
                    } catch (MalformedURLException e) {
                        throw new UnderdocxExecutionException(e);
                    }
                }, keepWidth
        );
    }


    @Override
    public void run() {
        engine.run();
    }
}
