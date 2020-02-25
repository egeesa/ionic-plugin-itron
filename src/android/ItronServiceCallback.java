package cordova.plugin.itronbridge;


import com.itron.wh.androiddriver.service.aidl.IItronServiceCallback;
public class ItronServiceCallback extends IItronServiceCallback.Stub{ 

    public ItronServiceCallback() {
        super();
    } 

    @Override 
    public void onStatusUpdated(String str) {
        
    }
}