package net.efrei.android.geodressr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;


/**
 * Etape 2 du jeu : le streetview
 * Affiche la streetview, le temps passé et la distance estimée dynamique du lieu à trouver.
 *
 * Paramètres :
 * - targetCoords : coordonnées GPS de la cible
 */
public class GameStreetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_street);

        loadStreetViewFragment();
    }



    private void loadStreetViewFragment() {
        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment) getSupportFragmentManager().findFragmentById(R.id.streetViewPanoramaView);

        streetViewPanoramaFragment.getStreetViewPanoramaAsync(streetViewPanorama -> {
            streetViewPanorama.setPosition(new LatLng(48.91522771645218, 2.2295974043058755));
        });
    }
}