package com.picsauditing.PICS.redFlagReport;

import java.sql.ResultSet;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;

import com.picsauditing.PICS.*;
import com.picsauditing.access.Permissions;
import com.picsauditing.search.SelectSQL;

public class Note extends DataBean{
	public String noteID = "";
	String opID = "";
	String conID = "";
	String whoIs = "";
	String note = "";
	String timeStamp = "";

	ResultSet listRS = null;
	int numResults = 0;
	public int count = 0;

	public Note(){
	}//Note

	public Note(String opID, String conID, String whoIs, String note){
		this.opID = opID;
		this.conID = conID;
		this.whoIs = whoIs;
		this.note = note;
	}//Note

	public String getNoteDisplay(){
		return timeStamp+" ("+whoIs+"): "+note;
	}//getNoteDisplay
	
	public void writeToDB()throws Exception{
		try{
			DBReady();
			String insertQuery = "INSERT INTO notes (opID,conID,whoIs,note,timeStamp) VALUES("+
				opID+","+conID+",'"+Utilities.escapeQuotes(whoIs)+"','"+Utilities.escapeQuotes(note)+"',NOW());";
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}//finally
	}//writeToDB

	public void deleteNote(String deleteID,String who)throws Exception{
		try{
			DBReady();
			String updateQuery = "UPDATE notes SET isDeleted=true, deletedDate=NOW(),whoDeleted='"+
					Utilities.escapeQuotes(who)+"' WHERE noteID="+deleteID+" LIMIT 1";
				SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
	}//writeToDB

	public void setList(String opID,String conID) throws Exception {
		String selectQuery = "SELECT *,DATE_FORMAT(timeStamp,'%c/%e/%y') AS formattedDate "+
			"FROM notes WHERE opID="+opID+" AND conID="+conID+" AND isDeleted=false ORDER BY timeStamp DESC;";
		try{
			DBReady();
			listRS = SQLStatement.executeQuery(selectQuery);
			numResults = 0;
		} catch (Exception e) {
			closeList();
			throw e;
		}//catch		
	}//setList

	/**
	 * 
	 * @param conID
	 * @param permissions
	 * @return columns: noteID, opID, whoIs, note, name
	 * @throws Exception
	 */
	public List<BasicDynaBean> getContractorNotes(String conID, Permissions permissions) throws Exception {
		SelectSQL sql = new SelectSQL();
		sql.setFromTable("notes n");
		sql.addField("n.noteID");
		sql.addField("n.opID");
		sql.addField("n.whoIs");
		sql.addField("n.note");
		sql.addField("a.name");
		sql.addField("DATE_FORMAT(n.timeStamp,'%c/%e/%y') AS formattedDate");
		sql.addJoin("JOIN accounts a ON a.id = n.opID");
		sql.addWhere("conID = '"+conID+"' AND isDeleted=0");
		sql.addOrderBy("timeStamp DESC");
		
		if (permissions.isPicsEmployee()) {
			// PICS employees can see ALL notes
		} else {
			String opID = permissions.getAccountIdString();
			sql.addWhere("n.opID IN (" +
				" SELECT opID FROM facilities WHERE corporateID in (SELECT corporateID FROM facilities where opID = "+opID+")" +
				" UNION SELECT opID FROM facilities WHERE corporateID = "+opID +
				" UNION SELECT "+opID+
				" UNION SELECT corporateID AS opID FROM facilities where opID="+opID+")");
		}
		
		try {
			DBReady();
			ResultSet rs = SQLStatement.executeQuery(sql.toString());
		    RowSetDynaClass rsdc = new RowSetDynaClass(rs, false);
		    rs.close();
		    return rsdc.getRows();
		} finally {
			DBClose();
		}//catch		
	}//getContractorNotes

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		noteID = SQLResult.getString("noteID");
		opID = SQLResult.getString("opID");
		conID= SQLResult.getString("conID");
		whoIs= SQLResult.getString("whoIs");
		note = SQLResult.getString("note");
		timeStamp = SQLResult.getString("formattedDate");
	}//setFromResultSet

	public boolean isNext() throws Exception{
		if (!listRS.next())
			return false;
		count++;
		setFromResultSet(listRS);
		return true;
	}//isNextRecord

	public void closeList() throws Exception {
		count = 0;
		numResults = 0;
		if (null != listRS) {
			listRS.close();
			listRS = null;
		}//if
		DBClose();
	}//closeList
}//Note
