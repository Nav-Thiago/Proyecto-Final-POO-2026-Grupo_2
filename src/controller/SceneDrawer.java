package controller;

import model.*;
import view.GamePanel;
import view.hud.HudData;

public class SceneDrawer {

    private final GameModel model;

    public SceneDrawer(GameModel model) {
        this.model = model;
    }

    public void drawScene(GamePanel panel) {
        GameState state = this.model.getState();
        Player player = state.getPlayer();

        int screenWidth = panel.getWidth();
        int screenHeight = panel.getHeight();
        int mapWidth = state.getMapWidth();
        int mapHeight = state.getMapHeight();

        // Cálculo de cámara
        int camX = Camera.calculateX(player, screenWidth, mapWidth);
        int camY = Camera.calculateY(player, screenHeight, mapHeight);

        panel.applyCamera(camX, camY);
        panel.drawBackground(mapWidth, mapHeight);


        drawPowerUps(panel, state); // [POWER-UP] primero, para que queden debajo de zombies y jugador
        drawGroundWeapons(panel, state); // [ARMAS] segundo, para que queden debajo de zombies y jugador
        drawZombies(panel, state);
        drawPoisonBullets(panel, state);
        drawPlayer(panel, state);
        drawFirstLayer(panel, state);
        drawDarkness(panel, state);
        drawBulletTracers(panel, state);
        drawMuzzleFlash(panel, state);

        panel.resetCamera(camX, camY);

        drawHud(panel, state);
        drawStatusMessage(panel);
    }

    private void drawPowerUps(GamePanel panel, GameState state) {
        for (PowerUp p : state.getPowerUps()) {
            panel.drawPowerUp(p.getX(), p.getY(), p.getWidth(), p.getHeight(), p.getClass().getSimpleName());
        }
    }

    private void drawGroundWeapons(GamePanel panel, GameState state) {
        for (WeaponItem item : state.getGroundWeapons()) {
            panel.drawGroundWeapon(item.getX(), item.getY(), item.getWidth(), item.getHeight(), item.getWeapon().getClass().getSimpleName());
        }
    }

    private void drawZombies(GamePanel panel, GameState state) {
        panel.updateZombieAnimations();
        for (Enemy zombie : state.getEnemies()) {
            if (zombie.isAlive()) {
                panel.drawZombie(zombie.getX(), zombie.getY(), zombie.getWidth(), zombie.getHeight(),
                zombie.getAngle(), zombie.getHealth(), zombie.getClass().getSimpleName(),
                zombie.getAttackProgress(), zombie.getSpitProgress());
            }
        }
    }

    private void drawPoisonBullets(GamePanel panel, GameState state) {
    for (Bullet poison : state.getPoisonBullets()) {
        panel.drawPoisonBullet(poison.getX(), poison.getY(), poison.getWidth(), poison.getHeight(),
                poison.getDirX(), poison.getDirY(), poison.getSpeed(), poison.getAge(),
                poison.getClass().getSimpleName());
    }
}

    private void drawPlayer(GamePanel panel, GameState state) {
        Player player = state.getPlayer();
        if (player.isAlive()) {
            Weapon weapon = player.getActiveWeapon();
            panel.drawPlayer(player.getX(), player.getY(), player.getWidth(), player.getHeight(),
                player.getAngle(), player.isMoving(), weapon.getClass().getSimpleName(),
                weapon.getSwingProgress());
        }
    }

    private void drawFirstLayer(GamePanel panel, GameState state) {
        panel.drawFirstLayer(state.getMapWidth(), state.getMapHeight());
    }

    private void drawDarkness(GamePanel panel, GameState state) {
        Player player = state.getPlayer();
        panel.drawDarkness(player.getX(), player.getY(), player.getWidth(), player.getHeight(), player.getAngle(), state.getMapWidth(), state.getMapHeight());
    }

    private void drawBulletTracers(GamePanel panel, GameState state) {
        for (Bullet bullet : state.getPlayerBullets()) {
            if (bullet.isActive()) {
                panel.drawBulletTracer(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight(), bullet.getDirX(), bullet.getDirY(), bullet.getSpeed(), bullet.getAge(), bullet.getKind().name());
            }
        }
    }

    private void drawMuzzleFlash(GamePanel panel, GameState state) {
        Player player = state.getPlayer();
        Weapon weapon = player.getActiveWeapon();

        if (player.isAlive() && weapon.isRanged()) {
            panel.drawMuzzleFlash(player.getX(), player.getY(), player.getWidth(), player.getHeight(), player.getAngle(), weapon.getClass().getSimpleName(), weapon.getLastShoot(), Weapon.MUZZLE_FORWARD, Weapon.MUZZLE_SIDE);
        }
    }

    private void drawHud(GamePanel panel, GameState state) {
        Player player = state.getPlayer();

        String pickup;
        while ((pickup = player.pollPickup()) != null) {
            panel.onPowerUpPickup(pickup);
        }

        Weapon weapon = player.getActiveWeapon();
        boolean ranged = weapon.isRanged();

        panel.drawHud(new HudData(
                player.getHealth(), player.getMaxHealth(),
                player.getShield(), ShieldDecorator.MAX_SHIELD,
                player.getDamageBonus(),
                ranged, weapon.getClass().getSimpleName(),
                ranged ? weapon.getActualMunition() : 0,
                ranged ? weapon.getMaxMunition() : 0));
    }

    private void drawStatusMessage(GamePanel panel) {
        GameModel.GameStatus status = this.model.getStatus();

        if (status == GameModel.GameStatus.GAME_OVER) {
            panel.drawGameOver(model.getMatchTimeMs());
        } else if (status == GameModel.GameStatus.GAME_WON) {
            panel.drawGameWon(model.getMatchTimeMs(), model.getBestTimeMs(), model.isNewRecord());
        } else if (status == GameModel.GameStatus.PAUSED) {
            panel.drawPause();
        } else if (status == GameModel.GameStatus.BOSS_INTRO) {
            panel.drawBossIntro(this.model.getBossIntroStep(), this.model.getBossIntroProgress());
        }
    }
}
