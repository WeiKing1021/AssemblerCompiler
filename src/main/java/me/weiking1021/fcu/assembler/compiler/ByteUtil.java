package me.weiking1021.fcu.assembler.compiler;

public class ByteUtil {

    private static final int MASK = (1 << Byte.SIZE);

    public static byte[] toByteArray(int value, int length) {

        byte[] result = new byte[length];

        for (int i = 0; i < length; i++) {

            result[i] = (byte) (value % MASK & 0xFF);
            value /= MASK;
        }

        return result;
    }

    public static byte[] fromAscii(String asciiText) {

        byte[] result = new byte[asciiText.length()];

        for (int i = asciiText.length() - 1; i >= 0; i--) {

            result[i] = (byte) (asciiText.charAt(asciiText.length() - i - 1) & 0xFF);
        }

        return result;
    }

    public static String toHexText(byte[] bytes) {

        StringBuilder buffer = new StringBuilder();

        for (int i = bytes.length - 1; i >= 0 ; i--) {

            String text = Integer.toHexString(Byte.toUnsignedInt(bytes[i]));

            buffer.append(text.length() % 2 == 0 ? text : '0' + text);
        }

        return buffer.toString();
    }

    public static String toHexText(int value, int length) {

        return toHexText(toByteArray(value, length));
    }
}
