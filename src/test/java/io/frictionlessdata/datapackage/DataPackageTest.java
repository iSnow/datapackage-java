package io.frictionlessdata.datapackage;

import io.frictionlessdata.datapackage.exceptions.DataPackageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * 
 */
class DataPackageTest {

    @Test
    @DisplayName("Must correctly read a well-formed ZIP-packaged DataPackage")
    void testReadFromValidZipFile() throws Exception{
        File inFile = getResourceZipFile("/testsuite-data/zip/countries-and-currencies.zip");
        new DataPackage(inFile, false);
    }

    /*
    @Test
    public void testSaveToAndReadFromZipFile() throws Exception{
        File createdFile = folder.newFile("test_save_datapackage.zip");
        
        // save the datapackage in zip file.
        PackageDescriptor savedPackage = this.getDataPackageFromFilePath(true);
        savedPackage.saveJson(createdFile.getAbsolutePath());
        
        // Read the datapckage we just saved in the zip file.
        PackageDescriptor readPackage = new PackageDescriptor(createdFile.getAbsolutePath(), false);
        
        // Check if two data packages are have the same key/value pairs.
        // For some reason JSONObject.similar() is not working even though both
        // json objects are exactly the same. Just compare lengths then.
        Assert.assertEquals(readPackage.getJson().toString().length(), savedPackage.getJson().toString().length());
    }
    */
    @Test
    @DisplayName("Must correctly throw reading an invalid ZIP-packaged DataPackage where the Descriptor's file name isn't 'datapackage.json'")
    void testReadFromZipFileWithInvalidDatapackageFilenameInside() throws Exception{
        File inFile = getResourceZipFile("/testsuite-data/zip/invalid_filename_datapackage.zip");
        assertThrows(DataPackageException.class, () -> new DataPackage(inFile, false));
    }

    @Test
    @DisplayName("Must correctly throw trying to read a DataPackage from a non-existing path")
    void testReadFromInvalidZipFilePath() throws Exception{
        File inFile = getResourceZipFile("/invalid/path/does/not/exist/datapackage.zip");
        assertThrows(IOException.class, () -> new DataPackage(inFile, false));
    }

    private static File getResourceZipFile(String fileName) throws URISyntaxException {
        try {
            // Create file-URL of source file:
            URL sourceFileUrl = DataPackageTest.class.getResource(fileName);
            // normal case: resolve against resources path
            Path path = Paths.get(sourceFileUrl.toURI());
            return path.toFile();
        } catch (NullPointerException ex) {
            // special case for invalid path test
            return new File (fileName);
        }
    }

    //TODO: come up with attribute edit tests:
    // Examples here: https://github.com/frictionlessdata/datapackage-py/blob/master/tests/test_datapackage.py
}
