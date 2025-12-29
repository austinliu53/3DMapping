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
 
    public ArrayList<DotMap> screenDotMap = new ArrayList<DotMap>();
    public ArrayList<int[]> edges = new ArrayList<int[]>();
    public double yRotation; // Rotation around the y-axis in degrees

    public RectPrism(double w, double h, double d, double[] c, double yR) {
        totalRectPrisms++;
        width = w;
        height = h;
        depth = d;
        
        coordinates = c;

        yRotation = yR; 
        
        for (Vertex vertex : calcVertices()) {
            //System.out.println("RectPrism 26 " + v.coords[0] + " " + v.coords[1] + " " + v.coords[2]);
            screenDotMap.add(new DotMap(vertex));
        }

        /*
        57
        46 

        13
        02 close
        */
        // Add edges
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    switch (i) {
                        case 0: // Vertical edges
                            edges.add(new int[] {2 * (j + (k * 2)), 2 * (j + (k * 2)) + 1}); 
                            System.out.println((2 * (j + k)) + " " + (2 * (j + k) + 1));
                            break;

                        case 1: // Left-right edges
                            edges.add(new int[] {j + (4 * k), j + (4 * k) + 2}); 
                            System.out.println((j + (4 * k)) + " " + (j + (4 * k) + 2));
                            break;
                            
                        case 2: // Forward-back edges
                            edges.add(new int[] {j + (k * 2), j + (k * 2) + 4}); 
                            System.out.println((j + k) + " " + (j + k + 4));
                            break;
                    }
                }
                

            }
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
        //System.out.println("RectPrism 74: Vertices calculated: " + vertices.size());
        return vertices;
    }

    

}
