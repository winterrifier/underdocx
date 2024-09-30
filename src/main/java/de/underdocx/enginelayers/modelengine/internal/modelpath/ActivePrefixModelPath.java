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

package de.underdocx.enginelayers.modelengine.internal.modelpath;

import de.underdocx.enginelayers.modelengine.internal.modelpath.elements.ModelPathElement;
import de.underdocx.enginelayers.modelengine.internal.modelpath.elements.ModelPathElementType;
import de.underdocx.enginelayers.modelengine.internal.modelpath.elements.PropertyModelPathElement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ActivePrefixModelPath extends ModelPath {

    private Supplier<String> prefixSupplier = null;

    public ActivePrefixModelPath(Supplier<String> prefixSupplier, List<ModelPathElement> elements) {
        super(elements);
        setPrefix(prefixSupplier);
    }

    public ActivePrefixModelPath(Supplier<String> prefixSupplier, ModelPath modelPath) {
        this(prefixSupplier, modelPath.elements);
    }

    public ActivePrefixModelPath(Supplier<String> prefixSupplier, String pathToParse) {
        this(prefixSupplier, new ModelPath(pathToParse));
    }

    public ActivePrefixModelPath(ActivePrefixModelPath toClone) {
        this(toClone.prefixSupplier, new ArrayList<>(toClone.elements));
    }


    public void setPrefix(Supplier<String> prefixSupplier) {
        this.prefixSupplier = prefixSupplier;
    }

    public void setPrefix(String prefix) {
        setPrefix(() -> prefix);
    }

    @Override
    public void interpret(String path) {
        List<ModelPathElement> subPath = new ModelPath(path).getElements();
        String prefix = prefixSupplier != null ? prefixSupplier.get() : null;
        if (prefix != null
                && subPath.size() > 0
                && subPath.get(0).getType() == ModelPathElementType.PROPERTY
                && ((PropertyModelPathElement) subPath.get(0)).getProperty().equals(prefix)) {
            subPath.remove(0);
        }
        super.interpret(subPath);
    }

    public ModelPath clone() {
        return new ActivePrefixModelPath(this);
    }

}
