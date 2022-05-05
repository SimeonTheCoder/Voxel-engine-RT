package objects.meshes;

import objects.Player;
import objects.Point3;

import java.awt.*;

public class Plane {
    Point3[] points;

    public Color color;
    public boolean active;
    public double lightLevel;

    public Cube parentCube;

    public Point3 avePoint;

    public Plane() {
        points = new Point3[4];

        active = true;
    }

    public Plane(Point3[] points) {
        this.points = points;

        active = true;

        avePoint = averagePoint();
    }

    public Point3[] getPoints() {
        return points;
    }

    public Point3 calculateNormal(Player player) {
        Point3 point0 = new Point3(points[0].x - player.x, points[0].y - player.y, points[0].z - player.z);
        Point3 point1 = new Point3(points[1].x - player.x, points[1].y - player.y, points[1].z - player.z);
        Point3 point2 = new Point3(points[2].x - player.x, points[2].y - player.y, points[2].z - player.z);

        Point3 line1 = new Point3(point1.x - point0.x, point1.y - point0.y, point1.z - point0.z);
        Point3 line2 = new Point3(point2.x - point1.x, point2.y - point1.y, point2.z - point1.z);
//        Point3 line3 = new Point3(points[3].x - points[2].x, points[3].y - points[2].y, points[3].z - points[2].z);
//        Point3 line4 = new Point3(points[3].x - points[0].x, points[3].y - points[0].y, points[3].z - points[0].z);

        Point3 normal = new Point3(line1.y * line2.z - line1.z * line2.y,
                line1.z * line2.x - line1.x * line2.z,
                line1.x * line2.y - line1.y * line2.x);

        double length = Math.sqrt(normal.x * normal.x + normal.y * normal.y + normal.z * normal.z);

        normal.x /= length;
        normal.y /= length;
        normal.z /= length;

        return normal;
    }

    public Point3 averagePoint() {
        Point3 points[] = getPoints();

        double xSum = points[0].x + points[1].x + points[2].x + points[3].x;
        double ySum = points[0].y + points[1].y + points[2].y + points[3].y;
        double zSum = points[0].z + points[1].z + points[2].z + points[3].z;

        double aveX = xSum / 4.;
        double aveY = ySum / 4.;
        double aveZ = zSum / 4.;

        Point3 planePoint = new Point3(aveX, aveY, aveZ);

        return planePoint;
    }

    public double distanceTo(Point3 target, boolean fastRoot) {
        Point3 points[] = getPoints();

        Point3 planePoint = avePoint;

        double deltaX = target.x - planePoint.x;
        double deltaY = target.y - planePoint.y;
        double deltaZ = target.z - planePoint.z;

        double d = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

        if(fastRoot) {
            return Double.longBitsToDouble(((Double.doubleToLongBits(d) - (1l << 52)) >> 1) + (1l << 61));
        }else{
            return Math.sqrt(d);
        }
    }

    public void rotate(double angle, Point3 pivot) {
        for (Point3 point : getPoints()) {
            point.rotate(angle, pivot);
        }
    }
}
