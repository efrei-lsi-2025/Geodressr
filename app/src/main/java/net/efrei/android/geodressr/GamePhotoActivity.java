package net.efrei.android.geodressr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * Etape 3 du jeu : prendre en photo le lieu
 *
 * Paramètres :
 * - targetCoords : coordonnées GPS de la cible
 */
public class GamePhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_photo);
    }


}