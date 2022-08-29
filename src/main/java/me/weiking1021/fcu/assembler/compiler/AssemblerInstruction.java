package me.weiking1021.fcu.assembler.compiler;

import me.weiking1021.fcu.assembler.compiler.exception.AssemblerSyntaxException;

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

    public AssemblerFormat formatType() {

        if (opcode.charAt(0) == TOKEN_EXT_FORMAT)
            return AssemblerFormat.FORMAT_4;

        return AssemblerFormat.FORMAT_3;
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
}
