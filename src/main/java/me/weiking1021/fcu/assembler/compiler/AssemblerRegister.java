package me.weiking1021.fcu.assembler.compiler;

public enum AssemblerRegister {

    A((short) 0),
    X((short) 1),
    L((short) 2),
    PC((short) 8),
    SW((short) 9),
    B((short) 3),
    S((short) 4),
    T((short) 5),
    F((short) 6),
    ;

    public final short address;

    AssemblerRegister(short address) {

        this.address = address;
    }
}
