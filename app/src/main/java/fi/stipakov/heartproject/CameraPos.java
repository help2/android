package fi.stipakov.heartproject;

/**
 * Created by stipa on 3.9.15.
 */
public class CameraPos {
    public double lat;
    public double lon;
    public float zoom;

    public CameraPos(double lat, double lon, float zoom) {
        this.lat = lat;
        this.lon = lon;
        this.zoom = zoom;
    }
}
