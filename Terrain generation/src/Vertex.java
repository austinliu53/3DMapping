
public class Vertex {
    double[] coords = new double[3]; 
    RectPrism shape; // The shape that this vertex is part of

    public Vertex(double x, double y, double z, RectPrism sh) {
        coords[0] = x;
        coords[1] = y;
        coords[2] = z;
        shape = sh;
    }
}
