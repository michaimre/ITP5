package at.itp.uno.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import at.itp.uno.wifi.WifiAdapter;
import at.itp_uno_wifi_provider.R;

public class Activity_WifiClient extends Activity implements Button.OnClickListener , ListView.OnItemClickListener{
	
	private Button b_searchSpots;
	private ListView lv_hotSpots;
	private WifiManager wifi_m;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wificlient);
        b_searchSpots = (Button)findViewById(R.id.b_searchSpots);
        b_searchSpots.setOnClickListener(this);
        lv_hotSpots = (ListView)findViewById(R.id.lv_hotSpots);
        lv_hotSpots.setOnItemClickListener(this);
        wifi_m = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if(wifi_m.isWifiEnabled()){
          wifi_m.setWifiEnabled(false);
        }else{
          wifi_m.setWifiEnabled(true);
        }
        
        wifi_m.createWifiLock(1, "WifiLock");
        
        
    }


	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		ScanResult hotSpot = (ScanResult) lv_hotSpots.getAdapter().getItem(position);
		WifiConfiguration config = new WifiConfiguration();
		
		/*List<WifiConfiguration> configList = wifi_m.getConfiguredNetworks();
		for(WifiConfiguration wifi : configList){
			Log.v("Configs", wifi.toString());
			
		}*/
	
		config.SSID = "\"" + hotSpot.SSID + "\""; //"\"SSIDName\"";
		//config.preSharedKey  ="\"samsamsam\"";
		config.hiddenSSID = false;
		config.status = WifiConfiguration.Status.ENABLED;        
		config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		//wifi_m.removeNetwork(0);
		//wifi_m.disconnect();
		
		int res = wifi_m.addNetwork(config);
		boolean networkEnabled = wifi_m.enableNetwork(res, true); 
		
		//for(int x=0; x <900;x++){ Log.v("s","sd"+x);}
		Log.d("WifiPreference", "add Network returned " + res );
		Log.d("WifiPreference", "enableNetwork returned " + networkEnabled );
		Log.d("WifiPreference", "enableNetwork returned " + wifi_m.getConnectionInfo().toString());
		Log.d("WifiPreference", "enableNetwork returned " + wifi_m.getDhcpInfo());
		Log.d("WifiPreference", "enableNetwork returned " + wifi_m.pingSupplicant());
		if(networkEnabled){
			Intent i = new Intent(this,Activity_ClientGame.class);
			startActivity(i);
		}
	}

	@Override
	public void onClick(View arg0) {
		if(!wifi_m.isWifiEnabled()){
			wifi_m.setWifiEnabled(true);
		}
		wifi_m.startScan();
		List<ScanResult> scan = (List<ScanResult>) wifi_m.getScanResults();
		if(scan == null){
			Log.v("onClick -->","NULL");
		}
		else{
			Log.v("onClick -->","SizeOfScan" + scan.size());
			WifiAdapter adapter = new WifiAdapter(this, android.R.layout.simple_expandable_list_item_1 , scan);
			lv_hotSpots.setAdapter(adapter);
		}
		
		
		
	}
}
