package io.frictionlessdata.datapackage;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestHelpers {


    static PackageDescriptor getDataPackageFromFilePath(String datapackageFilePath, boolean strict) throws Exception{
        // Get string content version of source file.
        String jsonString = getFileContents(datapackageFilePath);

        // Create DataPackage instance from jsonString

        return new PackageDescriptor(jsonString, strict);
    }

    static String getFileContents(String fileName) {
        try {
            // Create file-URL of source file:
            URL sourceFileUrl = TestHelpers.class.getResource(fileName);
            // Get path of URL
            Path path = Paths.get(sourceFileUrl.toURI());
            return new String(Files.readAllBytes(path));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
