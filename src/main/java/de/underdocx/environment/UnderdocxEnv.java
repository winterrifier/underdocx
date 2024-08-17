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

package de.underdocx.environment;

import de.underdocx.environment.logger.Logger;
import de.underdocx.tools.common.Convenience;

/**
 * Global framework settings as singleton
 */
public class UnderdocxEnv {
    private UnderdocxEnv() {
    }

    private static final UnderdocxEnv instance = new UnderdocxEnv();

    public static UnderdocxEnv getInstance() {
        return instance;
    }

    public static boolean isLibreOfficeInstalled() {
        return getInstance().libreOfficeExecutable != null;
    }

    /*

        settings

     */

    public Logger logger = new Logger() {
    };

    public boolean isDebug = false;

    public String libreOfficeExecutable = Convenience.build(path -> {
        path.value = System.getenv("LIBREOFFICE");
        if (path.value == null || path.value.isBlank()) {
            path.value = System.getProperty("libreoffice");
        }
        if (path.value == null || path.value.isBlank()) {
            path.value = null;
        }
    });


}
