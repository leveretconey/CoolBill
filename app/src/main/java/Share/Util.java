package Share;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Util {
    public static boolean isValidDate(String str) {
        boolean convertSuccess=true;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        try {
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            convertSuccess=false;
        }
        return convertSuccess;
    }
    public static void showSimpleAlert(Context context, String title,
                                       String message){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确认",null)
                .create()
                .show();
    }
}
