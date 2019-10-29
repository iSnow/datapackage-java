package io.frictionlessdata.datapackage;

import io.frictionlessdata.datapackage.exceptions.DataPackageException;
import io.frictionlessdata.datapackage.inputsource.FileInputSource;
import io.frictionlessdata.datapackage.inputsource.InputSource;
import io.frictionlessdata.datapackage.inputsource.UrlInputSource;
import io.frictionlessdata.datapackage.inputsource.ZipInputSource;
import jdk.internal.util.xml.impl.Input;
import org.everit.json.schema.ValidationException;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * This class implements the Tabular Data Package (TDP) specs. It is not a superclass of PackageDescriptor
 * as it requires either a folder or a ZIP file to read from. Reason is that TDP's are not parsed
 * from *one* JSON  like regular `PackageDescriptor`, but contain references to CSV files either inside
 * the ZIP file or as separate files in the file system
 */
public class DataPackage {
    private PackageDescriptor packageInfo;
    private InputSource source;

    /**
     * Load from a file. The `inputFile` can either point to a zip-packaged datapackage or
     * to the `datapackage.json` inside a directory on the local file system.
     *
     * If the file name indicates we are loading from a zip, the zip file is loaded
     * into memory and cached to allow for loading of the TDP `Resource`s.
     *
     * If the file name is `datapackage.json`, then we are loading this and storing the
     * parent path as the base path for resolution of relative paths to Resources.
     *
     * @param inputFile File path of the ZIP input file or `datapackage.json`
     * @param strict whether to enable strict parsing of JSON. Throws exceptions if a BOM is
     *               encountered and if JSON doesn't match schema
     * @throws IOException bubbling up if the underlying file operations throw an exception
     * @throws DataPackageException thrown if the `InputStream` doesn't contain a JSON string
     * @throws ValidationException thrown if the `InputStream` doesn't contain a valid JSON string
     */
    public DataPackage(File inputFile, boolean strict) throws Exception {
        if (null == inputFile)
            throw new DataPackageException("Input Path cannot be null");
        if (inputFile.getName().toLowerCase().endsWith(".zip")) {
            source = new ZipInputSource(inputFile.toPath());
        } else {
            Path path = inputFile.toPath();
            source = new FileInputSource(path);
        }
        try {
            packageInfo = new PackageDescriptor(source, strict);
        } catch (DataPackageException ex) {
            throw new DataPackageException("Input is not a ZIP or \"datapackage.json\" file");
        }
    }

    /**
     * Load from a file. The `inputFile` can either point to a zip-packaged datapackage or
     * to the `datapackage.json` inside a directory on the local file system.
     *
     * If the file name indicates we are loading from a zip, the zip file is loaded
     * into memory and cached to allow for loading of the TDP `Resource`s.
     *
     * If the file name is `datapackage.json`, then we are loading this and storing the
     * parent path as the base path for resolution of relative paths to Resources.
     *
     * @param inputUrl File path of the ZIP input file or `datapackage.json`
     * @param strict whether to enable strict parsing of JSON. Throws exceptions if a BOM is
     *               encountered and if JSON doesn't match schema
     * @throws IOException bubbling up if the underlying file operations throw an exception
     * @throws DataPackageException thrown if the `InputStream` doesn't contain a JSON string
     * @throws ValidationException thrown if the `InputStream` doesn't contain a valid JSON string
     */
    public DataPackage(URL inputUrl, boolean strict) throws Exception {
        if (null == inputUrl)
            throw new DataPackageException("Input Path cannot be null");
        URI uri = inputUrl.toURI();
        // first try parsing as JSON
        try {
            source = new UrlInputSource(inputUrl);
            packageInfo = new PackageDescriptor(source, strict);
        } catch (DataPackageException | ValidationException ex) {
            source = new ZipInputSource(inputUrl);
            packageInfo = new PackageDescriptor(source, strict);
        }
    }


    /**
     * Load from a path
     * @param inputPath File path of the input directory
     * @param strict whether to enable strict parsing of JSON. Throws exceptions if a BOM is
     *               encountered and if JSON doesn't match schema
     * @throws IOException
     * @throws DataPackageException thrown if the input directory doesn't contain a `inputsource.json` file
     * @throws ValidationException thrown if the `inputsource.json` file doesn't contain a valid JSON string
     */
    /*public DataPackage(Path inputPath, boolean strict) throws IOException, DataPackageException, ValidationException{
        if (null == inputPath)
            throw new DataPackageException("Input Path cannot be null");
        if(inputPath.toFile().isDirectory()) {
            source = new FileInputSource(inputPath);
            Path datapackageFile = null;
            Iterator<Path> iter = inputPath.iterator();
            while (iter.hasNext()) {
                Path child = iter.next();
                if (child.getFileName().toString().equals(Constants.DATAPACKAGE_FILENAME)) {
                    datapackageFile = child;
                }
            }
            // Throw exception if expected inputsource.json file not found.
            if (datapackageFile == null) {
                throw new DataPackageException("The source directory does not contain the expected file: " + Constants.DATAPACKAGE_FILENAME);
            }

            packageInfo = new PackageDescriptor(datapackageFile, strict);
        } else {
            throw new DataPackageException("Input is not a directory");
        }
    }*/

    public void saveToZip(Path outputFilePath) throws IOException, DataPackageException {
        try(FileOutputStream fos = new FileOutputStream(outputFilePath.toFile())){
            try(BufferedOutputStream bos = new BufferedOutputStream(fos)){
                try(ZipOutputStream zos = new ZipOutputStream(bos)){
                    // File is not on the disk, test.txt indicates
                    // only the file name to be put into the zip.
                    ZipEntry entry = new ZipEntry(Constants.DATAPACKAGE_FILENAME);

                    zos.putNextEntry(entry);
                    zos.write(packageInfo.getJson().toString(Constants.JSON_INDENT_FACTOR).getBytes());
                    zos.closeEntry();
                }
            }
        }
    }

    public void saveToDirectory(Path outputFilePath) throws IOException, DataPackageException {
        File dir = outputFilePath.toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        } else if ((dir.exists()) && (!dir.isDirectory())) {
            throw new IOException("Output path exists and is a regular file");
        }
        File packageFile = new File(dir, Constants.DATAPACKAGE_FILENAME);
        try (FileWriter writer = new FileWriter(packageFile)) {
            writer.write(packageInfo.getJson().toString(Constants.JSON_INDENT_FACTOR));
        }
        List<Resource> resources = getResources();
        for (Resource res : resources) {
            Object obj = res.getPath();
            Object data = res.getData();
            if (res.getProfile().equals(Profile.PROFILE_TABULAR_DATA_RESOURCE)) {
                if (obj instanceof Collection) {
                    for (Object pathHolder : ((Collection) obj)) {
                        String pathName = pathHolder.toString();
                    }
                } else if (res.getProfile().equals(Profile.PROFILE_TABULAR_DATA_RESOURCE)) {

                }
                //File resourceFile = new File(dir, res.getPath())
            }
        }
    }

    /*private static void writeSingleResource() {

    }*/

   /* private List<Resource> resolveResources() {
        List<Resource> resources = packageInfo.getResources();
        for (Resource res : resources) {
            String resContent = res.getData().toString();
        }
    }*/


    public Resource getResource(String resourceName){
        Iterator<Resource> iter = packageInfo.getResources().iterator();
        while(iter.hasNext()) {
            Resource resource = iter.next();
            if(resource.getName().equalsIgnoreCase(resourceName)){
                return resource;
            }
        }
        return null;
    }

    public List<Resource> getResources(){
        return packageInfo.getResources();
    }

}
