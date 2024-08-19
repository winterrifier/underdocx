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

import de.underdocx.common.doc.odf.OdfContainer;
import de.underdocx.environment.UnderdocxExecutionException;
import de.underdocx.tools.common.Pair;
import de.underdocx.tools.common.Regex;
import de.underdocx.tools.odf.OdfLengthUnit;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class SimpleDollarImagePlaceholderDataOdf implements SimpleDollarImagePlaceholderData {


    private final OdfContainer<?> doc;
    private Pair<DrawFrameElement, DrawImageElement> elements = null;
    private final static Regex number = new Regex("[0-9]+(\\.[0-9]+)?");

    public SimpleDollarImagePlaceholderDataOdf(OdfContainer<?> doc, Pair<DrawFrameElement, DrawImageElement> odfElements) {
        this.doc = doc;
        this.elements = odfElements;
    }

    public String getWidthUnit() {
        return OdfLengthUnit.parseValue(getWidthAttr()).get().toString();
    }

    public String getWidthAttr() {
        return elements.left.getSvgWidthAttribute();
    }

    public String getHeightAttr() {
        return elements.left.getSvgHeightAttribute();
    }

    private void setWidthAttr(String value) {
        elements.left.setSvgWidthAttribute(value);
    }

    private void setHeightAttr(String value) {
        elements.left.setSvgHeightAttribute(value);
    }

    public String getName() {
        return elements.left.getDrawNameAttribute();
    }

    public String getVariableName() {
        String name = getName();
        return name.startsWith("$") ? name.substring(1) : name;
    }

    private void setName(String name) {
        elements.left.setDrawNameAttribute(name);
    }

    public double getWidthValue() {
        return Double.parseDouble(number.findFirstAsString(getWidthAttr()).get());
    }

    public String getHeightUnit() {
        return OdfLengthUnit.parseValue(getHeightAttr()).get().toString();
    }

    public double getHeightValue() {
        return Double.parseDouble(number.findFirstAsString(getHeightAttr()).get());
    }

    public void setWidth(double value, String unit) {
        setWidthAttr(value + unit);
    }

    public void setHeight(double value, String unit) {
        setHeightAttr(value + unit);
    }

    public void exchangeImage(URL imageUri) {
        OdfDrawImage image = new OdfDrawImage(doc.getContentDom());
        try {
            String packagePath = image.newImage(imageUri.toURI());
            elements.right.setXlinkHrefAttribute(packagePath);
            setName(packagePath);
            // TODO set mimetype? String mimeType = OdfFileEntry.getMediaTypeString(imageUri.toString());
        } catch (Exception e) {
            throw new UnderdocxExecutionException(e);
        }
    }

    public static Pair<Double, Double> getDimension(URL url) {
        try {
            BufferedImage bufferedImage = ImageIO.read(url);
            return new Pair<>((double) bufferedImage.getWidth(), (double) bufferedImage.getHeight());
        } catch (IOException e) {
            throw new UnderdocxExecutionException(e);
        }
    }


}
