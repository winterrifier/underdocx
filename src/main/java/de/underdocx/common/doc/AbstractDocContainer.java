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

package de.underdocx.common.doc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractDocContainer<D> implements DocContainer<D> {

    protected D doc = null;

    public AbstractDocContainer() {
        doc = createEmptyDoc();
    }

    public AbstractDocContainer(InputStream is) throws IOException {
        this();
        load(is);
    }

    public AbstractDocContainer(File file) throws IOException {
        this();
        load(file);
    }

    public AbstractDocContainer(D doc) {
        this();
        setDocument(doc);
    }

    protected abstract D createEmptyDoc();

    @Override
    public D getDocument() {
        return doc;
    }

    @Override
    public void setDocument(D doc) {
        this.doc = doc;
    }

    public File createTmpFile(Long lifetime) throws IOException {
        File tmpFile = Files.createTempFile("tmpUri_", "." + getFileExtension()).toFile();
        if (lifetime != null) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    tmpFile.delete();
                }
            }, lifetime);
        }
        return tmpFile;
    }

    @Override
    public String createURI(Long lifetime) throws IOException {
        return createTmpFile(lifetime).toURI().toString();
    }


}
