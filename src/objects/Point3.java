package objects;

public class Point3 {
    public double x, y, z;

    public Point3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3() {
        x = 0;
        y = 0;
        z = 0;
    }

//    public void rotate(double angle, Point3 pivot) {
//        x -= pivot.x;
//        z -= pivot.z;
//
//        double distance = Math.sqrt(x * x + z * z);
//
//        x /= distance;
//        z /= distance;
//
//        double old_angle = Math.asin(z);
//
//        old_angle += angle;
//
//        x = Math.cos(old_angle);
//        z = Math.sin(old_angle);
//
//        x *= distance;
//        z *= distance;
//
//        x += pivot.x;
//        z += pivot.z;
//    }

    public void rotate(double radians, Point3 pivot) {
        double cosTheta = Math.cos(radians);
        double sinTheta = Math.sin(radians);

        x = (cosTheta * (x - pivot.x) - sinTheta * (z - pivot.z) + pivot.x);
        z = (sinTheta * (x - pivot.x) + cosTheta * (z - pivot.z) + pivot.z);
    }
}
