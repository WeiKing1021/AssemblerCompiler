package me.weiking1021.fcu.assembler.compiler;

import me.weiking1021.fcu.assembler.compiler.exception.AssemblerInstructionException;
import me.weiking1021.fcu.assembler.compiler.exception.AssemblerSyntaxException;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AssemblerInstruction {

    private static final char TOKEN_COMMENT = '.';
    private static final char TOKEN_SPLIT = ' ';
    private static final char TOKEN_STRING_BUCKET = '\'';

    private static final char TOKEN_EXT_FORMAT = '+';

    final String label;
    final String opcode;
    final String operand;
    final String comment;

    public AssemblerInstruction(String label, String opcode, String operand, String comment) {

        this.label = label;
        this.opcode = opcode;
        this.operand = operand;
        this.comment = comment;
    }

    public AssemblerFormat parseFormatType() {

        if (opcode.charAt(0) == TOKEN_EXT_FORMAT) {

            String originOpcode = opcode.substring(1);

            AssemblerInstructionSchema schema = AssemblerInstructionSetTable.INST_MAP.get(originOpcode);

            if (schema == null)
                throw new AssemblerInstructionException("Undefined opcode '" + opcode + "'");

            if (!schema.formats.contains(AssemblerFormat.FORMAT_4))
                throw new AssemblerInstructionException("Unsupported format '" + AssemblerFormat.FORMAT_4 +
                    "' with opcode '" + opcode + "'");

            return AssemblerFormat.FORMAT_4;
        }

        AssemblerInstructionSchema schema = AssemblerInstructionSetTable.INST_MAP.get(opcode);

        if (schema == null)
            throw new AssemblerInstructionException("Undefined opcode '" + opcode + "'");

        if (schema.formats.isEmpty())
            throw new AssemblerInstructionException("Empty format set");

        if (schema.formats.size() == 1) {

            AssemblerFormat format = schema.formats.iterator().next();

            if (format == AssemblerFormat.FORMAT_4)
                throw new AssemblerInstructionException("Unsupported format '" + AssemblerFormat.FORMAT_4 +
                    "' with opcode '" + opcode + "'");

            return format;
        }

        Set<AssemblerFormat> resultSet = schema.formats.stream()
            .filter(format -> format != AssemblerFormat.FORMAT_4)
            .collect(Collectors.toSet());

        if (resultSet.size() !=1)
            throw new AssemblerInstructionException("Multiple formats predict with instruction: " + this);

        return resultSet.iterator().next();
    }

    public static AssemblerInstruction parse(String nextLine) {

        if (nextLine.isEmpty())
            return null;

        if (nextLine.charAt(0) == TOKEN_COMMENT)
            return null;

        String sourceSyntax = nextLine;
        String label = null;
        String opcode = null;
        String operand = null;
        String comment = null;

        if (sourceSyntax.charAt(0) != TOKEN_SPLIT) {

            int index = lastIndexBeforeChar(sourceSyntax, TOKEN_SPLIT);

            if (index < 0)
                throw new AssemblerSyntaxException(nextLine);

            label = sourceSyntax.substring(0, index + 1);

            sourceSyntax = sourceSyntax.substring(index + 1);
        }

        // strip split token
        int opcodeIndexStart = firstIndexNotChar(sourceSyntax, TOKEN_SPLIT);
        sourceSyntax = sourceSyntax.substring(opcodeIndexStart);

        int opcodeIndexEnd = lastIndexBeforeChar(sourceSyntax, TOKEN_SPLIT);
        if (opcodeIndexEnd < 0)
            throw new AssemblerSyntaxException(nextLine);

        opcode = sourceSyntax.substring(0, opcodeIndexEnd + 1);
        sourceSyntax = sourceSyntax.substring(opcodeIndexEnd + 1);

        // strip split token
        int operandIndexStart = firstIndexNotChar(sourceSyntax, TOKEN_SPLIT);
        sourceSyntax = sourceSyntax.substring(operandIndexStart);

        int operandIndexEnd = lastIndexBeforeChar(sourceSyntax, TOKEN_SPLIT, TOKEN_STRING_BUCKET);
        if (operandIndexEnd < 0)
            throw new AssemblerSyntaxException(nextLine);

        operand = sourceSyntax.substring(0, operandIndexEnd + 1);
        sourceSyntax = sourceSyntax.substring(operandIndexEnd + 1);

        // strip split token
        int commentIndexStart = firstIndexNotChar(sourceSyntax, TOKEN_SPLIT);
        if (0 < commentIndexStart)
            comment = sourceSyntax.substring(commentIndexStart);

        return new AssemblerInstruction(label, opcode, operand, comment);
    }

    public static byte[] genFormat1Code(short op) {

        int value = 0;

        value += op;

        return ByteUtil.toByteArray(value, 1);
    }

    public static byte[] genFormat2Code(short op, short r1, short r2) {

        int value = 0;

        value += ((int) op) << 8;
        value += ((int) r1) << 4;
        value += ((int) r2);

        return ByteUtil.toByteArray(value, 2);
    }

    public static byte[] genFormat3Code(short op, boolean n, boolean i, boolean x, boolean b, boolean p, boolean e, int disp) {

        int value = 0;

        value += ((int) op) << 16;
        value += (n ? 1 : 0) << 17;
        value += (i ? 1 : 0) << 16;
        value += (x ? 1 : 0) << 15;
        value += (b ? 1 : 0) << 14;
        value += (p ? 1 : 0) << 13;
        value += (e ? 1 : 0) << 12;
        value += disp;

        return ByteUtil.toByteArray(value, 3);
    }

    public static byte[] genFormat4Code(short op, boolean n, boolean i, boolean x, boolean b, boolean p, boolean e, int disp) {

        int value = 0;

        value += ((int) op) << 24;
        value += (n ? 1 : 0) << 25;
        value += (i ? 1 : 0) << 24;
        value += (x ? 1 : 0) << 23;
        value += (b ? 1 : 0) << 22;
        value += (p ? 1 : 0) << 21;
        value += (e ? 1 : 0) << 20;
        value += disp;

        return ByteUtil.toByteArray(value, 4);
    }

    private static int lastIndexBeforeChar(String input, char endChar, char bucket) {

        char[] chars = input.toCharArray();

        int index = -1;

        boolean leftBucket = false;

        for (int i = 0; i < chars.length; i++) {

            if (chars[i] == bucket)
                leftBucket = !leftBucket;

            if (leftBucket || chars[i] != endChar)
                index = i;
            else
                break;
        }

        if (leftBucket)
            return -1;

        return index;
    }

    private static int lastIndexBeforeChar(String input, char endChar) {

        char[] chars = input.toCharArray();

        int index = -1;

        for (int i = 0; i < chars.length; i++) {

            if (chars[i] != endChar)
                index = i;
            else
                break;
        }

        return index;
    }

    private static int firstIndexNotChar(String input, char endChar) {

        char[] chars = input.toCharArray();

        for (int i = 0; i < chars.length; i++) {

            if (chars[i] != endChar)
                return i;
        }

        return -1;
    }

    @Override
    public String toString() {
        return "AssemblerInstruction{" +
            "label='" + label + '\'' +
            ", opcode='" + opcode + '\'' +
            ", operand='" + operand + '\'' +
            ", comment='" + comment + '\'' +
            '}';
    }
}
