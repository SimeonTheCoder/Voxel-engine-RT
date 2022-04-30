package objects.meshes;

import objects.Point3;

import java.awt.*;

public class Light extends Point3 {
    public double intensity;
    public boolean isAmbient;
    public Color color;

    public Light(double x, double y, double z, double intensity, Color color, boolean isAmbient) {
        super(x, y, z);

        this.intensity = intensity;
        this.color = color;
        this.isAmbient = isAmbient;
    }
}
