package cordova.plugin.itronbridge;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.LOG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.lang.ref.WeakReference;
import android.util.Log;

import com.itron.wh.androiddriver.service.aidl.IItronServiceCallback;


/**
 * This class echoes a string called from JavaScript.
 */
public class ItronBridge extends CordovaPlugin {

    private ItronBridgeService mDriverConnection;
    private boolean driverConnectionState = false;
    private Context mContext;
    private IItronServiceCallback callBack;
    
    //action
    private static final String SEND = "send";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        mContext = this.cordova.getActivity();

       this.mDriverConnection = new ItronBridgeService(new WeakReference(this));

        if (mDriverConnection != null) {
            driverConnectionState = this.mDriverConnection.safelyConnectTheService();
        } else {
            driverConnectionState = false;
        }
    }


    @Override
    public void onDestroy() {
       
        this.mDriverConnection.safelyDisconnectTheService();

        if(mDriverConnection != null) {
            unbindService(mDriverConnection);
            this.driverConnectionState =false;
        }
    }


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (SEND.equals(action)) {
            this.send(args, callbackContext);
            return true;
        }
        return false;
    }

    private void send(JSONArray args, CallbackContext callback)
    {
        if(args != null){
            try {
                string command = args.getJSONobject(0).getString("command");
                IItronServiceCallback callbackItron = new IItronServiceCallback();

                if(this.connectionState){
                    this.mDriverConnection.safelySendCommand(command,callbackItron);
                    
                     callback.success(true);
                } else {
                     callback.success(false);
                }

               

            } catch (Exception ex) {
                callback.error("Une erreur s'est produite: "+ex);
            }

        } else {
            callback.error("La liste des paramétres est null");
        }
    }

    private void connectService(CordovaArgs args, CallbackContext callback) throws JSONException {
       
        this.mDriverConnection = new ItronBridgeService(new WeakReference(this));

        if (this.mDriverConnection != null) {
            boolean connectionState = this.mDriverConnection.safelyConnectTheService();
            callback.success(connectionState);
        } else {
            callback.error("Echec connexion Itron Driver");
        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException {

        for(int result:grantResults) {
            if(result == PackageManager.PERMISSION_DENIED) {
                LOG.d(TAG, "User *rejected* location permission");
                this.permissionCallback.sendPluginResult(new PluginResult(
                        PluginResult.Status.ERROR,
                        "Location permission is required to discover unpaired devices.")
                    );
                return;
            }
        }
    }
}
