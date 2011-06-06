import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class TreeNode implements Serializable {
	public static final int READ_H_DATA = 0;
	public static final int READ_V_DATA = 1;
    public static final int MERGE_H_FRAG_TABLE = 2;
    public static final int MERGE_V_FRAG_TABLE = 2;
	public static final int MERGE_DIFF_TABLE = 4;
	public static final int MERGE_DECARE_TABLE = 5;
	public static final int TRANS_RESULT = 6;
	
	private SiteType siteType = null;
	private TreeNode lson = null, rson = null;
	private int kind = -1; //opeartion kind
	private HashSet<String> tableName = new HashSet<String>(); // tablenames
	private ArrayList<CompareOperation> condition = new ArrayList<CompareOperation>();
	private HashSet<String> selectItem = new HashSet<String>(); //tablename.tableitem
	private HashSet<String> vTableItem = new HashSet<String>();
	
	TreeNode(int kind) {
		this.kind = kind;
		tableName.clear();
		condition.clear();
		selectItem.clear();
		vTableItem.clear();
	}
	
	public void setSiteType(SiteType siteType) {
		this.siteType = siteType;
	}
	
	public void addCondition(ArrayList condition) {
		for (int i=0; i<condition.size(); i++) {
			CompareOperation cop = (CompareOperation)condition.get(i);
			this.condition.add(cop);
		}
	}
	
	public void addCondition(HashSet condition) {
		Iterator it = condition.iterator();
		while (it.hasNext()) {
			CompareOperation cop = (CompareOperation)it.next();
			this.condition.add(cop);
		}
	}
	
	public void addCondition(CompareOperation cop) {
		this.condition.add(cop);
	}
	
	public void setSon(TreeNode lson, TreeNode rson) {
		this.lson = lson; this.rson = rson;
	}
	
	public void addTableName(String tableName) {
		this.tableName.add(tableName);
	}
	
	public HashSet<String> getTableNameSet() {
		return tableName;
	}
	
	public void addTableName(HashSet<String> tableNameSet) {
		Iterator it = tableNameSet.iterator();
		while (it.hasNext()) {
			String newTableName = (String)it.next();
			this.tableName.add(newTableName);
		}
	}
	
	public void setSelectItem(HashSet<String> selectSet) {
		HashSet<String> newSelectSet = new HashSet<String>();
		newSelectSet.clear();
		Iterator it = selectSet.iterator();
		while (it.hasNext()) {
			String item = (String)it.next();
			this.addSelectItem(item);
			newSelectSet.add(item);
		}
		
		for (int i=0; i<condition.size(); i++) {
			CompareOperation cop = condition.get(i); 
			String item = cop.getLeft();
			newSelectSet.add(item);
			item = cop.getRight();
			char ch = item.charAt(0);
			if (!(ch == '\'' || ch >= '0' && ch <= '9')) newSelectSet.add(item);
		}
		
		it = selectItem.iterator();
		while (it.hasNext()) {
			String item = (String)it.next();
			System.out.print(item + "\t");
		}
		System.out.println();
		if (lson != null) lson.setSelectItem(newSelectSet);
		if (rson != null) rson.setSelectItem(newSelectSet);
	}
	
	public void addSelectItem(String item) {
		String[] fields = item.split("\\.");
		if (tableName.contains(fields[0])) {
			selectItem.add(item);
		}
	}
	/*public void addSelectItem(HashSet<String> selectItemSet) {
		Iterator it = selectItemSet.iterator();
		while (it.hasNext()) {
			String newTableItem = (String)it.next();
			this.selectItem.add(newTableItem);
		}
	}*/
	
	public void setVTableItem(HashSet<String> vTableItem) {
		Iterator it = vTableItem.iterator();
		while (it.hasNext()) {
			String newTableItem = (String)it.next();
			this.vTableItem.add(newTableItem);
		}
	}
}
