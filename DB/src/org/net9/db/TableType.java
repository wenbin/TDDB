package org.net9.db;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class TableType implements Serializable {
	private String tableName;
	private HashMap itemType = new HashMap(); // <itemName, typeName>
    private HashMap isKey = new HashMap(); // <itemName, true/false>
	
	TableType(String tableName) {
		this.tableName = tableName;
		itemType.clear();
		isKey.clear();
	}
	
	public void putItemType(String itemName, String typeName) {
		itemType.put(itemName, typeName);
		isKey.put(itemName, new Boolean(false));
	}
	
	public void putIsKey(String itemName, boolean value) {
		isKey.put(itemName, new Boolean(value));
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public void selectAll(SelectType selectTable, HashSet<String> selectSet) {
		Iterator it = itemType.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			String itemName = (String)entry.getKey();
			selectTable.putItem(itemName);
			selectSet.add(tableName + "." + itemName);
		}
	}
	
	public boolean selectItem(String itemName, SelectType selectTable) {
		if (itemType.get(itemName) == null) return false;
		else {
			selectTable.putItem(itemName);
			return true;
		}
	}
	
	public boolean hasItem(String itemName) {
		return (itemType.get(itemName) != null);
	}
	
	public String getTypeName(String itemName) {
		return (String)itemType.get(itemName);
	}
}