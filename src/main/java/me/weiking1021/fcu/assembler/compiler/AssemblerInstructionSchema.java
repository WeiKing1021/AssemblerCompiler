package me.weiking1021.fcu.assembler.compiler;

import java.util.Set;

public class AssemblerInstructionSchema {

    final String mnemonic;
    final Set<AssemblerFormat> formats;
    final short opcode;

    private AssemblerInstructionSchema(String mnemonic, Set<AssemblerFormat> formats, short opcode, String notes) {

        this.mnemonic = mnemonic;
        this.formats = formats;
        this.opcode = opcode;
    }

    public static AssemblerInstructionSchema create(String mnemonic, short opcode, String notes, AssemblerFormat... formats) {

        return new AssemblerInstructionSchema(mnemonic, Set.of(formats), opcode, notes);
    }
}
