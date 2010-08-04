package com.picsauditing.actions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.IndexableDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.IndexObject;

public class Indexer extends PicsActionSupport {

	private int id = 0;
	private String toRun = null;
	
	private AccountDAO accountDAO;
	private UserDAO userDAO;
	private EmployeeDAO empDAO;
	private ContractorAuditDAO conAuditDAO;
	
	private List<String> list;
	private static Map<String, IndexableDAO> indexTables;
	private static final int RUN_NUM = 100;
	
	public Indexer(AccountDAO accountDAO, UserDAO userDAO, EmployeeDAO empDAO, ContractorAuditDAO conAudDAO) {
		this.accountDAO = accountDAO;
		this.userDAO = userDAO;
		this.empDAO = empDAO;
		this.conAuditDAO = conAudDAO;
	}
	@Override
	public String execute() throws SQLException {
		indexTables = new HashMap<String, IndexableDAO>();
		// if not specified then we will run all tables to check the index
		if(toRun==null){
			indexTables.put("accounts", accountDAO);
			indexTables.put("users", userDAO);		
			indexTables.put("employee", empDAO);		
//			indexTables.put("contractor_audit", conAuditDAO);	
		} else{
			if(toRun.equals("accounts"))
				indexTables.put("accounts", accountDAO);
			else if(toRun.equals("users"))
				indexTables.put("users", userDAO);
			else if(toRun.equals("employee"))	
				indexTables.put("employee", empDAO);
//			else if(toRun.equals("conAudit"))	
//				indexTables.put("contractor_audit", conAuditDAO);	
		}
		for(Entry<String, IndexableDAO> entry : indexTables.entrySet()){
			// for each table get those rows that need indexing
			// and pass the list of ids in and run the indexer
			runIndexer(getIndexable(entry.getKey()), entry.getValue());
		}
		return SUCCESS;
	}
	
	public static void main(String[] args){
		String str = "Clean Uniforms and More! (formerly E&R Laundry & Dry Cleaners)";

		if(str!=null){
			String[] sA = str.toUpperCase().replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", " ").split(" ");
			for(String s : sA){
				//if(s!=null && !s.isEmpty())
					//l.add(s);
			}
		}
	}
	
	public void runIndexer(List<BasicDynaBean> ids, IndexableDAO dao){
		// batch is the number we use to control how many to run
		int batch = 0;
		Long t1 = System.currentTimeMillis();
		if(ids==null){
			System.out.println("Nothing to update");
			return;
		}
		// Base string for query, use stringbuilder as we will be building large strings
		// and String.concat would be too slow
		StringBuilder queryIndex = new StringBuilder("INSERT IGNORE INTO app_index VALUES ");
		StringBuilder queryStats = new StringBuilder("INSERT IGNORE INTO app_index_stats VALUES ");
		StringBuilder queryDelete = new StringBuilder("DELETE FROM app_index WHERE foreignKey = ");
		Database db = new Database();
		List<IndexObject> l = null; // our list of ids
		List<Integer> savedIds = new ArrayList<Integer>(); // will store the last ids to be ran per batch
		for(int i=0; i<ids.size(); i++){
			// Retrieve id from bean
			int id = 0;
			try {
				id = Integer.parseInt(ids.get(i).get("id").toString());
			} catch (Exception e1) {
				// Could not cast to int, check
				System.out.println("Could not cast to int: "+ids.get(i).get("id")+" of class "+ids.get(i).get("id").getClass().getName());
				e1.printStackTrace();
			}
			Indexable table = (Indexable)dao.find(id);	
			if(table==null) // not a supported entity or could not pull up record
				continue;
			// try/catch here so that if we get an exception we can still
			// try to run the remaining rows
			try{ 
				l = table.getIndexValues();				
			} catch(Exception e){
				System.out.println("Problem parsing values for id: "+id);
				continue;
			}
			// TODO check to delete old entries first
			queryDelete.append(id).append(" AND indexType = '").append(table.getIndexType()).append("'");
			try {
				db.executeUpdate(queryDelete.toString());
				queryDelete.setLength(0);
				queryDelete.append("DELETE FROM app_index WHERE foreignKey = ");
			} catch (SQLException e1) {
				System.out.println("Could not delete "+table.getIndexType()+"-"+id);
				continue;
			}
			// build the queries to insert
			for(IndexObject s : l){
				queryIndex.append("('").append(table.getIndexType()).append("',").append(id).append(",'").append(s.getValue()).append("','").append(s.getWeight()).append("'),");
				queryStats.append("('").append(table.getIndexType()).append("','").append(s.getValue()).append("',").append(1).append("),");
			}
			batch++;
			// add the id to the list of ids for saving
			savedIds.add(id);
			if(batch>=RUN_NUM){ // if we have this number of rows added, run the queries
				try { // try to run, catch exception so we can unsave the rows
					db.executeInsert(queryIndex.substring(0, queryIndex.length()-1));
					db.executeInsert(queryStats.substring(0, queryStats.length()-1));
				} catch (SQLException e) {
					// whenever we have an error we will unset the rows that we have saved since the last batch ran
					for(int idToUnSave : savedIds){
						table = (Indexable)dao.find(idToUnSave);	
						if(table!=null){
							table.setNeedsIndexing(true);
							dao.save((BaseTable)table);
						}
					}
					System.out.println("Unsaving ids");
					e.printStackTrace();
				}
				queryIndex.setLength(0);
				queryStats.setLength(0);
				queryIndex.append("INSERT IGNORE INTO app_index VALUES");
				queryStats.append("INSERT IGNORE INTO app_index_stats VALUES");
				savedIds.clear(); // no error, clear list
				batch = 0;
			}
			table.setNeedsIndexing(false);
			dao.save((BaseTable)table);
		}
		if(batch!=0){
			try {
				db.executeInsert(queryIndex.substring(0, queryIndex.length()-1));
				db.executeInsert(queryStats.substring(0, queryStats.length()-1));
			} catch (SQLException e) {
				for(int idToUnSave : savedIds){
					Indexable table = (Indexable)dao.find(idToUnSave);	
					table.setNeedsIndexing(false);
					dao.save((BaseTable)table);						
				}
				e.printStackTrace();
			}			
		}
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

	public String getToRun() {
		return toRun;
	}

	public void setToRun(String toRun) {
		this.toRun = toRun;
	}

}
