package me.weiking1021.fcu.assembler.compiler;

import java.util.HashMap;
import java.util.Map;
import static me.weiking1021.fcu.assembler.compiler.AssemblerInstructionSchema.*;
import static me.weiking1021.fcu.assembler.compiler.AssemblerFormat.*;

public class AssemblerInstructionSetTable {

    public static final Map<String, AssemblerInstructionSchema> INST_MAP = new HashMap<>();

    static {

        put(create("ADD", (short) 0x18, null, FORMAT_3, FORMAT_4));
        put(create("ADDF", (short) 0x58, "XF", FORMAT_3, FORMAT_4));
        put(create("ADDR", (short) 0x90, "X", FORMAT_2));
        put(create("AND", (short) 0x40, null, FORMAT_2));
        put(create("CLEAR", (short) 0xB4, "X", FORMAT_2));
        put(create("COMP", (short) 0x28, "C", FORMAT_3, FORMAT_4));
        put(create("COMPF", (short) 0x88, "XFC", FORMAT_3, FORMAT_4));
        put(create("COMPR", (short) 0xA0, "XC", FORMAT_2));
        put(create("DIV", (short) 0x24, null, FORMAT_3, FORMAT_4));
        put(create("DIVF", (short) 0x64, "XF", FORMAT_3, FORMAT_4));
        put(create("DIVR", (short) 0x9C, "X", FORMAT_2));
        put(create("FIX", (short) 0xC4, "XF", FORMAT_1));
        put(create("FLOAT", (short) 0xC0, "XF", FORMAT_1));
        put(create("HIO", (short) 0xF4, "PX", FORMAT_1));
        put(create("J", (short) 0x3C, null, FORMAT_3, FORMAT_4));
        put(create("JEQ", (short) 0x30, null, FORMAT_3, FORMAT_4));
        put(create("JGT", (short) 0x34, null, FORMAT_3, FORMAT_4));
        put(create("JLT", (short) 0x38, null, FORMAT_3, FORMAT_4));
        put(create("JSUB", (short) 0x48, null, FORMAT_3, FORMAT_4));
        put(create("LDA", (short) 0x00, null, FORMAT_3, FORMAT_4));
        put(create("LDB", (short) 0x68, "X", FORMAT_3, FORMAT_4));
        put(create("LDCH", (short) 0x50, null, FORMAT_3, FORMAT_4));
        put(create("LDF", (short) 0x70, "XF", FORMAT_3, FORMAT_4));
        put(create("LDL", (short) 0x08, null, FORMAT_3, FORMAT_4));
        put(create("LDS", (short) 0x6C, "X", FORMAT_3, FORMAT_4));
        put(create("LDT", (short) 0x74, "X", FORMAT_3, FORMAT_4));
        put(create("LDX", (short) 0x04, null, FORMAT_3, FORMAT_4));
        put(create("LPS", (short) 0xD0, "PX", FORMAT_3, FORMAT_4));

        put(create("MUL", (short) 0x20, null, FORMAT_3, FORMAT_4));
        put(create("MULF", (short) 0x60, "XF", FORMAT_3, FORMAT_4));
        put(create("MULR", (short) 0x98, "X", FORMAT_2));
        put(create("NORM", (short) 0xC8, "XF", FORMAT_1));
        put(create("OR", (short) 0x44, null, FORMAT_3, FORMAT_4));
        put(create("RD", (short) 0xD8, "P", FORMAT_3, FORMAT_4));
        put(create("RMO", (short) 0xAC, "X", FORMAT_2));
//        put(create("RSUB", (short) 0x4C, null, FORMAT_3, FORMAT_4));
        put(create("RSUB", (short) 0x4C, null, FORMAT_1));
        put(create("SHIFTL", (short) 0xA4, "X", FORMAT_2));
        put(create("SHIFTR", (short) 0xA8, "X", FORMAT_2));
        put(create("SIO", (short) 0xF0, "PX", FORMAT_1));

        put(create("SSK", (short) 0xEC, "PX", FORMAT_3, FORMAT_4));
        put(create("STA", (short) 0x0C, null, FORMAT_3, FORMAT_4));
        put(create("STB", (short) 0x78, "X", FORMAT_3, FORMAT_4));
        put(create("STCH", (short) 0x54, null, FORMAT_3, FORMAT_4));
        put(create("STF", (short) 0x80, "XF", FORMAT_3, FORMAT_4));
        put(create("STI", (short) 0xD4, "PX", FORMAT_3, FORMAT_4));
        put(create("STL", (short) 0x14, null, FORMAT_3, FORMAT_4));
        put(create("STS", (short) 0x7C, "X", FORMAT_3, FORMAT_4));
        put(create("STSW", (short) 0xE8, "P", FORMAT_3, FORMAT_4));
        put(create("STT", (short) 0x84, "X", FORMAT_3, FORMAT_4));
        put(create("STX", (short) 0x10, null, FORMAT_3, FORMAT_4));
        put(create("SUB", (short) 0x1C, null, FORMAT_3, FORMAT_4));
        put(create("SUBF", (short) 0x5C, "XF", FORMAT_3, FORMAT_4));
        put(create("SUBR", (short) 0x94, "X", FORMAT_2));
        put(create("SVC", (short) 0xB0, "X", FORMAT_2));
        put(create("TD", (short) 0xE0, "PC", FORMAT_3, FORMAT_4));
        put(create("TIO", (short) 0xF8, "PXC", FORMAT_1));
        put(create("TIX", (short) 0x2C, "C", FORMAT_3, FORMAT_4));
        put(create("TIXR", (short) 0xB8, "XC", FORMAT_2));
        put(create("WD", (short) 0xDC, "P", FORMAT_3, FORMAT_4));
//        put(create("ADD", (short) 0x18, null, FORMAT_3, FORMAT_4));
    }

    private static void put(AssemblerInstructionSchema schema) {

        INST_MAP.put(schema.mnemonic, schema);
    }

    public static AssemblerInstructionSchema get(String mnemonic) {

        return INST_MAP.get(mnemonic);
    }
}
