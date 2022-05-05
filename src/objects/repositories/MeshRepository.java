package objects.repositories;

import objects.Player;
import objects.Point3;
import objects.meshes.Cube;
import objects.meshes.Plane;

import java.util.ArrayList;
import java.util.List;

public class MeshRepository {
    public List<Plane> planes;

    public MeshRepository() {
        planes = new ArrayList<>();
    }

    public void addPlane(Plane plane) {
        planes.add(plane);
    }

    public void addCube(Cube cube) {
        planes.addAll(cube.getPlanes());
    }

    public void sortFaces(Player player, boolean fastRoot) {
        planes.sort((a, b) -> {
            if(a.getPoints()[0].z != b.getPoints()[0].z) {
                return Double.compare((b.getPoints()[0].z), a.getPoints()[0].z);
            }

            Double distanceA = a.distanceTo(player, fastRoot);
            Double distanceB = b.distanceTo(player, fastRoot);

            return distanceB.compareTo(distanceA);
        });
    }

    public void rotate(double angle, Point3 pivot) {
        for (Plane plane : planes) {
            plane.rotate(angle, new Point3(500, 500, 11));
        }
    }
}
