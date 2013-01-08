package at.itp.uno.wifi;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WifiAdapter extends ArrayAdapter<ScanResult> {
	
	private List<ScanResult> hotSpotList;
	Context mContext;
	int resLayout;
	
	public WifiAdapter(Context context, int textViewResourceId,
			List<ScanResult> deviceList) {
		super(context, textViewResourceId, deviceList);
		this.mContext = context;
        this.hotSpotList = deviceList;
        resLayout = textViewResourceId;
	}
	
	 @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	            View row = convertView;
	            if (row == null) {
	            	LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                row = inflater.inflate(resLayout, parent, false);
	            }
	            ScanResult result = this.hotSpotList.get(position); //Produce a row for each device
	            if (result != null) {
	                    TextView tv = (TextView)row.findViewById(android.R.id.text1);
	                    if (tv != null) {
	                          tv.setText(result.toString());
	                    }
	            }
	            return row;
	   }

}
