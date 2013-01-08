package at.itp.uno.activity;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListViewAdapterPlayers extends ArrayAdapter<String> {
	private ArrayList<String> _spieler;
    Context mContext;
    int resLayout;
    
    public ListViewAdapterPlayers(Context context, int textViewResourceId, ArrayList<String> spieler){
    	  super(context, textViewResourceId, spieler);
          this.mContext = context;
          _spieler = spieler;
          resLayout = textViewResourceId;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
            	LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(resLayout, parent, false);
            }
            String spieler = _spieler.get(position); //Produce a row for each spieler
            if (spieler != null) {
                    TextView tv = (TextView)row.findViewById(android.R.id.text1);
                    tv.setTextColor(Color.BLACK);
                    if (tv != null) {
                          tv.setText(spieler);
                    }
            }
            return row;
   }
}