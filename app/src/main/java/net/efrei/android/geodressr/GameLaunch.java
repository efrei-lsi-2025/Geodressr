package net.efrei.android.geodressr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * Etape 1 du jeu : recherche d'une zone à proximité
 * Une fois la zone trouvée, va démarrer {@link GameStreet} en lui passant les coordonnées en paramètres
 * Paramètres :
 * - level : easy, normal, hard
 */
public class GameLaunch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_launch);
    }
}