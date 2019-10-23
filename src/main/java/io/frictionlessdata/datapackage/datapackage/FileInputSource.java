package io.frictionlessdata.datapackage.datapackage;

import java.nio.file.Path;

public class FileInputSource implements InputSource<Path> {
    private Path source;

    public FileInputSource(Path inputPath) {
        source = inputPath;
    }

    @Override
    public Path getInput() {
        return source;
    }

    @Override
    public void setInput(Path input) {
        source = input;
    }
}
