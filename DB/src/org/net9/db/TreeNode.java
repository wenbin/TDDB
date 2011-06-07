package org.net9.db;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.io.*;
import java.sql.*;

public class TreeNode implements Serializable {
	public static final int READ_H_DATA = 0;
	public static final int READ_V_DATA = 1;
    public static final int MERGE_H_FRAG_TABLE = 2;
    public static final int MERGE_V_FRAG_TABLE = 3;
	public static final int MERGE_DIFF_TABLE = 4;
	public static final int MERGE_DECARE_TABLE = 5;
	public static final int TRANS_RESULT = 6;
	
	private static SiteType localSite = null;
	private SiteType siteType = null;
	

	private TreeNode lson = null;
	private TreeNode rson = null;
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
	
	public SiteType getSiteType() {
		return siteType;
	}
	public TreeNode getLson() {
		return lson;
	}

	public TreeNode getRson() {
		return rson;
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
			if (!(ch == '\'' || ch >= '0' && ch <= '9')) {
				newSelectSet.add(item);
			}
		}
		if (lson != null) this.lson.setSelectItem(newSelectSet);
		if (rson != null) this.rson.setSelectItem(newSelectSet);
	}
	
	public void setLocalSite(SiteType localSite) {
		this.localSite = localSite;
	}
	
	public void addSelectItem(String item) {
		String[] fields = item.split("\\.");
		if (tableName.contains(fields[0])) {
			selectItem.add(item);
		}
	}
	
	public void setVTableItem(HashSet<String> vTableItem) {
		Iterator it = vTableItem.iterator();
		while (it.hasNext()) {
			String newTableItem = (String)it.next();
			this.vTableItem.add(newTableItem);
		}
	}
	public ArrayList<HashMap> runAns(
			ArrayList<HashMap> lans,
			ArrayList<HashMap> rans)
	{
		ArrayList<HashMap> ans = null;
		//if (siteType.getSiteName().equals(localSite.getSiteName())) {
		switch (kind) {
			case READ_H_DATA :
				ans = new ArrayList<HashMap>();
				try {
					Class.forName("org.gjt.mm.mysql.Driver");  
					String url = "jdbc:mysql://";
					url = url + siteType.getAddress();
					url = url + "/ddb" + siteType.getSiteName().charAt(4);
					String query = getHQuery();
					if (query != null) {
						String username = "root";
						String password = "root";
						Connection conn = DriverManager.getConnection(url, username, password);
						Statement stmt = conn.createStatement();						
						ResultSet rs = stmt.executeQuery(query);
						while (rs.next()) {
							HashMap newItem = new HashMap();
							Iterator it = selectItem.iterator();
							while (it.hasNext()) {
								String tableItem = (String)it.next();
								String fields[] = tableItem.split("\\.");
								String value = rs.getString(fields[1]);
								newItem.put(tableItem, value);
							}
							if (newItem.size() > 0) {
								ans.add(newItem);
							}
						}
					}
				} catch (Exception e) {
					System.out.println(e);
				} finally {
					//to do
				}	
				break;
			case READ_V_DATA :
				ans = new ArrayList<HashMap>();
				try {
					Class.forName("org.gjt.mm.mysql.Driver");  
					String url = "jdbc:mysql://";
					url = url + siteType.getAddress();
					url = url + "/ddb" + siteType.getSiteName().charAt(4);
					String query = getVQuery();
					
					if (query != null) {
						String username = "root";
						String password = "root";
						Connection conn = DriverManager.getConnection(url, username, password);
						Statement stmt = conn.createStatement();						
						ResultSet rs = stmt.executeQuery(query);
						int tot = 0;
						while (rs.next()) {
							HashMap newItem = new HashMap();
							Iterator it = selectItem.iterator();
							while (it.hasNext()) {
								String tableItem = (String)it.next();
								if (vTableItem.contains(tableItem)) {
									String fields[] = tableItem.split("\\.");
									String value = rs.getString(fields[1]);
									newItem.put(tableItem, value);
								}
							}
							if (newItem.size() > 0) {
								ans.add(newItem);
							}
						}
					}
				} catch (Exception e) {
					System.out.println(e);
				} finally {
					//to do
				}
				break;
			case MERGE_H_FRAG_TABLE :
				ans = new ArrayList<HashMap>();
				for (int i=0; i<lans.size(); i++) {
					ans.add(lans.get(i));
				}
				for (int i=0; i<rans.size(); i++) {
					ans.add(rans.get(i));
				}
				break;
			case MERGE_V_FRAG_TABLE :
				ans = new ArrayList<HashMap>();
				for (int i=0; i<lans.size(); i++) {
					HashMap newItem = new HashMap();
					HashMap lmap = (HashMap)lans.get(i);
					Iterator lit = lmap.entrySet().iterator();
					while (lit.hasNext()) {
						Map.Entry lentry = (Map.Entry)lit.next();
						String item = (String)lentry.getKey();
						String value = (String)lentry.getValue();
						newItem.put(item, value);
					}
					HashMap rmap = (HashMap)rans.get(i);
					Iterator rit = rmap.entrySet().iterator();
					while (rit.hasNext()) {
						Map.Entry rentry = (Map.Entry)rit.next();
						String item = (String)rentry.getKey();
						String value = (String)rentry.getValue();
						newItem.put(item, value);
					}
					if (condition.size() > 0) {
						boolean satisfy = true;
						Iterator it = condition.iterator();
						while (it.hasNext()) {
							CompareOperation cop = (CompareOperation)it.next();
							String itemName = cop.getLeft();
							String cvalue = cop.getRight();
							String op = cop.getOP();
							String value = (String)newItem.get(itemName);
							if (cvalue.charAt(0) == '\'') {
								cvalue = cvalue.substring(1, cvalue.length() - 1);
								int cmp = cvalue.compareTo(value);
								if (op.equals("<") && cmp >= 0 ||
									op.equals("=") && cmp != 0 ||
									op.equals(">") && cmp <= 0) {
									satisfy = false; break;
								}
							} else {
								int icvalue = Integer.valueOf(cvalue);
								int ivalue = Integer.valueOf(value);
								int cmp = icvalue - ivalue;
								if (op.equals("<") && cmp >= 0 ||
									op.equals("=") && cmp != 0 ||
									op.equals(">") && cmp <= 0) {
									satisfy = false; break;
								}
							}
						}
						if (satisfy) ans.add(newItem);
					} else ans.add(newItem);
				}
				break;
			case MERGE_DIFF_TABLE :
				ans = new ArrayList<HashMap>();
				if (rans.size() > 0) {
					HashMap itemMap = new HashMap(); // itemName, valueMap<value, ArrayList>
					HashMap rmap = (HashMap)rans.get(0);
					Iterator rit = rmap.entrySet().iterator();
					while (rit.hasNext()) {
						Map.Entry rentry = (Map.Entry)rit.next();
						String item = (String)rentry.getKey();
						HashMap valueMap = new HashMap();
						itemMap.put(item, valueMap);
					}
					HashMap temp = null;
					for (int i=0; i<rans.size(); i++) {
						rmap = (HashMap)rans.get(i);
						rit = rmap.entrySet().iterator();
						while (rit.hasNext()) {
							Map.Entry rentry = (Map.Entry)rit.next();
							String item = (String)rentry.getKey();
							String value = (String)rentry.getValue();
							HashMap valueMap = (HashMap)itemMap.get(item);
							ArrayList itemList = (ArrayList)valueMap.get(value);
							if (itemList == null) {
								itemList = new ArrayList();
								itemList.add(rmap);
								valueMap.put(value, itemList);
								temp = valueMap;
							} else {
								itemList.add(rmap);
							}
						}
					}
				
					int tot = 0;
					rit = temp.entrySet().iterator();
					while (rit.hasNext()) {
						Map.Entry rentry = (Map.Entry)rit.next();
						String item = (String)rentry.getKey();
						ArrayList value = (ArrayList)rentry.getValue();
						tot += value.size();
					}
					
					for (int i=0; i<lans.size(); i++) {
						HashMap lmap = lans.get(i);
						CompareOperation cop = (CompareOperation)condition.get(0);
						String leftItem = cop.getLeft(), rightItem = cop.getRight();
						String value = (String)lmap.get(leftItem);
						HashMap valueMap = (HashMap)itemMap.get(rightItem);
						ArrayList itemList = (ArrayList)valueMap.get(value);
						if (itemList == null) continue;
						for (int listIndex=0; listIndex<itemList.size(); listIndex++) {
							boolean ok = true;
							rmap = (HashMap)itemList.get(listIndex);
							for (int j=1; j<condition.size(); j++) {
								cop = (CompareOperation)condition.get(j);
								leftItem = cop.getLeft(); rightItem = cop.getRight();
								String lvalue = (String)lmap.get(leftItem);
								String rvalue = (String)rmap.get(rightItem);
								if (!lvalue.equals(rvalue)) {
									ok = false; break;
								}
							}
							if (ok) {
								HashMap newItem = new HashMap();
								Iterator lit = lmap.entrySet().iterator();
								while (lit.hasNext()) {
									Map.Entry lentry = (Map.Entry)lit.next();
									String item = (String)lentry.getKey();
									value = (String)lentry.getValue();
									if (selectItem.contains(item)) newItem.put(item, value);
								}
								rit = rmap.entrySet().iterator();
								while (rit.hasNext()) {
									Map.Entry rentry = (Map.Entry)rit.next();
									String item = (String)rentry.getKey();
									value = (String)rentry.getValue();
									if (selectItem.contains(item)) newItem.put(item, value);
								}	
								ans.add(newItem);
							}
						}
					}
				}
				break;
			case MERGE_DECARE_TABLE :
				ans = new ArrayList<HashMap>();
				for (int lindex=0; lindex<lans.size(); lindex++) 
					for (int rindex=0; rindex<rans.size(); rindex++) {
						HashMap newItem = new HashMap();
						HashMap lmap = (HashMap)lans.get(lindex);
						Iterator lit = lmap.entrySet().iterator();
						while (lit.hasNext()) {
							Map.Entry lentry = (Map.Entry)lit.next();
							String item = (String)lentry.getKey();
							String value = (String)lentry.getValue();
							if (selectItem.contains(item)) newItem.put(item, value);
						}
						HashMap rmap = (HashMap)rans.get(rindex);
						Iterator rit = rmap.entrySet().iterator();
						while (rit.hasNext()) {
							Map.Entry rentry = (Map.Entry)rit.next();
							String item = (String)rentry.getKey();
							String value = (String)rentry.getValue();
							if (selectItem.contains(item)) newItem.put(item, value);
						}
						ans.add(newItem);
					}
				break;
			case TRANS_RESULT :
				ans = lans; 
				break;
			default :
				break;
		}
	//} else {
		//to do
	//}
		return ans;
	}
	
	public ArrayList<HashMap> run() {
		ArrayList<HashMap> lans = null;
		ArrayList<HashMap> rans = null;
		if (this.lson != null) {
			lans = this.lson.run();
		}
		if (this.rson != null) {
			rans = this.rson.run();
		}
		ArrayList<HashMap> ans = runAns(lans, rans);
		return ans;
	}
	
	private String getHQuery() {
		String query = "select ";
		int tot = 0;
		Iterator it = selectItem.iterator();
		while (it.hasNext()) {
			String newItem = (String)it.next();
			if (tot > 0) query = query + ",";
			query = query + " " + newItem + " ";
			tot++;
		}
		if (tot == 0) return null;
		query = query + "from ";
		it = tableName.iterator();
		query = query + (String)it.next();
		
		query = query + " where ";
		for (int i=0; i<condition.size(); i++) {
			CompareOperation cop = condition.get(i);
			String newItem = cop.getLeft() + cop.getOP() + cop.getRight();
			if (i > 0) {
				query = query + " and ";
			}
			query = query + " " + newItem + " ";
		}
		return query;
	}
	
	private String getVQuery() {
		String query = "select ";
		int tot = 0;
		Iterator it = selectItem.iterator();
		while (it.hasNext()) {
			String newItem = (String)it.next();
			if (vTableItem.contains(newItem)) {
				if (tot > 0) query = query + ",";
				query = query + " " + newItem + " ";
				tot++;
			}
		}
		if (tot == 0) return null;
		query = query + "from ";
		it = tableName.iterator();
		query = query + (String)it.next();
		return query;
	}
	
	/*private int compare(HashMap x, HashMap y, String itemName) {
		String xValue = (String)x.get(itemName);
		String yValue = (String)y.get(itemName);
		return xValue.compareTo(yValue);
	}
	
	private void qsort(ArrayList data, int left, int right, String itemName) {
		if (left >= right) return;
		HashMap x = (HashMap)data.get(left + (int)Math.floor(Math.random() * (right - left + 1)));
		int i = left, j = right;
		do {
			while (compare((HashMap)data.get(i), x, itemName) < 0) i++;
			while (compare(x, (HashMap)data.get(j), itemName) < 0) j--;
			if (i <= j) {
				HashMap temp = (HashMap)data.get(i);
				data.set(i, data.get(j));
				data.set(j, temp);
				i++; j--;
			}
		} while (i <= j);
		qsort(data, left, j, itemName); qsort(data, i, right, itemName);
	}*/
	
	public void printTree(String prefix) {
		final int width = 10;
		System.out.print("@" + siteType.getSiteName() + " : ");
		Iterator it = this.tableName.iterator();
		String tableName = (String)it.next();
		switch (this.kind) {
			case READ_H_DATA :
				System.out.print("Read_H_FRAG_TABLE");
				System.out.print("   from   " + tableName);
				break;
			case READ_V_DATA :
				System.out.print("Read_V_FRAG_TABLE");
				System.out.print("   from   " + tableName);
				break;
			case MERGE_H_FRAG_TABLE :
				System.out.print("MERGE_H_SAME_FRAG_TABLE");
				break;
			case MERGE_V_FRAG_TABLE :
				System.out.print("MERGE_V_SAME_FRAG_TABLE");
				break;
			case MERGE_DIFF_TABLE :
				System.out.print("MERGE_DIFF_TABLE");
				break;
			case MERGE_DECARE_TABLE :
				System.out.print("DECARE_MERGE_DIFF_TABLE");
				break;
			case TRANS_RESULT :
				System.out.print("TRANS_ANS_TO_LOCAL");
				break;
			default :
				break;
		}
		System.out.println();
		if (lson != null) {
			String newPrefix = prefix + "|";
			System.out.println(newPrefix);
			System.out.print(newPrefix);
			for (int i=0; i<width; i++)
				System.out.print("_");
			if (rson == null) newPrefix = prefix + " ";
			for (int i=0; i<width; i++)
				newPrefix = newPrefix + " ";
			this.lson.printTree(newPrefix);
		}
		if (rson != null) {
			System.out.println(prefix + "|");
			System.out.print(prefix + "|");
			for (int i=0; i<width; i++)
				System.out.print("_");
			String newPrefix = prefix + " ";
			for (int i=0; i<width; i++)
				newPrefix = newPrefix + " ";
			this.rson.printTree(newPrefix);
		}
	}
}
