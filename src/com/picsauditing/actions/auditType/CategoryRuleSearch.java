package com.picsauditing.actions.auditType;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectSQL;


@SuppressWarnings("serial")
public class CategoryRuleSearch extends AuditRuleSearch {
	
	@Override
	public String execute() throws Exception{
		if (!forceLogin())
			return LOGIN;	
		if(!permissions.hasPermission(OpPerms.ManageCategoryRules))
			throw new NoRightsException(OpPerms.ManageCategoryRules, OpType.View);

		sql =  new SelectSQL("audit_category_rule a_search");
		actionUrl = "CategoryRuleEditor.action?id=";
		filter.setShowCategory(true);
		return super.execute();
	}
	
	@Override
	public void buildQuery(){
		super.buildQuery();
		sql.addField("IFNULL(ac.name,'*') category");
		sql.addJoin("LEFT JOIN audit_category ac ON ac.id = a_search.catID");
	}
	
	@Override
	protected String runAutoAjax() throws SQLException {
		String str = "";
		Database db = new Database();
		if("auditType".equals(fieldName)){
			str = "SELECT auditName name FROM audit_type WHERE auditName LIKE '"+search+"%'";
		} else if("category".equals(fieldName)){
			str = "SELECT name FROM audit_category WHERE name LIKE '"+search+"%'";
		} else if("operator".equals(fieldName)){
			str = "SELECT a.name FROM accounts a WHERE a.name LIKE '"+search+"%' AND type='Operator'";
		} else if("tag".equals(fieldName)){
			str = "SELECT tag name FROM operator_tag WHERE tag LIKE '"+search+"%'";
		}	
		List<BasicDynaBean> re = db.select(str, false);
		Set<String> se = new HashSet<String>();
		StringBuilder sb = new StringBuilder();
		for(BasicDynaBean bdb : re){
			String name = bdb.get("name").toString();
			if(se.add(name))
				sb.append(name).append("\n");
		}
		output = sb.toString();
		
		return "autocomp";
	}
	protected void addFilterToSQL() {
		super.addFilterToSQL();
		if(filterOn(filter.getCategory())){
			report.addFilter(new SelectFilter("category", "ac.name = '?'", String.valueOf(filter.getCategory())));
		}
	}
}
