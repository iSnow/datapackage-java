package io.frictionlessdata.datapackage.inputsource;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

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

    @Override
    public String getDescriptorContent() throws Exception{
        try (BufferedReader reader = Files.newBufferedReader(source)) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException ex) {
            throw ex;
        }
    }
}
