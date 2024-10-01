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

package de.underdocx.enginelayers.modelengine.modelpath;

import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.enginelayers.modelengine.modelpath.elements.ModelPathElement;
import de.underdocx.enginelayers.modelengine.modelpath.parser.ModelPathCodec;
import de.underdocx.environment.UnderdocxExecutionException;
import de.underdocx.tools.common.Convenience;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModelPath {

    private static final ModelPathCodec codec = new ModelPathCodec();

    protected List<ModelPathElement> elements = new ArrayList<>();

    public ModelPath(List<ModelPathElement> elements) {
        this.elements.addAll(elements);
    }

    public ModelPath(ModelPath toClone) {
        this.elements.addAll(toClone.elements);
    }

    public ModelPath() {
    }

    public ModelPath(String toParse) {
        this.elements.addAll(codec.parse(toParse).orElseThrow(
                () -> new UnderdocxExecutionException("can't parse modelpath: " + toParse)
        ).getElements());
    }

    public List<ModelPathElement> getElements() {
        return new ArrayList<>(elements);
    }

    public String toString() {
        return codec.getTextContent(this);
    }

    public static Optional<ModelNode> interpret(String path, ModelNode node) {
        return new ModelPath(path).interpret(node);
    }

    public Optional<ModelNode> interpret(ModelNode rootModelNode) {
        return Convenience.buildOptional(rootModelNode, w -> {
            elements.forEach(element -> {
                if (w.value != null) {
                    w.value = element.interpret(w.value).orElse(null);
                }
            });
        });
    }

    public void interpret(String path) {
        List<ModelPathElement> subPath = new ModelPath(path).getElements();
        interpret(subPath);
    }

    protected void interpret(List<ModelPathElement> subPath) {
        subPath.forEach(subElement -> subElement.interpret(elements));
    }

    public ModelPath clone() {
        return new ModelPath(this);
    }


}
