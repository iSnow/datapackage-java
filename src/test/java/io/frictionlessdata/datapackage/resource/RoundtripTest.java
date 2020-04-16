package io.frictionlessdata.datapackage.resource;

import io.frictionlessdata.datapackage.Dialect;
import io.frictionlessdata.datapackage.Package;
import io.frictionlessdata.datapackage.TestUtil;
import io.frictionlessdata.tableschema.datasourceformat.DataSourceFormat;
import io.frictionlessdata.tableschema.schema.Schema;
import org.apache.commons.csv.CSVFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RoundtripTest {
    private static CSVFormat csvFormat = DataSourceFormat
            .getDefaultCsvFormat()
            .withDelimiter('\t');

    private static String resourceContent = "[\n" +
            "    {\n" +
            "\t  \"city\": \"london\",\n" +
            "\t  \"year\": 2017,\n" +
            "\t  \"population\": 8780000\n" +
            "\t},\n" +
            "\t{\n" +
            "\t  \"city\": \"paris\",\n" +
            "\t  \"year\": 2017,\n" +
            "\t  \"population\": 2240000\n" +
            "\t},\n" +
            "\t{\n" +
            "\t  \"city\": \"rome\",\n" +
            "\t  \"year\": 2017,\n" +
            "\t  \"population\": 2860000\n" +
            "\t}\n" +
            "  ]";

    @Test
    @DisplayName("Roundtrip test - write datapackage, read again and compare data")
    public void dogfoodingTest() throws Exception {
        List<Resource> resources = new ArrayList<>();
        Package pkg = new Package(resources);

        JSONDataResource res = new JSONDataResource("population", resourceContent);
        //set a schema to guarantee the ordering of properties
        Schema schema = Schema.fromJson(
                new File(TestUtil.getBasePath().toFile(), "/schema/population_schema.json"), true);
        res.setSchema(schema);
        res.setShouldSerializeToFile(true);
        res.setSerializationFormat(Resource.FORMAT_CSV);
        res.setDialect(Dialect.fromCsvFormat(csvFormat));
        pkg.addResource(res);

        Path tempDirPath = Files.createTempDirectory("datapackage-");
        File createdFile = new File(tempDirPath.toFile(), "test_save_datapackage.zip");
        pkg.write(createdFile, true);

        Package testPkg = new Package(createdFile.toPath(), true);
        Assertions.assertEquals(1, testPkg.getResources().size());

        Resource testRes = testPkg.getResource("population");
        List testData = testRes.getData(false, true, false, false);
        Resource validationRes = pkg.getResource("population");
        List validationData = validationRes.getData(false, true, false, false);
        Assertions.assertEquals(validationData.size(), testData.size());

        for (int i = 0; i < validationData.size(); i++) {
            Assertions.assertArrayEquals(((Object[])validationData.get(i)), ((Object[])testData.get(i)));

        }
    }

}
