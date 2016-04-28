package x7a.droid.signuplogin;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by wgs-lap148 on 11/30/15.
 */
public class Sc {
    public static void debug(String message) {
        Log.d("DEBUG", message);
    }
    public static void alert(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
