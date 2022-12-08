package io.frictionlessdata.datapackage;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtil {

    public static File getTestDataDirectory()throws Exception {
        URL u = TestUtil.class.getResource("/fixtures/multi_data_datapackage.json");
        Path path = Paths.get(u.toURI());
        return path.getParent().getParent().toFile();
    }

    public static Path getBasePath() {
        try {
            String pathName = "/fixtures/multi_data_datapackage.json";
            Path sourceFileAbsPath = Paths.get(TestUtil.class.getResource(pathName).toURI());
            return sourceFileAbsPath.getParent();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Path getResourcePath (String fileName) {
        try {
            String locFileName = fileName;
            if (!fileName.startsWith("/")){
                locFileName = "/"+fileName;
            }
            // Create file-URL of source file:
            URL sourceFileUrl = TestUtil.class.getResource(locFileName);
            // Get path of URL
            return Paths.get(sourceFileUrl.toURI());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
