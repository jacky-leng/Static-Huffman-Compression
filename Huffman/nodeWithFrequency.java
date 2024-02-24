package Huffman;

/**
 * Node class used in Huffman
 */
public class nodeWithFrequency implements Comparable<nodeWithFrequency> {

    // Helper structure for buildCodeTree()
    public final Node node;
    public final long frequency;

    public nodeWithFrequency(Node nd, long freq) {
        node = nd;
        frequency = freq;
    }


    /**
     * Sort by ascending frequency
     */
    public int compareTo(nodeWithFrequency other) {
        if (frequency < other.frequency) return -1;
        else return 1;
    }


}
