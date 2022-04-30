package generation.lightning;

public class LightningData {
    public int[][][] lmapr, lmapg, lmapb;

    public LightningData(int x, int y, int z) {
        lmapr = new int[z][x][y];
        lmapg = new int[z][x][y];
        lmapb = new int[z][x][y];
    }

    public void combine(LightningData data) {
//        int[][][] lmapr, lmapg, lmapb;

//        this.lmapr = data.lmapr;
//        this.lmapg = data.lmapg;
//        this.lmapb = data.lmapb;

        for(int i=0; i<lmapr.length; i++) {
            for(int j=0; j<lmapr[0].length; j++) {
                for(int k=0; k<lmapr[0][0].length; k++) {
                    this.lmapr[i][j][k] += data.lmapr[i][j][k];
                    this.lmapg[i][j][k] += data.lmapg[i][j][k];
                    this.lmapb[i][j][k] += data.lmapb[i][j][k];
                }
            }
        }
    }
}
