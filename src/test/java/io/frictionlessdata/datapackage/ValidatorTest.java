package io.frictionlessdata.datapackage;

import io.frictionlessdata.datapackage.exceptions.DataPackageException;
import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test calls for JSON Validator class.
 * 
 */
class ValidatorTest {

    private static Validator validator = null;

    @BeforeAll
    static void setup(){
        validator = new Validator();
    }
    
    @Test
    void testValidatingInvalidJsonObject() {
        JSONObject datapackageJsonObject = new JSONObject("{\"invalid\" : \"json\"}");

        assertThrows(ValidationException.class, () -> validator.validate(datapackageJsonObject));
    }
    
    @Test
    void testValidatingInvalidJsonString() {
        String datapackageJsonString = "{\"invalid\" : \"json\"}";

        assertThrows(ValidationException.class, () -> validator.validate(datapackageJsonString));
    }
    
    @Test
    void testValidationWithInvalidProfileId() throws Exception{
        URL url = new URL("https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/multi_data_datapackage.json");
        PackageDescriptor dp = new PackageDescriptor(url, false);
        
        String invalidProfileId = "INVALID_PROFILE_ID";
        dp.addProperty("profile", invalidProfileId);

        Exception exception = assertThrows(DataPackageException.class, dp::validate);
        assertEquals("Invalid profile id: " + invalidProfileId, exception.getMessage());
    }
    
    @Test
    void testValidationWithValidProfileUrl() throws Exception{
        URL url = new URL("https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/multi_data_datapackage.json");
        PackageDescriptor dp = new PackageDescriptor(url, false);
        dp.addProperty("profile", "https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/main/resources/schemas/data-package.json");
        
        dp.validate();
        
        // No exception thrown, test passes.
        assertEquals("https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/main/resources/schemas/data-package.json", dp.getProperty("profile"));
    }
    
    @Test
    void testValidationWithInvalidProfileUrl() throws Exception{
        URL url = new URL("https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/test/resources/fixtures/multi_data_datapackage.json");
        PackageDescriptor dp = new PackageDescriptor(url, false);
        
        
        String invalidProfileUrl = "https://raw.githubusercontent.com/frictionlessdata/datapackage-java/master/src/main/resources/schemas/INVALID.json";
        dp.addProperty("profile", invalidProfileUrl);

        Exception exception = assertThrows(DataPackageException.class, dp::validate);
        assertEquals("Invalid profile schema URL: " + invalidProfileUrl, exception.getMessage());
    }
}
