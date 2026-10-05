package model;

import java.io.InputStream;
import java.util.Scanner;

public class MapLoader {

    public static boolean[][] loadCollisionMatrix(String filePath, int cols, int rows) {
        boolean[][] matrix = new boolean[cols][rows];
        try (InputStream is = MapLoader.class.getResourceAsStream(filePath)) {
            if (is == null) {
                System.err.println("No se encontró el mapa en: " + filePath);
                return matrix;
            }
            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
            String json = scanner.hasNext() ? scanner.next() : "";

            // Buscamos el array en el JSON (tmj)
            int dataIndex = json.indexOf("\"data\":[");
            if (dataIndex != -1) {
                int startBracket = json.indexOf("[", dataIndex);
                int endBracket = json.indexOf("]", startBracket);
                String dataString = json.substring(startBracket + 1, endBracket);
                String[] values = dataString.split(",");

                int col = 0;
                int row = 0;
                for (String val: values) {
                    long id = Long.parseLong(val.trim());
                    matrix[col][row] = (id > 0); // ">" true si es cuadro rojo
                    col++;
                    if (col >= cols) {
                        col = 0;
                        row++;
                    }
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.err.println("Error leyendo el mapa: " + e.getMessage());
        }
        return matrix;
    }
}
