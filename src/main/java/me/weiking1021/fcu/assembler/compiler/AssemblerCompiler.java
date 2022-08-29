package me.weiking1021.fcu.assembler.compiler;

import me.weiking1021.fcu.assembler.compiler.exception.AssemblerSyntaxException;

public class AssemblerCompiler {

    private short locationCounter;

    public AssemblerCompiler() {

        this.locationCounter = -1;
    }

    public AssemblerInstruction write(String nextLine) {

        AssemblerInstruction instruction = AssemblerInstruction.parse(nextLine);

        if (instruction == null)
            return null;

        return instruction;
    }
}
