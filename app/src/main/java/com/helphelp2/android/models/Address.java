package com.helphelp2.android.models;

import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;

public class Address {

    public String city;
    public double lat;
    public double lon;
    public String street;
    public String zip;

    private String addr1;

    public String getAddr1() {
        if (addr1 != null) {
            return addr1;
        }
        List<String> address = new LinkedList<>();
        address.add(street);
        if (!zip.isEmpty()) {
            address.add(zip);
        }
        address.add(city);
        addr1 = TextUtils.join(", ", address);
        return addr1;
    }

    @Override
    public String toString() {
        return "city: " + city +
                "; lat: " + lat +
                "; lon: " + lon +
                "; street: " + street +
                "; zip: " + zip;
    }

}
