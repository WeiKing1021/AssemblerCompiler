package me.weiking1021.fcu.assembler.compiler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AssemblerIO {

    private static final String OUTPUT_DIR = "/object_code";

    private static File getRoot() {

        return new File(".");
    }

    public static File getOutputDirectory() {

        return new File(getRoot(), OUTPUT_DIR);
    }

    public static String fileNameWithoutExt(String fileName) {

        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public static FileInputStream createInputStream(String path) throws FileNotFoundException {

        return new FileInputStream(new File(getRoot(), path));
    }

    public static FileOutputStream createOutputStream(String path) throws IOException {

        File outputFile = new File(getOutputDirectory(), path);

        // delete first
        assert !outputFile.exists() || outputFile.delete();

        // create new file
        assert outputFile.exists() || outputFile.mkdirs() && outputFile.createNewFile();

        return new FileOutputStream(outputFile);
    }

    public static List<String> readSourceCodes(InputStream inputStream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        List<String> resultList = new ArrayList<>();;

        try {

            String next;
            while ((next = reader.readLine()) != null) {

                resultList.add(next);
            }
        }
        finally {

            reader.close();
        }

        return resultList;
    }
}
