import java.util.ArrayList;

public class TerrainGeneration {

    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;
    public static final int DEPTH = 8;
    public static long SEED = 12345L;
    public static char[] characters = {' ', '.', ':', 'i', '1', 'I', 'T', 'E', '#', '@'}; // From least to most dense

    public static void main(String[] args) throws Exception {
        generateTerrain3D(WIDTH, DEPTH, SEED);
    }

    public static int[][] generateTerrain3D(int width, int depth, long seed) {
        int[][] terrain = new int[width][depth];

        for (int z = 0; z < depth; z++) {
            
            for (int x = 0; x < width; x++) {
                // Simple example: create a flat terrain at half the height

                
                
                terrain[x][z] = ((int)seed % (x * 3 - z ^ 4)) % 10; // Solid block
                

                System.out.println(x + ", " + z + ", " + terrain[x][z]);
                
            }
            
        }
        return terrain;
        
    }
}
