package fi.stipakov.heartproject;

import java.util.List;

/**
 * Created by stipa on 29.8.15.
 */
public class Place {
    String name;
    String id;
    String phone;
    String hours;
    String website;
    String person;
    String street;
    String city;
    String zipcode;
    double lat;
    double lon;
    double dist;
    String distStr;

    String addr1;
    String addr2;

    static String getDistanceStr(double dist) {
        String res = "";

        if (dist != 0) {
            if (dist < 100) {
                res += dist + "m";
            } else if (dist >= 100 && dist <= 1000) {
                res += (int) (dist / 100) + "00m";
            } else {
                res += String.format("%.1f km", dist / 1000.);
            }
        }

        return res;
    }

    List<String> items;
}
