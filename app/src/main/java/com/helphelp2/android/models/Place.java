package com.helphelp2.android.models;

import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by stipa on 29.8.15.
 */
public class Place {

    public String name;
    public String id;
    public String phone;
    public String hours;
    public String website;
    public String person;
    public double distance;
    public String distStr; // Not contained in JSON response
    public boolean helpers;
    public Address addr;
    public List<String> items;

    private String addr2;

    public String getAddr2() {
        if (addr2 != null) {
            return addr2;
        }
        List<String> address = new LinkedList<>();
        if (!person.isEmpty()) {
            address.add(person);
        }
        if (!phone.isEmpty()) {
            address.add(String.format("<a href=\"tel:%s\">%s</a>", phone, phone));
        }
        if (!hours.isEmpty()) {
            address.add(hours);
        }
        if (!website.isEmpty()) {
            address.add(String.format("<a href=\"%s\">%s</a>", website, website));
        }
        addr2 = TextUtils.join(", ", address);
        return addr2;
    }

    public static String getDistanceStr(double dist) {
        String res = "";

        if (dist != 0) {
            if (dist < 100) {
                res += (int)dist + "m";
            } else if (dist >= 100 && dist <= 1000) {
                res += (int) (dist / 100) + "00m";
            } else {
                res += String.format("%.1f km", dist / 1000.);
            }
        }

        return res;
    }

    @Override
    public String toString() {
        return "id: " + id +
                "; distance: " + distance +
                "; helpers: " + helpers +
                "; hours: " + hours +
                "; name: " + name +
                "; person: " + person +
                "; phone: " + phone +
                "; website: " + website +
                "; items: " + items +
                "; addr: { " + addr + " }";
    }

}
