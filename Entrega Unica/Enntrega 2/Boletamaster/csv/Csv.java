package Boletamaster.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Csv {

    public static List<String[]> readAll(String file, boolean skipHeader) {
        List<String[]> out = new ArrayList<>();
        try {
            Path p = Paths.get(file);
            if (!Files.exists(p)) return out;

            try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
                String line;
                boolean first = true;
                while ((line = br.readLine()) != null) {
                    if (first && skipHeader) { first = false; continue; }
                    if (line.isBlank()) { first = false; continue; }
                    out.add(parse(line));
                    first = false;
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo " + file + ": " + e.getMessage());
        }
        return out;
    }

    public static void writeAll(String file, String header, List<String[]> rows) {
        try {
            Path p = Paths.get(file);
            if (p.getParent() != null && !Files.exists(p.getParent())) {
                Files.createDirectories(p.getParent());
            }
            try (BufferedWriter bw = Files.newBufferedWriter(
                    p, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

                if (header != null) { bw.write(header); bw.newLine(); }
                for (String[] r : rows) {
                    bw.write(serialize(r));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error escribiendo " + file + ": " + e.getMessage());
        }
    }

    // ----- helpers -----
    private static String[] parse(String line) {
        ArrayList<String> cols = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQ = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQ) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') { sb.append('"'); i++; }
                    else inQ = false;
                } else sb.append(c);
            } else {
                if (c == ',') { cols.add(sb.toString()); sb.setLength(0); }
                else if (c == '"') { inQ = true; }
                else sb.append(c);
            }
        }
        cols.add(sb.toString());
        return cols.toArray(new String[0]);
    }

    private static String serialize(String[] cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escape(cols[i]));
        }
        return sb.toString();
    }

    private static String escape(String v) {
        if (v == null) v = "";
        boolean needQ = v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r");
        String w = v.replace("\"", "\"\"");
        return needQ ? "\"" + w + "\"" : w;
    }
}