package me.weiking1021.fcu.assembler.compiler;

public enum AssemblerFormat {

    FORMAT_1((byte) 1),
    FORMAT_2((byte) 2),
    FORMAT_3((byte) 3),
    FORMAT_4((byte) 4),
    ;


    final byte bytes;

    AssemblerFormat(byte bytes) {

        this.bytes = bytes;
    }
}
