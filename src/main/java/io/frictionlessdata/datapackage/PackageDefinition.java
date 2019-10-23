package io.frictionlessdata.datapackage;

import io.frictionlessdata.datapackage.exceptions.DataPackageException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.everit.json.schema.ValidationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import static io.frictionlessdata.datapackage.util.JsonUtils.getJsonStringContentFromInputStream;

/**
 * Load, validate and create a datapackage object.
 *
 * This class allows for JSON input validation. If enabled,
 * we don't allow BOM in JSON, and we validate against JSON schema. All
 * validation errors terminate parsing if strict validation is enabled.
 * In non-strict mode, validation errors are simply reported and BOM's
 * are silently ignored.
 */
public class PackageDefinition {
    private JSONObject jsonObject = new JSONObject();
    private boolean strictValidation = false;
    private List<Resource> resources;
    private List<Exception> errors;
    private Validator validator = new Validator();

    public PackageDefinition(){
    }


    /**
     * Load from InputStream.
     * @param inStream InputStream to read `datapackage.json` content from
     * @param strict enable strict validation (don't allow BOM in JSON, validate against JSON schema)
     * @throws DataPackageException thrown if the `InputStream` doesn't contain a JSON string
     * @throws ValidationException thrown if the `InputStream` doesn't contain a valid JSON string
     */
    public PackageDefinition(InputStream inStream, boolean strict) throws IOException, DataPackageException, ValidationException {
        this.strictValidation = strict;

        String content = getJsonStringContentFromInputStream(inStream, this.strictValidation);
        parseAndValidate (content);
    }

    /**
     * Load from String representation of JSON object
     * @param jsonStringSource
     * @param strict
     * @throws IOException
     * @throws DataPackageException thrown if the `InputStream` doesn't contain a JSON string
     * @throws ValidationException thrown if the `InputStream` doesn't contain a valid JSON string
     */
    public PackageDefinition(String jsonStringSource, boolean strict) throws IOException, DataPackageException, ValidationException{
        this.strictValidation = strict;

        // If String representation of desriptor JSON object is provided.
        parseAndValidate(jsonStringSource);
    }

    /**
     * Load from URL (must be in either 'http' or 'https' schemes).
     * @param urlSource
     * @param strict
     * @throws DataPackageException
     * @throws ValidationException
     * @throws IOException
     * @throws FileNotFoundException
     */
    public PackageDefinition(URL urlSource, boolean strict) throws DataPackageException, ValidationException, IOException, FileNotFoundException{
        this.strictValidation = strict;

        // Get string content of given remove file.
        String jsonString =  getJsonStringContentFromInputStream(urlSource.openStream(), this.strictValidation);
        parseAndValidate (jsonString);
    }

    /**
     * Load from local file system path.
     * @param filePath
     * @param strict
     * @throws DataPackageException
     * @throws ValidationException
     * @throws FileNotFoundException
     */
    public PackageDefinition(Path filePath, boolean strict) throws IOException, DataPackageException, ValidationException, FileNotFoundException {
        this.strictValidation = strict;

        String content = getJsonStringContentFromInputStream(Files.newInputStream(filePath), this.strictValidation);
        parseAndValidate (content);
    }

    private void writeJson(Writer writer) throws IOException {
        writer.write(this.getJson().toString(Constants.JSON_INDENT_FACTOR));
    }

    public void saveJson(OutputStream outStream) throws IOException, DataPackageException{
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(outStream))) {
            writeJson(writer);
        }
    }

    public void saveJson(String outputFilePath) throws IOException, DataPackageException{
        try (FileWriter writer = new FileWriter(outputFilePath)) {
            writeJson(writer);
        }
    }

    public void infer(){
        this.infer(false);
    }

    public void infer(boolean pattern){
        throw new UnsupportedOperationException();
    }

    public Resource getResource(String resourceName){
        Iterator<Resource> iter = this.resources.iterator();
        while(iter.hasNext()) {
            Resource resource = iter.next();
            if(resource.getName().equalsIgnoreCase(resourceName)){
                return resource;
            }
        }
        return null;
    }

    public List<Resource> getResources(){
        return this.resources;
    }

    public void addResource(Resource resource) throws IOException, ValidationException, DataPackageException{
        DataPackageException dpe = null;

        // If a name property isn't given...
        if(StringUtils.isEmpty(resource.getName())){
            dpe = new DataPackageException ("The resource does not have a name property.");
        } else if(resource.getPath() == null && (resource.getData() == null || resource.getFormat() == null)){
            dpe = new DataPackageException ("Invalid Resource. The path property or the data and format properties cannot be null.");
        } else {
            // Check if there is duplication.
            Resource res = getResource(resource.getName());
            if (null != res) {
                dpe = new DataPackageException("A resource with the same name already exists.");
            }
        }

        if (dpe != null) {
            if (this.strictValidation) {
                throw dpe;
            } else {
                errors.add(dpe);
            }
        }

        // Validate.
        this.validate();

        this.resources.add(resource);
    }

    public void removeResource(String name){
        this.resources.removeIf(resource -> resource.getName().equalsIgnoreCase(name));
    }

    public Object getProperty(String key){
        return this.getJson().get(key);
    }

    public Object getPropertyString(String key){
        return this.getJson().getString(key);
    }

    public Object getPropertyJSONObject(String key){
        return this.getJson().getJSONObject(key);
    }

    public Object getPropertyJSONArray(String key){
        return this.getJson().getJSONArray(key);
    }

    private void addProperty(String key, Object value) throws DataPackageException{
        if(this.getJson().has(key)){
            throw new DataPackageException("A property with the same key already exists.");
        }else{
            this.getJson().put(key, value);
        }
    }

    public void addProperty(String key, String value) throws DataPackageException{
        addProperty(key, (Object)value);
    }

    public void addProperty(String key, JSONObject value) throws DataPackageException{
        addProperty(key, (Object)value);
    }

    public void addProperty(String key, JSONArray value) throws DataPackageException{
        addProperty(key, (Object)value);
    }

    public void removeProperty(String key){
        this.getJson().remove(key);
    }


    /**
     * Takes a String representing JSON content and parses it into a JSON object.
     * Then validates the content and stores it in the PackageDefinition object.
     *
     * @param jsonContent String representation of a JSON object. Can't be null or empty.
     * @throws IOException thrown if JSON parsing fails
     * @throws DataPackageException if `jsonContent` is either null or empty
     */
    private void parseAndValidate (String jsonContent) throws IOException, DataPackageException {
        if (!StringUtils.isEmpty(jsonContent)) {
            JSONObject sourceJsonObject = new JSONObject(jsonContent);

            this.setJson(sourceJsonObject);
            this.validate();
        } else {
            throw new DataPackageException("No Content was provided");
        }
    }

    /**
     * Validation is strict or non-strict depending on how the package was
     * instantiated with the strict flag.
     * @throws IOException
     * @throws DataPackageException
     * @throws ValidationException thrown if a parsing error happens
     *      and strict validation is enabled
     */
    public void validate() throws IOException, DataPackageException, ValidationException{
        try{
            this.validator.validate(this.getJson());

        }catch(ValidationException ve){
            if(this.strictValidation){
                throw ve;
            }else{
                errors.add(ve);
            }
        }
    }

    public JSONObject getJson(){
        JSONArray resourcesJsonArray = new JSONArray();
        if (resources != null) {
            Iterator<Resource> resourceIter = resources.iterator();

            while (resourceIter.hasNext()) {
                Resource resource = resourceIter.next();
                resourcesJsonArray.put(resource.getJson());
            }
        }
        if(resourcesJsonArray.length() > 0){
            this.jsonObject.put(Constants.JSON_KEY_RESOURCES, resourcesJsonArray);
        }

        return this.jsonObject;
    }

    public List<Exception> getErrors(){
        return this.errors;
    }

    public void setStrictValidation (boolean strict) {
        this.strictValidation = strict;
    }

    public boolean isStrictValidation() {
        return this.strictValidation;
    }


    private String getJsonStringContentFromLocalFile(String absoluteFilePath) throws IOException, JSONException{
        // Read file, it should be a JSON.
        return new String(Files.readAllBytes(Paths.get(absoluteFilePath)));
    }

    private void setJson(JSONObject jsonObjectSource) throws IOException, MalformedURLException, FileNotFoundException, DataPackageException{
        this.jsonObject = jsonObjectSource;

        // Create Resource list, is there are resources.
        if(jsonObjectSource.has(Constants.JSON_KEY_RESOURCES)){
            JSONArray resourcesJsonArray = jsonObjectSource.getJSONArray(Constants.JSON_KEY_RESOURCES);
            for(int i=0; i < resourcesJsonArray.length(); i++){
                JSONObject resourceJson = resourcesJsonArray.getJSONObject(i);

                //FIXME: Again, could be greatly simplified amd much more
                // elegant if we use a library like GJSON...
                String name = resourceJson.has(Resource.JSON_KEY_NAME) ? resourceJson.getString(Resource.JSON_KEY_NAME) : null;
                Object path = resourceJson.has(Resource.JSON_KEY_PATH) ? resourceJson.get(Resource.JSON_KEY_PATH) : null;
                Object data = resourceJson.has(Resource.JSON_KEY_DATA) ? resourceJson.get(Resource.JSON_KEY_DATA) : null;
                String profile = resourceJson.has(Resource.JSON_KEY_PROFILE) ? resourceJson.getString(Resource.JSON_KEY_PROFILE) : null;
                String title = resourceJson.has(Resource.JSON_KEY_TITLE) ? resourceJson.getString(Resource.JSON_KEY_TITLE) : null;
                String description = resourceJson.has(Resource.JSON_KEY_DESCRIPTION) ? resourceJson.getString(Resource.JSON_KEY_DESCRIPTION) : null;
                String format = resourceJson.has(Resource.JSON_KEY_FORMAT) ? resourceJson.getString(Resource.JSON_KEY_FORMAT) : null;
                String mediaType = resourceJson.has(Resource.JSON_KEY_MEDIA_TYPE) ? resourceJson.getString(Resource.JSON_KEY_MEDIA_TYPE) : null;
                String encoding = resourceJson.has(Resource.JSON_KEY_ENCODING) ? resourceJson.getString(Resource.JSON_KEY_ENCODING) : null;
                Integer bytes = resourceJson.has(Resource.JSON_KEY_BYTES) ? resourceJson.getInt(Resource.JSON_KEY_BYTES) : null;
                String hash = resourceJson.has(Resource.JSON_KEY_HASH) ? resourceJson.getString(Resource.JSON_KEY_HASH) : null;

                JSONArray sources = resourceJson.has(Resource.JSON_KEY_SOURCES) ? resourceJson.getJSONArray(Resource.JSON_KEY_SOURCES) : null;
                JSONArray licenses = resourceJson.has(Resource.JSON_KEY_LICENSES) ? resourceJson.getJSONArray(Resource.JSON_KEY_LICENSES) : null;

                // Get the schema and dereference it. Enables validation against it.
                Object schemaObj = resourceJson.has(Resource.JSON_KEY_SCHEMA) ? resourceJson.get(Resource.JSON_KEY_SCHEMA) : null;
                JSONObject dereferencedSchema = this.getDereferencedObject(schemaObj);

                // Now we can build the resource objects
                Resource resource = null;

                if(path != null){
                    // Get the dialect and dereference it. Enables validation against it.
                    Object dialectObj = resourceJson.has(Resource.JSON_KEY_DIALECT) ? resourceJson.get(Resource.JSON_KEY_DIALECT) : null;
                    JSONObject dereferencedDialect = this.getDereferencedObject(dialectObj);

                    resource = new Resource(name, path, dereferencedSchema, dereferencedDialect,
                            profile, title, description, mediaType, encoding, bytes, hash, sources, licenses);

                }else if(data != null && format != null){
                    resource = new Resource(name, data, format, dereferencedSchema,
                            profile, title, description, mediaType, encoding, bytes, hash, sources, licenses);

                }else{
                    DataPackageException dpe = new DataPackageException("Invalid Resource. The path property or the data and format properties cannot be null.");

                    if(this.strictValidation){
                        this.jsonObject = null;
                        this.resources.clear();

                        throw dpe;

                    }else{
                        this.errors.add(dpe);
                    }
                }

                if(resource != null){
                    this.resources.add(resource);
                }

            }
        }
    }

    private JSONObject getDereferencedObject(Object obj) throws IOException, FileNotFoundException, MalformedURLException{
        // The JSONObject that will represent the schema.
        JSONObject dereferencedObj = null;

        // Object is already a dereferences object.
        if(obj instanceof JSONObject){

            // Don't need to do anything, just cast and return.
            dereferencedObj = (JSONObject)obj;

        }else if(obj instanceof String){

            // The string value of the given object value.
            String objStr = (String)obj;

            // If object value is Url.
            // Grab the JSON string content of that remote file.
            String[] schemes = {"http", "https"};
            UrlValidator urlValidator = new UrlValidator(schemes);

            if (urlValidator.isValid(objStr)) {

                // Create the dereferenced object from the remote file.
                String jsonContentString = getJsonStringContentFromInputStream(
                        new URL(objStr).openStream(),
                        this.strictValidation);
                dereferencedObj = new JSONObject(jsonContentString);

            }else{
                // If schema is file path.
                File sourceFile = new File(objStr);
                if(sourceFile.exists()){
                    // Create the dereferenced schema object from the local file.
                    String jsonContentString = this.getJsonStringContentFromLocalFile(sourceFile.getAbsolutePath());
                    dereferencedObj = new JSONObject(jsonContentString);

                }else{
                    throw new FileNotFoundException("Local file not found: " + sourceFile);
                }
            }
        }

        return dereferencedObj;
    }

}
