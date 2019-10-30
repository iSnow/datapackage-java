package io.frictionlessdata.datapackage.inputsource;

import io.frictionlessdata.datapackage.Constants;
import io.frictionlessdata.datapackage.exceptions.DataPackageException;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipInputSource implements InputSource<ZipFile> {
    private ZipFile source;
    private URL zipUrl;
    private boolean isRemote = false;

    public ZipInputSource(Path zipFile) throws IOException {
        source = new ZipFile(zipFile.toFile());
    }

    public ZipInputSource(URL zipUrl) throws IOException {
        isRemote = true;
        this.zipUrl = zipUrl;
        /*ZipInputStream zis = new ZipInputStream(zipFile.openStream());
        File tempFile = File.createTempFile("datapackage-", ".zip");
        tempFile.deleteOnExit();
        ReadableByteChannel readChannel = Channels.newChannel(zipFile.openStream());
        FileOutputStream outStream = new FileOutputStream(tempFile);
        FileChannel writeChannel = outStream.getChannel();
        writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);
        writeChannel.close();
        readChannel.close();
        source = new ZipFile(tempFile);*/
    }

    @Override
    public ZipFile getInput() {
        return source;
    }

    @Override
    public void setInput(ZipFile input) {
        source = input;
    }

    @Override
    public String getDescriptorContent() throws Exception {
        String content = null;
        if (isRemote) {
            ZipInputStream zis = new ZipInputStream(zipUrl.openStream());
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                if (zipEntry.getName().equals(Constants.DATAPACKAGE_FILENAME)) {
                    BufferedReader rdr = new BufferedReader(new InputStreamReader(zis));
                    content = rdr.lines().collect(Collectors.joining("\n"));
                    rdr.close();
                    zis.close();
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } else {
            ZipEntry entry = null;
            Enumeration entries = source.entries();
            while (entries.hasMoreElements()) {
                ZipEntry e = (ZipEntry)entries.nextElement();
                if ((e.getName().equals(Constants.DATAPACKAGE_FILENAME))
                    || (e.getName().endsWith("/"+Constants.DATAPACKAGE_FILENAME))) {
                    entry = e;
                    break;
                }
            }
            // Throw exception if expected inputsource.json file not found.
            if (entry == null) {
                throw new DataPackageException("Input zip file does not contain the definition file: " + Constants.DATAPACKAGE_FILENAME);
            }
            InputStream is = source.getInputStream(entry);
            BufferedReader rdr = new BufferedReader(new InputStreamReader(is));
            content = rdr.lines().collect(Collectors.joining("\n"));
            rdr.close();
            is.close();

        }
        return content;
    }


}
