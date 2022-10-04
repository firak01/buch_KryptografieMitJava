public class ArrayUtil {
    private ArrayUtil() {} // static methods only
    private static final int ZEROES_LEN = 500; // adjust for memory/speed tradeoff
    private static byte[] zeroes = new byte[ZEROES_LEN];
    public static void clear (byte[] buf) { clear(buf, 0, buf.length); }
    public static void clear (byte[] buf, int offset, int length) {
        if (length <= ZEROES_LEN)
            System.arraycopy(zeroes, 0, buf, offset, length);
        else {
            System.arraycopy(zeroes, 0, buf, offset, ZEROES_LEN);
            int halflength = length / 2;
            for (int i = ZEROES_LEN; i < length; i += i) {
                System.arraycopy(buf, offset, buf, offset+i,
                    (i <= halflength) ? i : length-i);
            }
        }
    }
    public static int toInt(short s0, short s1) { return (s0 & 0xFFFF) | (s1 << 16); }
    public static short toShort(byte b0, byte b1) { return (short) (b0 & 0xFF | b1 << 8); }
    public static byte[] toBytes(int n) {
        byte[] buf = new byte[4];
            
        for (int i = 3; i >= 0; i--) {
            buf[i] = (byte) (n & 0xFF);
            n >>>= 8;
        }
        return buf;
    }
    public static byte[] toBytes(short[] array, int offset, int length) {
        byte[] buf = new byte[2 * length];
        int j = 0;

        for (int i = offset; i < offset + length; i++) {
            buf[j++] = (byte) ((array[i] >>> 8) & 0xFF);
            buf[j++] = (byte) (array[i] & 0xFF);
        }
        return buf;
    }
    public static byte[] toBytes(short[] array) { return toBytes(array, 0, array.length); }
    public static short[] toShorts(byte[] array, int offset, int length) {
        short[] buf = new short[length / 2];
        int j = 0;

        for (int i = offset; i < offset + length - 1; i += 2)
            buf[j++] = (short) (((array[i] & 0xFF) << 8) | (array[i + 1] & 0xFF));

        return buf;
    }
    public static short[] toShorts(byte[] array) { return toShorts(array, 0, array.length); }
    public static boolean areEqual(byte[] a, byte[] b) {
        int aLength = a.length;
        if (aLength != b.length) return false;
        for (int i = 0; i < aLength; i++)
            if (a[i] != b[i]) return false;
        return true;
    }
    public static boolean areEqual(int[] a, int[] b) {
        int aLength = a.length;
        if (aLength != b.length) return false;
        for (int i = 0; i < aLength; i++)
            if (a[i] != b[i]) return false;
        return true;
    }
    public static int compared (byte[] a, byte[] b, boolean msbFirst) {
        int aLength = a.length;
        if (aLength < b.length) return -1;
        if (aLength > b.length) return 1;
        int b1, b2;
        if (msbFirst)
            for (int i = aLength - 1; i >= 0; i--) {
                b1 = a[i] & 0xFF;
                b2 = b[i] & 0xFF;
                if (b1 < b2) return -1;
                if (b1 > b2) return 1;
            }
        else
            for (int i = 0; i < aLength; i++) {
                b1 = a[i] & 0xFF;
                b2 = b[i] & 0xFF;
                if (b1 < b2) return -1;
                if (b1 > b2) return 1;
            }
        return 0;
    }
    public static boolean isText (byte[] buffer) {
        int len = buffer.length;
        if (len == 0) return false;
        for (int i = 0; i < len; i++) {
            int c = buffer[i] & 0xFF;
            if (c < '\u0020' || c > '\u007F')
                switch (c) {  // control chars that are allowed in text files
                    case '\u0007':  // BEL
                    case '\u0008':  // BS
                    case '\t':      // HT
                    case '\n':      // LF
                    case '\u000B':  // VT
                    case '\u000C':  // FF
                    case '\r':      // CR
                    case '\u001A':  // EOF
                    case '\u001B':  // ESC
                    case '\u009B':  // CSI
                        continue;
                    default: // anything else, isn't.
                        return false;
                }
        }
        return true;
    }
}