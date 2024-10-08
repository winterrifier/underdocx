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

package de.underdocx.enginelayers.baseengine.internal.placeholdersprovider.dollar.image;

import de.underdocx.environment.UnderdocxExecutionException;
import de.underdocx.tools.common.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public interface SimpleDollarImagePlaceholderData {

    String getWidthUnit();

    String getWidthAttr();

    String getHeightAttr();

    String getName();

    String getVariableName();

    double getWidthValue();

    String getHeightUnit();

    double getHeightValue();

    void setWidth(double value, String unit);

    void setHeight(double value, String unit);

    void exchangeImage(URL imageUrl);

    static Pair<Double, Double> getDimension(URL url) {
        try {
            BufferedImage bufferedImage = ImageIO.read(url);
            return new Pair<>((double) bufferedImage.getWidth(), (double) bufferedImage.getHeight());
        } catch (IOException e) {
            throw new UnderdocxExecutionException(e);
        }
    }


}
