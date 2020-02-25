package cordova.plugin.itronbridge;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.LOG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.pm.PackageManager;
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
import cordova.plugin.itronbridgeservice.ItronBridgeService;
/**
 * This class echoes a string called from JavaScript.
 */
public class ItronBridge extends CordovaPlugin {

    private ItronBridgeService mDriverConnection;
    private boolean driverConnectionState = false;
    private boolean connectionState = false;
    private Context mContext;
    private IItronServiceCallback callBack;
    private static final String TAG = "ITRONTAG";

    //action
    private static final String SEND = "send";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.d(this.getClass().getName(), "initialize");
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
        Log.d(this.getClass().getName(), "onDestroy");
        this.mDriverConnection.safelyDisconnectTheService();

        if(mDriverConnection != null) {
            cordova.getActivity().getApplicationContext().unbindService(mDriverConnection);
            this.driverConnectionState =false;
        }
    }


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(this.getClass().getName(), "execute : " + action);
        if (action.equals("send")) {
            this.send(args, callbackContext);
            return true;
        }
        return false;
    }

    private void send(JSONArray args, CallbackContext callback)
    {
        if(args != null){
            try {
                String command = args.getJSONObject(0).getString("command");
                Log.d(this.getClass().getName(), "send cmd : " + command);
                IItronServiceCallback callbackItron = new ItronServiceCallback();

                if(connectionState){
                    this.mDriverConnection.safelySendCommand(command,callbackItron);
                    
                     callback.success();
                } else {
                     callback.error("Echec de connexion");
                }

               

            } catch (Exception ex) {
                callback.error("Une erreur s'est produite: "+ex);
            }

        } else {
            callback.error("La liste des paramétres est null");
        }
    }

    private void connectService(CordovaArgs args, CallbackContext callback) throws JSONException {
        Log.d(this.getClass().getName(), "connectService");
      
        this.mDriverConnection = new ItronBridgeService(new WeakReference(this));

        if (mDriverConnection != null) {
            this.connectionState = this.mDriverConnection.safelyConnectTheService();
            callback.success();
        } else {
            callback.error("Echec connexion Itron Driver");
        }
    }
/*
    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException {

        PackageManager packageManager = this.cordova.getActivity().getPackageManager();
        for(int result:grantResults) {
            if(result == packageManager.PERMISSION_DENIED) {
                LOG.d(TAG, "User *rejected* location permission");
                this.permissionCallback.sendPluginResult(new PluginResult(
                        PluginResult.Status.ERROR,
                        "Location permission is required to discover unpaired devices.")
                    );
                return;
            }
        }
    }*/
}
