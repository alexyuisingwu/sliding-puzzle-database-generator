package alexyuisingwu.util;


import java.util.Arrays;

// NOTE: NO error checking included
public class ArrayUtils {

    public static int[] createIntArray(int length, int fillValue) {
        int[] output = new int[length];
        Arrays.fill(output, fillValue);
        return output;
    }

    public static byte[] createByteArray(int length, byte fillValue) {
        byte[] output = new byte[length];
        Arrays.fill(output, fillValue);
        return output;
    }
}
