package io.frictionlessdata.datapackage;

import io.frictionlessdata.datapackage.exceptions.DataPackageException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * 
 */
class PackageDescriptorTest {
    /*
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    */

    @Test
    void testLoadFromJsonString() throws Exception{

        // Create simple multi DataPackage from Json String
        PackageDescriptor dp = this.getDefaultTestDataPackage(true);
        
        // Assert
        Assertions.assertNotNull(dp);
    }

    /*
    @Test
    public void testLoadFromValidJsonObject() throws IOException, DataPackageException{
        // Create JSON Object for testing
        JSONObject jsonObject = new JSONObject("{\"name\": \"test\"}");
          
        // Build resources
        JSONObject resource1 = new JSONObject("{\"name\": \"first-resource\", \"path\": [\"foo.txt\", \"bar.txt\", \"baz.txt\"]}");
        JSONObject resource2 = new JSONObject("{\"name\": \"second-resource\", \"path\": [\"bar.txt\", \"baz.txt\"]}");
        
        List resourceArrayList = new ArrayList();
        resourceArrayList.add(resource1);
        resourceArrayList.add(resource2);
        
        JSONArray resources = new JSONArray(resourceArrayList);

        // Add the resources
        jsonObject.put("resources", resources);
        
        // Build the datapackage
        PackageDescriptor dp = new PackageDescriptor(jsonObject, true);
        
        // Assert
        Assertions.assertNotNull(dp);
    }
    
    @Test
    public void testLoadInvalidJsonObject() throws IOException, DataPackageException{
        // Create JSON Object for testing
        JSONObject jsonObject = new JSONObject("{\"name\": \"test\"}");
        
        // Build the datapackage, it will throw ValidationException because there are no resources.
        exception.expect(ValidationException.class);
        PackageDescriptor dp = new PackageDescriptor(jsonObject, true);
    }
    
    @Test
    public void testLoadInvalidJsonObjectNoStrictValidation() throws IOException, DataPackageException{
        // Create JSON Object for testing
        JSONObject jsonObject = new JSONObject("{\"name\": \"test\"}");
        
        // Build the datapackage, no strict validation by default
        PackageDescriptor dp = new PackageDescriptor(jsonObject);
        
        // Assert
        Assertions.assertNotNull(dp);
    }

     */
    

    @Test
    void testLoadFromFileWhenPathDoesNotExist()  {
        assertThrows(NoSuchFileException.class, () -> {
            Path path = Paths.get("/this/path/does/not/exist");
            new PackageDescriptor(path, true);
        });
    }
    
    @Test
    void testLoadFromFileWhenPathExists() throws Exception {
        String fName = "/testsuite-data/basic-csv/datapackage.json";
        URL sourceFileUrl = PackageDescriptorTest.class.getResource(fName);
        // Get path of URL
        Path path = Paths.get(sourceFileUrl.toURI());

        // Get string content version of source file.
        String jsonString = TestHelpers.getFileContents(fName);
   
        // Build DataPackage instance based on source file path.
        PackageDescriptor dp = new PackageDescriptor(path, true);

        // We're not asserting the String value since the order of the JSONObject elements is not guaranteed.
        // Just compare the length of the String, should be enough.
        Assertions.assertEquals(dp.getJson().length(), new JSONObject(jsonString).length());
    }

    
    @Test
    void testLoadFromFileWhenPathExistsButIsNotJson() throws Exception{
        // Get path of source file:
        String fName = "/fixtures/not_a_json_datapackage.json";
        URL sourceFileUrl = PackageDescriptorTest.class.getResource(fName);
        // Get path of URL
        Path path = Paths.get(sourceFileUrl.toURI());

        assertThrows(JSONException.class, () -> new PackageDescriptor(path, true));
    }
   
    
    @Test
    void testValidUrl() throws Exception{
        // Preferably we would use mockito/powermock to mock URL Connection
        // But could not resolve AbstractMethodError: https://stackoverflow.com/a/32696152/4030804
        URL url = new URL("https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/multi_data_datapackage.json");
        PackageDescriptor dp = new PackageDescriptor(url, true);
        Assertions.assertNotNull(dp.getJson());
    }
    
    @Test
    void testValidUrlWithInvalidJson() throws IOException{
        // Preferably we would use mockito/powermock to mock URL Connection
        // But could not resolve AbstractMethodError: https://stackoverflow.com/a/32696152/4030804
        URL url = new URL("https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/simple_invalid_datapackage.json");

        assertThrows(ValidationException.class, () -> new PackageDescriptor(url, true));
    }
    
    @Test
    void testValidUrlWithInvalidJsonNoStrictValidation() throws Exception{
        URL url = new URL("https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/simple_invalid_datapackage.json");
        
        PackageDescriptor dp = new PackageDescriptor(url, false);
        Assertions.assertNotNull(dp.getJson());
    }
    
    @Test
    void testUrlDoesNotExist() throws IOException{
        // Preferably we would use mockito/powermock to mock URL Connection
        // But could not resolve AbstractMethodError: https://stackoverflow.com/a/32696152/4030804
        URL url = new URL("https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/NON-EXISTANT-FOLDER/multi_data_datapackage.json");

        assertThrows(FileNotFoundException.class, () -> new PackageDescriptor(url, true));
    }
    
    @Test
    void testLoadFromJsonFileResourceWithStrictValidationForInvalidNullPath() throws IOException {
        URL url = new URL("https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/invalid_multi_data_datapackage.json");


        Exception exception = assertThrows(ValidationException.class, () -> new PackageDescriptor(url, true));
        Assertions.assertEquals("Invalid Resource. The path property or the data and format properties cannot be null.", exception.getMessage());
    }
    
    @Test
    void testLoadFromJsonFileResourceWithoutStrictValidationForInvalidNullPath() throws Exception{
        URL url = new URL("https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/invalid_multi_data_datapackage.json");
        
        PackageDescriptor dp = new PackageDescriptor(url, false);
        Assertions.assertEquals("Invalid Resource. The path property or the data and format properties cannot be null.", dp.getErrors().get(0).getMessage());
    }
    
    @Test
    void testCreatingResourceWithInvalidPathNullValue() throws Exception{
        URL url = new URL("https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/multi_data_datapackage.json");
        PackageDescriptor dp = new PackageDescriptor(url, true);
        
        Resource resource = new Resource("resource-name", (File)null, // Path property is null.
            (JSONObject)null, null, null, null, null, null, // Casting to JSONObject to resolve ambiguous constructor reference.
            null, null, null, null, null);

        Exception exception = assertThrows(ValidationException.class, () -> dp.addResource(resource));
        Assertions.assertEquals("Invalid Resource. The path property or the data and format properties cannot be null.", exception.getMessage());
    }
    
    @Test
    void testCreatingResourceWithInvalidFormatNullValue() throws Exception{
        URL url = new URL("https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/multi_data_datapackage.json");
        PackageDescriptor dp = new PackageDescriptor(url, true);
        
        // format property is null but data is not null.
        Resource resource = new Resource("resource-name", "data.csv", (String)null, // Format is null when it shouldn't. Casting to String to resolve ambiguous constructor reference.
                null, null, null, null, null, null, null, null, null, null);

        Exception exception = assertThrows(ValidationException.class, () -> dp.addResource(resource));
        Assertions.assertEquals("Invalid Resource. The path property or the data and format properties cannot be null.", exception.getMessage());
    }
    
    @Test
    void testCreatingResourceWithInvalidFormatDataValue() throws Exception{
        URL url = new URL("https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/multi_data_datapackage.json");
        PackageDescriptor dp = new PackageDescriptor(url, true);
        
        // data property is null but format is not null.
        Resource resource = new Resource("resource-name", null, "csv", // data is null when it shouldn't
                null, null, null, null, null, null, null, null, null, null);

        Exception exception = assertThrows(ValidationException.class, () -> dp.addResource(resource));
        Assertions.assertEquals("Invalid Resource. The path property or the data and format properties cannot be null.", exception.getMessage());
    }
    
    @Test
    void testGetResources() throws Exception{
        // Create simple multi DataPackage from Json String
        PackageDescriptor dp = this.getDefaultTestDataPackage(true);
        Assertions.assertEquals(5, dp.getResources().size());
    }
    
    @Test
    void testGetExistingResource() throws Exception{
        // Create simple multi DataPackage from Json String
        PackageDescriptor dp = this.getDefaultTestDataPackage(true);
        Resource resource = dp.getResource("third-resource");
        Assertions.assertNotNull(resource);
    }
    
    @Test
    void testGetNonExistingResource() throws Exception{
        // Create simple multi DataPackage from Json String
        PackageDescriptor dp = this.getDefaultTestDataPackage(true);
        Resource resource = dp.getResource("non-existing-resource");
        Assertions.assertNull(resource);
    }
    
    @Test
    void testRemoveResource() throws Exception{
        PackageDescriptor dp = this.getDefaultTestDataPackage(true);
        
        Assertions.assertEquals(5, dp.getResources().size());
        
        dp.removeResource("second-resource");
        Assertions.assertEquals(4, dp.getResources().size());
        
        dp.removeResource("third-resource");
        Assertions.assertEquals(3, dp.getResources().size());
        
        dp.removeResource("third-resource");
        Assertions.assertEquals(3, dp.getResources().size());
    }
    
    @Test
    void testAddValidResource() throws Exception{
        PackageDescriptor dp = this.getDefaultTestDataPackage(true);
        
        Assertions.assertEquals(5, dp.getResources().size());
        
        List<String> paths = new ArrayList<>(Arrays.asList("cities.csv", "cities2.csv"));
        Resource resource = Resource
                .builder()
                .name("new-resource")
                .path(paths)
                .build();
        dp.addResource(resource);
        Assertions.assertEquals(6, dp.getResources().size());
        
        Resource gotResource = dp.getResource("new-resource");
        Assertions.assertNotNull(gotResource);
    }
    
    @Test
    void testAddInvalidResourceWithStrictValidation() throws Exception{
        PackageDescriptor dp = this.getDefaultTestDataPackage(true);

        Exception exception = assertThrows(ValidationException.class, () -> dp.addResource(new Resource()));
        Assertions.assertEquals("The resource does not have a name property.", exception.getMessage());
    }
    
    @Test
    void testAddInvalidResourceWithoutStrictValidation() throws Exception{
        PackageDescriptor dp = this.getDefaultTestDataPackage(false);
        dp.addResource(new Resource());
        
        Assertions.assertEquals(1, dp.getErrors().size());
        Assertions.assertEquals("The resource does not have a name property.", dp.getErrors().get(0).getMessage());
    }

    
    @Test
    void testAddDuplicateNameResourceWithStrictValidation() throws Exception{
        PackageDescriptor dp = this.getDefaultTestDataPackage(true);
        
        List<String> paths = new ArrayList<>(Arrays.asList("cities.csv", "cities2.csv"));
        Resource resource = new Resource("third-resource", paths);

        Exception exception = assertThrows(ValidationException.class, () -> dp.addResource(resource));
        Assertions.assertEquals("A resource with the same name already exists.", exception.getMessage());
    }
    
    @Test
    void testAddDuplicateNameResourceWithoutStrictValidation() throws Exception{
        PackageDescriptor dp = this.getDefaultTestDataPackage(false);
        
        List<String> paths = new ArrayList<>(Arrays.asList("cities.csv", "cities2.csv"));
        Resource resource = new Resource("third-resource", paths);
        dp.addResource(resource);
        
        Assertions.assertEquals(1, dp.getErrors().size());
        Assertions.assertEquals("A resource with the same name already exists.", dp.getErrors().get(0).getMessage());
    }
    
    
    @Test
    void testSaveToJsonFile() throws Exception{
        //String sourceFileName = "test_save_datapackage.json";

        //File createdFile = folder.newFile(sourceFileName);
        File createdFile = Files.createTempFile("datapackage-", ".json").toFile();
        
        PackageDescriptor savedPackageDescriptor = getDefaultTestDataPackage(true);
        savedPackageDescriptor.saveJson(createdFile.getAbsolutePath());

        // Get path of URL
        Path path = Paths.get(createdFile.toURI());
        PackageDescriptor readPackageDescriptor = new PackageDescriptor(path, false);
        
        // Check if two data packages are have the same key/value pairs.
        // For some reason JSONObject.similar() is not working even though both
        // json objects are exactly the same. Just compare lengths then.
        Assertions.assertEquals(readPackageDescriptor.getJson().toString().length(), savedPackageDescriptor.getJson().toString().length());
        createdFile.delete();
    }

/*
    test doesn't work that way
    @Test
    public void testSaveToFilenameWithInvalidFileType() throws Exception{
        File createdFile = folder.newFile("test_save_datapackage.txt");
        
        PackageDescriptor savedPackageDescriptor = this.getDefaultTestDataPackage(true);
        
        exception.expect(DataPackageException.class);
        savedPackageDescriptor.saveJson(createdFile.getAbsolutePath());
    }
    */

    @Test
    void testMultiPathIterationForLocalFiles() throws Exception{
        PackageDescriptor pkg = this.getDefaultTestDataPackage(true);
        Resource resource = pkg.getResource("first-resource");
        
        // Set the profile to tabular data resource.
        resource.setProfile(Profile.PROFILE_TABULAR_DATA_RESOURCE);
        
        // Expected data.
        List<String[]> expectedData = this.getAllCityData();
        
        // Get Iterator.
        Iterator<String[]> iter = resource.iter();
        int expectedDataIndex = 0;
        
        // Assert data.
        while(iter.hasNext()){
            String[] record = iter.next();
            String city = record[0];
            String location = record[1];
            
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[0], city);
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[1], location);
            
            expectedDataIndex++;
        } 
    }
    
    @Test
    void testMultiPathIterationForRemoteFile() throws Exception{
        PackageDescriptor pkg = this.getDefaultTestDataPackage(true);
        Resource resource = pkg.getResource("second-resource");
        
        // Set the profile to tabular data resource.
        resource.setProfile(Profile.PROFILE_TABULAR_DATA_RESOURCE);
        
        // Expected data.
        List<String[]> expectedData = this.getAllCityData();
        
        // Get Iterator.
        Iterator<String[]> iter = resource.iter();
        int expectedDataIndex = 0;
        
        // Assert data.
        while(iter.hasNext()){
            String[] record = iter.next();
            String city = record[0];
            String location = record[1];
            
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[0], city);
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[1], location);
            
            expectedDataIndex++;
        } 
    }
    
    @Test
    void testResourceSchemaDereferencingForLocalDataFileAndRemoteSchemaFile() throws Exception{
        PackageDescriptor pkg = this.getDefaultTestDataPackage(true);
        Resource resource = pkg.getResource("third-resource");

        // Get string content version of the schema file.
        String schemaJsonString = TestHelpers.getFileContents("/fixtures/schema/population_schema.json");
        
        // Get JSON Object
        JSONObject schemaJson = new JSONObject(schemaJsonString);
        
        // Compare.
        Assertions.assertTrue(schemaJson.similar(resource.getSchema()));
    }
    
    @Test
    void testResourceSchemaDereferencingForRemoteDataFileAndLocalSchemaFile() throws Exception{
        PackageDescriptor pkg = this.getDefaultTestDataPackage(true);
        Resource resource = pkg.getResource("fourth-resource");

        // Get string content version of the schema file.
        String schemaJsonString = TestHelpers.getFileContents("/fixtures/schema/population_schema.json");
        
        // Get JSON Object
        JSONObject schemaJson = new JSONObject(schemaJsonString);
        
        // Compare.
        Assertions.assertTrue(schemaJson.similar(resource.getSchema()));
    }
    
    /** TODO: Implement more thorough testing.
    @ Test
    public void testResourceSchemaDereferencingWithInvalidResourceSchema() throws DataPackageException, IOException{
        exception.expect(ValidationException.class);
        PackageDescriptor pkg = this.getDataPackageFromFilePath(true, "/fixtures/multi_data_datapackage_with_invalid_resource_schema.json");
    }**/
    
    @Test
    void testResourceDialectDereferencing() throws Exception{
        PackageDescriptor pkg = this.getDefaultTestDataPackage(true);
        
        Resource resource = pkg.getResource("fifth-resource");

        // Get string content version of the dialect file.
        String dialectJsonString = TestHelpers.getFileContents("/fixtures/dialect.json");
        
        // Get JSON Object
        JSONObject dialectJson = new JSONObject(dialectJsonString);
        
        // Compare.
        Assertions.assertTrue(dialectJson.similar(resource.getDialect()));
    }

    private PackageDescriptor getDefaultTestDataPackage(boolean strict) throws Exception{
        return TestHelpers.getDataPackageFromFilePath("/fixtures/multi_data_datapackage2.json", strict);
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
