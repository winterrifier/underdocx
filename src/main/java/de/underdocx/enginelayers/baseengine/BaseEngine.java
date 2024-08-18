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
import de.underdocx.enginelayers.baseengine.internal.BaseSelection;
import de.underdocx.environment.UnderdocxEnv;
import de.underdocx.tools.common.Pair;
import de.underdocx.tools.tree.Enumerator;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;

import static de.underdocx.tools.common.Convenience.build;

public class BaseEngine<C extends DocContainer<D>, D> implements Runnable {

    protected final C doc;

    protected int step = 0;
    protected boolean errDetected = false;
    protected boolean rescan = true;

    public BaseEngine(C doc) {
        this.doc = doc;
    }

    protected ArrayListValuedHashMap<PlaceholdersProvider<C, ?, D>, CommandHandler<C, ?, D>> registry = new ArrayListValuedHashMap<>();

    public <X> BaseEngine<C, D> registerCommandHandler(PlaceholdersProvider<C, X, D> provider, CommandHandler<C, X, D> commandHandler) {
        registry.put(provider, commandHandler);
        return this;
    }

    protected Enumerator<Pair<PlaceholdersProvider<C, ?, D>, Node>> createPlaceholdersEnumerator() {
        return new PlaceholdersEnumerator<>(registry.keySet());
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

    protected Selection<C, ?, D> createSelection(PlaceholdersProvider<C, ?, D> provider, Node node) {
        return new BaseSelection(doc, node, provider.getPlaceholderData(node), provider.getPlaceholderToolkit().orElse(null));
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
            Enumerator<Pair<PlaceholdersProvider<C, ?, D>, Node>> enumerator = createPlaceholdersEnumerator();
            rescan = false;
            while (!rescan && !errDetected && enumerator.hasNext()) {
                step++;
                Pair<PlaceholdersProvider<C, ?, D>, Node> placeholder = enumerator.next();
                Selection<C, ?, D> selection = createSelection(placeholder.left, placeholder.right);
                CommandHandler.CommandHandlerResult executionResult = findAndExecCommandHandler(placeholder.left, selection);
                reactOnExecutionResult(executionResult, selection);
            }
        }
    }
}
