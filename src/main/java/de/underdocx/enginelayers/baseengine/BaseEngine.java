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

package de.underdocx.enginelayers.baseengine;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.enginelayers.baseengine.internal.EngineAccessImpl;
import de.underdocx.enginelayers.baseengine.internal.PlaceholdersEnumerator;
import de.underdocx.enginelayers.baseengine.internal.SelectionImpl;
import de.underdocx.enginelayers.baseengine.modifiers.EngineListener;
import de.underdocx.environment.UnderdocxEnv;
import de.underdocx.tools.common.Pair;
import de.underdocx.tools.tree.enumerator.LookAheadEnumerator;
import de.underdocx.tools.tree.enumerator.SimpleLookAheadEnumerator;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static de.underdocx.tools.common.Convenience.*;

public class BaseEngine<C extends DocContainer<D>, D> implements Runnable {

    protected final C doc;

    protected int step = 0;
    protected boolean errDetected = false;
    protected boolean rescan = true;

    protected ArrayListValuedHashMap<PlaceholdersProvider<C, ?, D>, CommandHandler<C, ?, D>> registry = new ArrayListValuedHashMap<>();
    protected LinkedHashSet<EngineListener<C, D>> listeners = new LinkedHashSet<>();


    public BaseEngine(C doc) {
        this.doc = doc;
    }


    public <X> BaseEngine<C, D> registerCommandHandler(PlaceholdersProvider<C, X, D> provider, CommandHandler<C, X, D> commandHandler) {
        registry.put(provider, commandHandler);
        return this;
    }

    protected LookAheadEnumerator<Pair<PlaceholdersProvider<C, ?, D>, Node>> createPlaceholdersEnumerator() {
        return new SimpleLookAheadEnumerator<>(new PlaceholdersEnumerator<>(registry.keySet()));
    }

    protected CommandHandler.CommandHandlerResult findAndExecCommandHandler(PlaceholdersProvider<C, ?, D> provider, Selection<C, ?, D> selection) {
        return build(CommandHandler.CommandHandlerResult.IGNORED, result -> {
            for (CommandHandler<C, ?, D> commandHandler : registry.get(provider)) {
                result.value = commandHandler.tryExecuteCommand((Selection) selection);
                if (result.value != CommandHandler.CommandHandlerResult.IGNORED) {
                    UnderdocxEnv.getInstance().logger.trace("Step " + step + ": commandHandler " + commandHandler + " has been executed, rescan: " + rescan + ", (changed) node: " + selection.getNode());
                    break;
                }
            }
        });
    }

    protected Selection<C, ?, D> createSelection(PlaceholdersProvider<C, ?, D> provider, Node node, EngineAccess<C, D> engineAccess) {
        return new SelectionImpl<>(doc, node, provider, engineAccess);
    }

    protected void stepExecuted(Selection<C, ?, D> selection) {
        if (UnderdocxEnv.getInstance().isDebug) {
            try {
                String fileName = "Debug_" + step + "_" + System.currentTimeMillis() + "." + selection.getDocContainer().getFileExtension();
                String fileDir = System.getProperty("java.io.tmpdir");
                File tmpFile = new File(fileDir + "/" + fileName);
                UnderdocxEnv.getInstance().logger.trace("save debug file: " + tmpFile);
                selection.getDocContainer().save(tmpFile);
            } catch (IOException e) {
                UnderdocxEnv.getInstance().logger.error("Failed to create debug file", e);
            }

        }
    }

    public void run() {
        try {
            runUncatched();
        } catch (Exception e) {
            errDetected("Exception during engine execution", e);
        }
    }

    protected void errDetected(String message, Exception e) {
        UnderdocxEnv.getInstance().logger.error(message, e);
        errDetected = true;
    }

    protected void reactOnExecutionResult(CommandHandler.CommandHandlerResult executionResult, Selection<C, ?, D> selection) {
        switch (executionResult) {
            case IGNORED -> errDetected("No Command handler found four " + selection.getNode(), null);
            case EXECUTED -> stepExecuted(selection);
            case EXECUTED_RESCAN_REQUIRED -> {
                stepExecuted(selection);
                rescan = true;
            }
            default -> errDetected("Unexpected command result", null);
        }
    }

    protected void runUncatched() {
        while (rescan && !errDetected) {
            LookAheadEnumerator<Pair<PlaceholdersProvider<C, ?, D>, Node>> enumerator = createPlaceholdersEnumerator();
            List<Node> visited = new ArrayList<>();
            rescan = false;
            EngineAccess<C, D> engineAccess = new EngineAccessImpl<>(listeners, () -> rescan = true, enumerator, visited);
            while (!rescan && !errDetected && enumerator.hasNext()) {
                step++;
                Pair<PlaceholdersProvider<C, ?, D>, Node> placeholder = enumerator.next();
                Selection<C, ?, D> selection = createSelection(placeholder.left, placeholder.right, engineAccess);
                CommandHandler.CommandHandlerResult executionResult = findAndExecCommandHandler(placeholder.left, selection);
                reactOnExecutionResult(executionResult, selection);
                visited.add(selection.getNode());
            }

            listeners.forEach(listener -> {
                if (!errDetected && !rescan) {
                    listener.eodReached(doc, engineAccess);
                }
            });
        }
    }
}
