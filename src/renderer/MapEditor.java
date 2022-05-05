package renderer;

import keyboard.InputHandler;
import keyboard.KeyHandler;
import objects.meshes.Light;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MapEditor extends JPanel {
    public static int INPUT_BUFFER = 0;

    private JFrame frame;
    public List<Light> lightList;

    public int[][] floorMap;
    public int[][] ceilMap;
    public int[][] tagMap;

    private int cursorX = 0;
    private int cursorY = 0;

    private int markerAX = 0;
    private int markerAY = 0;
    private int markerBX = 0;
    private int markerBY = 0;

    private int mapMode = 0;

    public void init() {
        frame = new JFrame();

        frame.add(this);

        frame.addKeyListener(new KeyHandler());

        frame.setSize(500, 500);

        frame.setTitle("Voxel Engine Map Editor");

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        lightList = new ArrayList<Light>();

        floorMap = new int[40][40];
        ceilMap = new int[40][40];
        tagMap = new int[40][40];

        readFloorMap("data/wfloor.txt", "data/wceil.txt", "data/tmap.txt");

//        for(int i=0; i<40; i++) {
//            for(int j=0; j<40; j++) {
//                if(i % 10 == 0 || j % 10 == 0) {
//                    floorMap[i][j] = 40;
//                }
//            }
//        }
    }

    public void readFloorMap(String filename, String filename2, String filename3) {
        File file = new File(filename);
        File file2 = new File(filename2);
        File file3 = new File(filename3);

        Scanner scanner = null;
        Scanner scanner2 = null;
        Scanner scanner3 = null;

        try {
            scanner = new Scanner(file);
            scanner2 = new Scanner(file2);
            scanner3 = new Scanner(file3);
        } catch (IOException ignored) {
        }

        int j = 0;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            String[] content = line.split(" ");

            for (int i = 0; i < 40; i++) {
                floorMap[j][i] = Integer.parseInt(content[i]);
            }

            j++;
        }

        j = 0;

        while (scanner2.hasNextLine()) {
            String line = scanner2.nextLine();

            String[] content = line.split(" ");

            for (int i = 0; i < 40; i++) {
                ceilMap[j][i] = Integer.parseInt(content[i]);
            }

            j++;
        }

        j = 0;

        while (scanner3.hasNextLine()) {
            String line = scanner3.nextLine();

            String[] content = line.split(" ");

            for (int i = 0; i < 40; i++) {
                tagMap[j][i] = Integer.parseInt(content[i]);
            }

            j++;
        }
    }

    public void writeToFile(String filenameA, String filenameB, String filenameC, String filenameD) {
        File fileA = new File(filenameA);

        FileWriter writerA = null;

        try {
            writerA = new FileWriter(fileA);
        } catch (IOException ignored) {
        }

        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 40; j++) {
                try {
                    writerA.write(String.valueOf(floorMap[i][j]));

                    writerA.write(" ");
                } catch (IOException ignored) {
                }
            }

            try {
                writerA.write(System.lineSeparator());
            } catch (IOException ignored) {
            }
        }

        try {
            writerA.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File fileB = new File(filenameB);

        FileWriter writerB = null;

        try {
            writerB = new FileWriter(fileB);
        } catch (IOException ignored) {
        }

        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 40; j++) {
                try {
                    writerB.write(String.valueOf(ceilMap[i][j]));

                    writerB.write(" ");
                } catch (IOException ignored) {
                }
            }

            try {
                writerB.write(System.lineSeparator());
            } catch (IOException ignored) {
            }
        }

        try {
            writerB.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File fileD = new File(filenameD);

        FileWriter writerD = null;

        try {
            writerD = new FileWriter(fileD);
        } catch (IOException ignored) {
        }

        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 40; j++) {
                try {
                    writerD.write(String.valueOf(tagMap[i][j]));

                    writerD.write(" ");
                } catch (IOException ignored) {
                }
            }

            try {
                writerD.write(System.lineSeparator());
            } catch (IOException ignored) {
            }
        }

        try {
            writerD.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File fileC = new File(filenameC);

        FileWriter writerC = null;

        try {
            writerC = new FileWriter(fileC);
        } catch (IOException ignored) {
        }

        for (Light light : lightList) {
            try {
                writerC.write(light.x + " " + light.y + " " + light.z
                        + " " + light.color.getRed() + " " + light.color.getGreen() +
                        " " + light.color.getBlue() + " " + light.intensity + " " + (light.isAmbient ? 1 : 0));

                writerC.write(System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try{
            writerC.close();
        }catch (IOException ignored){}
    }

    public void handleInput() {
        if (INPUT_BUFFER != 0) {
            switch (INPUT_BUFFER) {
                case 1 -> cursorY--;
                case 2 -> cursorY++;
                case 3 -> cursorX--;
                case 4 -> cursorX++;
                case 5 -> {
                    markerAX = cursorX;
                    markerAY = cursorY;
                }
                case 6 -> {
                    markerBX = cursorX;
                    markerBY = cursorY;
                }
                case 7 -> {
                    if (mapMode == 0) {
                        for (int i = markerAY; i <= markerBY; i++) {
                            for (int j = markerAX; j <= markerBX; j++) {
                                floorMap[i][j]++;

                                floorMap[i][j] = Math.min(40, floorMap[i][j]);
                            }
                        }
                    } else if(mapMode == 1){
                        for (int i = markerAY; i <= markerBY; i++) {
                            for (int j = markerAX; j <= markerBX; j++) {
                                ceilMap[i][j]++;

                                ceilMap[i][j] = Math.min(40, ceilMap[i][j]);
                            }
                        }
                    } else if(mapMode == 2) {
                        for (int i = markerAY; i <= markerBY; i++) {
                            for (int j = markerAX; j <= markerBX; j++) {
                                tagMap[i][j]++;

                                tagMap[i][j] = Math.min(40, tagMap[i][j]);
                            }
                        }
                    }
                }
                case 8 -> {
                    if (mapMode == 0) {
                        for (int i = markerAY; i <= markerBY; i++) {
                            for (int j = markerAX; j <= markerBX; j++) {
                                floorMap[i][j]--;

                                floorMap[i][j] = Math.max(0, floorMap[i][j]);
                            }
                        }
                    } else if (mapMode == 1){
                        for (int i = markerAY; i <= markerBY; i++) {
                            for (int j = markerAX; j <= markerBX; j++) {
                                ceilMap[i][j]--;

                                ceilMap[i][j] = Math.max(0, ceilMap[i][j]);
                            }
                        }
                    } else if (mapMode == 2) {
                        for (int i = markerAY; i <= markerBY; i++) {
                            for (int j = markerAX; j <= markerBX; j++) {
                                tagMap[i][j]--;

                                tagMap[i][j] = Math.max(0, tagMap[i][j]);
                            }
                        }
                    }
                }
                case 9 -> {
                    writeToFile("data/wfloor.txt", "data/wceil.txt", "data/lights.txt", "data/tmap.txt");
                }
                case 10 -> {
                    mapMode = 1;
//                    System.out.println(mapMode);
                }
                case 11 -> {
                    for (int i = 0; i < 40; i++) {
                        for (int j = 0; j < 40; j++) {
                            ceilMap[i][j] = 40;
                            floorMap[i][j] = 0;
                            tagMap[i][j] = 1;
                        }
                    }
                }
                case 12 -> {
                    int lightHeight = 40 - (ceilMap[cursorY][cursorX] + 2);

                    Light light = new Light(cursorY, lightHeight, cursorX, 2, Color.WHITE, false);

                    lightList.add(light);
                }

                case 13 -> {
                    mapMode = 0;
                }
                case 14 -> {
                    mapMode = 2;
                }
            }

            INPUT_BUFFER = 0;
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        handleInput();

        for (int i = 0; i < floorMap.length; i++) {
            for (int j = 0; j < floorMap[0].length; j++) {
                if (i == cursorY && j == cursorX) {
                    g.setColor(Color.RED);
                } else if (i >= markerAY && i <= markerBY && j >= markerAX && j <= markerBX) {
                    g.setColor(Color.GREEN);
                } else {
                    int b = 0;

                    if (mapMode == 0) {
                        b = floorMap[i][j] * 5;
                    } else if(mapMode == 1){
                        b = ceilMap[i][j] * 5;
                    } else if(mapMode == 2) {
                        b = tagMap[i][j] * 5;
                    }

                    b = Math.max(0, Math.min(255, b));

                    Color color = new Color(b, b, b);

                    g.setColor(color);
                }

                g.fillRect(j * 15, i * 15, 15, 15);

                if (floorMap[i][j] > 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.BLUE);
                }

                g.drawRect(j * 15, i * 15, 15, 15);

                g.setColor(Color.WHITE);

                if (mapMode == 0) {
                    g.drawString(String.valueOf(floorMap[i][j]), j * 15 + 7, i * 15 + 7);
                } else if (mapMode == 1){
                    g.drawString(String.valueOf(ceilMap[i][j]), j * 15 + 7, i * 15 + 7);
                } else if (mapMode == 2) {
                    g.drawString(String.valueOf(tagMap[i][j]), j * 15 + 7, i * 15 + 7);
                }
            }
        }

        repaint();
    }
}
