package me.weiking1021.fcu.assembler.compiler;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {

        FileInputStream input = AssemblerIO.createInputStream(args[0]);
        FileOutputStream output = AssemblerIO.createOutputStream(AssemblerIO.fileNameWithoutExt(args[0]) + ".asm");

        AssemblerCompiler compiler = new AssemblerCompiler();

        AssemblerIO.readSourceCodes(input).forEach(compiler::writeSourceCode);

        compiler.compile();
    }
}
