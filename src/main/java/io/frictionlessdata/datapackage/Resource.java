package io.frictionlessdata.datapackage;

import io.frictionlessdata.datapackage.exceptions.DataPackageException;
import io.frictionlessdata.tableschema.Schema;
import io.frictionlessdata.tableschema.Table;
import io.frictionlessdata.tableschema.TableIterator;
import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.validator.routines.UrlValidator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * Resource.
 * Based on specs: http://frictionlessdata.io/specs/data-resource/
 */
public class Resource {
    
    // Data properties.
    private Object path = null;
    private Object data = null;
    
    // Metadata properties.
    // Required properties.
    private String name = null;
    
    // Recommended properties.
    private String profile = null;
    
    // Optional properties.
    private String title = null;
    private String description = null;
    private String format = null;
    private String mediaType = null;
    private String encoding = null;
    private Integer bytes = null;
    private String hash = null;
    
    private JSONObject dialect = null;
    private JSONArray sources = null;
    private JSONArray licenses = null;

    // Schema
    private Schema schema = null;
    
    public final static String FORMAT_CSV = "csv";
    public final static String FORMAT_JSON = "json";
    
    // JSON keys.
    // FIXME: Use somethign like GSON instead so this explicit mapping is not
    // necessary?
    public final static String JSON_KEY_PATH = "path";
    public final static String JSON_KEY_DATA = "data";
    public final static String JSON_KEY_NAME = "name";
    public final static String JSON_KEY_PROFILE = "profile";
    public final static String JSON_KEY_TITLE = "title";
    public final static String JSON_KEY_DESCRIPTION = "description";
    public final static String JSON_KEY_FORMAT = "format";
    public final static String JSON_KEY_MEDIA_TYPE = "mediaType";
    public final static String JSON_KEY_ENCODING = "encoding";
    public final static String JSON_KEY_BYTES = "bytes";
    public final static String JSON_KEY_HASH = "hash";
    public final static String JSON_KEY_SCHEMA = "schema";
    public final static String JSON_KEY_DIALECT = "dialect";
    
    public final static String JSON_KEY_SOURCES = "sources";
    public final static String JSON_KEY_LICENSES = "licenses";

    public Resource(){}

    private Resource(
            Object path,
            Object data,
            String name,
            String profile,
            String title,
            String description,
            String format,
            String mediaType,
            String encoding,
            Integer bytes,
            String hash,
            JSONObject dialect,
            JSONArray sources,
            JSONArray licenses,
            Schema schema) {

        this.name = name;
        this.data = data;
        this.path = path;
        this.format = format;
        this.schema = schema;
        this.profile = profile;
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.encoding = encoding;
        this.bytes = bytes;
        this.hash = hash;
        this.dialect = dialect;
        this.sources = sources;
        this.licenses = licenses;
    }

    public Resource(String name, File path) throws Exception {
        this(name, path, new JSONObject());
    }
    public Resource(String name, URL path) throws Exception {
        this(name, path, new JSONObject());
    }

    public Resource(String name, JSONObject data) throws Exception {
        this.name = name;
        this.data = data;
    }

    public Resource(String name, File path, JSONObject schema) throws Exception {
        this.name = name;
        this.path = path;
        if(schema.length() > 0){
            this.schema = new Schema(schema, true);
        }
    }

    public Resource(String name, URL path, JSONObject schema) throws Exception {
        this.name = name;
        this.path = path;
        if(schema.length() > 0){
            this.schema = new Schema(schema.toString(), true);
        }
    }

    public Resource(String name, JSONArray paths) throws Exception {
        this.name = name;
        this.path = paths;
    }

    public Resource(String name, List paths) throws Exception {
        this.name = name;
        this.path = paths;
    }

    public Resource(String name, Object data, String format) throws Exception {
        this(name, data, format, null);
    }

    public Resource(String name, Object data, String format, JSONObject schema) throws Exception {
        this.name = name;
        this.data = data;
        this.format = format;
        if (schema != null)
            this.schema = new Schema(schema.toString(), true);
    }

    public Resource(String name, File path, JSONObject schema, JSONObject dialect, String profile, String title,
            String description, String mediaType,
            String encoding, Integer bytes, String hash, JSONArray sources, JSONArray licenses) throws Exception {

        this.name = name;
        this.path = path;
        if (schema != null)
            this.schema = new Schema(schema, true);
        this.dialect = dialect;
        this.profile = profile;
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.encoding = encoding;
        this.bytes = bytes;
        this.hash = hash;
        this.sources = sources;
        this.licenses = licenses;
    }

    public Resource(String name, Object path, JSONObject schema, JSONObject dialect, String profile, String title,
                    String description, String mediaType,
                    String encoding, Integer bytes, String hash, JSONArray sources, JSONArray licenses) throws Exception {

        this.name = name;
        this.path = path;
        if (schema != null)
            this.schema = new Schema(schema, true);
        this.dialect = dialect;
        this.profile = profile;
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.encoding = encoding;
        this.bytes = bytes;
        this.hash = hash;
        this.sources = sources;
        this.licenses = licenses;
    }

    public Resource(String name, String data, JSONObject schema, JSONObject dialect, String profile, String title,
                    String description, String mediaType,
                    String encoding, Integer bytes, String hash, JSONArray sources, JSONArray licenses) throws Exception {

        this.name = name;
        this.data = data;
        if (schema != null)
            this.schema = new Schema(schema, true);
        this.dialect = dialect;
        this.profile = profile;
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.encoding = encoding;
        this.bytes = bytes;
        this.hash = hash;
        this.sources = sources;
        this.licenses = licenses;

    }

    public Resource(String name, Object data, String format, JSONObject schema, String profile,
            String title, String description, String mediaType,
            String encoding, Integer bytes, String hash, JSONArray sources, JSONArray licenses) throws Exception {
        
        this.name = name;
        this.data = data;
        this.format = format;
        if (schema != null)
            this.schema = new Schema(schema, true);
        this.profile = profile;
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.encoding = encoding;
        this.bytes = bytes;
        this.hash = hash;
        this.sources = sources;
        this.licenses = licenses;
    }

    public static ResourceBuilder builder() {
        return new ResourceBuilder();
    }

    public Iterator iter() throws Exception{
        return this.iter(false, false, true, false);
    }
    
    public Iterator iter(boolean keyed) throws Exception{
       return this.iter(keyed, false, true, false);
    }
    
    public Iterator iter(boolean keyed, boolean extended) throws Exception{
       return this.iter(keyed, extended, true, false);
    }
    
    public Iterator iter(boolean keyed, boolean extended, boolean cast) throws Exception{
       return this.iter(keyed, extended, cast, false);
    }
    
    public Iterator iter(boolean keyed, boolean extended, boolean cast, boolean relations) throws Exception{
        // Error for non tabular
        if(this.profile == null || !this.profile.equalsIgnoreCase(Profile.PROFILE_TABULAR_DATA_RESOURCE)){
            throw new DataPackageException("Unsupported for non tabular data.");
        }
        
        // If the path of a data file has been set.
        if(this.getPath() != null){
            
            // And if it's just a one part resource (i.e. only one file path is given).
            if(this.getPath() instanceof File){
                // then just return the interator for the data located in that file
                File file = (File)this.getPath();
                Table table = (this.schema != null) ? new Table(file, this.schema) : new Table(file);
                
                return table.iterator(keyed, extended, cast, relations);
  
            }else if(this.getPath() instanceof URL){
                URL url = (URL)this.getPath();
                Table table = (this.schema != null) ? new Table(url, this.schema) : new Table(url);
                return table.iterator(keyed, extended, cast, relations);
                
            }else if(this.getPath() instanceof JSONArray){ // If multipart resource (i.e. multiple file paths are given).
                
                // Create an iterator for each file, chain them, and then return them as a single iterator.
                JSONArray paths = ((JSONArray)this.getPath());
                Iterator[] tableIteratorArray = new TableIterator[paths.length()];
                
                // Chain the iterators.
                for(int i = 0; i < paths.length(); i++){
                    
                    String[] schemes = {"http", "https"};
                    UrlValidator urlValidator = new UrlValidator(schemes);

                    String thePath = paths.getString(i);
                    
                    if (urlValidator.isValid(thePath)) {
                        URL url = new URL(thePath);
                        Table table = (this.schema != null) ? new Table(url, this.schema) : new Table(url);
                        tableIteratorArray[i] = table.iterator(keyed, extended, cast, relations);
                
                    }else{
                        File file = new File(thePath);
                        Table table = (this.schema != null) ? new Table(file, this.schema) : new Table(file);
                        tableIteratorArray[i] = table.iterator(keyed, extended, cast, relations);
                    }
                }
                
                IteratorChain iterChain = new IteratorChain(tableIteratorArray);
                return iterChain;
                
            }else{
                throw new DataPackageException("Unsupported data type for Resource path. Should be String or List but was " + this.getPath().getClass().getTypeName());
            }
               
        }else if (this.getData() != null){
            
            // Data is in String, hence in CSV Format.
            if(this.getData() instanceof String && this.getFormat().equalsIgnoreCase(FORMAT_CSV)){
                Table table = new Table((String)this.getData());
                return table.iterator();
            }
            // TODO decide to either enforce format matching (then drop this) or not
            else if((this.getData() instanceof JSONArray) && (this.getFormat() == null)){
                JSONArray dataJsonArray = (JSONArray)this.getData();
                Table table = new Table(dataJsonArray);
                return table.iterator();

            }
            // Data is not String, hence in JSON Array format.
            else if(this.getData() instanceof JSONArray && this.getFormat().equalsIgnoreCase(FORMAT_JSON)){
                JSONArray dataJsonArray = (JSONArray)this.getData();            
                Table table = new Table(dataJsonArray);
                return table.iterator();
                
            }else{
                // Data is in unexpected format. Throw exception.
                throw new DataPackageException("A resource has an invalid data format. It should be a CSV String or a JSON Array.");
            }
            
        }else{
            throw new DataPackageException("No data has been set.");
        }
    }
    
    public List<Object[]> read() throws Exception{
        return this.read(false);
    }

    public List<Object[]> read(boolean cast) throws Exception{
        if(!this.profile.equalsIgnoreCase(Profile.PROFILE_TABULAR_DATA_RESOURCE)){
            throw new DataPackageException("Unsupported for non tabular data.");
        }
        
        if(this.getPath() != null){
            // And if it's just a one part resource (i.e. only one file path is given).
            if(this.getPath() instanceof File){
                // then just return the interator for the data located in that file
                File file = (File)this.getPath();
                Table table = new Table(file);
                
                return table.read(cast);
  
            }else if(this.getPath() instanceof URL){
                URL url = (URL)this.getPath();
                Table table = new Table(url);
                
                return table.read(cast);
                
                //FIXME: Multipart data.
            }else{
                throw new DataPackageException("Unsupported data type for Resource path. Should be File or URL but was " + this.getPath().getClass().getTypeName());
            }
            
        }else if (this.getData() != null){
            // Data is in String, hence in CSV Format.
            if(this.getData() instanceof String && this.getFormat().equalsIgnoreCase(FORMAT_CSV)){
                Table table = new Table((String)this.getData());
                return table.read(cast);
            }
            // Data is not String, hence in JSON Array format.
            else if(this.getData() instanceof JSONArray && this.getFormat().equalsIgnoreCase(FORMAT_JSON)){
                JSONArray dataJsonArray = (JSONArray)this.getData();            
                Table table = new Table(dataJsonArray.toString());
                return table.read(cast);
                
            }else{
                // Data is in unexpected format. Throw exception.
                throw new DataPackageException("A resource has an invalid data format. It should be a CSV String or a JSON Array.");
            }
            
        }else{
            throw new DataPackageException("No data has been set.");
        }
    }
    
    public String[] getHeaders() throws Exception{
        if(!this.profile.equalsIgnoreCase(Profile.PROFILE_TABULAR_DATA_RESOURCE)){
            throw new DataPackageException("Unsupported for non tabular data.");
        }
        
        if(this.getPath() != null){
            // And if it's just a one part resource (i.e. only one file path is given).
            if(this.getPath() instanceof File){
                // then just return the interator for the data located in that file
                File file = (File)this.getPath();
                Table table = new Table(file);
                
                return table.getHeaders();
  
            }else if(this.getPath() instanceof URL){
                URL url = (URL)this.getPath();
                Table table = new Table(url);
                
                return table.getHeaders();
                
                //FIXME: Multipart data.
            }else{
                throw new DataPackageException("Unsupported data type for Resource path. Should be String or List but was " + this.getPath().getClass().getTypeName());
            }
            
        }else if (this.getData() != null){
            // Data is in String, hence in CSV Format.
            if(this.getData() instanceof String && this.getFormat().equalsIgnoreCase(FORMAT_CSV)){
                Table table = new Table((String)this.getData());
                return table.getHeaders();
            }
            // Data is not String, hence in JSON Array format.
            else if(this.getData() instanceof JSONArray && this.getFormat().equalsIgnoreCase(FORMAT_JSON)){
                JSONArray dataJsonArray = (JSONArray)this.getData();            
                Table table = new Table(dataJsonArray.toString());
                return table.getHeaders();
                
            }else{
                // Data is in unexpected format. Throw exception.
                throw new DataPackageException("A resource has an invalid data format. It should be a CSV String or a JSON Array.");
            }
            
        }else{
            throw new DataPackageException("No data has been set.");
        }
    }
    
    /**
     * Get JSON representation of the object.
     * @return 
     */
    public JSONObject getJson(){
        //FIXME: Maybe use something lke GSON so we don't have to explicitly
        //code this...
        JSONObject json = new JSONObject();
        
        // Null values will not actually be "put," as per JSONObject specs.
        json.put(JSON_KEY_NAME, this.getName());
        json.put(JSON_KEY_PATH, this.getPath());
        json.put(JSON_KEY_DATA, this.getData());
        json.put(JSON_KEY_PROFILE, this.getProfile());
        json.put(JSON_KEY_TITLE, this.getTitle());
        json.put(JSON_KEY_DESCRIPTION, this.getDescription());
        json.put(JSON_KEY_FORMAT, this.getFormat());
        json.put(JSON_KEY_DIALECT, this.getDialect());
        json.put(JSON_KEY_MEDIA_TYPE, this.getMediaType());
        json.put(JSON_KEY_ENCODING, this.getEncoding());
        json.put(JSON_KEY_BYTES, this.getBytes());
        json.put(JSON_KEY_HASH, this.getHash());
        json.put(JSON_KEY_SOURCES, this.getSources());
        json.put(JSON_KEY_LICENSES, this.getLicenses());
        json.put(JSON_KEY_SCHEMA, this.getSchema());
        
        return json;
    }

    /**
     * @return the path
     */
    public Object getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(Object path) {
        this.path = path;
    }

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return the mediaType
     */
    public String getMediaType() {
        return mediaType;
    }

    /**
     * @param mediaType the mediaType to set
     */
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param encoding the encoding to set
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * @return the bytes
     */
    public Integer getBytes() {
        return bytes;
    }

    /**
     * @param bytes the bytes to set
     */
    public void setBytes(Integer bytes) {
        this.bytes = bytes;
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash the hash to set
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return the dialect
     */
    public JSONObject getDialect() {
        return dialect;
    }

    /**
     * @param dialect the dialect to set
     */
    public void setDialect(JSONObject dialect) {
        this.dialect = dialect;
    }    
    
    public Schema getSchema(){
        return this.schema;
    }

    /**
     * @return the sources
     */
    public JSONArray getSources() {
        return sources;
    }

    /**
     * @param sources the sources to set
     */
    public void setSources(JSONArray sources) {
        this.sources = sources;
    }

    /**
     * @return the licenses
     */
    public JSONArray getLicenses() {
        return licenses;
    }

    /**
     * @param licenses the licenses to set
     */
    public void setLicenses(JSONArray licenses) {
        this.licenses = licenses;
    }

    public static class ResourceBuilder {
        private Object path;
        private Object data;
        private String name;
        private String profile;
        private String title;
        private String description;
        private String format;
        private String mediaType;
        private String encoding;
        private Integer bytes;
        private String hash;
        private JSONObject dialect;
        private JSONArray sources;
        private JSONArray licenses;
        private Schema schema;

        ResourceBuilder() {
        }

        public Resource.ResourceBuilder path(Object path) {
            this.path = path;
            return this;
        }

        public Resource.ResourceBuilder data(Object data) {
            this.data = data;
            return this;
        }

        public Resource.ResourceBuilder name(String name) {
            this.name = name;
            return this;
        }

        public Resource.ResourceBuilder profile(String profile) {
            this.profile = profile;
            return this;
        }

        public Resource.ResourceBuilder title(String title) {
            this.title = title;
            return this;
        }

        public Resource.ResourceBuilder description(String description) {
            this.description = description;
            return this;
        }

        public Resource.ResourceBuilder format(String format) {
            this.format = format;
            return this;
        }

        public Resource.ResourceBuilder mediaType(String mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Resource.ResourceBuilder encoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        public Resource.ResourceBuilder bytes(Integer bytes) {
            this.bytes = bytes;
            return this;
        }

        public Resource.ResourceBuilder hash(String hash) {
            this.hash = hash;
            return this;
        }

        public Resource.ResourceBuilder dialect(JSONObject dialect) {
            this.dialect = dialect;
            return this;
        }

        public Resource.ResourceBuilder sources(JSONArray sources) {
            this.sources = sources;
            return this;
        }

        public Resource.ResourceBuilder licenses(JSONArray licenses) {
            this.licenses = licenses;
            return this;
        }

        public Resource.ResourceBuilder schema(Schema schema) {
            this.schema = schema;
            return this;
        }

        public Resource.ResourceBuilder schema(URL schemaUrl) throws Exception {
            this.schema = new Schema(schemaUrl);
            return this;
        }

        public Resource build() {
            return new Resource(path, data, name, profile, title, description, format, mediaType, encoding, bytes, hash, dialect, sources, licenses, schema);
        }

        public String toString() {
            return "Resource.ResourceBuilder(path=" + this.path + ", data=" + this.data + ", name=" + this.name + ", profile=" + this.profile + ", title=" + this.title + ", description=" + this.description + ", format=" + this.format + ", mediaType=" + this.mediaType + ", encoding=" + this.encoding + ", bytes=" + this.bytes + ", hash=" + this.hash + ", dialect=" + this.dialect + ", sources=" + this.sources + ", licenses=" + this.licenses + ", schema=" + this.schema + ")";
        }
    }
}
