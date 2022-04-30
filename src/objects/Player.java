package objects;

public class Player extends Point3{
    private int angle = 0;

    public Player() {
        angle = 0;
    }

    public Player(int angle) {
        this.angle = angle;
    }

    public Player(int angle, double x, double y, double z){
        super(x, y, z);

        this.angle = angle;
    }
}
