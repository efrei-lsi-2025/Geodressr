package net.efrei.android.geodressr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


/**
 * Etape 2 du jeu : le streetview
 * Affiche la streetview, le temps passé et la distance estimée dynamique du lieu à trouver.
 *
 * Paramètres :
 * - targetCoords : coordonnées GPS de la cible
 */
public class GameStreet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_street);
    }
}