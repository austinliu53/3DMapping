//import java.awt.event.*;
import java.util.ArrayList;

//import javax.swing.Action;

public class RectPrism {
    public static int totalRectPrisms = 0;
    public static ArrayList<RectPrism> rectPrisms = new ArrayList<RectPrism>();
    public double width;
    public double height;
    public double depth;

    public double[] coordinates;
 
    public ArrayList<DotMap> screenMap = new ArrayList<DotMap>();
    public double yRotation; // Rotation around the y-axis in degrees

    public RectPrism(double w, double h, double d, double[] c, double yR) {
        totalRectPrisms++;
        width = w;
        height = h;
        depth = d;
        
        coordinates = c;

        yRotation = yR; 
        
        for (Vertex v : calcVertices()) {
            //System.out.println("RectPrism 26 " + v.coords[0] + " " + v.coords[1] + " " + v.coords[2]);

            
            screenMap.add(new DotMap(v));
        }

        rectPrisms.add(this);
        
    }



    public void setCoords(double[] newC) {
        coordinates = newC;
    }

    public ArrayList<Vertex> calcVertices() {
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();

        for (int i = 0; i <= 1; i++) {
            for (int j = 0; j <= 1; j++) {
                for (int k = 0; k <= 1; k++) {
                    double x = coordinates[0] + (i * width);
                    double y = coordinates[1] + (j * height);
                    double z = coordinates[2] + (k * depth);

                    // Apply rotation around the y-axis
                    double centerX = coordinates[0] + width / 2;
                    double centerZ = coordinates[2] + depth / 2;

                    double translatedX = x - centerX;
                    double translatedZ = z - centerZ;

                    double radians = Math.toRadians(yRotation);
                    double rotatedX = translatedX * Math.cos(radians) - translatedZ * Math.sin(radians);
                    double rotatedZ = translatedX * Math.sin(radians) + translatedZ * Math.cos(radians);

                    x = rotatedX + centerX;
                    z = rotatedZ + centerZ;

                    //System.out.println("RectPrism 64: " + x + " " + y + " " + z);
                    vertices.add(new Vertex(x, y, z, this));
                }
            }
        }
        System.out.println("Vertices calculated: " + vertices.size());
        return vertices;
    }

    

}
