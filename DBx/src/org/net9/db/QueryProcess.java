package org.net9.db;
import java.io.*;
import java.util.*;
import java.io.Serializable;

import org.net9.db.rmi.HostService;
import org.net9.db.rmi.ServiceConfig;

public class QueryProcess implements Serializable {
	final String fileName = "benchmark.txt";
	private SiteType localSite = null; // to do !!!!!!!!!!!!!!!!!
	
	private HashMap tableTypeInfo = new HashMap(); // <talbeName, tableType>
	private HashMap fragmentInfo = new HashMap(); // <tableName, fragmentType>
	private HashMap siteInfo = new HashMap(); // <siteName, siteType>
	private HashMap serviceInfo = new HashMap(); // <siteName, ServiceConfig>
	private HashMap topNodeSite = new HashMap(); // <tableName, siteType>
	
	public HashMap getServiceInfo() {
		return serviceInfo;
	}
	
	public SiteType getSiteType(String siteName) {
		return (SiteType)siteInfo.get(siteName);
	}
	
	private String deleteMoreBlank(String st) {
		String[] items = st.split(" ");
		String temp = "";
		for (int i=0; i<items.length; i++)
			if (items[i] != null) {
				if (temp.length() > 0) temp = temp + " " + items[i]; else temp = items[i];
			}	
		return temp;
	}
	
	public HashMap getSiteInfo() {
		return siteInfo;
	}
	
	public void initialDB()
	{
		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			String newLine = br.readLine();
			while (newLine != null) {
				newLine = deleteMoreBlank(newLine);
				String[] items = newLine.split(" ");
				if (items[0].equals("define")) {
					if (items[1].equals("site")) {
						String siteName = items[2];
						String siteAddress = items[3];
						SiteType siteType = new SiteType(siteName, siteAddress);
						siteInfo.put(siteName, siteType);
						if (localSite == null) {
							if (siteAddress.startsWith("127.0.0.1")) localSite = siteType; 
						}
					} else if (items[1].equals("service")) {
						String siteName = items[2];
						String[] strs = items[3].split(":");
						String addr = strs[0];
						int port = Integer.parseInt(strs[1]);
						ServiceConfig service = new ServiceConfig(siteName, addr, port, HostService.SERVICE_NAME);
						serviceInfo.put(siteName, service);
					}
				} else
				if (items[0].equals("create")) {
					TableType tableType = new TableType(items[2]); 
					int index = 3;
					while (index < items.length) {
						tableType.putItemType(items[index], items[index + 1]);
						index += 2;
						if ((index < items.length) && (items[index].equals("key"))) {
							tableType.putIsKey(items[index - 2], true);
							index += 2;
						} else index += 1;
					}
					tableTypeInfo.put(items[2], tableType);
				} else
				if (items[0].equals("fragment")) {
					if (items[2].equals("horizontally")) {
						FragmentType fType = new FragmentType(items[1], 'h');
						int index = 4;
						while (index < items.length) {
							HashSet fItem = new HashSet();
							while (index < items.length && !items[index].equals(",")) {
								if (!items[index].equals("and")) {
									int opIndex = 0;
									String item = items[index];
									while (opIndex < item.length() && 
										   item.charAt(opIndex) != '<' && item.charAt(opIndex) != '>' && item.charAt(opIndex) != '=') {
										opIndex++;
									}
									// one variable to be done
									CompareOperation fcop = new CompareOperation();
									String newItem = items[1] + "." + item.substring(0, opIndex);
									fcop.setLeft(newItem);
									char ch = item.charAt(opIndex + 1);
									if (ch == '<' || ch == '>' || ch == '=') {
										ch = item.charAt(opIndex + 2);
										if (ch == '\'' || ch >= '0' && ch <= '9') {
											newItem = item.substring(opIndex + 2, item.length());
										} else {
											newItem = items[1] + "." + item.substring(opIndex + 2, item.length());
										}
										fcop.setRight(newItem);
										fcop.setOp(item.substring(opIndex, opIndex + 2));
									} else {
										ch = item.charAt(opIndex + 1);
										if (ch == '\'' || ch >= '0' && ch <= '9') {
											newItem = item.substring(opIndex + 1, item.length());
										} else {
											newItem = items[1] + "." + item.substring(opIndex + 1, item.length());
										}
										fcop.setRight(newItem);
										fcop.setOp(item.substring(opIndex, opIndex + 1));
									}
									fItem.add(fcop);
								}
								index++;
							}
							fType.addItem(fItem);
							index++;
						}
						fragmentInfo.put(items[1], fType);
					} else 
					if (items[2].equals("vertically")) {
						FragmentType fType = new FragmentType(items[1], 'v');
						int index = 4;
						while (index < items.length) {
							HashSet<String> fItem = new HashSet<String>();
							while (index < items.length && !items[index].equals(",")) {
								String newItem = items[1] + "." + items[index];
								fItem.add(newItem);
								index++;
							}
							fType.addItem(fItem);
							index++;
						}
						fragmentInfo.put(items[1], fType);
					}
				} else 
				if (items[0].equals("allocate")) {
					String fields[] = items[1].split("\\$");
					int siteNum = Integer.valueOf(fields[1]) - 1;
					SiteType siteType = (SiteType)siteInfo.get(items[3]);
					topNodeSite.put(fields[0], siteType);
					FragmentType fType = (FragmentType)fragmentInfo.get(fields[0]);
					fType.setFragmentSite(String.valueOf(siteNum), siteType);
				}
				newLine = br.readLine();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private void printInputError()
	{
		System.out.println("Input Error!\n");
	}
	
	public TreeNode queryParse(String newLine) {
		final TreeNode commoandError = null;
		
		//get the items of the command, separated by blank and comma
		ArrayList<String> items = new ArrayList<String>();
		int index = 0;
		while (index < newLine.length()) {
			if (newLine.charAt(index) != ' ' && newLine.charAt(index) != ',' && newLine.charAt(index) != '\t') {
				int start = index;
				while (index < newLine.length() && newLine.charAt(index) != ' ' && newLine.charAt(index) != ',') {
					if (newLine.charAt(index) == '\'') {
						index++;
						while (index < newLine.length() && newLine.charAt(index) != '\'') index++;
						if (index >= newLine.length()) {
							printInputError(); return commoandError;
						}
					}
					index++;
				}
				items.add(newLine.substring(start, index));
			}
			index++;
		}
		//count the number of select, from, where items
		int selectIndex = -1, fromIndex = -1, whereIndex = -1;
		for (int i=0; i<items.size(); i++) {
			if (items.get(i).equals("select")) {
				if (selectIndex > -1) {
					printInputError(); return commoandError;
				}
				selectIndex = i;
			}
			if (items.get(i).equals("from")) {
				if (selectIndex == -1 || fromIndex > -1) {
					printInputError(); return commoandError;
				}
				fromIndex = i;
			}
			if (items.get(i).equals("where")) {
				if (selectIndex == -1 || fromIndex == -1 || whereIndex > -1) {
					printInputError(); return commoandError;
				}
				whereIndex = i;
			}
		}		
		if (selectIndex != 0 || fromIndex < 0) {
			printInputError(); return commoandError;
		}
		//get the item amount of select, from and where
		int selectNum = fromIndex - selectIndex - 1, fromNum = 0, whereNum = 0;
		if (whereIndex > -1) {
			fromNum = whereIndex - fromIndex - 1;
			whereNum = items.size() - whereNum - 1;
		} else {
			fromNum = items.size() - fromIndex - 1;
			whereNum = 0;
		}
		//get the select tables
		HashMap selectMap = new HashMap(); // <tableName, selectType>
		HashSet<String> selectSet = new HashSet<String>(); // tableName.tableItem
		HashMap DBNodeMap = new HashMap(); // <tableName, DBNode>
		ArrayList<CompareOperation> whereOP = new ArrayList<CompareOperation>();
		for (int i=1; i<=fromNum; i++) {
			String fromItem = items.get(fromIndex + i);
			TableType table = (TableType)tableTypeInfo.get(fromItem);
			if (table == null) {
				printInputError(); return commoandError;
			}
			if (selectMap.get(fromItem) == null) {
				SelectType selectTable = new SelectType(fromItem);
				selectMap.put(fromItem, selectTable);
				DBNode newNode = new DBNode(fromItem);
				DBNodeMap.put(fromItem, newNode);
			}
		}
		//get the select table-items
		for (int i=1; i<=selectNum; i++) {
			String selectItem = items.get(selectIndex + i);
			if (selectItem.equals("*")) {
				Iterator it = selectMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry)it.next();
					String tableName = (String)entry.getKey();
					SelectType selectTable = (SelectType)entry.getValue();
					TableType table = (TableType)tableTypeInfo.get(tableName);
					table.selectAll(selectTable, selectSet);
				}
			} else {
				String[] fields = selectItem.split("\\.");
				switch (fields.length) {
					case 1 :
						{
							if (selectMap.size() != 1) {
								printInputError(); return commoandError;
							}
							Map.Entry entry = (Map.Entry)selectMap.entrySet().iterator().next();
							String tableName = (String)entry.getKey();
							SelectType selectTable = (SelectType)entry.getValue();
							TableType table = (TableType)tableTypeInfo.get(tableName);
							if (!table.selectItem(selectItem, selectTable)) {
								printInputError(); return commoandError;
							} else {
								selectSet.add(tableName + "." + selectItem);
							}
						}
						break;
					case 2 :
						{
							String tableName = fields[0];
							SelectType selectTable = (SelectType)selectMap.get(tableName);
							String itemName = fields[1];
							if (selectTable == null) {
								printInputError(); return commoandError;
							}
							TableType table = (TableType)tableTypeInfo.get(tableName);
							if (!table.selectItem(itemName, selectTable)) {
								printInputError(); return commoandError; 
							} else {
								selectSet.add(tableName + "." + itemName);
							}
						}
						break;
					default : printInputError(); return commoandError;
				}
 			}
		}
		Iterator it = selectMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			String tableName = (String)entry.getKey();
			SelectType selectTable = (SelectType)entry.getValue();
			DBNode dbNode = (DBNode)DBNodeMap.get(tableName);
			dbNode.copySelectItem(selectTable);
		}
		//where conditions
		if (whereIndex > -1) {
			for (int i=whereIndex + 1; i<items.size(); i++) {
				String item = items.get(i);
				String leftItem = "", rightItem = "", op = "";
				int startIndex = 0, endIndex = 0;
				do {
					while (endIndex < item.length() && item.charAt(endIndex) != '>' && 
						   item.charAt(endIndex) != '<' && item.charAt(endIndex) != '=') {
						endIndex++;
					}
					if (endIndex >= item.length() && i < items.size()) {
						i++;
						if (i < items.size()) item = item + items.get(i);
					}
				} while (i < items.size() && item.charAt(endIndex) != '>' && 
						 item.charAt(endIndex) != '<' && item.charAt(endIndex) != '=');
				if (i >= items.size()) {
					printInputError(); return commoandError;
				} else leftItem = item.substring(startIndex, endIndex);
				//op
				op = item.substring(endIndex, endIndex + 1);
				endIndex = endIndex + 1;
				if (endIndex < item.length()) {
					rightItem = item.substring(endIndex, item.length());
				} else {
					i++;
					if (i >= items.size()) {
						printInputError(); return commoandError;
					} else {
						item = item + items.get(i);
						rightItem = item.substring(endIndex, item.length());					
					}
				}
				if (i + 1 < items.size()) {
					i++; 
					item = items.get(i);
					if (!item.toLowerCase().equals("and")) {
						printInputError(); return commoandError;
					}
				}
				//leftItem
				String fields[] = leftItem.split("\\.");
				switch (fields.length) {
					case 1:
						{
							if (selectMap.size() != 1) {
								printInputError(); return commoandError;
							}
							Map.Entry entry = (Map.Entry)selectMap.entrySet().iterator().next();
							String tableName = (String)entry.getKey();
							TableType table = (TableType)tableTypeInfo.get(tableName);
							if (!table.hasItem(leftItem)) {
								printInputError(); return commoandError;
							} else leftItem = tableName + "." + leftItem;
						}
						break;
					case 2:
						{
							String tableName = fields[0];
							SelectType selectTable = (SelectType)selectMap.get(tableName);
							String itemName = fields[1];
							if (selectTable == null) {
								printInputError(); return commoandError;
							}
							TableType table = (TableType)tableTypeInfo.get(tableName);
							if (!table.hasItem(itemName)) {
								printInputError(); return commoandError;
							}
						}
						break;						
					default :
						printInputError(); return commoandError;
				}
				//right item
				if (rightItem.charAt(0) == '\'') {
					if (rightItem.length() < 2 || rightItem.charAt(rightItem.length()-1) != '\'') {
						printInputError(); return commoandError;
					}
					String lfields[] = leftItem.split("\\.");
					TableType table = (TableType)tableTypeInfo.get(lfields[0]);
					String ltype = table.getTypeName(lfields[1]);
					if (!ltype.equals("string")) {
						printInputError(); return commoandError;
					}
					CompareOperation cOP = new CompareOperation(leftItem, op, rightItem, true);
					DBNode dbNode = (DBNode)DBNodeMap.get(lfields[0]);
					dbNode.addCondition(cOP);
				} else 
				if (rightItem.charAt(0) >= '0' && rightItem.charAt(0) <= '9') {
					try {
						int value = Integer.parseInt(rightItem);
					} catch (Exception e) {
						System.out.println(e);
						printInputError(); 
						return commoandError;
					}
					String lfields[] = leftItem.split("\\.");
					TableType table = (TableType)tableTypeInfo.get(lfields[0]);
					String ltype = table.getTypeName(lfields[1]);
					if (!ltype.equals("int")) {
						printInputError(); return commoandError;
					}
					CompareOperation cOP = new CompareOperation(leftItem, op, rightItem, true);
					DBNode dbNode = (DBNode)DBNodeMap.get(lfields[0]);
					dbNode.addCondition(cOP);
				} else {
					String lfields[] = leftItem.split("\\.");
					String rfields[] = rightItem.split("\\.");
					switch (rfields.length) {
						case 1:
							{
								if (selectMap.size() != 1) {
									printInputError(); return commoandError;
								}
								Map.Entry entry = (Map.Entry)selectMap.entrySet().iterator().next();
								String tableName = (String)entry.getKey();
								TableType table = (TableType)tableTypeInfo.get(tableName);
								if (!table.hasItem(leftItem)) {
									printInputError(); return commoandError;
								} else leftItem = tableName + "." + leftItem;
							}
							break;
						case 2:
							{
								String tableName = rfields[0];
								SelectType selectTable = (SelectType)selectMap.get(tableName);
								String itemName = rfields[1];
								if (selectTable == null) {
									printInputError(); return commoandError;
								}
								TableType table = (TableType)tableTypeInfo.get(tableName);
								if (!table.hasItem(itemName)) {
									printInputError(); return commoandError;
								}
							}
							break;						
						default :
							printInputError(); return commoandError;		
					}
					rfields = rightItem.split("\\.");
					TableType table = (TableType)tableTypeInfo.get(lfields[0]);
					String ltype = table.getTypeName(lfields[1]);
					table = (TableType)tableTypeInfo.get(rfields[0]);
					String rtype = table.getTypeName(rfields[1]);
					if (!ltype.equals(rtype)) {
						printInputError(); return commoandError;
					}
					CompareOperation cOP = new CompareOperation(leftItem, op, rightItem, false);
					whereOP.add(cOP);
				}
			}
		} else {
			whereOP.clear();
		}
		
		//read fragment data and merge node
		HashMap tableTopNode = new HashMap(); // <tableName, TreeNode>
		it = DBNodeMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			String tableName = (String)entry.getKey();
			DBNode dbNode = (DBNode)entry.getValue();
			FragmentType ftype = (FragmentType)fragmentInfo.get(tableName);
			if (ftype.getKind() == 'h') {
				ArrayList fItems = ftype.getFragmentItems();
				HashMap fSite = ftype.getFragmentSite();
				for (int i=fItems.size()-1; i>=0; i--) {
					TreeNode newNode = new TreeNode(TreeNode.READ_H_DATA);
					HashSet opSet = (HashSet)fItems.get(i);
					SiteType fsite = (SiteType)fSite.get(String.valueOf(i));
					newNode.setSiteType(fsite);
					newNode.addTableName(tableName);
					newNode.addCondition(opSet);
					newNode.addCondition(dbNode.getCondition());
					TreeNode topNode = (TreeNode)tableTopNode.get(tableName);
					if (topNode == null) {
						tableTopNode.put(tableName, newNode);
					} else {
						TreeNode mergeNode = new TreeNode(TreeNode.MERGE_H_FRAG_TABLE);
						mergeNode.addTableName(tableName);
						mergeNode.setSiteType((SiteType)topNodeSite.get(tableName));
						mergeNode.setSon(topNode, newNode);
						tableTopNode.put(tableName, mergeNode);
					}
				}
			} else {
				ArrayList fItems = ftype.getFragmentItems();
				HashMap fSite = ftype.getFragmentSite();
				for (int i=fItems.size() - 1; i>=0; i--) {
					TreeNode newNode = new TreeNode(TreeNode.READ_V_DATA);		
					HashSet<String> fitemSet = (HashSet<String>)fItems.get(i);
					SiteType fsite = (SiteType)fSite.get(String.valueOf(i));
					newNode.setSiteType(fsite);
					newNode.addTableName(tableName);
					//newNode.addCondition(dbNode.getCondition());
					newNode.setVTableItem(fitemSet);
					TreeNode topNode = (TreeNode)tableTopNode.get(tableName);
					if (topNode == null) {
						tableTopNode.put(tableName, newNode);
					} else {
						TreeNode mergeNode = new TreeNode(TreeNode.MERGE_V_FRAG_TABLE);
						mergeNode.addTableName(tableName);
						mergeNode.setSiteType((SiteType)topNodeSite.get(tableName));
						mergeNode.setSon(topNode, newNode);
						tableTopNode.put(tableName, mergeNode);
					}
				}
				TreeNode topNode = (TreeNode)tableTopNode.get(tableName);
				topNode.addCondition(dbNode.getCondition());
			}
		}
		
		//diff_table merge node
		while (whereOP.size() > 0) {
			CompareOperation cop = whereOP.get(0);
			whereOP.remove(0);
			TreeNode newNode = new TreeNode(TreeNode.MERGE_DIFF_TABLE);
			String[] lfield = cop.getLeft().split("\\.");
			String[] rfield = cop.getRight().split("\\.");
			TreeNode lsonNode = (TreeNode)tableTopNode.get(lfield[0]);
			TreeNode rsonNode = (TreeNode)tableTopNode.get(rfield[0]);
			newNode.setSiteType(lsonNode.getSiteType());
			newNode.setSon(lsonNode, rsonNode);
			HashSet<String> tableNameSet = lsonNode.getTableNameSet();
			newNode.addTableName(tableNameSet);
			tableNameSet = rsonNode.getTableNameSet();
			newNode.addTableName(tableNameSet);
			updateTableTopNode(tableTopNode, newNode, lsonNode, rsonNode);
			
			newNode.addCondition(cop);
			for (int i=0; i<whereOP.size(); i++) {
				cop = whereOP.get(i);
				String ltableName = cop.getLeft().split("\\.")[0];
				String rtableName = cop.getRight().split("\\.")[0];
				if (ltableName == lfield[0] && rtableName == rfield[0] ||
					ltableName == rfield[0] && rtableName == lfield[0]) {
					newNode.addCondition(cop);
					whereOP.remove(i);
					i--;
				}
			}
		}
		
		//decare_merege node
		boolean hasMoreMerge = true;
		while (hasMoreMerge) {
			hasMoreMerge = false;
			it = tableTopNode.entrySet().iterator();
			Map.Entry entry = (Map.Entry)it.next();
			String tableName = (String)entry.getKey();
			TreeNode lsonNode = (TreeNode)entry.getValue();
			while (it.hasNext()) {
				entry = (Map.Entry)it.next();
				tableName = (String)entry.getKey();
				TreeNode rsonNode = (TreeNode)entry.getValue();
				if (lsonNode != rsonNode) {
					TreeNode newNode = new TreeNode(TreeNode.MERGE_DECARE_TABLE);
					newNode.setSiteType(lsonNode.getSiteType());
					newNode.setSon(lsonNode, rsonNode);
					HashSet<String> tableNameSet = lsonNode.getTableNameSet();
					newNode.addTableName(tableNameSet);
					tableNameSet = rsonNode.getTableNameSet();
					newNode.addTableName(tableNameSet);
					updateTableTopNode(tableTopNode, newNode, lsonNode, rsonNode);
					hasMoreMerge = true;
					break;
				}
			}
		}
		
		//transmit result node
		it = tableTopNode.entrySet().iterator();
		Map.Entry entry = (Map.Entry)it.next();
		TreeNode sonNode = (TreeNode)entry.getValue();
		TreeNode rootNode = new TreeNode(TreeNode.TRANS_RESULT);
		rootNode.setSiteType(localSite);
		rootNode.addTableName(sonNode.getTableNameSet());
		rootNode.setSon(sonNode, null);
		
		rootNode.setSelectItem(selectSet);
		rootNode.setLocalSite(localSite);
		
		return rootNode;
	}
	
	public ArrayList<HashMap> run(TreeNode rootNode)
	{
		ArrayList<HashMap> ans = rootNode.run();
		/*for (int i=0; i<ans.size(); i++) {
			HashMap item = (HashMap)ans.get(i);
			it = item.entrySet().iterator();
			while (it.hasNext()) {
				entry = (Map.Entry)it.next();
				String key = (String)entry.getKey();
				String value = (String)entry.getValue();
				System.out.print(key + ":" + value + "       ");
			}
			System.out.println();
		}*/
		System.out.println("The size of query result set = " + ans.size());
		return ans;
	}
	
	public void updateTableTopNode(HashMap tableTopNode, TreeNode newNode, TreeNode lson, TreeNode rson) {
		HashSet<String> tableNameSet = lson.getTableNameSet();
		Iterator it = tableNameSet.iterator();
		while (it.hasNext()) {
			String tableName = (String)it.next();
			tableTopNode.put(tableName, newNode);
		}
		tableNameSet = rson.getTableNameSet();
		it = tableNameSet.iterator();
		while (it.hasNext()) {
			String tableName = (String)it.next();
			tableTopNode.put(tableName, newNode);
		}
	}
	
	public void getQuery()
	{
		initialDB();
		try {
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
			String newLine = stdin.readLine();
			while (!newLine.equals("exit")) {
				long startTime = System.currentTimeMillis();
				
				TreeNode rootNode = queryParse(newLine);
				if (rootNode != null) {
					String printTree = rootNode.printTree("");
					System.out.println(printTree);
					run(rootNode);
				}
				
				long endTime = System.currentTimeMillis();
				System.out.println("Time used = " + (double)(endTime - startTime) / (double)1000 + " s ");
				
				newLine = stdin.readLine();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static void main(String argc[]) {
		QueryProcess run = new QueryProcess();
		run.getQuery();
		
	}
}