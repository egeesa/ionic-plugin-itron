package cordova.plugin.itronbridge;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.LOG;

import org.json.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.AsyncTask;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.util.Log;

import com.itron.wh.androiddriver.service.aidl.IItronServiceApi;
import com.itron.wh.androiddriver.service.aidl.IItronServiceApi.Stub;
import com.itron.wh.androiddriver.service.aidl.IItronServiceCallback;

/**
 * This class echoes a string called from JavaScript.
 */
public class ItronBridge extends CordovaPlugin {

    private CallbackContext PUBLIC_CALLBACKS = null;
    private boolean connectionState = false;
    private Context mContext;
    private IItronServiceApi mItronServiceApi;
    private static final String TAG = "ITRONTAG";
    private ArrayList<String> mModuleList;

    //action
    private static final String ITRON_DRIVER_ACTION = "com.itron.wh.androiddriver.service.intent.action.EXECUTE";
    private static final String SERVICE_NAME = "com.itron.wh.androiddriver.service.services.ItronDriverService";
    private static final String SERVICE_PACKAGE_NAME = "com.itron.wh.androiddriver.service";
    private static final String EGEE_APPLICATION_ID = "Egee4Itron"; // Utilisé dans la commande Send et cancel pour éviter la collision avec d'autre application qui consommerait le même service
    private WeakReference<Activity> mActivity;

    private static final String EGEE_GUID = "d70741e1-585c-4cae-8f7c-e58f0b81c59e"; // Doit matcher avec la licence
    private static final String OPENCONNECTION = "openConnection";
    private static final String CLOSECONNECTION = "closeConnection";
    private static final String READCYBLE = "readCyble";
    private static final String READCYBLEENHANCED = "readCybleEnhanced";
    private static final String READPULSE = "readPulse";
    private static final String READPULSEENHANCED = "readPulseEnhanced";
    private static final String UPDATELICENSE = "updateLicense";
    private static final String READCYBLEPOLLING = "readCyblePolling";
    private static final String READPULSEPOLLING = "readPulsePolling";
    private static final String CONFIGUREENHANCEDDATEANDTIME = "configureEnhancedDateAndTime";
    private static final String CONFIGUREDATEANDTIME = "configureDateAndTime";


    private IItronServiceCallback mItronServiceCallback;
    private String mMessage = "";
    private Integer connectionId = 0;
    private Integer retourSendCommand;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.d(TAG + this.getClass().getName(), "Initialisation du plugin");
        mContext = this.cordova.getActivity();
        mActivity = new WeakReference(mContext);
        mModuleList = new ArrayList<String>();

        Intent bindIntent = new Intent(ITRON_DRIVER_ACTION);
        bindIntent.setClassName(SERVICE_PACKAGE_NAME, SERVICE_NAME);
        this.mActivity.get().bindService(convertImplicitIntentToExplicitIntent(bindIntent, mContext), serviceConnexion,
                mContext.BIND_AUTO_CREATE);

        this.mItronServiceCallback = new ReceiveItronMessage();
    }

    
    @Override
    public void onDestroy() {
        Log.d(TAG+this.getClass().getName(), "onDestroy");
       
    }


    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext)
            throws JSONException {
        
        PUBLIC_CALLBACKS = callbackContext;

        Log.d(TAG + this.getClass().getName(), "execute : " + action);
        
       if(OPENCONNECTION.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    
                    openConnection(args, callbackContext);
                }
           }); 
        } else if(CLOSECONNECTION.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    
                    closeConnection(args, callbackContext);
                }
           }); 
        } else if(READCYBLE.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    
                    readCyble(args, callbackContext);
                }
           }); 
        } else if(READPULSE.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    
                    readPulse(args, callbackContext);
                }
           }); 
        } else if(UPDATELICENSE.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    
                    updateLicense(args, callbackContext);
                }
           }); 
        } else if(READCYBLEPOLLING.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    
                    readCyblePolling(args, callbackContext);
                }
           }); 
        } else  if(READPULSEPOLLING.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    
                    readPulsePolling(args, callbackContext);
                }
           }); 
        } else  if(READCYBLEENHANCED.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    
                    readCybleEnhanced(args, callbackContext);
                }
           }); 
        } else  if(READPULSEENHANCED.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    
                    readPulseEnhanced(args, callbackContext);
                }
           }); 
        } else  if(CONFIGUREENHANCEDDATEANDTIME.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    
                    configureEnhancedDateAndTime(args, callbackContext);
                }
           }); 
        } else  if(CONFIGUREDATEANDTIME.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    
                    configureDateAndTime(args, callbackContext);
                }
           }); 
        }else {
            return false;
        }
      
        PluginResult pluginResult = new  PluginResult(PluginResult.Status.NO_RESULT); 
        pluginResult.setKeepCallback(true);
        return true;
    }

    
     /**
     * Cette fonction permet de lire un module Itron de type CYBLE RF
     *
     * @param args un object contenant le numéro du module ex: {numeroModule: "090298685", connectionId: 3}
     * @param callbackContext A Cordova callback context
     * @return un object contenant les données du module (index, alarmes ...etc)
     */
    private void readCyble(JSONArray args, CallbackContext callback)
    {
        if (args != null) {

            try {
                JSONObject params = args.getJSONObject(0);
                String param1 = params.getString("numeroModule");
                Integer param2 = Integer.parseInt(params.getString("connectionId"));
                Integer param3 = Integer.parseInt(params.getString("requestUserId"));
                
                Log.d(TAG + this.getClass().getName(), "numeroModule : " + param1);

                String cmdReadCyble = "{\"Request\" : {\"RequestUserId\" : " + param3 + ", \"Driver\" : \"ItronWHDriverCyble\",\"Command\" : \"ReadCyble\",\"ConnectionId\" : "
                        + param2 + ", \"Guid\": \"" + EGEE_GUID + "\",\"Parameters\" : {\"SerialNumber\" : \"" + param1
                        + "\"}}}";

                if (mItronServiceApi != null) {
                    retourSendCommand = mItronServiceApi.send(EGEE_APPLICATION_ID, cmdReadCyble,
                            this.mItronServiceCallback);
                    checkErrorCode(retourSendCommand, cmdReadCyble);
                } else {
                    callback.error("Echec instanciation du driver service Itron. Réessayer");
                }

            } catch (RemoteException e) {
                callback.error("Erreur RemoteException: " + e.toString());
            } catch (JSONException jsonEx) {
                callback.error("Erreur JSONException: " + jsonEx.toString());
            } catch (Exception exc) {
                callback.error("Erreur Exception: " + exc.toString());
            }

        } else {
            callback.error("La liste des paramétres est vide");
        }
    }

    /**
     * Cette fonction permet de lire un module Itron de type CYBLE ENHANCED
     *
     * @param args un object contenant le numéro du module ex: {numeroModule: "090298685", connectionId: 3}
     * @param callbackContext A Cordova callback context
     * @return un object contenant les données du module (index, alarmes ...etc)
     */
    private void readCybleEnhanced(JSONArray args, CallbackContext callback)
    {
        if (args != null) {

            try {
                JSONObject params = args.getJSONObject(0);
                String param1 = params.getString("numeroModule");
                Integer param2 = Integer.parseInt(params.getString("connectionId"));
                Integer param3 = Integer.parseInt(params.getString("requestUserId"));
                
                Log.d(TAG + this.getClass().getName(), "numeroModule : " + param1);

                String cmdReadCyble = "{\"Request\" : {\"RequestUserId\" : " + param3 + ", \"Driver\" : \"ItronWHDriverCyble\",\"Command\" : \"ReadNRFFrance\",\"ConnectionId\" : "
                        + param2 + ", \"Guid\": \"" + EGEE_GUID + "\",\"Parameters\" : {\"SerialNumber\" : \"" + param1
                        + "\"}}}";

                if (mItronServiceApi != null) {
                    retourSendCommand = mItronServiceApi.send(EGEE_APPLICATION_ID, cmdReadCyble,
                            this.mItronServiceCallback);
                    checkErrorCode(retourSendCommand, cmdReadCyble);
                } else {
                    callback.error("Echec instanciation du driver service Itron. Réessayer");
                }

            } catch (RemoteException e) {
                callback.error("Erreur RemoteException: " + e.toString());
            } catch (JSONException jsonEx) {
                callback.error("Erreur JSONException: " + jsonEx.toString());
            } catch (Exception exc) {
                callback.error("Erreur Exception: " + exc.toString());
            }

        } else {
            callback.error("La liste des paramétres est vide");
        }
    }


     /**
     * Cette fonction permet de lire un module Itron de type PULSE RF
     *
     * @param args un object contenant le numéro du module ex: {numeroModule: "090298685", connectionId: 3, requestUserId: }
     * @param callbackContext A Cordova callback context
     * @return un object contenant les données du module (index, alarmes ...etc)
     */
    private void readPulse(JSONArray args, CallbackContext callback)
     {
         if (args != null) {

             try {
                 JSONObject params = args.getJSONObject(0);
                 String param1 = params.getString("numeroModule");
                 Integer param2 = Integer.parseInt(params.getString("connectionId"));
                 Integer param3 = Integer.parseInt(params.getString("requestUserId"));

                 Log.d(TAG + this.getClass().getName(), "numeroModule : " + param1 + " requestUserId : " + param3);

                 String cmdreadPulse = "{\"Request\" : {\"RequestUserId\" : "
                         + param3 + ", \"Driver\" : \"ItronWHDriverPulse\",\"Command\" : \"ReadCyble\",\"ConnectionId\" : "
                         + param2 + ", \"Guid\": \"" + EGEE_GUID + "\",\"Parameters\" : {\"SerialNumber\" : \"" + param1
                         + "\"}}}";

                 if (mItronServiceApi != null) {
                     retourSendCommand = mItronServiceApi.send(EGEE_APPLICATION_ID, cmdreadPulse,
                             this.mItronServiceCallback);
                     checkErrorCode(retourSendCommand, cmdreadPulse);
                 } else {
                     callback.error("Echec instanciation du driver service Itron. Réessayer");
                 }

             } catch (RemoteException e) {
                 callback.error("Erreur RemoteException: " + e.toString());
             } catch (JSONException jsonEx) {
                 callback.error("Erreur JSONException: " + jsonEx.toString());
             } catch (Exception exc) {
                 callback.error("Erreur Exception: " + exc.toString());
             }

         } else {
             callback.error("La liste des paramètres est vide");
         }
     }

          /**
     * Cette fonction permet de lire un module Itron de type PULSE Enhanced
     *
     * @param args un object contenant le numéro du module ex: {numeroModule: "090298685", connectionId: 3, requestUserId: }
     * @param callbackContext A Cordova callback context
     * @return un object contenant les données du module (index, alarmes ...etc)
     */
    private void readPulseEnhanced(JSONArray args, CallbackContext callback)
    {
        if (args != null) {

            try {
                JSONObject params = args.getJSONObject(0);
                String param1 = params.getString("numeroModule");
                Integer param2 = Integer.parseInt(params.getString("connectionId"));
                Integer param3 = Integer.parseInt(params.getString("requestUserId"));

                Log.d(TAG + this.getClass().getName(), "numeroModule : " + param1 + " requestUserId : " + param3);

                String cmdreadPulse = "{\"Request\" : {\"RequestUserId\" : "
                        + param3 + ", \"Driver\" : \"ItronWHDriverPulse\",\"Command\" : \"ReadNRFFrance\",\"ConnectionId\" : "
                        + param2 + ", \"Guid\": \"" + EGEE_GUID + "\",\"Parameters\" : {\"SerialNumber\" : \"" + param1
                        + "\"}}}";

                if (mItronServiceApi != null) {
                    retourSendCommand = mItronServiceApi.send(EGEE_APPLICATION_ID, cmdreadPulse,
                            this.mItronServiceCallback);
                    checkErrorCode(retourSendCommand, cmdreadPulse);
                } else {
                    callback.error("Echec instanciation du driver service Itron. Réessayer");
                }

            } catch (RemoteException e) {
                callback.error("Erreur RemoteException: " + e.toString());
            } catch (JSONException jsonEx) {
                callback.error("Erreur JSONException: " + jsonEx.toString());
            } catch (Exception exc) {
                callback.error("Erreur Exception: " + exc.toString());
            }

        } else {
            callback.error("La liste des paramètres est vide");
        }
    }
    
      /**
      * Cette fonction permet de lire un module Itron de type CYBLE RF en mode polling
      *
      * TESTER en partie, pouvoir retourner la liste des modules requêtés
      * @param args un object contenant le numéro du module ex: {modulesList: ["090298685","100258561","100258561"], connectionId: 3}
      * @param callbackContext A Cordova callback context
      * @return un object contenant les données des modules (index, alarmes ...etc)
      */
    private void readCyblePolling(JSONArray args, CallbackContext callback)
      {
          if (args != null) {

              try {
                  String[] param1;
                  JSONObject params = args.getJSONObject(0);
                  JSONArray modulesList = params.getJSONArray("modulesList");
                  Integer param2 = Integer.parseInt(params.getString("connectionId"));
                  Log.d(TAG + this.getClass().getName(), "Connection ID : " + param2);
                  if (modulesList.length() != 0) {
                      param1 = new String[modulesList.length()];
                      for (int i = 0; i < modulesList.length(); i++) {
                          param1[i] = modulesList.getString(i);
                      }

                      Log.d(TAG + this.getClass().getName(), "Nombre de module à lire : " + modulesList.length());

                      String cmdReadCyblePolling = "{\"Request\" : {\"RequestUserId\" : \"1\", \"Driver\" : \"ItronWHDriverCyble\",\"Command\" : \"ReadPollingCyble\",\"ConnectionId\" : "
                              + param2 + ", \"Guid\": \"" + EGEE_GUID + "\",\"Parameters\" : {\"SerialNumbers\" : "
                              + Arrays.toString(param1) + "}}}";

                      if (mItronServiceApi != null) {
                          retourSendCommand = mItronServiceApi.send(EGEE_APPLICATION_ID, cmdReadCyblePolling,
                                  this.mItronServiceCallback);
                          checkErrorCode(retourSendCommand, cmdReadCyblePolling);
                      } else {
                          callback.error("Echec instanciation du driver service Itron. Réessayer");
                      }
                  } else {
                      callback.error("Aucun module à lire");
                  }

              } catch (RemoteException e) {
                  callback.error("Erreur RemoteException: " + e.toString());
              } catch (JSONException jsonEx) {
                  callback.error("Erreur JSONException: " + jsonEx.toString());
              } catch (Exception exc) {
                  callback.error("Erreur Exception: " + exc.toString());
              }

          } else {
              callback.error("La liste des paramètres est vide");
          }
      }
    
        /**
      * Cette fonction permet de lire un module Itron de type PULSE RF en mode polling
      *
      * A TESTER
      * @param args un object contenant le numéro du module ex: {modulesList: ["090298685","100258561","100258561"], connectionId: 3}
      * @param callbackContext A Cordova callback context
      * @return un object contenant les données des modules (index, alarmes ...etc)
      */
    private void readPulsePolling(JSONArray args, CallbackContext callback)
    {
        if (args != null) {

            try {
                String[] param1 ;
                JSONObject params = args.getJSONObject(0);
                JSONArray modulesList = params.getJSONArray("modulesList");
                Integer param2 = Integer.parseInt(params.getString("connectionId"));
                Log.d(TAG + this.getClass().getName(), "Connection ID : " + param2);
                if (modulesList.length() != 0) {
                    param1 = new String[modulesList.length()];
                    for (int i = 0; i < modulesList.length(); i++) {
                        param1[i] = modulesList.getString(i);
                    }
                    
                    Log.d(TAG + this.getClass().getName(), "Nombre de module à lire : " + modulesList.length());

                    String cmdReadPulsePolling = "{\"Request\" : {\"RequestUserId\" : \"1\", \"Driver\" : \"ItronWHDriverCyble\",\"Command\" : \"ReadPollingPulse\",\"ConnectionId\" : "
                            + param2 + ", \"Guid\": \"" + EGEE_GUID + "\",\"Parameters\" : {\"SerialNumbers\" : " + Arrays.toString(param1) + "}}}";

                    if (mItronServiceApi != null) {
                        retourSendCommand = mItronServiceApi.send(EGEE_APPLICATION_ID, cmdReadPulsePolling,
                                this.mItronServiceCallback);
                        checkErrorCode(retourSendCommand, cmdReadPulsePolling);
                    } else {
                        callback.error("Echec instanciation du driver service Itron. Réessayer");
                    }
                } else {
                    callback.error("Aucun module à lire");
                }

            } catch (RemoteException e) {
                callback.error("Erreur RemoteException: " + e.toString());
            } catch (JSONException jsonEx) {
                callback.error("Erreur JSONException: " + jsonEx.toString());
            } catch (Exception exc) {
                callback.error("Erreur Exception: " + exc.toString());
            }

        } else {
            callback.error("La liste des paramètres est vide");
        }
    }

    
    /**
     * Cette fonction permet d'établir la connexion Bluetooth avec le RF Master
     *
     * @param args un object contenant l'adresse MAC du RF Master ex: { macAddress: '00:07:80:10:E8:4A'}
     * @param callbackContext A Cordova callback context
     * @return un object contenant l'identifiant de connexion.
     */
    private void openConnection(JSONArray args, CallbackContext callback)
    {
        if (args != null) {

            try {

                JSONObject params = args.getJSONObject(0);
                String param1 = params.getString("macAddress");
                //
                if (param1 != null || param1 != "") {

                    Log.d(TAG + this.getClass().getName(), "Mac Adresse : " + param1);

                    String openBluetoothCmd = "{\"Request\" : {\"RequestUserId\" : \"1\", \"Driver\" : \"ItronWHDriverCommon\",\"Command\" : \"OpenBluetooth\",\"ConnectionId\" : \"null \", \"Guid\": \""
                            + EGEE_GUID + "\",\"Parameters\" : {\"MacAddress\" : \"" + param1 + "\"}}}";

                    if (mItronServiceApi != null) {
                        retourSendCommand = mItronServiceApi.send(EGEE_APPLICATION_ID, openBluetoothCmd,
                                this.mItronServiceCallback);
                        checkErrorCode(retourSendCommand, openBluetoothCmd);
                    } else {
                        callback.error("echec");
                    }
                } else {
                    callback.error("La liste des paramètres est vide");
                }

            } catch (RemoteException e) {
                callback.error("Erreur RemoteException: " + e.toString());
            } catch (JSONException jsonEx) {
                callback.error("Erreur JSONException: " + jsonEx.toString());
            } catch (Exception exc) {
                callback.error("Erreur Exception: " + exc.toString());
            }

        } else {
            callback.error("La liste des paramètres est vide");
        }
    }
    

    /**
     * Cette fonction permet de fermer la connexion Bluetooth avec le RF Master
     *
     * @param args un object contenant l'identifiant de connexion ex: { connectionId: 3}
     * @param callbackContext A Cordova callback context
     * @return un object contenant le message de confirmation de fermeture.
     */
    private void closeConnection(JSONArray args, CallbackContext callback)
    {
        try {

            JSONObject params = args.getJSONObject(0);
            Integer param1 = Integer.parseInt(params.getString("connectionId"));

            String closeBluetoothCmd = "{\"Request\" : {\"RequestUserId\" : \"1\", \"Driver\" : \"ItronWHDriverCommon\",\"Command\" : \"CloseBluetooth\",\"ConnectionId\" : "
                    + param1 + ", \"Guid\": \"" + EGEE_GUID + "\",\"Parameters\" : \"null\"}}";

            Log.d(TAG + this.getClass().getName(), "send cmd: " + closeBluetoothCmd);

            if (mItronServiceApi != null) {

                retourSendCommand = mItronServiceApi.send(EGEE_APPLICATION_ID, closeBluetoothCmd,
                        this.mItronServiceCallback);
                checkErrorCode(retourSendCommand, closeBluetoothCmd);

            } else {
                callback.error("Echec instanciation du driver service Itron. Réessayer");
            }

        } catch (RemoteException e) {
            callback.error("Erreur RemoteException: " + e.toString());
        } catch (Exception exc) {
            callback.error("Erreur Exception: " + exc.toString());
        }
    }

    /**
     * Cette fonction permet de mettre à jour la licence Itron
     *
     * @param callbackContext A Cordova callback context
     * @return un object contenant le message de confirmation de fermeture.
     */
    private void updateLicense(JSONArray args, CallbackContext callback)
    {
        try {

            String updateLicenseCmd = "{\"Request\" : {\"RequestUserId\" : \"1\", \"Driver\" : \"ItronWHDriverCommon\",\"Command\" : \"UpdateLicense\",\"ConnectionId\" : \"null \", \"Guid\": \"" + EGEE_GUID + "\",\"Parameters\" : {\"LicenseFileName\" : \"/storage/emulated/0/Itron Driver Service/ItronLicense.lic\"}}}";
            
            Log.d(TAG + this.getClass().getName(), "send cmd: " + updateLicenseCmd);
            
            if (mItronServiceApi != null) {

                retourSendCommand = mItronServiceApi.send(EGEE_APPLICATION_ID, updateLicenseCmd, this.mItronServiceCallback);
                checkErrorCode(retourSendCommand, updateLicenseCmd);
                
            } else {
                callback.error("Echec instanciation du driver service Itron. Réessayer") ;
            }
            
        } catch (RemoteException e) {
            callback.error("Erreur RemoteException: "+e.toString());
        }  catch (Exception exc) {
            callback.error("Erreur Exception: "+exc.toString());
        }
    }


     /**
     * Cette fonction permet de mettre à jour la date et l'heure d'un module cyble enhanced
     *
     * @param callbackContext A Cordova callback context
     * @return un object contenant le message de confirmation de fermeture.
     */
    private void configureEnhancedDateAndTime(JSONArray args, CallbackContext callback)
    {
        try {

            JSONObject params = args.getJSONObject(0);
            String param1 = params.getString("numeroModule");
            String param2 = params.getString("dateTime"); //format attendu YYYY-MM-DDThh:mm:ss
            Integer param3 = Integer.parseInt(params.getString("connectionId"));
            
            Log.d(TAG + this.getClass().getName(), "numeroModule : " + param1);

            String cmd = "{\"Request\" : {\"RequestUserId\" : \"1\", \"Driver\" : \"ItronWHDriverCyble\",\"Command\" : \"ConfigureEnhancedDateAndTime\",\"ConnectionId\" : "+ param3 + ", \"Guid\": \"" + EGEE_GUID + "\",\"Parameters\" : {\"SerialNumber\" : \"" + param1 + "\", \"MiuDate\":\"" + param2 + "\" }}}"; 
            
            Log.d(TAG + this.getClass().getName(), "send cmd: " + cmd);
            
            if (mItronServiceApi != null) {

                retourSendCommand = mItronServiceApi.send(EGEE_APPLICATION_ID, cmd, this.mItronServiceCallback);
                checkErrorCode(retourSendCommand, cmd);
                
            } else {
                callback.error("Echec instanciation du driver service Itron. Réessayer") ;
            }
            
        } catch (RemoteException e) {
            callback.error("Erreur RemoteException: "+e.toString());
        }  catch (Exception exc) {
            callback.error("Erreur Exception: "+exc.toString());
        }
    }

    /**
     * Cette fonction permet de mettre à jour la date et l'heure d'un module cyble RF
     *
     * @param callbackContext A Cordova callback context
     * @return un object contenant le message de confirmation de fermeture.
     */
    private void configureDateAndTime(JSONArray args, CallbackContext callback)
    {
        try {

            JSONObject params = args.getJSONObject(0);
            String param1 = params.getString("numeroModule");
            String param2 = params.getString("dateTime"); //format attendu YYYY-MM-DDThh:mm:ss
            Integer param3 = Integer.parseInt(params.getString("connectionId"));
            
            Log.d(TAG + this.getClass().getName(), "numeroModule : " + param1);

            String cmd = "{\"Request\" : {\"RequestUserId\" : \"1\", \"Driver\" : \"ItronWHDriverCyble\",\"Command\" : \"ConfigureDateAndTime\",\"ConnectionId\" : "+ param3 + ", \"Guid\": \"" + EGEE_GUID + "\",\"Parameters\" : {\"SerialNumber\" : \"" + param1 + "\", \"DateTimeAndWakeUpMode\": {\"MiuDate\":\"" + param2 + "\" }}}}"; 
            
            Log.d(TAG + this.getClass().getName(), "send cmd: " + cmd);
            
            if (mItronServiceApi != null) {

                retourSendCommand = mItronServiceApi.send(EGEE_APPLICATION_ID, cmd, this.mItronServiceCallback);
                checkErrorCode(retourSendCommand, cmd);
                
            } else {
                callback.error("Echec instanciation du driver service Itron. Réessayer") ;
            }
            
        } catch (RemoteException e) {
            callback.error("Erreur RemoteException: "+e.toString());
        }  catch (Exception exc) {
            callback.error("Erreur Exception: "+exc.toString());
        }
    }


    //  transfert des messages vers javascript
    public void transmitToJs(JSONObject message) {
        if (PUBLIC_CALLBACKS == null) { return; }
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, message);
        pluginResult.setKeepCallback(true); 
        PUBLIC_CALLBACKS.sendPluginResult(pluginResult);
    }

    private class ReceiveItronMessage extends IItronServiceCallback.Stub {
        public void onStatusUpdated(String message) {
            //Lancer la tâche des traitements des messages callbackItron
           
            if (message != "") {
                
                cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        try {

                            Log.d(TAG+this.getClass().getName(), "CallbackItron : " + message);
                           
                            JSONObject jsonObject = new JSONObject(message);
    
                            for (int i = 0; i < jsonObject.names().length(); i++) {
                                
                                if(("Information").equals(jsonObject.names().getString(i))){
    
                                    String myObjectData = jsonObject.get("Information").toString();
                                    JSONObject jsonCmd = new JSONObject(myObjectData);
                                    String cmd = getCommand(jsonCmd);

                                    if ("ReadCyble".equals(cmd) || "ReadNRFFrance".equals(cmd)) {
                                        String msg0 = getMessage(jsonCmd);
                                        if (!"Command started".equals(msg0)) {
                                            transmitToJs(jsonObject);
                                        }
                                    }

                                    if ("ReadPollingCyble".equals(cmd)) {
                                        String msg1 = getMessage(jsonCmd);
                                        if (!"Command started".equals(msg1)) {
                                            transmitToJs(jsonObject);
                                        }
                                    }
    
                                };
                               
                                if(("Error").equals(jsonObject.names().getString(i))){
    
                                    Log.d(TAG+this.getClass().getName(), "ERREUR : " + jsonObject.get(jsonObject.names().getString(i)));
                                    transmitToJs(jsonObject);
    
                                };
    
                                if (("Success").equals(jsonObject.names().getString(i))) {

                                    Log.d(TAG + this.getClass().getName(),
                                            "SUCCES : " + jsonObject.get(jsonObject.names().getString(i)));
                                    String myObjectSuccess = jsonObject.get("Success").toString();
                                    JSONObject jsonCmd = new JSONObject(myObjectSuccess);
                                    String cmd = getCommand(jsonCmd);

                                    if ("OpenBluetooth".equals(cmd)) {
                                        transmitToJs(jsonObject);
                                    }

                                    if ("CloseBluetooth".equals(cmd)) {
                                        JSONObject msg = new JSONObject();
                                        msg.put("message", "Liaison Bluetooth fermée.");
                                        transmitToJs(msg);
                                    }
                                };
    
                                if (("Data").equals(jsonObject.names().getString(i))) {
                                    Log.d(TAG + this.getClass().getName(),
                                            "DATA : " + jsonObject.get(jsonObject.names().getString(i)));
                                    String myObjectData = jsonObject.get("Data").toString();
                                    JSONObject jsonCmd = new JSONObject(myObjectData);
                                    String cmd = getCommand(jsonCmd);
                                    if ("ReadCyble".equals(cmd) || "ReadNRFFrance".equals(cmd)) {
                                        transmitToJs(jsonObject);
                                    }

                                    if ("ReadPollingCyble".equals(cmd)) {
                                        transmitToJs(jsonObject);
                                    }
                                };
                            }
                        } catch (JSONException e) {
                            Log.d(TAG+this.getClass().getName(), "Erreur JSONObject : " + e.toString());
                            throw new RuntimeException(e);
                        }
                    }
               }); 
            }
        }
    }

    private ServiceConnection serviceConnexion = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
           
            mItronServiceApi = IItronServiceApi.Stub.asInterface(service);
         }
    
         @Override
        public void onServiceDisconnected(ComponentName name) {
           try {
               retourSendCommand = mItronServiceApi.cancel(EGEE_APPLICATION_ID);
               checkErrorCode(retourSendCommand, "");
           } catch (RemoteException ex) {
            Log.d(TAG+this.getClass().getName(), "RemoteException onServiceDisconnected: " + ex.toString());
           }
            
        }
    };

    public Intent convertImplicitIntentToExplicitIntent(Intent implicitIntent, Context context) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryIntentServices(implicitIntent, 0);
        if (resolveInfoList == null || resolveInfoList.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfoList.get(0);
        ComponentName component = new ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }
    
    private String getCommand(JSONObject jsonObject) throws JSONException {
        if (jsonObject != null) {
            for (int i = 0; i < jsonObject.names().length(); i++) {
                if ("Command".equals(jsonObject.names().getString(i))) {
                    return jsonObject.get(jsonObject.names().getString(i)).toString();
                }
            }
            return "";
        } else {
            return "";
        }
    }
    
    private String getMessage(JSONObject jsonObject) throws JSONException {
        if (jsonObject != null) {
            for (int i = 0; i < jsonObject.names().length(); i++) {
                if ("Message".equals(jsonObject.names().getString(i))) {
                    return jsonObject.get(jsonObject.names().getString(i)).toString();
                }
            }
            return "";
        } else {
            return "";
        }
    }
    
    private JSONObject getDataObject(JSONObject jsonObject) throws JSONException {
        if (jsonObject != null) {
            for (int i = 0; i < jsonObject.names().length(); i++) {
                if ("Data".equals(jsonObject.names().getString(i))) {
                    String myStringObject = jsonObject.get(jsonObject.names().getString(i)).toString();
                    JSONObject jsonDataObject = new JSONObject(myStringObject);
                    for (int j = 0; j < jsonDataObject.names().length(); j++) {
                        if ("ConnectionId".equals(jsonDataObject.names().getString(j))) {
                            return jsonDataObject;
                        }
                    }
                    return null;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    private Integer getConnectionId(JSONObject jsonObject) throws JSONException {
        if (jsonObject != null) {
            for (int i = 0; i < jsonObject.names().length(); i++) {
                if ("Data".equals(jsonObject.names().getString(i))) {
                    String myStringObject = jsonObject.get(jsonObject.names().getString(i)).toString();
                    JSONObject jsonDataObject = new JSONObject(myStringObject);
                    for (int j = 0; j < jsonDataObject.names().length(); j++) {
                        if ("ConnectionId".equals(jsonDataObject.names().getString(j))) {
                            String connexionId = jsonDataObject.get(jsonDataObject.names().getString(j)).toString();
                            return Integer.parseInt(connexionId);
                        }
                    }
                    return 0;
                }
            }
            return 0;
        } else {
            return 0;
        }
    }

    public void checkErrorCode(int errorCode, String cmd) throws RemoteException {
       
          switch (errorCode) {
              case 0:
              Log.i(TAG + this.getClass().getName(), "Commande envoyée : " + cmd);
              case 5:
                  return;
              case 1:
                  throw new RemoteException("Commande invalide");
              case 2:
                  throw new RemoteException("Callback invalide");
              case 3:
                  throw new RemoteException("Erreur interne");
              case 4:
                  throw new RemoteException("Identifiant Application Itron incorrect");
              default:
                  throw new RemoteException("Erreur inconnu");
          }
      }
}
