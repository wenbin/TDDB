import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;

public class SelectType implements Serializable {
	private String tableName;
	private HashSet<String> selectItem = new HashSet<String>(); // <itemName>
	
	SelectType(String tableName) {
		this.tableName = tableName;
		selectItem.clear();
	}
	
	SelectType(String tableName, SelectType selectType) {
		this.tableName = tableName;
		this.selectItem.clear();
		Iterator it = selectType.getSelectItem().iterator();
		while (it.hasNext()) {
			String item = (String)it.next();
			selectItem.add(item);
		}
	}
	
	public void copySelectItem(SelectType selectType) {
		this.selectItem.clear();
		Iterator it = selectType.getSelectItem().iterator();
		while (it.hasNext()) {
			String item = (String)it.next();
			selectItem.add(item);
		}
	}
	
	public HashSet<String> getSelectItem() {
		return selectItem;
	}
	
	public void putItem(String itemName) {
		selectItem.add(itemName);
	}
	
}
