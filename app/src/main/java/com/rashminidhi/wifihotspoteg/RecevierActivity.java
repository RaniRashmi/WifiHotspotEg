package com.rashminidhi.wifihotspoteg;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RecevierActivity extends AppCompatActivity {

    EditText senderSSIDEditText,senderPasswordEditText;
    Button connectButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recevier);

        senderSSIDEditText = (EditText)findViewById(R.id.ssidEt);
        senderPasswordEditText = (EditText)findViewById(R.id.passwdEt);

        connectButton = (Button)findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ssid = senderSSIDEditText.getText().toString();
                String password = senderPasswordEditText.getText().toString();
                connectToSenderWifi(ssid,password);
            }
        });
    }

    private void connectToSenderWifi(String ssid,String password) {

        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = String .format("\"%s\"", ssid);
        wc.preSharedKey = String.format("\"%s\"", password);

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        int netId = wifiManager.addNetwork(wc);
        if (wifiManager.isWifiEnabled()){
            wifiManager.disconnect();
        }
        else {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.enableNetwork(netId,true);
        wifiManager.reconnect();
    }
}
