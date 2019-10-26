package io.frictionlessdata.datapackage.inputsource;

import java.util.zip.ZipFile;

public class ZipInputSource implements InputSource<ZipFile> {
    private ZipFile source;

    public ZipInputSource(ZipFile zipFile) {
        source = zipFile;
    }

    @Override
    public ZipFile getInput() {
        return source;
    }

    @Override
    public void setInput(ZipFile input) {
        source = input;
    }


}
