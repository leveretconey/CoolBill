package Util;

import android.app.Application;
import android.content.Context;


public class AppContext extends Application {

    public static DBHelper dbHelper;
    public static void appInitialization(Context context){
        dbHelper=new DBHelper(context,
                "BillItem.db",null,1);
    }
}
