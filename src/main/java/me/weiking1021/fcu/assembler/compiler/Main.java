package me.weiking1021.fcu.assembler.compiler;

import java.io.*;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        FileInputStream input = AssemblerIO.createInputStream(args[0]);
        FileOutputStream output = AssemblerIO.createOutputStream(AssemblerIO.fileNameWithoutExt(args[0]) + ".asm");

        List<String> sourceCodes = AssemblerIO.readSourceCodes(input);

        AssemblerCompiler compiler = new AssemblerCompiler();

        sourceCodes.forEach(compiler::write);
    }
}
