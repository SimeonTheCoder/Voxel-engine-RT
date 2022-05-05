package objects.meshes;

import objects.Player;
import objects.Point3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Cube {
    public List<Plane> planes;

    public int xSector, ySector, zSector;

    public Cube(Point3 point1A, Point3 point2A) {
        planes = new ArrayList<>();

        Point3 point1 = new Point3(point1A.x, point1A.y, point1A.z);
        Point3 point2 = new Point3(point2A.x, point1A.y, point1A.z);
        Point3 point3 = new Point3(point2A.x, point2A.y, point1A.z);
        Point3 point4 = new Point3(point1A.x, point2A.y, point1A.z);
        Point3 point5 = new Point3(point1A.x, point1A.y, point2A.z);
        Point3 point6 = new Point3(point2A.x, point1A.y, point2A.z);
        Point3 point7 = new Point3(point2A.x, point2A.y, point2A.z);
        Point3 point8 = new Point3(point1A.x, point2A.y, point2A.z);

        planes.add(new Plane(new Point3[]{point1, point2, point3, point4})); //Front face 0
        planes.add(new Plane(new Point3[]{point1, point2, point6, point5})); //Up face 1
        planes.add(new Plane(new Point3[]{point3, point4, point8, point7})); //Bottom face 2
        planes.add(new Plane(new Point3[]{point1, point4, point8, point5})); //Left face 3
        planes.add(new Plane(new Point3[]{point2, point3, point7, point6})); //Right face 4
        planes.add(new Plane(new Point3[]{point5, point6, point7, point8})); //Back face 5
        planes.add(new Plane(new Point3[]{point5, point6, point3, point4})); //Ramp face 6

//        planes.get(0).color = Color.RED;
        planes.get(0).color = Color.YELLOW;
        planes.get(6).color = Color.WHITE;
        planes.get(5).color = Color.YELLOW;
//        planes.get(4).color = Color.GREEN;
        planes.get(4).color = Color.YELLOW;
//        planes.get(3).color = Color.GREEN;
        planes.get(3).color = Color.YELLOW;
//        planes.get(2).color = Color.BLUE;
        planes.get(2).color = Color.GREEN;
//        planes.get(1).color = Color.BLUE;
        planes.get(1).color = Color.GREEN;

//        for (Plane plane : planes) {
//            plane.color = Color.WHITE;
//        }

        planes.get(0).parentCube = this;
        planes.get(6).parentCube = this;
        planes.get(5).parentCube = this;
        planes.get(4).parentCube = this;
        planes.get(3).parentCube = this;
        planes.get(2).parentCube = this;
        planes.get(1).parentCube = this;
    }

    public void removePlane(int index) {
        planes.get(index).active = false;
    }

    public List<Plane> getPlanes() {
        return this.planes;
    }

    public void rotate(double angle, Point3 pivot) {
        for (Plane plane : getPlanes()) {
            plane.rotate(angle, pivot);
        }
    }
}
