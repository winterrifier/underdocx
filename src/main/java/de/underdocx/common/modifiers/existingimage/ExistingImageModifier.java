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

package de.underdocx.common.modifiers.existingimage;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.common.modifiers.Modifier;
import de.underdocx.enginelayers.baseengine.Selection;
import de.underdocx.enginelayers.baseengine.internal.placeholdersprovider.dollar.image.SimpleDollarImagePlaceholderData;
import de.underdocx.tools.common.Pair;

import java.net.URL;

public class ExistingImageModifier<C extends DocContainer<D>, D> implements Modifier<C, SimpleDollarImagePlaceholderData, D, ExistingImageModifierData> {


    @Override
    public void modify(Selection<C, SimpleDollarImagePlaceholderData, D> selection, ExistingImageModifierData modifierData) {
        SimpleDollarImagePlaceholderData placeholder = selection.getPlaceholderData();
        URL importImage = modifierData.getImageURL();
        Pair<Double, Double> importImageWidthHeight = SimpleDollarImagePlaceholderData.getDimension(importImage);
        placeholder.exchangeImage(modifierData.getImageURL());
        if (modifierData.isKeepWidth()) {
            String newHeightUnit = placeholder.getWidthUnit();
            double height = placeholder.getWidthValue() * importImageWidthHeight.right / importImageWidthHeight.left;
            placeholder.setHeight(height, newHeightUnit);
        } else {
            String newWidthUnit = placeholder.getHeightUnit();
            double width = placeholder.getHeightValue() * importImageWidthHeight.left / importImageWidthHeight.right;
            placeholder.setWidth(width, newWidthUnit);
        }
    }
}
