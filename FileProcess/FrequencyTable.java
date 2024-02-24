package FileProcess;


/**
 * A table of bytePattern frequencies, mainly based on int[]
 * <p>
 * Protect against array outbound and int_overflow
 */
public final class FrequencyTable {
    /**
     * A byte is represented in int [0,255].
     * <p>
     * Reserve 256 for EOF.
     * <p>
     * 257 patterns in total.
     */
    public static final int BYTE_PATTERNS_NUM = 257;
    private final int[] frequencies;

    /**
     * Construct a frequency table with length BYTE_PATTERNS_NUM
     */
    public FrequencyTable() {
        frequencies = new int[BYTE_PATTERNS_NUM];
    }


    /**
     * Increment the frequency of the specified bytePattern in this frequency table.
     */
    public void increment(int bytePattern) {
        isValidBytePattern(bytePattern);
        // protect against int overflow
        if (frequencies[bytePattern] == Integer.MAX_VALUE) throw new IllegalStateException("Frequency int overflow");
        frequencies[bytePattern]++;
    }


    /**
     * protect against array out of bounds
     */
    private void isValidBytePattern(int bytePattern) {
        if (bytePattern < 0 || bytePattern >= frequencies.length)
            throw new IllegalArgumentException("Symbol out of range");
    }

    /**
     * return the frequency of input bytePattern
     */
    public int get(int bytePattern) {
        isValidBytePattern(bytePattern);
        return frequencies[bytePattern];
    }

}
