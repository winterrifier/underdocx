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

package de.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder;

public interface DeletePlaceholderModifierData {

    static DeletePlaceholderModifierData DEFAULT = new Simple(Strategy.DELETE_BLANK_PARAGRAPH, true);

    enum Strategy {
        KEEP_PARAGRAPH,
        DELETE_EMPTY_PARAGRAPH,
        DELETE_BLANK_PARAGRAPH,
        DELETE_PARAGRAPH
    }

    Strategy getDeleteStrategy();

    boolean copyPageStyle();

    class Simple implements DeletePlaceholderModifierData {

        public Simple(Strategy strategy, boolean copyPageStyle) {
            this.strategy = strategy;
            this.copyPageStyle = copyPageStyle;
        }

        private Strategy strategy;
        private boolean copyPageStyle;

        @Override
        public Strategy getDeleteStrategy() {
            return strategy;
        }

        @Override
        public boolean copyPageStyle() {
            return copyPageStyle;
        }
    }

}
