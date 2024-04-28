package net.efrei.android.geodressr;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import net.efrei.android.geodressr.timer.TimerUtils;

/**
 * Etape 3 du jeu : prendre en photo le lieu
 *
 * Paramètres :
 * - positionLatitude + positionLongitude : double - coordonnées GPS trouvées.
 * - timeSpent : long - nombre de secondes passées pour cette partie
 */
public class GamePhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_photo);
        getIntent().getDoubleExtra("positionLatitude", 0);
        getIntent().getDoubleExtra("positionLongitude", 0);

        setupText();
    }
    public void setupText() {
        long timeSpentSec = getIntent().getLongExtra("timeSpent", 1);
        TextView timeSpent = findViewById(R.id.photoTimeSpent);
        timeSpent.setText(TimerUtils.formatTime(timeSpentSec));
    }

    public void onAddPhotoClick(View view) {
        dispatchTakePictureIntent();
    }

    public void onSavePhotoClick(View view) {
    }

    private Uri cam_uri;
    private void handlePhotoReceived(ActivityResult result) {
        if (result.getResultCode() != RESULT_OK) {
            return;
        }
        // There are no request codes
        ImageView imgView = findViewById(R.id.photoContainer);
        imgView.setImageURI(cam_uri);
        Button saveBtn = findViewById(R.id.photoSaveBtn);
        saveBtn.setEnabled(true);
    }
    private void dispatchTakePictureIntent() {
        try {
            ActivityResultLauncher<Intent> startCamera = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), this::handlePhotoReceived
            );
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            cam_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cam_uri);
            startCamera.launch(cameraIntent); // VERY NEW WAY
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}