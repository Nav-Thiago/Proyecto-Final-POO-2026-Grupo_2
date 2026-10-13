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
            scanner.close();

            int startData = json.indexOf("\"data\":[");
            if (startData == -1) {
                System.err.println("ERROR MapLoader: No se encontró ningún array 'data'");
                return matrix;
            }
            int startBracket = json.indexOf("[", startData);
            int endBracket = json.indexOf("]", startBracket);

            if (startBracket == -1 || endBracket == -1) {
                System.err.println("ERROR MapLoader: No se encontraron los corchetes de data");
                return matrix;
            }

            String dataString = json.substring(startBracket + 1, endBracket);
            String[] values = dataString.split(",");

            System.out.println("MapLoader: Total de tiles leídos en la capa = " + values.length);

            int solidCount = 0;
            int col = 0;
            int row = 0;

            for (String val : values) {
                String trimmed = val.trim();
                if (!trimmed.isEmpty()) {
                    long id = Long.parseLong(trimmed);
                    boolean isSolid = (id > 0);

                    if (col < cols && row < rows) {
                        matrix[col][row] = isSolid;
                        if (isSolid) solidCount++;
                    }

                    col++;
                    if (col >= cols) {
                        col = 0;
                        row++;
                    }
                }
            }

            System.out.println("MapLoader: Carga completada. Obstáculos sólidos detectados: " + solidCount);
        } catch (Exception e) {
            System.err.println("Error leyendo el mapa: " + e.getMessage());
            e.printStackTrace();
        }

        return matrix;
    }
}
