package generation;

import files.ReadFile;
import generation.lightning.LightningData;
import objects.Point3;
import objects.meshes.Cube;
import objects.meshes.Light;
import objects.meshes.Plane;
import objects.repositories.MeshRepository;
import renderer.CONSTANTS;

import java.awt.*;

public class Generator {
    public static int[][][] getStates() {
        int[][] worldFloorMap = ReadFile.read("data/wfloor.txt");
        int[][] worldCeilMap = ReadFile.read("data/wceil.txt");

        int[][][] states = new int[40][40][40];

        int scale = CONSTANTS.SCALE;

        for (int i = 0; i < states.length * scale; i += scale) {
            for (int j = 0; j < states[0].length * scale; j += scale) {
                for (int k = 0; k < states[0][0].length * scale; k += scale) {
                    if (i == 10) {
                        System.out.println();
                    }

                    if (k <= worldFloorMap[j / scale / 2][i / scale / 2] || k >= worldCeilMap[j / scale / 2][i / scale / 2]) {
                        for (int l = 0; l < scale * 2; l++) {
                            for (int m = 0; m < scale * 2; m++) {
                                for (int p = 0; p < scale * 2; p++) {
                                    try {
                                        states[i / scale + l][j / scale + m][k / scale + p] = 1;
                                    } catch (Exception exception) {
                                        System.out.println();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return states;
    }

    public static LightningData getLightmap(int[][][] states, Light currLight, int[][][] lightmapr, int[][][] lightmapg, int[][][] lightmapb, boolean ao) {
        LightningData data = new LightningData(lightmapr.length, lightmapr[0].length, lightmapr[0][0].length);

        int[][][] ambientMap = new int[states.length][states[0].length][states[0][0].length];

        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                for (int k = 0; k < states[0][0].length; k++) {
                    int nCount = 0;

                    if (i > 0 && states[i - 1][j][k] != 0) {
                        nCount++;
                    }

                    if (i < states.length - 1 && states[i + 1][j][k] != 0) {
                        nCount++;
                    }

                    if (j > 0 && states[i][j - 1][k] != 0) {
                        nCount++;
                    }

                    if (j < states[0].length - 1 && states[i][j + 1][k] != 0) {
                        nCount++;
                    }

                    if (k > 0 && states[i][j][k - 1] != 0) {
                        nCount++;
                    }

                    if (k < states[0][0].length - 1 && states[i][j][k + 1] != 0) {
                        nCount++;
                    }

                    ambientMap[i][j][k] = 6 - nCount;
                }
            }
        }

        for (int i = 0; i < states.length; i += CONSTANTS.SCALE) {
            for (int j = 0; j < states[0].length; j += CONSTANTS.SCALE) {
                for (int k = 0; k < states[0][0].length; k += CONSTANTS.SCALE) {
                    if (currLight.isAmbient) {
                        lightmapr[i][j][k] = (int) currLight.intensity * 5;
                        lightmapg[i][j][k] = (int) currLight.intensity * 5;
                        lightmapb[i][j][k] = (int) currLight.intensity * 5;

                        data.lmapr[i][j][k] = lightmapr[i][j][k];
                        data.lmapg[i][j][k] = lightmapg[i][j][k];
                        data.lmapb[i][j][k] = lightmapb[i][j][k];

                        for (int m = 0; m < CONSTANTS.SCALE; m++) {
                            for (int n = 0; n < CONSTANTS.SCALE; n++) {
                                for (int p = 0; p < CONSTANTS.SCALE; p++) {
                                    data.lmapr[i + m][j + n][k + p] = data.lmapr[i][j][k];
                                    data.lmapg[i + m][j + n][k + p] = data.lmapg[i][j][k];
                                    data.lmapb[i + m][j + n][k + p] = data.lmapb[i][j][k];
                                }
                            }
                        }

                        continue;
                    }

                    if (states[i][j][k] == 2) {
                        lightmapg[i][j][k] = 255;

                        data.lmapr[i][j][k] = lightmapr[i][j][k];
                        data.lmapg[i][j][k] = lightmapg[i][j][k];
                        data.lmapb[i][j][k] = lightmapb[i][j][k];

                        for (int m = 0; m < CONSTANTS.SCALE; m++) {
                            for (int n = 0; n < CONSTANTS.SCALE; n++) {
                                for (int p = 0; p < CONSTANTS.SCALE; p++) {
                                    data.lmapr[i + m][j + n][k + p] = data.lmapr[i][j][k];
                                    data.lmapg[i + m][j + n][k + p] = data.lmapg[i][j][k];
                                    data.lmapb[i + m][j + n][k + p] = data.lmapb[i][j][k];
                                }
                            }
                        }

                        continue;
                    }

                    Light light = new Light(currLight.x, currLight.y, currLight.z, currLight.intensity, currLight.color, currLight.isAmbient);

                    int ll = 0;

                    if (states[i][j][k] == 1) {
                        double deltaX = light.x - j;
                        double deltaY = light.y - k;
                        double deltaZ = light.z - i;

                        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                        deltaX /= distance;
                        deltaY /= distance;
                        deltaZ /= distance;

                        boolean collision = false;

                        for (double d = 3.5 / CONSTANTS.SCALE; d < distance; d += .1 / CONSTANTS.SCALE) {
                            int newX = (int) (j + d * deltaX);
                            int newY = (int) (k + d * deltaY);
                            int newZ = (int) (i + d * deltaZ);

                            if (newX > states[0].length - 1 || newX < 0 || newY > states[0][0].length - 1 || newY < 0 || newZ > states.length - 1 || newZ < 0) {
                                break;
                            }

                            if (states[newZ][newX][newY] == 1) {
                                collision = true;

                                break;
                            } else if (states[newZ][newX][newY] == 2) {
                                int ng = (light.color.getGreen() + 255);

                                ng = Math.max(0, Math.min(255, ng));

                                light.color = new Color(light.color.getRed(), ng, light.color.getBlue());
                            }
                        }

                        if (!collision) {
                            ll += (int) ((50*Math.sqrt(light.intensity) - distance * 1.5) * light.intensity);

//                            ll -= ambientMap[i][j][k] * 200;

                            lightmapr[i][j][k] += ll * light.color.getRed() / 200;
                            lightmapg[i][j][k] += ll * light.color.getGreen() / 200;
                            lightmapb[i][j][k] += ll * light.color.getBlue() / 200;
//                            lightmap[i][j][k] = 255;
                        }
                    }

                    data.lmapr[i][j][k] = lightmapr[i][j][k];
                    data.lmapg[i][j][k] = lightmapg[i][j][k];
                    data.lmapb[i][j][k] = lightmapb[i][j][k];

                    if (ao) {
                        data.lmapr[i][j][k] -= ambientMap[i][j][k] * 20;
                        data.lmapg[i][j][k] -= ambientMap[i][j][k] * 20;
                        data.lmapb[i][j][k] -= ambientMap[i][j][k] * 20;
                    }

                    for (int m = 0; m < CONSTANTS.SCALE; m++) {
                        for (int n = 0; n < CONSTANTS.SCALE; n++) {
                            for (int p = 0; p < CONSTANTS.SCALE; p++) {
                                try {
                                    data.lmapr[i + m][j + n][k + p] = data.lmapr[i][j][k];
                                    data.lmapg[i + m][j + n][k + p] = data.lmapg[i][j][k];
                                    data.lmapb[i + m][j + n][k + p] = data.lmapb[i][j][k];
                                } catch (Exception exception) {
                                    System.out.println();
                                }
                            }
                        }
                    }
                }
            }
        }

        return data;
    }

    public static void getReflectionMap(int[][][] states, Light light, Plane plane, MeshRepository repository) {
        int i = plane.parentCube.zSector;
        int j = plane.parentCube.xSector;
        int k = plane.parentCube.ySector;

        double deltaX = j - light.x;
        double deltaY = k - light.y;
        double deltaZ = i - light.z;

        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        deltaX /= distance;
        deltaY /= distance;
        deltaZ /= distance;

        deltaY = 0;

//        deltaY = -deltaY;
        deltaZ = -deltaZ;

        for (double d = 1 / CONSTANTS.SCALE; d < distance; d += .1 / CONSTANTS.SCALE * 10) {
            int newX = (int) (j + d * deltaX);
            int newY = (int) (k + d * deltaY);
            int newZ = (int) (i + d * deltaZ);

            if (newX > states[0].length - 1 || newX < 0 || newY > states[0][0].length - 1 || newY < 0 || newZ > states.length - 1 || newZ < 0) {
                plane.color = Color.BLACK;

                break;
            }

            if (states[newZ][newX][newY] == 1) {
                Plane reflected = null;

                for (Plane plane1 : repository.planes) {
                    if (plane1.parentCube.zSector == newZ && plane1.parentCube.xSector == newX && plane1.parentCube.ySector == newY) {
                        reflected = plane1;
                    }
                }

                plane.color = reflected.color;

//                plane.color = Color.YELLOW;

                break;
            }
        }
    }

    public static MeshRepository generate(int[][][] states) {
        MeshRepository repository = new MeshRepository();

        int scale = CONSTANTS.SCALE;

        for (int i = states.length - 1; i > -1; i--) {
            for (int j = 0; j < states[0].length; j++) {
                for (int k = 0; k < states[0][0].length; k++) {
                    if (states[i][j][k] != 0) {
                        Cube cube = new Cube(
                                new Point3(CONSTANTS.STEP_SIZE * scale * j, CONSTANTS.STEP_SIZE * scale * (states[0][0].length - k), i * scale * CONSTANTS.STEP_SIZE * 2),
                                new Point3(CONSTANTS.STEP_SIZE * scale * j + CONSTANTS.STEP_SIZE * scale, CONSTANTS.STEP_SIZE * scale * (states[0][0].length - k) + CONSTANTS.STEP_SIZE * scale, CONSTANTS.STEP_SIZE * scale*2 + i * CONSTANTS.STEP_SIZE * scale * 2)
                        );

                        cube.xSector = j;
                        cube.ySector = k;
                        cube.zSector = i;

                        if (i > 0 && states[i - 1][j][k] != 0) {
                            cube.removePlane(0);
                        }

                        if (i < states.length - 1 && states[i + 1][j][k] != 0) {
                            cube.removePlane(5);
                        }

                        if (j > 0 && states[i][j - 1][k] != 0) {
                            cube.removePlane(3);
                        }

                        if (j < states[0].length - 1 && states[i][j + 1][k] != 0) {
                            cube.removePlane(4);
                        }

                        if (k > 0 && states[i][j][k - 1] != 0) {
                            cube.removePlane(2);
                        }

                        if (k < states[0][0].length - 1 && states[i][j][k + 1] != 0) {
                            cube.removePlane(1);
                        }

                        if(k == 39) {
                            cube.removePlane(1);
                        }else if(k == 0) {
                            cube.removePlane(2);
                        }

                        if(j == 0) {
                            cube.removePlane(3);
                        }else if(j == 39) {
                            cube.removePlane(4);
                        }

                        if(i == 0) {
                            cube.removePlane(0);
                        }else if(i == 39) {
                            cube.removePlane(5);
                        }

                        if (states[i][j][k] == 2) {
                            for (Plane plane : cube.getPlanes()) {
                                plane.color = new Color(0, 255, 0, 100);
                            }
                        }

                        repository.addCube(cube);
                    }
                }
            }
        }

        return repository;
    }
}
