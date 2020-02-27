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
    private static final String OpenBluetooth = "OpenBluetooth";
    private static final String EGEE_GUID = "d70741e1-585c-4cae-8f7c-e58f0b81c59e"; // Doit matcher avec la licence Itron
    private static final String macAdress = "00:07:80:10:e8:4a"; 
   
    String openBluetoothCmd = "{\"Request\" : {\"RequestUserId\" : \"1\", \"Driver\" : \"ItronWHDriverCommon\",\"Command\" : \"OpenBluetooth\",\"ConnectionId\" : \"27\", \"Guid\": \""+ EGEE_GUID +"\",\"Parameters\" : {\"MacAddress\" : \"" + macAdress + "\"}}}";
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.d(this.getClass().getName(), "initialize");
        mContext = this.cordova.getActivity();

       this.mDriverConnection = new ItronBridgeService(new WeakReference(mContext));

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
            //TODO unbindService(mDriverConnection);
            this.driverConnectionState =false;
        }
    }


    @Override
    public boolean execute(String action, String args, CallbackContext callbackContext) throws JSONException {
        Log.d(this.getClass().getName(), "execute : " + action);
        if (SEND.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    this.send(args, callbackContext);
                    callbackContext.success(); // Thread-safe.
                }
            });
            return true;
        }
        return false;
    }

    private void send(String args, CallbackContext callback)
    {
        if(args != null){
            try {
                String command =  openBluetoothCmd;
                if(OpenBluetooth.equals(args)){
                    command =  openBluetoothCmd;
                }
                //String command = args.getJSONObject(0).getString("command");
                Log.d(this.getClass().getName(), "send cmd : " + command);
                Log.d(this.getClass().getName(), "driverConnectionState :" + driverConnectionState);
                IItronServiceCallback callbackItron = new ItronServiceCallback();

                if(driverConnectionState){
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

    /*private void connectService(CordovaArgs args, CallbackContext callback) throws JSONException {
        //TODO n'est pas appelé ?
        Log.d(this.getClass().getName(), "connectService");
      
        this.mDriverConnection = new ItronBridgeService(new WeakReference(this));

        if (mDriverConnection != null) {
            this.connectionState = this.mDriverConnection.safelyConnectTheService();
            callback.success();
        } else {
            callback.error("Echec connexion Itron Driver");
        }
    }*/
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
