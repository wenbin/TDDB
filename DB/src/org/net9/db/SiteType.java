package org.net9.db;
import java.io.Serializable;


public class SiteType implements Serializable {
	private String siteName;
	private String address;
	
	SiteType(String siteName, String address) {
		this.siteName = siteName;
		this.address = address;
	}
	
	public String getSiteName() {
		return siteName;
	}
	
	public String getAddress() {
		return address;
	}
}
