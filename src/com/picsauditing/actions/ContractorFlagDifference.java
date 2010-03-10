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
		
		String sql = "SELECT gc.subID conID,con.name as ContractorName, gc.genID opID, op.name as OperatorName, gc.flag newColor, f.flag oldColor, gc.waitingOn newwaitingon, f.waitingon oldwaitingOn "
			  + "FROM generalcontractors gc "
			  + "JOIN old_flags f ON gc.genID = f.opID AND gc.subID = f.conID "
			  + "JOIN accounts op ON op.id = gc.genid "
			  + "JOIN accounts con ON con.id = gc.subid "
			  + "LEFT JOIN flag_dirty fd ON fd.conid = gc.subid AND fd.opId = gc.genid ";
		
		String where = "WHERE (gc.flag != f.flag OR gc.waitingOn != f.waitingOn) "
			  + "AND con.status = 'Active' "
			  + "AND con.type = 'Contractor' "
			  + "AND op.status = 'Active' "
			  + "AND op.type = 'Operator' "
			  + "AND fd.id IS NULL ";
		
		String orderBy = "ORDER BY conid,opid LIMIT 100";
		
		if ("delete".equals(button)) {
			String insert = " INSERT INTO flag_dirty values (null,"+ conID +", "+ opID +" ,'"+ flag +"' ,'Good')";
			db.executeInsert(insert);
			return BLANK;
		}
		
		if(("oldWaitingOn").equals(button)){
			where = "WHERE (gc.waitingOn != f.waitingOn) "
				  + "AND con.status = 'Active' "
				  + "AND con.status = 'Active' "
				  + "AND op.status = 'Active' "
				  + "AND op.type = 'Operator' "
				  + "AND fd.id IS NULL ";
			
			orderBy = "ORDER BY f.waitingOn,conid,opid LIMIT 100";
		}
		
		if(("newWaitingOn").equals(button)){
			where = "WHERE (gc.waitingOn != f.waitingOn) "
				  + "AND con.status = 'Active' "
				  + "AND con.status = 'Active' "
				  + "AND op.status = 'Active' "
				  + "AND op.type = 'Operator' "
				  + "AND fd.id IS NULL ";
			
			orderBy = "ORDER BY gc.waitingOn,conid,opid LIMIT 100";
		}
		
		if(("oldFlag").equals(button)){
			where = "WHERE (gc.flag != f.flag) "
				  + "AND con.status = 'Active' "
				  + "AND con.status = 'Active' "
				  + "AND op.status = 'Active' "
				  + "AND op.type = 'Operator' "
				  + "AND fd.id IS NULL ";
			
			orderBy = "ORDER BY f.flag,conid,opid LIMIT 100";
		}

		if(("newFlag").equals(button)){
			where = "WHERE (gc.flag != f.flag) "
				  + "AND con.status = 'Active' "
				  + "AND con.status = 'Active' "
				  + "AND op.status = 'Active' "
				  + "AND op.type = 'Operator' "
				  + "AND fd.id IS NULL ";
			
			orderBy = "ORDER BY gc.flag,conid,opid LIMIT 100";
		}
		
		if(("contractor").equals(button)){
			where = "WHERE (gc.flag != f.flag OR gc.waitingOn != f.waitingOn) "
				  + "AND con.status = 'Active' "
				  + "AND con.status = 'Active' "
				  + "AND op.status = 'Active' "
				  + "AND op.type = 'Operator' "
				  + "AND fd.id IS NULL ";
			
			orderBy = "ORDER BY conid,opid LIMIT 100";
		}
		
		if(("operator").equals(button)){
			where = "WHERE (gc.flag != f.flag OR gc.waitingOn != f.waitingOn) "
				  + "AND con.status = 'Active' "
				  + "AND con.status = 'Active' "
				  + "AND op.status = 'Active' "
				  + "AND op.type = 'Operator' "
				  + "AND fd.id IS NULL ";
			
			orderBy = "ORDER BY opid,conid LIMIT 100";
		}
		
		sql += where + orderBy;
		
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