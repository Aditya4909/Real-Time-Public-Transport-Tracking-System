package com.transittrack.util;

/**
 * Geospatial computation utilities implementing the Haversine formula.
 * Calculates great-circle distances between points on a sphere from their latitudes and longitudes.
 */
public final class GeoUtils {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double DEFAULT_URBAN_SPEED_KMH = 30.0; // Typical urban transit average speed

    private GeoUtils() {
    }

    /**
     * Calculate the great-circle distance between two coordinates in kilometers using the Haversine formula.
     *
     * @param lat1 Latitude of point 1 (decimal degrees)
     * @param lon1 Longitude of point 1 (decimal degrees)
     * @param lat2 Latitude of point 2 (decimal degrees)
     * @param lon2 Longitude of point 2 (decimal degrees)
     * @return Distance in kilometers
     */
    public static double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double rLat1 = Math.toRadians(lat1);
        double rLat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(rLat1) * Math.cos(rLat2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round(EARTH_RADIUS_KM * c * 100.0) / 100.0;
    }

    /**
     * Calculate estimated travel time in minutes given distance and speed.
     * If speed is 0 (e.g., bus is boarding passengers or at a red light), uses default urban average.
     *
     * @param distanceKm Distance in kilometers
     * @param currentSpeedKmh Current speed in km/h
     * @return Estimated minutes (rounded to nearest integer, minimum 1 if distance > 0.05 km)
     */
    public static int calculateEtaMinutes(double distanceKm, double currentSpeedKmh) {
        if (distanceKm <= 0.05) {
            return 0; // Effectively at the stop
        }

        double effectiveSpeed = (currentSpeedKmh > 5.0) ? currentSpeedKmh : DEFAULT_URBAN_SPEED_KMH;
        double hours = distanceKm / effectiveSpeed;
        int minutes = (int) Math.round(hours * 60);

        return Math.max(1, minutes);
    }
}
