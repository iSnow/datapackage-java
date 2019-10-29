package io.frictionlessdata.datapackage.inputsource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

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

    @Override
    public String getDescriptorContent() throws Exception {
        InputStream inStream = source.openStream();
        BufferedReader rdr = new BufferedReader(new InputStreamReader(inStream));
        String content = rdr.lines().collect(Collectors.joining("\n"));
        inStream.close();
        rdr.close();
        return content;
    }


}
