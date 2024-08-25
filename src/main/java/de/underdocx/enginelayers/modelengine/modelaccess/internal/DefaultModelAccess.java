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

package de.underdocx.enginelayers.modelengine.modelaccess.internal;

import de.underdocx.enginelayers.modelengine.internal.modelpath.ModelPath;
import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.enginelayers.modelengine.modelaccess.ModelAccess;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefaultModelAccess implements ModelAccess {

    private final Consumer<ModelNode> listener;
    private final Supplier<ModelNode> modelProvider;


    public DefaultModelAccess(Supplier<ModelNode> modelProvider, Consumer<ModelNode> modelSetListener) {
        this.modelProvider = modelProvider;
        this.listener = modelSetListener;
    }

    @Override
    public ModelNode getCurrentModelNode() {
        return modelProvider.get();
    }

    @Override
    public Optional<ModelNode> interpret(String path, boolean setAsCurrent) {
        Optional<ModelNode> result = ModelPath.interpret(path, getCurrentModelNode());
        if (result.isPresent() && setAsCurrent) {
            listener.accept(result.get());
        }
        return result;
    }
}
