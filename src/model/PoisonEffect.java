package model;

public class PoisonEffect extends TimedEffect {
    private long lastTickTime;
    private final int tickInterval = 1000;
    private final int damagePerTick = 5;

    public PoisonEffect(long duration){
        super(duration);
        this.lastTickTime = System.currentTimeMillis();
    }

    @Override 
    public void update (Player player){
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastTickTime >= tickInterval) {
            player.takeDamage(damagePerTick);
            lastTickTime = currentTime;
        }
    }
    @Override
    public void apply(Player player) {
    // Opcional: Puedo hacer que el jugador se ponga verde cuando lo envenenan por 4 segundos
    }

    @Override
    public void remove(Player player) {
    // Opcional: Luego del efecto de veneno, devolverlo a su color normal
    }
}
