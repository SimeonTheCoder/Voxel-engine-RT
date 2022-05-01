package math;

import objects.Player;
import objects.Point2;
import objects.Point3;
import objects.meshes.Plane;

import java.awt.*;

public class Transformations {
    public static Point2 projectPoint(Point3 point3, Player player) {
        Point3 copy = new Point3(point3.x, point3.y, point3.z);

        copy.x -= player.x;
        copy.y -= player.y;
        copy.z -= player.z;

        Point2 point2 = new Point2();

        copy.z = Math.max(0, copy.z);

        double newX = copy.x / copy.z * 250. * 3 * 2 * 2 + 500;
        double newY = copy.y / copy.z * 250. * 3 * 2 + 500;

        point2 = new Point2(newX, newY);

        return point2;
    }

    public static Polygon projectPlane(Plane plane, Player player) {
        int[] xPoints = new int[4];
        int[] yPoints = new int[4];

        int n = 4;

        for (int i=0; i<plane.getPoints().length; i++) {
            Point3 point = plane.getPoints()[i];

            Point2 point2 = projectPoint(point, player);

            xPoints[i] = (int) point2.x;
            yPoints[i] = (int) point2.y;
        }

        Polygon polygon = new Polygon(xPoints, yPoints, n);

        return polygon;
    }
}
