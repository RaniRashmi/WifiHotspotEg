package com.rashminidhi.wifihotspoteg;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MyActivity";
    WifiManager wifiManager;
    Button enableHotspotButton,receiverButton,disableHotspotButton,enableMobileDataButton;
    EditText ssidEditText,passwordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //enabling mobile data

//        ConnectivityManager dataManager  = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//            Method dataMtd = null;
//            try {
//                dataMtd =ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }
//            dataMtd.setAccessible(true);
//            try {
//                dataMtd.invoke(dataManager, true);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }


        enableHotspotButton = (Button)findViewById(R.id.enableHotspot);
        enableMobileDataButton = (Button) findViewById(R.id.enableMobleData);
        receiverButton  = (Button)findViewById(R.id.receiver);
        disableHotspotButton = (Button) findViewById(R.id.disableHotspot);

        ssidEditText = (EditText)findViewById(R.id.ssid);
        passwordEditText = (EditText)findViewById(R.id.passwd);

        enableMobileDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.android.settings",
                        "com.android.settings.Settings$DataUsageSummaryActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        enableHotspotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createWifiAccessPoint();
            }
        });
        receiverButton.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this,RecevierActivity.class);
                            startActivity(intent);
                    }
        });

        disableHotspotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopWifiAccessPoint();
            }
        });
    }


    //disabling the hotspot
    public void stopWifiAccessPoint() {

        wifiManager = (WifiManager) getBaseContext().getSystemService
                (Context.WIFI_SERVICE);
        try
        {
            Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();

            for (Method method : wmMethods) {
                if (method.getName().equals("setWifiApEnabled")) {
                    try {
                        method.invoke(wifiManager, null, false);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch(Exception e)
        {
            Log.d(this.getClass().toString(), "",e);

        }
    }

    //method for hotspot creation
    public void createWifiAccessPoint() {

        wifiManager = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }

        //configure wifi network
        WifiConfiguration netConfig = new WifiConfiguration();

        netConfig.SSID = ssidEditText.getText().toString();
        netConfig.preSharedKey = passwordEditText.getText().toString();

        //open system authentication
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        //pre-shared  WPA password key
        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);


        try {
            //creating hotspot
            Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            final boolean apstatus = (Boolean) setWifiApMethod.invoke(wifiManager, netConfig, true);

            Method isWifiApEnabledmethod = wifiManager.getClass().getMethod("isWifiApEnabled");
            while (!(Boolean) isWifiApEnabledmethod.invoke(wifiManager)) {};
            Method getWifiApStateMethod = wifiManager.getClass().getMethod("getWifiApState");  //getWifiApState switched on/off hotspot
            int apstate = (Integer) getWifiApStateMethod.invoke(wifiManager);
            Log.i(this.getClass().toString(), "Apstate ::: "+apstate);
            Method getWifiApConfigurationMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            netConfig = (WifiConfiguration) getWifiApConfigurationMethod.invoke(wifiManager);
            Log.e("CLIENT", "\nSSID:" + netConfig.SSID + "\nPassword:" + netConfig.preSharedKey + "\n");

            if (apstatus) {
                Log.d(TAG, "Access Point created");
                Toast.makeText(this, "Access Point created", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Access Point creation failed");
            }

        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);

        }

    }

}
