package me.snorochevskiy.vault.client.lib.properties;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PropertiesFilePatcher {

    public static void patch(String file, Map<String, Object> pairs) throws IOException {
        System.out.println("Patching: " + file);
        List<String> lines = Files.lines(Paths.get(file))
                .collect(Collectors.toList());

        Map<String, Object> toAdd = new HashMap<>(pairs);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int eqInd = line.indexOf("=");
            if (eqInd == -1) {
                continue;
            }
            String propName = line.substring(eqInd).trim();
            if (!pairs.containsKey(propName) || !(pairs.get(propName) instanceof String)) {
                continue;
            }
            toAdd.remove(propName);
            lines.set(i, propName + "=" + pairs.get(propName));
        }

        for (Map.Entry entry : toAdd.entrySet()) {
            if (entry.getValue() instanceof String) {
                lines.add(entry.getKey() + "=" + entry.getValue());
            }
        }
        Files.write(Paths.get(file), lines, Charset.defaultCharset());
    }
}
