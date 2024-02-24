package FileProcess;

/**
 * StopWatch in seconds
 */
public class StopWatch {
    private final long startTime;

    public StopWatch() {
        startTime = System.currentTimeMillis();
    }

    public double getRunningSeconds() {
        long currentTime = System.currentTimeMillis();
        long runningTimeMilliseconds = currentTime - startTime;
        return runningTimeMilliseconds / 1000.0; // Convert milliseconds to seconds
    }
}
