import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class FragmentType implements Serializable {
	private String tableName;
	private ArrayList items = new ArrayList(); // opset or itemset
	private HashMap fragmentSite = new HashMap(); // <String : 0, 1, 2, ... , siteType> 
	private char kind; // 'h' or 'v'
	
	FragmentType(String tableName, char kind) {
		this.tableName = tableName;
		this.kind = kind;
	}
	
	public void addItem(Object fcop) {
		items.add(fcop);
	}
	
	public void setFragmentSite(String fragmentNum, SiteType siteType) {
		fragmentSite.put(fragmentNum, siteType);
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public char getKind() {
		return kind;
	}
	
	public ArrayList getFragmentItems() {
		return items;
	}
	
	public HashMap getFragmentSite() {
		return fragmentSite;
	}
}
