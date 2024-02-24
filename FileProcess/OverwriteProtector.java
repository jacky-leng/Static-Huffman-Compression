package FileProcess;

import java.io.File;
import java.util.Scanner;

public class OverwriteProtector {
    private boolean overwriteAll;

    public OverwriteProtector() {
        overwriteAll = false;
    }

    public void askUser(File f) {
        if(overwriteAll){
            return;
        }
        Scanner scanner = new Scanner(System.in);
        String fileClass;
        if (f.isDirectory()) {
            fileClass = "Directory: ";
        } else {
            fileClass = "File: ";
        }
        System.out.println(fileClass + f + " already exists.");
        System.out.println("Do you want to overwrite it? (y/n)  Overwrite All files? (a)");
        String input = scanner.nextLine();

        while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n") && !input.equalsIgnoreCase("a")) {
            System.out.println("Invalid input. 'y' to overwrite, 'n' to terminate, 'a' to overwrite all");
            input = scanner.nextLine();
        }

        if(input.equalsIgnoreCase("a")){
            overwriteAll = true;
        }
        if (input.equalsIgnoreCase("n")) {
            System.out.println("Program terminated.");
            System.exit(0);
        }
    }

}
