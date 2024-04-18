package net.efrei.android.geodressr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Pair;
import android.widget.TextView;

import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import net.efrei.android.geodressr.timer.ThreadedTimer;
import net.efrei.android.geodressr.timer.TimerUtils;


/**
 * Etape 2 du jeu : le streetview
 * Affiche la streetview, le temps passé et la distance estimée dynamique du lieu à trouver.
 *
 * Paramètres :
 * - targetCoordsLongitude : longitude du lieu à trouver
 * - targetCoordsLatitude : latitude du lieu à trouver
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

        // get intent targetCoords
        LatLng targetCoords = new LatLng(
                getIntent().getDoubleExtra("targetCoordsLatitude", 0),
                getIntent().getDoubleExtra("targetCoordsLongitude", 0)
        );

        StreetViewPanoramaOptions options = new StreetViewPanoramaOptions()
                .position(targetCoords)
                .streetNamesEnabled(false)
                .userNavigationEnabled(false);

        SupportStreetViewPanoramaFragment fragment =
                SupportStreetViewPanoramaFragment.newInstance(options);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.streetViewPanoramaView, fragment)
                .commit();

        launchTimer();
    }

    private void launchTimer() {
        TextView timerTextView = findViewById(R.id.timerTextView);
        ThreadedTimer timer = new ThreadedTimer(secondsRemaining -> {
            runOnUiThread(() -> timerTextView.setText(TimerUtils.formatTime(secondsRemaining)));
        });
        timer.start();
    }
}