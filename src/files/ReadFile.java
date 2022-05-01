package files;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadFile {
    public static int[][] read(String filename) {
        File file = new File(filename);

        Scanner scanner = null;

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int i=0;
        int j=0;

        int[][] result = new int[40][40];

        while(scanner.hasNextLine()) {
            j = 0;

            String line = scanner.nextLine();

            String content[] = line.split(" ");

            for (String s : content) {
                int value = Integer.parseInt(s);

                result[i][j] = value;

                j++;
            }

            i++;
        }

        return result;
    }
}
