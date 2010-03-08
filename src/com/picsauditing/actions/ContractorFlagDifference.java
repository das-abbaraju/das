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
	
	@Override
	public String execute() throws Exception {
		
		if ("delete".equals(button)) {
			String insert = " INSERT INTO flag_dirty values (null,"+ conID +", "+ opID +" ,'"+ flag +"' ,'Good')";
			db.executeInsert(insert);
			return BLANK;
		}
		
		String sql = "SELECT gc.subID conID,con.name as ContractorName, gc.genID opID, op.name as OperatorName, gc.flag newColor, f.flag oldColor, gc.waitingOn newwaitingon, f.waitingon oldwaitingOn "
			  + "FROM generalcontractors gc "
			  + "JOIN old_flags f ON gc.genID = f.opID AND gc.subID = f.conID "
			  + "JOIN accounts op ON op.id = gc.genid "
			  + "JOIN accounts con ON con.id = gc.subid "
			  + "LEFT JOIN flag_dirty fd ON fd.conid = gc.subid AND fd.opId = gc.genid "
			  + "WHERE (gc.flag != f.flag OR gc.waitingOn != f.waitingOn) "
			  + "AND con.status = 'Active' "
			  + "AND con.status = 'Active' "
			  + "AND op.status = 'Active' "
			  + "AND op.type = 'Operator' "
			  + "AND fd.id IS NULL "
			  + "ORDER BY conid,opid " 
			  +	"LIMIT 100";
		
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