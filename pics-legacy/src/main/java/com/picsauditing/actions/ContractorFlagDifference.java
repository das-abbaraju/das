package com.picsauditing.actions;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.search.Database;


@SuppressWarnings("serial")
public class ContractorFlagDifference extends PicsActionSupport {
	Database db = new Database();
	private List<BasicDynaBean> data;
	protected int conID;
	protected int opID;
	protected String flag;
	protected boolean switchOrder = false;
	private int rowCount = 0;
	
	private final Logger logger = LoggerFactory.getLogger(ContractorFlagDifference.class);
	@Override
	public String execute() throws Exception {
		
		String sql = "SELECT SQL_CALC_FOUND_ROWS co.conID conID,con.name as ContractorName, co.opID opID, op.name as OperatorName, co.flag newColor, f.flag oldColor, co.waitingOn newwaitingon, f.waitingon oldwaitingOn "
			  + "FROM contractor_operator co "
			  + "JOIN pics_yesterday.contractor_operator f ON co.opID = f.opID AND co.conID = f.conID "
			  + "JOIN accounts op ON op.id = co.opID "
			  + "JOIN accounts con ON con.id = co.conID "
			  + "LEFT JOIN flag_dirty fd ON fd.conid = co.conID AND fd.opId = co.opID ";
		
		String baseWhere = "WHERE con.status = 'Active' "
		  + "AND op.status = 'Active' "
		  + "AND con.type = 'Contractor' "
		  + "AND op.type = 'Operator' "
		  + "AND fd.id IS NULL ";
		
		String where = "AND co.flag != f.flag ";
		String orderBy = "ORDER BY con.name, op.name";
		
		if ("delete".equals(button)) {
			String insert = " INSERT INTO flag_dirty values (null,"+ conID +", "+ opID +" ,'"+ flag +"' ,'Good')";
			db.executeInsert(insert);
			return BLANK;
		}
		
		if(("oldWaitingOn").equals(button)){
			where = "AND co.waitingOn != f.waitingOn";
			orderBy = "ORDER BY f.waitingOn,con.name,op.name";
		}
		
		if(("newWaitingOn").equals(button)){
			where = "AND co.waitingOn != f.waitingOn";
			orderBy = "ORDER BY co.waitingOn,con.name,op.name";
		}
		
		if(("oldFlag").equals(button)){
			orderBy = "ORDER BY f.flag,con.name,op.name";
		}

		if(("newFlag").equals(button)){
			orderBy = "ORDER BY co.flag,con.name,op.name";
		}
		
		if(("contractor").equals(button)){
			orderBy = "ORDER BY con.name,op.name";
		}
		
		if(("operator").equals(button)){
			orderBy = "ORDER BY op.name,con.name";
		}
		
		sql += baseWhere + where + " " + orderBy + "  LIMIT 100";
		logger.debug(sql);
		data = db.select(sql, true);
		rowCount  = db.getAllRows();
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
	
	public int getRowCount() {
		return rowCount;
	}
}