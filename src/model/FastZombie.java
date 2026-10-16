package model;

public class FastZombie  extends Enemy {
    
    public FastZombie(int x, int y){
     super(x, y, 40, 64, 3.0, 50,50);
    }

    @Override 
    public int attack(){
        return 25;
    }
}
