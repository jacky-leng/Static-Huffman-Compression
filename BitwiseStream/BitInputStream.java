package BitwiseStream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


public final class BitInputStream implements AutoCloseable {
    private final InputStream input;
    private int byteBuffer;
    private int numBitsRemaining;

    /**
     * Constructs a bit stream based on the specified byte input stream.
     */
    public BitInputStream(InputStream in) {
        input = Objects.requireNonNull(in);
        byteBuffer = 0;
        numBitsRemaining = 0;
    }


    /**
     * Reads a bit from this stream.
     *
     * @return the next bit, only 0 or 1 possible
     * @throws EOFException if reached the end of input
     */
    public int read() throws IOException {
        int result = readHelper();
        if (result != -1) return result;
        else throw new EOFException();
    }


    /**
     * destroy the stream by setting byteBuffer = -1
     * leave the underlying input stream for future usage
     */
    public void close() {
        byteBuffer = -1;
        numBitsRemaining = 0;
    }


    /**
     * Reads a bit from this stream. Returns 0 or 1 if a bit is available, or -1 if
     * the end of stream is reached. The end of stream always occurs on a byte boundary.
     *
     * @return the next bit of 0 or 1, or -1 for the end of stream
     */
    private int readHelper() throws IOException {
        if (byteBuffer == -1) return -1;
        if (numBitsRemaining == 0) {
            byteBuffer = input.read();
            if (byteBuffer == -1) return -1;
            numBitsRemaining = 8;
        }
        if (numBitsRemaining <= 0) throw new AssertionError();
        numBitsRemaining--;
        return (byteBuffer >>> numBitsRemaining) & 1;
    }

}
