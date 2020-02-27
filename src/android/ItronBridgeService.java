package cordova.plugin.itronbridgeservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

//import com.google.gson.JsonObject;
import org.json.JSONObject;
import com.itron.wh.androiddriver.service.aidl.IItronServiceApi;
import com.itron.wh.androiddriver.service.aidl.IItronServiceApi.Stub;
import com.itron.wh.androiddriver.service.aidl.IItronServiceCallback;

import java.lang.ref.WeakReference;

public class ItronBridgeService implements ServiceConnection
{

    private static final String ITRON_DRIVER_ACTION = "com.itron.wh.androiddriver.service.intent.action.EXECUTE";
    private static final String SERVICE_NAME = "com.itron.wh.androiddriver.service.services.ItronDriverService";
    private static final String SERVICE_PACKAGE_NAME = "com.itron.wh.androiddriver.service";
    private static final String LOG_TAG = ItronBridgeService.class.getCanonicalName();
    private static final String EGEE_GUID = "d70741e1-585c-4cae-8f7c-e58f0b81c59e"; // Doit matcher avec la licence Itron
    private static final String EGEE_APPLICATION_ID = "Egee4Itron"; // Utilisé daans la commande Send


    // ------------------- Données privées -------------------

    private WeakReference<Activity> activity;
    private IItronServiceApi mService;

    // ------------------- Méthodes publiques -------------------

    // Constructeur

    public ItronBridgeService(WeakReference<Activity> activity) {
      this.activity = activity;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(LOG_TAG, "Connexion au service Itron activée!");
        this.mService = Stub.asInterface(service);
        if (mService == null) {
            Intent bindIntent = new Intent(ITRON_DRIVER_ACTION);
            bindIntent.setClassName(SERVICE_PACKAGE_NAME, SERVICE_NAME);
            this.activity.get().bindService(bindIntent, this, Context.BIND_AUTO_CREATE);
        }
        resetDriverSettings();
     }

     @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(LOG_TAG, "Connexion au service Itron désactivée.");
        safelyDisconnectTheService();
        //mService = null;
    }

     public boolean safelyConnectTheService() {
      boolean result = false;
      if (mService == null) {
          Intent bindIntent = new Intent(ITRON_DRIVER_ACTION);
          bindIntent.setClassName(SERVICE_PACKAGE_NAME, SERVICE_NAME);
          this.activity.get().bindService(bindIntent, this, Context.BIND_AUTO_CREATE);
          result = true;
      }
      return result;
  }

  public void safelyDisconnectTheService() {
      if (mService != null) {
          this.activity.get().unbindService(this);
          this.mService = null;
      }
  }

  public void safelySendCommand(String command, IItronServiceCallback callback) throws RemoteException {
      Log.d(LOG_TAG, "Trying to query the message from the Service.");
      if (mService == null) {
          throw new RemoteException("Lien au service inexistant!");
      }
      checkErrorCode(this.mService.send(EGEE_APPLICATION_ID, command, callback));
      Log.d(LOG_TAG, "Commande envoyée");
  }

     public int safelySetDriverSettings(String driverSettings) throws RemoteException {
      Log.d(LOG_TAG, "Trying to set Driver settings");
      if (mService == null) {
          throw new RemoteException("Service no binded!");
      }
      int result = this.mService.setSettings(driverSettings);
      checkErrorCode(result);
      Log.d(LOG_TAG, "Driver settings set");
      return result;
  }

     public void safelyCancel() throws RemoteException {
      Log.d(LOG_TAG, "Trying to query the message from the Service.");
      if (this.mService == null) {
          throw new RemoteException("Service not bound!");
      }
      checkErrorCode(this.mService.cancel(EGEE_APPLICATION_ID));
      Log.d(LOG_TAG, "Commande annulée");
  }

  public void checkErrorCode(int errorCode) throws RemoteException {
    Log.d(LOG_TAG, "checkErrorCode " + errorCode);
      switch (errorCode) {
          case 0:
              Log.d(LOG_TAG, "Commande envoyée");
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


     private void resetDriverSettings() {
      try {
          safelySetDriverSettings(getDefaultDriverSettings());
      } catch (RemoteException e) {
          Log.w("MyItron MainActivity", "Echec actualisation des paramètres du driver Itron");
      }
    }

     private String getDefaultDriverSettings() {
        String settings ="{\"ATS\" : true,\"INTELIS_CONTINUOUS_WATER_SUPPLY\" : false,\"INTELIS_IGNORE_REVERSEDMETER_ON_NON_CONTINUOUS_WATER_SUPPLY\": false}";
        return settings;
  }

}