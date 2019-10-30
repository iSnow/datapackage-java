package io.frictionlessdata.datapackage;

import io.frictionlessdata.datapackage.exceptions.DataPackageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * 
 */
public class DataPackageTest {

    @Test
    @DisplayName("Must correctly read a well-formed ZIP-packaged DataPackage")
    public void testReadFromValidZipFile() throws Exception{
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
    public void testReadFromZipFileWithInvalidDatapackageFilenameInside() throws Exception{
        File inFile = getResourceZipFile("/testsuite-data/zip/invalid_filename_datapackage.zip");
        assertThrows(DataPackageException.class, () -> {
            DataPackage p = new DataPackage(inFile, false);
        });
    }

    @Test
    @DisplayName("Must correctly throw trying to read a DataPackage from a non-existing path")
    public void testReadFromInvalidZipFilePath() throws Exception{
        File inFile = getResourceZipFile("/invalid/path/does/not/exist/datapackage.zip");
        assertThrows(IOException.class, () -> {
            DataPackage p = new DataPackage(inFile, false);
        });
    }


    private static String getFileContents(String fileName) {
        try {
            // Get path of URL
            Path path = getResourcePath(fileName);
            return new String(Files.readAllBytes(path));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Path getResourcePath(String fileName) {
        try {
            // Create file-URL of source file:
            URL sourceFileUrl = DataPackageTest.class.getResource(fileName);
            // Get path of URL
            return Paths.get(sourceFileUrl.toURI());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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
    
    private List<String[]> getAllCityData(){
        List<String[]> expectedData  = new ArrayList();
        expectedData.add(new String[]{"libreville", "0.41,9.29"});
        expectedData.add(new String[]{"dakar", "14.71,-17.53"});
        expectedData.add(new String[]{"ouagadougou", "12.35,-1.67"});
        expectedData.add(new String[]{"barranquilla", "10.98,-74.88"});
        expectedData.add(new String[]{"rio de janeiro", "-22.91,-43.72"});
        expectedData.add(new String[]{"cuidad de guatemala", "14.62,-90.56"});
        expectedData.add(new String[]{"london", "51.50,-0.11"});
        expectedData.add(new String[]{"paris", "48.85,2.30"});
        expectedData.add(new String[]{"rome", "41.89,12.51"});
        
        return expectedData;
    }
    
    //TODO: come up with attribute edit tests:
    // Examples here: https://github.com/frictionlessdata/datapackage-py/blob/master/tests/test_datapackage.py
}
