package net.efrei.android.geodressr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GameLocationService extends Service {
    public GameLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}