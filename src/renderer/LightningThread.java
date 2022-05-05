package renderer;

import generation.Generator;
import generation.lightning.LightningData;
import objects.Player;
import objects.meshes.Light;

public class LightningThread implements Runnable {
    private Light light;
    private int[][][] lmapr;
    private int[][][] lmapg;
    private int[][][] lmapb;

    private boolean ao;
    private int region;

    private int[][][] states;

    private LightningData data;

    private Player player;

    private boolean fastRoots;

    public LightningThread(Light light, int[][][] lmapr, int[][][] lmapg, int[][][] lmapb,
                           boolean ao, int region, LightningData data, int[][][] states, Player player, boolean fastRoots) {
        this.light = light;
        this.lmapr = lmapr;
        this.lmapg = lmapg;
        this.lmapb = lmapb;

        this.fastRoots = fastRoots;

        this.ao = ao;
        this.region = region;

        this.data = data;
        this.states = states;

        this.player = player;
    }

    @Override
    public void run() {
        data.combine(Generator.getLightmap(states, light, lmapr, lmapg, lmapb, ao, region, player, fastRoots));
    }
}
