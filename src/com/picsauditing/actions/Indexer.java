package com.picsauditing.actions;

import java.util.List;
import java.util.Map;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.search.SelectSQL;

public class Indexer extends PicsActionSupport {
	
	private AccountDAO accountDAO;
	private SelectSQL sql;
	
	public Indexer(AccountDAO accountDAO){
		this.accountDAO = accountDAO;
	}
	
	public String execute(){
		List<String> l;
		sql = new SelectSQL();
		sql.addField("id");
		sql.addWhere("needsIndexing = 1");
		List<Integer> ids;
		
		return SUCCESS;
	}
	
	public static void main(String[] args){
		String s = "Scott's Lawn Care LLC";
		String s1 = s;
		s = s.toUpperCase().replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", " ");
		s1 = s1.toUpperCase().replaceAll("\\W&&\\S", "");
	}

}
