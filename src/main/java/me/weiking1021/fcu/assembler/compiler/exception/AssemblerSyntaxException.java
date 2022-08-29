package me.weiking1021.fcu.assembler.compiler.exception;

public class AssemblerSyntaxException extends RuntimeException {

    public AssemblerSyntaxException(String line) {

        super("Illegal syntax in input text'" + line + "'");
    }
}
