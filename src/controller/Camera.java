package controller;

import model.Player;

public class Camera {

    private Camera(){}

    public static int calculateX(Player player, int screenWidth, int mapWidth) {
        int camX = (int) player.getX() - (screenWidth / 2) + (player.getWidth() / 2);
        return Math.max(0, Math.min(camX, mapWidth - screenWidth));
    }

    public static int calculateY(Player player, int screenHeight, int mapHeight) {
        int camY = (int) player.getY() - (screenHeight / 2) + (player.getHeight() / 2);
        return Math.max(0, Math.min(camY, mapHeight - screenHeight));
    }
}
