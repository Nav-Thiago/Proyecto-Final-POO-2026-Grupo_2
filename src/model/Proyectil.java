package model;

import java.awt.Rectangle; 

public class Proyectil extends Entity {
    private double dirX;
    private double dirY;
    private double velocidad;
    private double danio;
    private boolean activo; // booleano para saber si la bala debe 'desaparecer'

    public Proyectil(double x, double y, double dirX, double dirY, double velocidad, double danio) {
        super(x, y, 6, 6);
        this.dirX = dirX;
        this.dirY = dirY;
        this.velocidad = velocidad;
        this.danio = danio;
        this.activo = true;
    }

    
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    public double getDanio(){
        return danio;
    }

    public void update(int mapWidth, int mapHeight) {
        this.x += dirX * velocidad; 
        this.y += dirY * velocidad;

        // Desactivar la bala si sale de los limites de la ventana
        if (x < 0 || x > mapWidth || y < 0 || y > mapHeight) {
            activo = false;
        }
    }

}
