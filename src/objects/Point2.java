package objects;

public class Point2 {
    public double x, y;

    public Point2() {
        x = 0;
        y = 0;
    }

    public Point2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }
}
