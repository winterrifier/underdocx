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
    ELEMENT_SPAN("text:span"),
    ELEMENT_SPACE("text:s"),
    ELEMENT_PARAGRAPH("text:p"),
    ELEMENT_TAB("text:tab"),
    ELEMENT_LINEBREAK("text:lineBreak"),
    ELEMENT_A("text:a"),

    ELEMENT_TEXT("office:text"),

    ATTRIBUTE_SPACE_COUNT("text:c");


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
}
