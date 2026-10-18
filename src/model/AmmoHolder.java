package model;

/* 
 * Contrato que tiene que cumplir cualquier arma que quiera recibir munición de los power ups.
 
 */
public interface AmmoHolder {
    void addAmmo(int amount);
    void refillAmmo();
    int getAmmoPerPickup();
}
