package model;

public class GameConstructor {

    private static final int MAP_COLS = 200;
    private static final int MAP_ROWS = 200;
    private static final String MAP_FILE = "/Resources/mapa_bosque_ultimo-111026/mapa_bosque_NWB.tmj";

    private GameConstructor() {}

    public static GameModel createGame(MatchDAO matchDAO) {
        GameClock clock = new GameClock();
        Player player = new Player(128, 3040, clock);
        player.equipWeapon(new Handgun(20, 400, 20, 20));

        GameState state = new GameState(player, MAP_COLS * GameState.TILE_SIZE, MAP_ROWS * GameState.TILE_SIZE);
        state.setCollisionMatrix(MapLoader.loadCollisionMatrix(MAP_FILE, MAP_COLS, MAP_ROWS));

        state.getGroundWeapons().add(new WeaponItem(1753, 1415, new Shotgun(30, 800, 7, 7)));
        state.getGroundWeapons().add(new WeaponItem(5774, 2226, new Shotgun(30, 800, 7, 7)));
        state.getGroundWeapons().add(new WeaponItem(2807, 323, new Rifle(30, 200, 30, 30)));
        state.getGroundWeapons().add(new WeaponItem(4825, 5342, new Rifle(30, 200, 30, 30)));

        return new GameModel(state, matchDAO);
    }
}
