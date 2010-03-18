package com.picsauditing.actions;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.search.Database;


@SuppressWarnings("serial")
public class ContractorFlagDifference extends PicsActionSupport {
	Database db = new Database();
	private List<BasicDynaBean> data;
	protected int conID;
	protected int opID;
	protected String flag;
	protected boolean switchOrder = false;
	
	@Override
	public String execute() throws Exception {
		
		String sql = "SELECT SQL_CALC_FOUND_ROWS gc.subID conID,con.name as ContractorName, gc.genID opID, op.name as OperatorName, gc.flag newColor, f.flag oldColor, gc.waitingOn newwaitingon, f.waitingon oldwaitingOn "
			  + "FROM generalcontractors gc "
			  + "JOIN old_flags f ON gc.genID = f.opID AND gc.subID = f.conID "
			  + "JOIN accounts op ON op.id = gc.genid "
			  + "JOIN accounts con ON con.id = gc.subid "
			  + "LEFT JOIN flag_dirty fd ON fd.conid = gc.subid AND fd.opId = gc.genid ";
		
		String baseWhere = "WHERE con.status = 'Active' "
		  + "AND op.status = 'Active' "
		  + "AND con.type = 'Contractor' "
		  + "AND op.type = 'Operator' "
		  + "AND fd.id IS NULL ";
		
		String where = "AND gc.flag != f.flag ";
		String orderBy = "ORDER BY con.name, op.name";
		
		if ("delete".equals(button)) {
			String insert = " INSERT INTO flag_dirty values (null,"+ conID +", "+ opID +" ,'"+ flag +"' ,'Good')";
			db.executeInsert(insert);
			return BLANK;
		}
		
		if(("oldWaitingOn").equals(button)){
			where = "AND gc.waitingOn != f.waitingOn";
			orderBy = "ORDER BY f.waitingOn,con.name,op.name";
		}
		
		if(("newWaitingOn").equals(button)){
			where = "AND gc.waitingOn != f.waitingOn";
			orderBy = "ORDER BY gc.waitingOn,con.name,op.name";
		}
		
		if(("oldFlag").equals(button)){
			orderBy = "ORDER BY f.flag,con.name,op.name";
		}

		if(("newFlag").equals(button)){
			orderBy = "ORDER BY gc.flag,con.name,op.name";
		}
		
		if(("contractor").equals(button)){
			orderBy = "ORDER BY con.name,op.name";
		}
		
		if(("operator").equals(button)){
			orderBy = "ORDER BY con.name,op.name";
		}
		
		sql += baseWhere + where + orderBy + "  LIMIT 100";
		
		data = db.select(sql, true);
		return SUCCESS;
	}
	
	public List<BasicDynaBean> getData() {
		return data;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
}