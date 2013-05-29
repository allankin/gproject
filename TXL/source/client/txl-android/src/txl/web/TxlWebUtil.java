package txl.web;

import android.app.Activity;
import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

public class TxlWebUtil {
	public static void settingWebView(WebView wv,Activity ctx){
		WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
        // Set the nav dump for HTC
        settings.setNavDump(true);
        // Enable database
        settings.setDatabaseEnabled(true);
        String databasePath = ctx.getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setDatabasePath(databasePath);
        // Enable DOM storage
        settings.setDomStorageEnabled(true);
        // Enable built-in geolocation
        settings.setGeolocationEnabled(true);
        settings.setBuiltInZoomControls(true);
	}
}
