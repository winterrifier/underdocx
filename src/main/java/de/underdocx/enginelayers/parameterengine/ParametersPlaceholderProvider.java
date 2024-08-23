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

package de.underdocx.enginelayers.parameterengine;

import de.underdocx.common.doc.odf.OdfContainer;
import de.underdocx.enginelayers.baseengine.internal.placeholdersprovider.AbstractTextualPlaceholdersProvider;
import de.underdocx.enginelayers.parameterengine.internal.ParametersPlaceholderToolkit;
import org.odftoolkit.odfdom.doc.OdfDocument;

public class ParametersPlaceholderProvider<C extends OdfContainer<D>, D extends OdfDocument> extends AbstractTextualPlaceholdersProvider<C, ParametersPlaceholderData, D> {
    public ParametersPlaceholderProvider(OdfContainer<?> doc) {
        super(doc, ParametersPlaceholderToolkit.ODF_INSTANCE);
    }

}
