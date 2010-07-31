package com.picsauditing.actions;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.PicsDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {

	private int id = 0;
	private AccountDAO accountDAO;
	private UserDAO userDAO;
	private List<String> list;
	private static String[] toIndex = {"accounts", "users"};
	private static Map<String, PicsDAO> indexTables;

	public Trevor(AccountDAO accountDAO, UserDAO userDAO) {
		this.accountDAO = accountDAO;
		this.userDAO = userDAO;
		if(indexTables==null){
			indexTables = new HashMap<String, PicsDAO>();
			//indexTables.put("accounts", accountDAO);
			indexTables.put("users", userDAO);
		}
	}

	@Override
	public String execute() throws SQLException {
		for(Map.Entry<String, PicsDAO> entry : indexTables.entrySet()){
			runIndexer(getIndexable(entry.getKey()), entry.getValue());
		}
		return SUCCESS;
	}
	
	public void runIndexer(List<BasicDynaBean> ids, PicsDAO dao) throws SQLException{
		Long t1 = System.currentTimeMillis();
		if(ids==null){
			System.out.println("Nothing to update");
			return;
		}
		StringBuilder queryIndex = new StringBuilder("INSERT IGNORE INTO app_index VALUES ");
		StringBuilder queryStats = new StringBuilder("INSERT IGNORE INTO app_index_stats VALUES ");
		Database db = new Database();
		List<String> l;
		for(int i=0; i<ids.size(); i++){
			int id = (Integer)ids.get(i).get("id");
			Indexable table = null;	
			if(dao instanceof AccountDAO)
				table = accountDAO.find(id);
			else if(dao instanceof UserDAO)
				table = userDAO.find(id);
			if(table==null) // not a supported entity
				continue;
			l = table.getIndexValues();
			for(String s : l){
				queryIndex.append("('").append(table.getIndexType()).append("',").append(id).append(",'").append(s).append("'),");
				queryStats.append("('").append(table.getIndexType()).append("','").append(s).append("',").append(1).append("),");
			}
			if(i%100==0){
				db.executeInsert(queryIndex.substring(0, queryIndex.length()-1));
				db.executeInsert(queryStats.substring(0, queryStats.length()-1));
				queryIndex = new StringBuilder("INSERT IGNORE INTO app_index VALUES ");
				queryStats = new StringBuilder("INSERT IGNORE INTO app_index_stats VALUES");
			}
			table.setNeedsIndexing(false);
			dao.save((BaseTable)table);
		}
		db.executeInsert(queryIndex.substring(0, queryIndex.length()-1));
		db.executeInsert(queryStats.substring(0, queryStats.length()-1));
		System.out.println("Fin");
		Long t2 = System.currentTimeMillis();
		System.out.println("Time to complete: "+(t2-t1)/1000f);
		
	}
	
	public List<BasicDynaBean> getIndexable(String tblName) throws SQLException{
		SelectSQL sql = new SelectSQL(tblName);
		sql.addField("id");
		sql.addWhere("needsIndexing = 1");	
		sql.addOrderBy("id");
		Database db = new Database();
		return db.select(sql.toString(), false);
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

}
