package io.frictionlessdata.datapackage.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class JsonUtils {

    public static String getJsonStringContentFromInputStream(InputStream stream, boolean strictValidation) {
        List<String> lines = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.toList());
        // in non-strict mode, check whether the JSON texts starts with a BOM, and remove if so
        if (!strictValidation && !lines.isEmpty()) {
            lines.set(0, lines.get(0).replaceFirst("\\uFEFF", ""));
        }
        return String.join("\n", lines);
    }
}
