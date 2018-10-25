package Share;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
    public static void saveIntoFile(Context context,String content,String path)
    throws IOException
    {
        BufferedWriter bw=null;
        try{
            bw=new BufferedWriter(new OutputStreamWriter
                    (context.openFileOutput(path,Context.MODE_PRIVATE)));
            bw.write(content);
            bw.close();
        }
        catch (IOException e){
            if (bw !=null){
                bw.close();
            }
            throw e;
        }
    }
    public  static  String loadFromFile(Context context,String path )
            throws IOException {
        BufferedReader br=null;
        try{
            br=new BufferedReader(new InputStreamReader(context.openFileInput(path)));
            StringBuilder sb=new StringBuilder();
            String buffer;
            while ((buffer=br.readLine())!=null){
                sb.append(buffer);
            }
            br.close();
            return sb.toString();
        }
        catch (IOException e){
            if (br !=null){
                br.close();
            }
            throw e;
        }
    }
}
