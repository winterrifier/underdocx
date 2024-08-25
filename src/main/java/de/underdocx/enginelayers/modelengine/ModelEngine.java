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

package de.underdocx.enginelayers.modelengine;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.enginelayers.baseengine.BaseEngine;
import de.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import de.underdocx.enginelayers.baseengine.Selection;
import de.underdocx.enginelayers.modelengine.internal.MSelectionWrapper;
import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.enginelayers.modelengine.model.simple.MapModelNode;
import de.underdocx.enginelayers.modelengine.modelaccess.internal.DefaultModelAccess;
import org.w3c.dom.Node;

public class ModelEngine<C extends DocContainer<D>, D> extends BaseEngine {

    protected ModelNode model = new MapModelNode();
    protected ModelNode currentModelNode = model;

    public ModelEngine(DocContainer doc) {
        super(doc);
    }

    public void setModel(ModelNode model) {
        this.model = model;
        currentModelNode = model;
    }


    @Override
    protected Selection createSelection(PlaceholdersProvider provider, Node node) {
        Selection baseSelection = super.createSelection(provider, node);
        return new MSelectionWrapper(baseSelection, new ModelEngineModelAccess());
    }

    private class ModelEngineModelAccess extends DefaultModelAccess {
        public ModelEngineModelAccess() {
            super(() -> currentModelNode, newModelNode -> currentModelNode = newModelNode);
        }
    }

    public <X> BaseEngine<C, D> registerCommandHandler(PlaceholdersProvider<C, X, D> provider, MCommandHandler<C, X, D> commandHandler) {
        return super.registerCommandHandler(provider, commandHandler);
    }
}
