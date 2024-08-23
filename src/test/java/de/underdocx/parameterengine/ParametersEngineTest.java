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

package de.underdocx.parameterengine;

import de.underdocx.AbstractOdtTest;
import de.underdocx.DefaultODTEngine;
import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.enginelayers.baseengine.CommandHandler;
import org.junit.jupiter.api.Test;

import static de.underdocx.tools.common.Convenience.build;

public class ParametersEngineTest extends AbstractOdtTest {

    @Test
    public void testParametersEngine() {
        OdtContainer doc = new OdtContainer("ABC ${Key att1:\"value1\"} XYZ");
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.registerParametersCommandHandler(
                selection -> build(CommandHandler.CommandHandlerResult.EXECUTED,
                        w -> selection.getPlaceholderToolkit().ifPresent(
                                toolkit -> toolkit.replacePlaceholderWithText(
                                        selection.getNode(),
                                        selection.getPlaceholderData().getStringAttribute("att1").get()))));
        engine.run();
        assertContains(doc, "ABC value1 XYZ");
        assertNoPlaceholders(doc);
    }
}
