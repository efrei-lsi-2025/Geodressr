package net.efrei.android.geodressr.location;

import android.location.Location;
import android.util.Pair;

import java.util.Random;

public class LocationUtils {

    public static Location getRandomPointAround(Location currentLocation, Pair<Integer, Integer> minMaxRadius) {
        Random random = new Random();
        int min = minMaxRadius.first;
        int max = minMaxRadius.second;
        double distance = random.nextInt(max - min + 1) + min;
        double angle = random.nextInt(360);
        return calculateDestinationPoint(currentLocation, distance, angle);
    }


    private static Location calculateDestinationPoint(Location position, double distanceInMeter, double angle) {
        // https://stackoverflow.com/q/44419722
        double startLatitude = Math.toRadians(position.getLatitude());
        double startLongitude = Math.toRadians(position.getLongitude());
        double bearing = Math.toRadians(angle);
        double earthRadius = 6371e3;
        double angularDistance = distanceInMeter / earthRadius;

        double destinationLatitudeRadians = Math.asin(Math.sin(startLatitude) * Math.cos(angularDistance) +
                Math.cos(startLatitude) * Math.sin(angularDistance) * Math.cos(bearing));
        double destinationLongitudeRadians = startLongitude + Math.atan2(Math.sin(bearing) * Math.sin(angularDistance) * Math.cos(startLatitude),
                Math.cos(angularDistance) - Math.sin(startLatitude) * Math.sin(destinationLatitudeRadians));

        Location result = new Location(position);
        result.setLatitude(Math.toDegrees(destinationLatitudeRadians));
        result.setLongitude((Math.toDegrees(destinationLongitudeRadians) + 540) % 360 - 180);
        return result;
    }
}
