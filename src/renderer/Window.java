package renderer;

import generation.Generator;
import generation.lightning.LightningData;
import keyboard.InputHandler;
import math.Transformations;
import objects.Player;
import objects.Point2;
import objects.Point3;
import objects.meshes.Light;
import objects.meshes.Plane;
import objects.repositories.MeshRepository;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Window extends JPanel {
    public static int INPUT_BUFFER;

    public int[][][] states;
    public int[][][] lmapr;
    public int[][][] lmapg;
    public int[][][] lmapb;

    public List<Light> lightList;

    private JFrame frame;

    private MeshRepository repository;

    private Player player;

    private Light lighta;
    private Light lightb;
    private int time = 0;

    private boolean ao = true;
    private boolean wireframe = false;
    private boolean unlit = false;
    private boolean multiThreading = false;
    private boolean fastRoots = false;

    private int lastPcx = 0;
    private int lastPcy = 0;
    private int lastPcz = 0;

    public void readLights(String filename) {
        File file = new File(filename);

        Scanner scanner = null;

        try{
            scanner = new Scanner(file);
        }catch (IOException exception) {}

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();

            String[] content = line.split(" ");

            double light_x = Double.parseDouble(content[0]);
            double light_y = Double.parseDouble(content[1]);
            double light_z = Double.parseDouble(content[2]);

            int light_r = Integer.parseInt(content[3]);
            int light_g = Integer.parseInt(content[4]);
            int light_b = Integer.parseInt(content[5]);

            Color light_color = new Color(light_r, light_g, light_b);

            double light_i = Double.parseDouble(content[6]);

            boolean isAmbient = (Integer.parseInt(content[7]) != 0);

            Light light = new Light(light_x, light_y, light_z, light_i, light_color, isAmbient);

            lightList.add(light);
        }
    }

    public void init() {
        lightList = new ArrayList<>();

        frame = new JFrame();

        frame.add(this);

        frame.addKeyListener(new InputHandler());

        frame.setSize(1920, 1080);

        frame.setTitle("Voxel Engine");

        generateMap();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

//        lighta = new Light(6, 40 - 27, -3, 3, Color.RED, false);

//        lightList.add(lighta);
//        lightList.add(new Light(0, 0, 0, 10, Color.WHITE, true));
    }

    public void generateMap() {
        readLights("data/lights.txt");

        repository = new MeshRepository();

        states = Generator.getStates();

        repository = Generator.generate(states);
    }

//    public void setRepository(MeshRepository repository) {
//        this.repository = repository;
//    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    private void handleInput() {
        if (INPUT_BUFFER != 0) {
            switch (INPUT_BUFFER) {
                case 1 -> player.z += .1 * 100 * 2;
                case 2 -> player.z -= .1 * 100 * 2;
                case 3 -> player.x -= .1 * 100;
                case 4 -> player.x += .1 * 100;
                case 5 -> player.y -= .1 * 100;
                case 6 -> player.y += .1 * 100;
                case 7 -> repository.rotate(Math.PI / 180 * 90, player);
                case 8 -> repository.rotate(-Math.PI / 180 * 90, player);
                case 9 -> lightList.add(new Light(player.x / CONSTANTS.STEP_SIZE, states[0][0].length - player.y / CONSTANTS.STEP_SIZE, player.z / CONSTANTS.STEP_SIZE, 4, Color.RED, false));
                case 10 -> wireframe = !wireframe;
                case 11 -> {
                    states[(int) (player.z / CONSTANTS.STEP_SIZE + 1)][(int) (player.x / CONSTANTS.STEP_SIZE)][(int) (states[0][0].length - player.y / CONSTANTS.STEP_SIZE)] = 1;
                    repository = Generator.generate(states);
                }
                case 12 -> {
                    states[(int) (player.z / CONSTANTS.STEP_SIZE + 1)][(int) (player.x / CONSTANTS.STEP_SIZE)][(int) (states[0][0].length - player.y / CONSTANTS.STEP_SIZE)] = 2;
                    repository = Generator.generate(states);
                }

                case 13 -> {
                    for (int i = 0; i < states.length; i++) {
                        for (int j = 0; j < states[0].length; j++) {
                            for (int k = 0; k < states[0][0].length; k++) {
                                lmapr[i][j][k] = 0;
                                lmapg[i][j][k] = 0;
                                lmapb[i][j][k] = 0;
                            }
                        }
                    }

                    LightningData data = new LightningData(states.length, states[0].length, states[0][0].length);

                    ao = !ao;

                    for (Light light : lightList) {
                        data.combine(Generator.getLightmap(states, light, lmapr, lmapg, lmapb, ao, 0, player, fastRoots));
                    }
                }

                case 14 -> unlit = !unlit;

                case 15 -> {
                    MapEditor editor = new MapEditor();

                    editor.init();
                }

                case 16 -> {
                    generateMap();
                }

                case 17 -> {
                    multiThreading = !multiThreading;
                }

                case 18 -> {
                    fastRoots = !fastRoots;
                }
            }

            System.out.println(player.x + " " + player.y + " " + player.z);

            INPUT_BUFFER = 0;
        }
    }

    public boolean occlusionClipping(Point3 point) {
        if (point.z < player.z) {
            return true;
        }

        if (point.z - CONSTANTS.Z_VIEW_RANGE > player.z) {
            return true;
        }

        if (point.x + CONSTANTS.X_VIEW_RANGE < player.x) {
            return true;
        }

        if (point.x - CONSTANTS.X_VIEW_RANGE > player.x) {
            return true;
        }

        if (point.y + CONSTANTS.Y_VIEW_RANGE < player.y) {
            return true;
        }

        if (point.y - CONSTANTS.Y_VIEW_RANGE > player.y) {
            return true;
        }

        return false;
    }

    @Override
    public void paint(Graphics g) {
        long start = System.nanoTime();

        super.paint(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 1920, 1080);

        handleInput();

        Light l = new Light(player.x / 25, 40 - player.y / 25, player.z / 25, 3, Color.WHITE, false);

//        lighta.x = Math.sin(time / 300000.) * 30;
//        lighta.x = Math.max(0, lighta.x);

        lightList.add(l);

        int pcx = (int) (player.x / CONSTANTS.STEP_SIZE);
        int pcy = (int) (40 - player.y / CONSTANTS.STEP_SIZE);
        int pcz = (int) (player.z / CONSTANTS.STEP_SIZE);

        if(pcx != lastPcx || pcy != lastPcy || pcz != lastPcz) {
            if (!unlit) {
                for (int i = 0; i < states.length; i++) {
                    for (int j = 0; j < states[0].length; j++) {
                        for (int k = 0; k < states[0][0].length; k++) {
                            lmapr[i][j][k] = 0;
                            lmapg[i][j][k] = 0;
                            lmapb[i][j][k] = 0;
                        }
                    }
                }

                LightningData data = new LightningData(states.length, states[0].length, states[0][0].length);
//
                for (Light light : lightList) {
                    if(multiThreading) {
                        for (int i = 0; i < 8; i++) {
                            LightningThread thread = new LightningThread(light, lmapr, lmapg, lmapb, ao, i, data, states, player, fastRoots);

                            thread.run();
//                    data.combine(Generator.getLightmap(states, light, lmapr, lmapg, lmapb, ao, i));
                        }
                    }else{
                        data.combine(Generator.getLightmap(states, light, lmapr, lmapg, lmapb, ao, 8, player, fastRoots));
                    }
                }

                lmapr = data.lmapr;
                lmapg = data.lmapg;
                lmapb = data.lmapb;
            }
        }

        repository.sortFaces(player, fastRoots);

        for (Plane plane : repository.planes) {
            if (!plane.active) continue;

            if (occlusionClipping(plane.getPoints()[0])) {
                continue;
            }

            if (!plane.active) continue;

            if (states[plane.parentCube.zSector][plane.parentCube.xSector][plane.parentCube.ySector] == 3) {
                Generator.getReflectionMap(states, new Light(player.x / 50, player.y / 50, player.z / 50, 10, Color.WHITE, false), plane, repository);
            }

            Polygon polygon = Transformations.projectPlane(plane, player);

            if (unlit) {
                int r = plane.color.getRed();
                int gr = plane.color.getGreen();
                int b = plane.color.getBlue();

                if(plane.getPoints()[0].z - player.z > 0) {
                    r -= (plane.getPoints()[0].z - player.z - 0) / 8;
                    gr -= (plane.getPoints()[0].z - player.z - 0) / 8;
                    b -= (plane.getPoints()[0].z - player.z - 0) / 8;
                }

                r = Math.max(0, Math.min(255, r));
                gr = Math.max(0, Math.min(255, gr));
                b = Math.max(0, Math.min(255, b));

                g.setColor(new Color(r, gr, b, plane.color.getAlpha()));
            }

            if (!unlit) {
                int currSectorX = plane.parentCube.xSector;
                int currSectorY = plane.parentCube.ySector;
                int currSectorZ = plane.parentCube.zSector;

                int brightnessr = (int) (lmapr[currSectorZ][currSectorX][currSectorY] / 512. * plane.color.getRed() + lmapr[currSectorZ][currSectorX][currSectorY] / 2);
                int brightnessg = (int) (lmapg[currSectorZ][currSectorX][currSectorY] / 512. * plane.color.getGreen() + lmapg[currSectorZ][currSectorX][currSectorY] / 2);
                int brightnessb = (int) (lmapb[currSectorZ][currSectorX][currSectorY] / 512. * plane.color.getBlue() + lmapb[currSectorZ][currSectorX][currSectorY] / 2);
                int brightnessa = plane.color.getAlpha();

                brightnessr = Math.min(255, Math.max(0, brightnessr));
                brightnessg = Math.min(255, Math.max(0, brightnessg));
                brightnessb = Math.min(255, Math.max(0, brightnessb));

                if(plane.getPoints()[0].z - player.z > 1500) {
                    brightnessr -= (plane.getPoints()[0].z - player.z - 1500) / 2;
                    brightnessg -= (plane.getPoints()[0].z - player.z - 1500) / 2;
                    brightnessb -= (plane.getPoints()[0].z - player.z - 1500) / 2;
                }

                brightnessr = Math.min(255, Math.max(0, brightnessr));
                brightnessg = Math.min(255, Math.max(0, brightnessg));
                brightnessb = Math.min(255, Math.max(0, brightnessb));

                g.setColor(new Color(brightnessr, brightnessg, brightnessb, brightnessa));
            }

            g.fillPolygon(polygon);

            if (wireframe) {
                g.setColor(Color.DARK_GRAY);
                g.drawPolygon(polygon);
            }

            time++;
        }

        g.drawString("S: " + player.x / CONSTANTS.STEP_SIZE + " " + player.y / CONSTANTS.STEP_SIZE + " " + player.z / CONSTANTS.STEP_SIZE, CONSTANTS.STEP_SIZE, CONSTANTS.STEP_SIZE);

        long end = System.nanoTime();
        long elapsedTime = end - start;

        double elapsed = elapsedTime / 1000000.;

        g.setColor(Color.GREEN);
        g.drawString(String.format("FPS: %.2f", 1 / elapsed * 1000) , 100, 100);
        g.drawString("Multithreading: " + multiThreading, 100, 150);
        g.drawString("FastRoots: " + fastRoots, 100, 200);
//        g.drawString("FPS: " + 1 / elapsed * 1000 , 100, 100);

        lightList.remove(l);

        lastPcx = pcx;
        lastPcy = pcy;
        lastPcz = pcz;

        repaint();
    }
}
