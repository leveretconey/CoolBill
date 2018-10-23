package Share;

import android.content.Context;


public class Global {

    public static DBHelper dbHelper;
    private static boolean initialied=false;
    public static void appInitialization(Context context){

        if (!initialied) {
            initialied=true;
            dbHelper=new DBHelper(context,
                    "BillItem.db",null,1);
        }
    }
}
