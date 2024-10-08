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

package de.underdocx.tools.odf;

public enum OdfNodeType {
    SPAN_ELEMENT("text:span"),
    PARAGRAPH_ELEMENT("text:p"),
    TAB_ELEMENT("text:tab"),
    LINEBREAK_ELEMENT("text:lineBreak"),
    A_ELEMENT("text:a"),

    SPACE_ELEMENT("text:s"),
    SPACE_COUNT_ATTRIBUTE("text:c"),

    TEXT_ELEMENT("office:text"),

    FRAME_ELEMENT("draw:frame"),
    FRAME_NAME_ATTRIBUTE("draw:name"),
    FRAME_X_ATTRIBUTE("svg:x"),
    FRAME_Y_ATTRIBUTE("svg:y"),
    FRAME_WIDTH_ATTRIBUTE("svg:width"),
    FRAME_HEIGHT_ATTRIBUTE("svg:hight"),


    IMAGE_ELEMENT("draw:image"),
    IMAGE_HREF_ATTRIBUTE("xlink:href");


    private final String name;

    OdfNodeType(String name) {
        this.name = name;
    }

    public String getElementName() {
        return name;
    }

    public String getPureName() {
        int lastIndex = name.lastIndexOf(":");
        return lastIndex >= 0 ? name.substring(lastIndex + 1) : name;
    }

    public String toString() {
        return name;
    }
}
