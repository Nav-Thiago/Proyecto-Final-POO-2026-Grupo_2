package model;

public class TankZombie extends Enemy {

    public TankZombie(int x, int y){
        super(x, y, 72, 72, 1.0, 250, 250);
    }

    @Override 
    public int attack(){
        return 15;
    }
    
    @Override 
    public double getAngle(){
        return super.getAngle() - (Math.PI / 2);
    }
}
