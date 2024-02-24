package FileProcess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Print given strings of filePath in a tree style
 */
public class PathTreePrint {
    /**
     * Print out all paths in tree style to System. out
     *
     * @param paths List of paths
     */
    public static void printTree(List<String> paths) {
        PathTreePrint ptt = new PathTreePrint();
        PathTreePrint.fileNode rootNode = ptt.buildTree(paths);
        System.out.print("File Structure:");
        ptt.printTreeHelper(rootNode, "", true);
    }

    private void printTreeHelper(fileNode fileNode, String prefix, boolean isRoot) {
        System.out.println(prefix + fileNode.name);
        if (isRoot) {
            for (fileNode child : fileNode.subPaths.values()) {
                printTreeHelper(child, prefix, false);
            }
        } else {
            for (fileNode child : fileNode.subPaths.values()) {
                printTreeHelper(child, prefix + "│---", false);
            }
        }
    }


    /**
     * build a trie with List of paths
     *
     * @param paths in String
     * @return root node of this trie
     */
    private fileNode buildTree(List<String> paths) {
        fileNode root = new fileNode("");
        for (String path : paths) {
            fileNode current = root;
            String[] names = path.split("\\\\");
            for (String name : names) {
                current.subPaths.putIfAbsent(name, new fileNode(name));
                current = current.subPaths.get(name);
            }
        }
        return root;
    }


    /**
     * a Path, with his subPaths as subPaths node
     */
    private static class fileNode {
        /**
         * a node is a word in path
         */
        final String name;
        /**
         * subDirectory under this path
         */
        final Map<String, fileNode> subPaths = new HashMap<>();

        fileNode(String name) {
            this.name = name;
        }
    }
}
