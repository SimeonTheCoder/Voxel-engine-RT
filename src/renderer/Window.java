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
import java.util.ArrayList;
import java.util.List;

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

    public void init() {
        frame = new JFrame();

        frame.add(this);

        frame.addKeyListener(new InputHandler());

        frame.setSize(1920, 1080);

        frame.setTitle("Voxel Engine");

        generateMap();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        lightList = new ArrayList<>();

        lighta = new Light(6, 40-27, -3, 3, Color.RED, false);

        lightList.add(lighta);
//        lightList.add(new Light(0, 0, 0, 10, Color.WHITE, true));
    }

    public void generateMap() {
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
                        data.combine(Generator.getLightmap(states, light, lmapr, lmapg, lmapb, ao));
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
        super.paint(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 1920, 1080);

        handleInput();

        Light l = new Light(player.x/25, 40-player.y/25, player.z/25, 3, Color.WHITE, false);

        lighta.x = Math.sin(time / 300000.) * 30;
        lighta.x = Math.max(0, lighta.x);

//        lightList.add(l);

//        cameraCulling(new Player(0, 0, 0, 0));

        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                for (int k = 0; k < states[0][0].length; k++) {
                    lmapr[i][j][k] = 0;
                    lmapg[i][j][k] = 0;
                    lmapb[i][j][k] = 0;
                }
            }
        }

        if(!unlit) {
            LightningData data = new LightningData(states.length, states[0].length, states[0][0].length);
//
            for (Light light : lightList) {
                data.combine(Generator.getLightmap(states, light, lmapr, lmapg, lmapb, ao));
            }

            lmapr = data.lmapr;
            lmapg = data.lmapg;
            lmapb = data.lmapb;
        }

        repository.sortFaces(player);

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
                g.setColor(plane.color);
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

                int brightnessr2 = 0;
                int brightnessg2 = 0;
                int brightnessb2 = 0;

                if(currSectorX - 1 >= 0 && currSectorX + 1 <= 39) {
                    brightnessr2 = (int) (lmapr[currSectorZ][currSectorX - 1][currSectorY] / 512. * plane.color.getRed() + lmapr[currSectorZ][currSectorX - 1][currSectorY] / 2);
                    brightnessg2 = (int) (lmapg[currSectorZ][currSectorX - 1][currSectorY] / 512. * plane.color.getGreen() + lmapg[currSectorZ][currSectorX - 1][currSectorY] / 2);
                    brightnessb2 = (int) (lmapb[currSectorZ][currSectorX - 1][currSectorY] / 512. * plane.color.getBlue() + lmapb[currSectorZ][currSectorX - 1][currSectorY] / 2);

                    brightnessr2 = Math.min(255, Math.max(0, brightnessr2));
                    brightnessg2 = Math.min(255, Math.max(0, brightnessg2));
                    brightnessb2 = Math.min(255, Math.max(0, brightnessb2));
                }

                g.setColor(new Color(brightnessr, brightnessg, brightnessb, brightnessa));

//                Graphics2D g2d = (Graphics2D)g;
//                Color s1 = new Color(brightnessr2, brightnessg2, brightnessb2);
//                Color e = new Color(brightnessr, brightnessg, brightnessb);
//                GradientPaint gradient = new GradientPaint(polygon.xpoints[2],polygon.ypoints[2],s1,polygon.xpoints[0],polygon.ypoints[0],e,true);
//                g2d.setPaint(gradient);
//                g2d.fillPolygon(polygon);
            }

            g.fillPolygon(polygon);

            if (wireframe) {
                g.setColor(Color.DARK_GRAY);
                g.drawPolygon(polygon);
            }

            time++;
        }

        g.drawString("S: " + player.x / CONSTANTS.STEP_SIZE + " " + player.y / CONSTANTS.STEP_SIZE + " " + player.z / CONSTANTS.STEP_SIZE, CONSTANTS.STEP_SIZE, CONSTANTS.STEP_SIZE);

        lightList.remove(l);

        repaint();
    }
}
