package com.xiaomi.mibrainsdkdemo;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;

public class AssetsHelper {
    public static InputStream getAssetsFile(Context context, String name) throws IOException {
        InputStream inputStream = context.getAssets().open(name);
        return inputStream;
    }
}
