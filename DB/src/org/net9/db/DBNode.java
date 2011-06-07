package org.net9.db;
import java.io.Serializable;
import java.util.ArrayList;

public class DBNode implements Serializable {	
	private String tableName;
    private SelectType selectTable;
    private ArrayList<CompareOperation> condition = new ArrayList<CompareOperation>(); 
    
	DBNode(String tableName) {
		this.tableName = tableName;
		this.selectTable = new SelectType(tableName);
	}
	
	public void addCondition(CompareOperation cOP) {
		condition.add(cOP);
	}
	
	public void addCondition(ArrayList condition) {
		for (int i=0; i<condition.size(); i++) {
			this.condition.add((CompareOperation)(condition.get(i)));
		}
	}
	
	public void copySelectItem(SelectType selectTable) {
		this.selectTable.copySelectItem(selectTable);
	}
	
	public ArrayList getCondition() {
		return condition;
	}
}
