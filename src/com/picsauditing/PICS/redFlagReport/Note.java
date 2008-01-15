package com.picsauditing.PICS.redFlagReport;

import java.sql.ResultSet;
import com.picsauditing.PICS.*;

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
