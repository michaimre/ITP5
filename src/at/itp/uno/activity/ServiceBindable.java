package at.itp.uno.activity;

import at.itp.uno.wifi.Service_WifiAdmin.Binder_Service_WifiAdmin;

public interface ServiceBindable {

	public void setIBinder(Binder_Service_WifiAdmin binder);
	
}
