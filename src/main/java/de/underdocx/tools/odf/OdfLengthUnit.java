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

import java.util.Optional;

public enum OdfLengthUnit {
    CM("cm"),
    MM("mm"),
    IN("in"),
    PT("pt"),
    PC("pc"),
    PX("px"),
    EM("em");


    private final String unitStr;

    OdfLengthUnit(String str) {
        this.unitStr = str;
    }

    public String toString() {
        return this.unitStr;
    }

    public static Optional<OdfLengthUnit> parseValue(String value) {
        for (OdfLengthUnit element : values()) {
            if (value.endsWith(element.unitStr)) {
                return Optional.of(element);
            }
        }
        return Optional.empty();
    }
}
