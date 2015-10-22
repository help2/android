package com.helphelp2.android.utils;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Locale;

public abstract class IntentHelper {

    public static final String GEO_INTENT_PATTERN = "geo:0,0?q=%s";

    public static Intent getLookUpAddressIntent(@NonNull String address) {
        String uri = String.format(Locale.ENGLISH, GEO_INTENT_PATTERN, address);
        return new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    }

}
