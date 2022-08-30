package me.weiking1021.fcu.assembler.compiler;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
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

    public static List<String> writeObjectCodes(OutputStream outputStream, AssemblerObjectCode objectCode) throws IOException {

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        writer.write("H");
        writer.write(String.format("%6s", objectCode.programName));
        writer.write(ByteUtil.toHexText(objectCode.startAddress, 3));
        writer.write(ByteUtil.toHexText(objectCode.programByteLength, 3));
        writer.write(System.lineSeparator());

        Iterator<AssemblerObjectCode.TextRecord> iterator = objectCode.textRecords.iterator();

        int bytesCounter = 0;
        List<AssemblerObjectCode.TextRecord> recordTmp = new ArrayList<>();

        while (iterator.hasNext()) {

            AssemblerObjectCode.TextRecord next = iterator.next();

            if (bytesCounter + next.objects().length * 2 < 60) {

                recordTmp.add(next);
                bytesCounter += next.objects().length * 2;

                continue;
            }

            AssemblerObjectCode.TextRecord first = recordTmp.get(0);

            writer.write("T");
            writer.write(ByteUtil.toHexText(first.address(), 3));
            writer.write(ByteUtil.toHexText(bytesCounter, 1));

            for (AssemblerObjectCode.TextRecord textRecord : recordTmp)
                writer.write(ByteUtil.toHexText(textRecord.objects()));

            writer.write(System.lineSeparator());

            bytesCounter = 0;
            recordTmp.clear();
        }

        if (!recordTmp.isEmpty()) {

            AssemblerObjectCode.TextRecord first = recordTmp.get(0);

            writer.write("T");
            writer.write(ByteUtil.toHexText(first.address(), 3));
            writer.write(ByteUtil.toHexText(bytesCounter, 1));

            for (AssemblerObjectCode.TextRecord textRecord : recordTmp)
                writer.write(ByteUtil.toHexText(textRecord.objects()));

            writer.write(System.lineSeparator());
        }

        writer.write("E");
        writer.write(ByteUtil.toHexText(objectCode.startAddress, 3));

        writer.close();

        return null;
    }
}
