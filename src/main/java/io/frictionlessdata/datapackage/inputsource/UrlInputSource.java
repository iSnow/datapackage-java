package io.frictionlessdata.datapackage.inputsource;

import java.net.URL;

public class UrlInputSource implements InputSource<URL> {
    private URL source;

    public UrlInputSource(URL inputUrl) {
        source = inputUrl;
    }

    @Override
    public URL getInput() {
        return source;
    }

    @Override
    public void setInput(URL input) {
        source = input;
    }


}
