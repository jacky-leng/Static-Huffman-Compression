package Huffman;


import FileProcess.FrequencyTable;

import java.util.*;

public final class HuffmanTree {

    /**
     * The root node of this Huffman (require not null)
     * <p>
     * Used for decoding
     */
    private final InternalNode root;

    /**
     * Stores the code for each symbol, or null if the symbol has no code.
     * <p>
     * Used for Encoding
     */
    private final List<List<Integer>> codes;

    /**
     * Constructs a code tree from the specified tree root
     *
     * @param root the root of the tree
     * @throws NullPointerException if tree root is null
     */
    public HuffmanTree(InternalNode root) {
        this.root = Objects.requireNonNull(root);
        if (FrequencyTable.BYTE_PATTERNS_NUM < 2) throw new IllegalArgumentException("At least 2 symbols needed");

        codes = new ArrayList<>();
        for (int i = 0; i < FrequencyTable.BYTE_PATTERNS_NUM; i++)
            codes.add(null);
        buildCodeList(root, new ArrayList<>());
    }


    public HuffmanTree(FrequencyTable frequencyTable) {
        root = buildHuffmanTree(frequencyTable);
        codes = new ArrayList<>();  // Initially all null
        for (int i = 0; i < FrequencyTable.BYTE_PATTERNS_NUM; i++)
            codes.add(null);
        buildCodeList(root, new ArrayList<>());  // Fill 'codes' with appropriate data
    }


    /**
     * Build a Huffman
     *
     * @return root node of the tree
     */
    private static InternalNode buildHuffmanTree(FrequencyTable frequencyTable) {
        Queue<nodeWithFrequency> pq = new PriorityQueue<>();

        // Add non zero bytePatterns
        for (int i = 0; i < FrequencyTable.BYTE_PATTERNS_NUM; i++) {
            if (frequencyTable.get(i) > 0) pq.add(new nodeWithFrequency(new Leaf(i), frequencyTable.get(i)));
        }

        // To avoid empty file conner case add nodes, until the tree has two leaves
        // Considering EOF, we should have at least one node, but use a loop to be secure
        for (int i = 0; i < FrequencyTable.BYTE_PATTERNS_NUM && frequencyTable.get(i) == 0 && pq.size() < 2; ++i) {
            pq.add(new nodeWithFrequency(new Leaf(i), 0));
        }

        // Repeatedly tie together two nodes with the lowest frequency
        while (pq.size() > 1) {
            nodeWithFrequency x = pq.remove();
            nodeWithFrequency y = pq.remove();
            pq.add(new nodeWithFrequency(new InternalNode(x.node, y.node), x.frequency + y.frequency));
        }

        // Return the root of Huffman
        return (InternalNode) pq.remove().node;
    }

    /**
     * Recursive helper function for the constructor
     */
    private void buildCodeList(Node node, List<Integer> prefix) {
        if (node instanceof InternalNode internalNode) {
            prefix.add(0);
            buildCodeList(internalNode.leftChild(), prefix);
            prefix.remove(prefix.size() - 1);

            prefix.add(1);
            buildCodeList(internalNode.rightChild(), prefix);
            prefix.remove(prefix.size() - 1);

        } else if (node instanceof Leaf leaf) {
            if (leaf.symbol() >= codes.size()) throw new IllegalArgumentException("Symbol exceeds symbol limit");
            if (codes.get(leaf.symbol()) != null) throw new IllegalArgumentException("Symbol has more than one code");
            codes.set(leaf.symbol(), new ArrayList<>(prefix));
        }
    }


    public List<List<Integer>> getCodesList() {
        return codes;
    }

    public InternalNode getRoot() {
        return root;
    }
}
