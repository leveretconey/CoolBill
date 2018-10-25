package Share;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.InvalidParameterException;

public class Options{
    private static final Options options=new Options();
    private String ipAddress=DEFAULT_IP;
    private int port=DEFAULT_PORT;
    private static final String DEFAULT_IP="192.168.1.2";
    private static final int DEFAULT_PORT=8080;
    private final static String FILE_PATH="options";


    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public static Options getInstance() {
        return options;
    }

    private Options() {
    }
    public Options setAddress(int[] ipPart) throws InvalidParameterException {
        if(ipPart.length!=4)
            throw new InvalidParameterException("无法解析ip地址");
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<4;i++){
            if(0 <= ipPart[i] && ipPart[i] <256){
                if (i!=0)
                    sb.append(".");
                sb.append(String.valueOf(ipPart[i]));
            }else {
                throw new InvalidParameterException("无法解析ip地址");
            }
        }
        this.ipAddress=sb.toString();
        return this;
    }
    public void initialize(Context context){
        try {
            String json=Util.loadFromFile(context,FILE_PATH);
            Gson gson=new Gson();
            Options temp=gson.fromJson(json,Options.class);
            this.ipAddress=temp.ipAddress;
            this.port=temp.port;
        }catch (IOException e1){
            try {
                save(context);
            }catch (Exception e2){
                e2.printStackTrace();
            }

            e1.printStackTrace();
        }
    }
    public Options setPort(int port) throws InvalidParameterException{
        if(port >=0 && port <=65536){
            this.port=port;
        }else {
            throw new InvalidParameterException("无法解析端口号");
        }
        return this;
    }
    public void save(Context context){
        try{
            Gson gson=new Gson();
            Util.saveIntoFile(context,gson.toJson(this),FILE_PATH);
        }catch (IOException e){

        }
    }
}
