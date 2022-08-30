package me.weiking1021.fcu.assembler.compiler;

public class AssemblerOperand {

    private static final char TOKEN_INDIRECT_ADR = '@';
    private static final char TOKEN_IMMEDIATE_ADR = '#';
    private static final String TOKEN_INDEXED_ADR = ",X";

    final boolean indirectAddressing;
    final boolean immediateAddressing;
    final boolean indexedAddressing;
    final String value;

    public AssemblerOperand(boolean indirectAddressing, boolean immediateAddressing, boolean indexedAddressing, String value) {

        this.indirectAddressing = indirectAddressing;
        this.immediateAddressing = immediateAddressing;
        this.indexedAddressing = indexedAddressing;
        this.value = value;
    }

    public static AssemblerOperand parse(String operand) {

        boolean indirectAddressing = false;
        boolean immediateAddressing = false;
        boolean indexedAddressing = false;

        if (operand.charAt(0) == TOKEN_INDIRECT_ADR)
            indirectAddressing = true;

        if (operand.charAt(0) == TOKEN_IMMEDIATE_ADR)
            immediateAddressing = true;

        if (operand.endsWith(TOKEN_INDEXED_ADR))
            indexedAddressing = true;

        if (indirectAddressing)
            operand = operand.substring(1);

        if (immediateAddressing)
            operand = operand.substring(1);

        if (indexedAddressing)
            operand = operand.substring(0, operand.length() - TOKEN_INDEXED_ADR.length());

        return new AssemblerOperand(indirectAddressing, immediateAddressing, indexedAddressing, operand);
    }
}
