package BitwiseStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;


/**
 * A stream where bits can be written to.
 * End aligned with 0's up to finish a byte.
 */
public final class BitOutputStream implements AutoCloseable {
    private final OutputStream output;
    /**
     * a byte buffer accumulating bits to be output to stream
     */
    private int byteBuffer;
    /**
     * Number of bits in the current byte
     */
    private int numBitsInBuffer;


    /**
     * Constructs a bitOutput stream based on the specified byte output stream.
     *
     * @param out the byte output stream
     * @throws NullPointerException if the output stream is {@code null}
     */
    public BitOutputStream(OutputStream out) {
        output = Objects.requireNonNull(out);
        byteBuffer = 0;
        numBitsInBuffer = 0;
    }


    /**
     * Writes a bit to the stream.
     *
     * @param b the bit to write, which must be 0 or 1
     */
    public void write(int b) throws IOException {
        if (b != 0 && b != 1) throw new IllegalArgumentException("Bit must be 0 or 1");
        // push_back to the right
        byteBuffer = (byteBuffer << 1) | b;
        numBitsInBuffer++;
        // filled a byte
        if (numBitsInBuffer == 8) {
            // flush
            output.write(byteBuffer);
            byteBuffer = 0;
            numBitsInBuffer = 0;
        }
    }


    /**
     * Close this stream.
     * leave the output working.
     * If not aligned yet, align with 0.
     */
    public void close() throws IOException {
        while (numBitsInBuffer != 0) write(0);
    }

}
