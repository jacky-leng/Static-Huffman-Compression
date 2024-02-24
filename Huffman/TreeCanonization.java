package Huffman;

import FileProcess.FrequencyTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Canonical Huffman code, can only describe the bit length of each symbol.
 * bitLength 0 means no code for the symbol.
 */
public final class TreeCanonization {
    private final int[] lengthsTable;

    /**
     * Constructs a canonical Huffman code from the specified array of symbol code lengths.
     * <p>
     * Each code length must be non-negative. Code length 0 means no code for the symbol.
     * <p>
     * This constructor is for Decode
     *
     * @param lengthsTable array of code lengths of bytePatterns.
     */
    public TreeCanonization(int[] lengthsTable) {
        Objects.requireNonNull(lengthsTable);
        this.lengthsTable = lengthsTable;
    }

    /**
     * Builds a canonical Huffman code from the specified code tree.
     *
     * @param tree the code tree to analyze
     * @throws NullPointerException     if the tree is {@code null}
     * @throws IllegalArgumentException if the symbol limit is less than 2, or a
     *                                  leaf node in the tree has symbol value greater or equal to the symbol limit
     */
    public TreeCanonization(HuffmanTree tree) {
        Objects.requireNonNull(tree);
        if (FrequencyTable.BYTE_PATTERNS_NUM < 2) throw new IllegalArgumentException("At least 2 symbols needed");
        lengthsTable = new int[FrequencyTable.BYTE_PATTERNS_NUM];
        buildCodeLengths(tree.getCodesList());
    }

    /**
     * Returns the maximum value in the given array
     *
     * @param intArray require not empty
     * @throws IllegalArgumentException if the array is empty
     */
    private static int getMaxItemInIntArray(int[] intArray) {
        if (intArray.length == 0) {
            throw new IllegalArgumentException("Empty Array");
        }
        int result = intArray[0];
        for (int x : intArray)
            result = Math.max(x, result);
        return result;
    }


    /**
     * Build a lengthsTable based on codesList
     *
     * @param codes the codesList of the bytePatterns
     */
    private void buildCodeLengths(List<List<Integer>> codes) {
        for (int i = 0; i < codes.size(); ++i) {
            if (codes.get((i)) == null) {
                lengthsTable[i] = 0;
            } else {
                lengthsTable[i] = codes.get(i).size();
            }
        }
    }


    /**
     * @return the code length of the specified bytePattern value, which is non-negative
     * @throws IllegalArgumentException if the bytePattern is out of range
     */
    public int[] getLengthsTable() {
        return lengthsTable;
    }


    /**
     * Build a canonical Huffman with frequencyTable
     * <p>
     * Guarantee to yield same Trees with same frequencyTables
     *
     * @return the root of canonical code tree
     */
    public HuffmanTree getHuffmanTree() {
        List<Node> nodes = new ArrayList<>();

        // merge the nodes until there is only one root node
        for (int maxCodeLength = getMaxItemInIntArray(lengthsTable); maxCodeLength >= 0; maxCodeLength--) {
            nodes = mergeNodes(maxCodeLength, nodes);
        }

        if (nodes.size() != 1) throw new AssertionError("Violation of canonical code invariants");
        return new HuffmanTree((InternalNode) nodes.get(0));
    }


    private List<Node> mergeNodes(int maxLength, List<Node> nodes) {
        List<Node> newNodes = new ArrayList<>();

        if (maxLength > 0) {
            // merge two nodes with the longest lengths
            // if equal, two with the largest index will be selected
            for (int j = 0; j < FrequencyTable.BYTE_PATTERNS_NUM; j++) {
                if (lengthsTable[j] == maxLength) newNodes.add(new Leaf(j));
            }
        }

        // Connect them
        for (int j = 0; j < nodes.size(); j += 2)
            newNodes.add(new InternalNode(nodes.get(j), nodes.get(j + 1)));

        return newNodes;
    }

}
