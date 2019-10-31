package io.frictionlessdata.datapackage;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.frictionlessdata.tableschema.Schema;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * 
 */
class ResourceTest {

    @Test
    void testResourceDialectDereferencing() throws Exception {
        String content = getFileContents("/fixtures/multi_data_datapackage.json");
        URL schemaUrl = new URL("https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/schema/population_schema.json");
        // create a tabular data resource from remote location
        Resource resource = Resource
                .builder()
                .name("population")
                .schema(schemaUrl)
                .data(content)
                .path("src/test/resources/fixtures/data/population.csv")
                .profile(Profile.PROFILE_TABULAR_DATA_RESOURCE)
                .build();
    }
    
    @Test
    void testIterateDataFromUrlPath() throws Exception{
       
        String urlString = "https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/data/population.csv";
        URL dataSource = new URL(urlString);
        // create a tabular data resource from remote location
        Resource resource = Resource
                .builder()
                .name("population")
                .path(dataSource)
                .profile(Profile.PROFILE_TABULAR_DATA_RESOURCE)
                .build();
        
        // Expected data.
        List<String[]> expectedData = this.getExpectedPopulationData();
        
        // Get iterator.
        Iterator<String[]> iter = resource.iter();
        int expectedDataIndex = 0;
        
        // Assert data.
        while(iter.hasNext()){
            String[] record = iter.next();
            String city = record[0];
            String year = record[1];
            String population = record[2];
            
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[0], city);
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[1], year);
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[2], population);
            
            expectedDataIndex++;
        } 
    }
            
    @Test
    void testIterateDataFromFilePath() throws Exception{

        String filePath = ResourceTest.class.getResource("/fixtures/data/population.csv").getPath();
        File file = new File(filePath);
        // create a tabular data resource from local file
        Resource resource = Resource
                .builder()
                .name("population")
                .path(file)
                .profile(Profile.PROFILE_TABULAR_DATA_RESOURCE)
                .build();

        // Expected data.
        List<String[]> expectedData = this.getExpectedPopulationData();
        
        // Get iterator.
        Iterator<String[]> iter = resource.iter();
        int expectedDataIndex = 0;
        
        // Assert data.
        while(iter.hasNext()){
            String[] record = iter.next();
            String city = record[0];
            String year = record[1];
            String population = record[2];
            
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[0], city);
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[1], year);
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[2], population);
            
            expectedDataIndex++;
        }    
    }
    
    @Test
    void testIterateDataFromMultipartFilePath() throws Exception{
        List<String[]> expectedData  = new ArrayList<>();
        expectedData.add(new String[]{"libreville", "0.41,9.29"});
        expectedData.add(new String[]{"dakar", "14.71,-17.53"});
        expectedData.add(new String[]{"ouagadougou", "12.35,-1.67"});
        expectedData.add(new String[]{"barranquilla", "10.98,-74.88"});
        expectedData.add(new String[]{"rio de janeiro", "-22.91,-43.72"});
        expectedData.add(new String[]{"cuidad de guatemala", "14.62,-90.56"});
        expectedData.add(new String[]{"london", "51.50,-0.11"});
        expectedData.add(new String[]{"paris", "48.85,2.30"});
        expectedData.add(new String[]{"rome", "41.89,12.51"});
        
        JSONArray multipartPathJsonArray = new JSONArray("[\"src/test/resources/fixtures/data/cities.csv\", \"src/test/resources/fixtures/data/cities2.csv\", \"src/test/resources/fixtures/data/cities3.csv\"]");
        // create a tabular data resource from path array
        Resource resource = Resource
                .builder()
                .name("coordinates")
                .path(multipartPathJsonArray)
                .profile(Profile.PROFILE_TABULAR_DATA_RESOURCE)
                .build();
        
        Iterator<String[]> iter = resource.iter();
        int expectedDataIndex = 0;
        
        // Assert data.
        while(iter.hasNext()){
            String[] record = iter.next();
            String city = record[0];
            String coords = record[1];
            
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[0], city);
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[1], coords);
            
            expectedDataIndex++;
        }
    }
    
    @Test
    void testIterateDataFromMultipartURLPath() throws Exception{
        List<String[]> expectedData  = new ArrayList<>();
        expectedData.add(new String[]{"libreville", "0.41,9.29"});
        expectedData.add(new String[]{"dakar", "14.71,-17.53"});
        expectedData.add(new String[]{"ouagadougou", "12.35,-1.67"});
        expectedData.add(new String[]{"barranquilla", "10.98,-74.88"});
        expectedData.add(new String[]{"rio de janeiro", "-22.91,-43.72"});
        expectedData.add(new String[]{"cuidad de guatemala", "14.62,-90.56"});
        expectedData.add(new String[]{"london", "51.50,-0.11"});
        expectedData.add(new String[]{"paris", "48.85,2.30"});
        expectedData.add(new String[]{"rome", "41.89,12.51"});
        
        JSONArray multipartPathJsonArray = new JSONArray("[\"https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/data/cities.csv\", \"https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/data/cities2.csv\", \"https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/data/cities3.csv\"]");
        // create a tabular data resource from path array
        Resource resource = Resource
                .builder()
                .name("coordinates")
                .path(multipartPathJsonArray)
                .profile(Profile.PROFILE_TABULAR_DATA_RESOURCE)
                .build();
        
        Iterator<String[]> iter = resource.iter();
        int expectedDataIndex = 0;
        
        // Assert data.
        while(iter.hasNext()){
            String[] record = iter.next();
            String city = record[0];
            String coords = record[1];
            
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[0], city);
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[1], coords);
            
            expectedDataIndex++;
        }
    }
    
    @Test
    void testIterateDataWithCast() throws Exception{
        
        // Get data file.
        String filePath = ResourceTest.class.getResource("/fixtures/data/population.csv").getPath();
        File file = new File(filePath);

        // Get string content version of the schema file.
        String schemaJsonString = getFileContents("/fixtures/schema/population_schema.json");

        // Get JSON Object
        JSONObject schemaJson = new JSONObject(schemaJsonString);
        // create a tabular data resource with schema
        Resource resource = Resource
                .builder()
                .name("population")
                .path(file)
                .schema(new Schema(schemaJson))
                .profile(Profile.PROFILE_TABULAR_DATA_RESOURCE)
                .build();
        
        Iterator<Object[]> iter = resource.iter(false, false, true);
        
        // Assert data.
        while(iter.hasNext()){
            Object[] record = iter.next();
            
            Assertions.assertEquals(String.class, record[0].getClass());
            Assertions.assertEquals(Integer.class, record[1].getClass());
            Assertions.assertEquals(Integer.class, record[2].getClass());
        }
    }
    
    @Test
    void testIterateDataFromCsvFormat() throws Exception{
        String dataString = "city,year,population\nlondon,2017,8780000\nparis,2017,2240000\nrome,2017,2860000";
        // create a tabular data resource from CSV data
        Resource resource = Resource
                .builder()
                .name("population")
                .data(dataString)
                .format(Resource.FORMAT_CSV)
                .profile(Profile.PROFILE_TABULAR_DATA_RESOURCE)
                .build();

        // Expected data.
        List<String[]> expectedData = this.getExpectedPopulationData();
        
        // Get Iterator.
        Iterator<String[]> iter = resource.iter();
        int expectedDataIndex = 0;
        
        // Assert data.
        while(iter.hasNext()){
            String[] record = iter.next();
            String city = record[0];
            String year = record[1];
            String population = record[2];
            
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[0], city);
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[1], year);
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[2], population);
            
            expectedDataIndex++;
        }  
    }
    
    @Test
    void testIterateDataFromJSONFormat() throws Exception{
        JSONArray jsonData = new JSONArray("[" +
            "{" +
              "\"city\": \"london\"," +
              "\"year\": 2017," +
              "\"population\": 8780000" +
            "}," +
            "{" +
              "\"city\": \"paris\"," +
              "\"year\": 2017," +
              "\"population\": 2240000" +
            "}," +
            "{" +
              "\"city\": \"rome\"," +
              "\"year\": 2017," +
              "\"population\": 2860000" +
            "}" +
        "]");

        // create a tabular data resource from CSV data
        Resource resource = Resource
                .builder()
                .name("population")
                .data(jsonData)
                .format(Resource.FORMAT_JSON)
                .profile(Profile.PROFILE_TABULAR_DATA_RESOURCE)
                .build();
        
        // Expected data.
        List<String[]> expectedData = this.getExpectedPopulationData();
        
        // Get Iterator.
        Iterator<String[]> iter = resource.iter();
        int expectedDataIndex = 0;
        
        // Assert data.
        while(iter.hasNext()){
            String[] record = iter.next();
            String city = record[0];
            String year = record[1];
            String population = record[2];
            
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[0], city);
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[1], year);
            Assertions.assertEquals(expectedData.get(expectedDataIndex)[2], population);
            
            expectedDataIndex++;
        } 
    }
    
    @Test
    void testRead() throws Exception{
        // Get data file.
        String filePath = ResourceTest.class.getResource("/fixtures/data/population.csv").getPath();
        File file = new File(filePath);
        // create a tabular data resource from CSV data file
        Resource resource = Resource
                .builder()
                .name("population")
                .path(file)
                .format(Resource.FORMAT_CSV)
                .profile(Profile.PROFILE_TABULAR_DATA_RESOURCE)
                .build();

        // Assert
        Assertions.assertEquals(3, resource.read().size());
    }
    
    @Test
    void testHeadings() throws Exception{
        // Get data file.
        String filePath = ResourceTest.class.getResource("/fixtures/data/population.csv").getPath();
        File file = new File(filePath);

        // create a tabular data resource from CSV data file
        Resource resource = Resource
                .builder()
                .name("population")
                .path(file)
                .format(Resource.FORMAT_CSV)
                .profile(Profile.PROFILE_TABULAR_DATA_RESOURCE)
                .build();
        
        // Assert
        Assertions.assertEquals("city", resource.getHeaders()[0]);
        Assertions.assertEquals("year", resource.getHeaders()[1]);
        Assertions.assertEquals("population", resource.getHeaders()[2]);
    }


    private static String getFileContents(String fileName) {
        try {
            // Create file-URL of source file:
            URL sourceFileUrl = ResourceTest.class.getResource(fileName);
            // Get path of URL
            Path path = Paths.get(sourceFileUrl.toURI());
            return new String(Files.readAllBytes(path));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private List<String[]> getExpectedPopulationData(){
        List<String[]> expectedData  = new ArrayList<>();
        //expectedData.add(new String[]{"city", "year", "population"});
        expectedData.add(new String[]{"london", "2017", "8780000"});
        expectedData.add(new String[]{"paris", "2017", "2240000"});
        expectedData.add(new String[]{"rome", "2017", "2860000"});
        
        return expectedData;
    }
}
