package model;

import java.util.ArrayDeque;

 /*
 Calcula a qué zonas del mapa puede llegar realmente el jugador desde donde empieza
 Usa la MISMA regla de colisión con la que el jugador se mueve
 */
public class ReachableArea {

    private static final int STEP = 16; // resolución de la grilla (px) // Thiago: cambio a 16 por motivos de rendimiento
    private static final int PICKUP_REACH = 32; // cuánto puede estar el centro del jugador del centro del ítem
    private static final int MIN_CELLS = 100; // si da menos, el cálculo no es confiable

    private final boolean[][] reachable; // [col][fila] -> posición (esq. sup. izq.) del jugador
    private final int cols;
    private final int rows;
    private final int reachableCells;

    public ReachableArea(GameState state) {
        Player player = state.getPlayer();
        cols = (state.getMapWidth() - player.getWidth()) / STEP + 1;
        rows = (state.getMapHeight() - player.getHeight()) / STEP + 1;
        reachable = new boolean[Math.max(cols, 0)][Math.max(rows, 0)];

        if (cols <= 0 || rows <= 0) {
            reachableCells = 0;
            return;
        }

        int startCol = clamp(Math.round((float) player.getX() / STEP), 0, cols - 1);
        int startRow = clamp(Math.round((float) player.getY() / STEP), 0, rows - 1);

        ArrayDeque<int[]> queue = new ArrayDeque<>();
        reachable[startCol][startRow] = true;
        queue.add(new int[]{startCol, startRow});

        int count = 1;
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            for (int[] d : directions) {
                int c = cell[0] + d[0];
                int r = cell[1] + d[1];
                if (c < 0 || r < 0 || c >= cols || r >= rows || reachable[c][r]) continue;
                if (player.isCollidingWithMap(c * STEP, r * STEP, state)) continue;

                reachable[c][r] = true;
                queue.add(new int[]{c, r});
                count++;
            }
        }
        reachableCells = count;
    }

    // false si el cálculo no sirvió
    public boolean isUsable() {
        return reachableCells >= MIN_CELLS;
    }

    // true si el jugador puede llegar caminando lo bastante cerca como para agarrar un ítem centrado en .
    public boolean canPickUpAt(double itemCenterX, double itemCenterY, Player player) {
        double playerLeft = itemCenterX - player.getWidth() / 2.0;
        double playerTop = itemCenterY - player.getHeight() / 2.0;

        int fromCol = (int) Math.floor((playerLeft - PICKUP_REACH) / STEP);
        int toCol = (int) Math.ceil((playerLeft + PICKUP_REACH) / STEP);
        int fromRow = (int) Math.floor((playerTop - PICKUP_REACH) / STEP);
        int toRow = (int) Math.ceil((playerTop + PICKUP_REACH) / STEP);

        for (int c = Math.max(fromCol, 0); c <= Math.min(toCol, cols - 1); c++) {
            for (int r = Math.max(fromRow, 0); r <= Math.min(toRow, rows - 1); r++) {
                if (reachable[c][r]) return true;
            }
        }
        return false;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
}
