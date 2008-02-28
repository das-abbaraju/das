package com.picsauditing.PICS;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;

import com.picsauditing.servlet.upload.UploadConHelper;
import com.picsauditing.servlet.upload.UploadProcessorFactory;

public class FormBean extends DataBean {
	public ArrayList<String> categories = null;
	public ArrayList<String> formsAL = null;
	ListIterator li = null;
	public int count = 0;
	public int totalCount = 0;

	static final String DEFAULT_SELECT_CATEGORY = "SELECT A CATEGORY";
	static final String DEFAULT_SELECT_FORM = "SELECT A FORM";

	public String formID = "";
	public String formName = "";
	public String file = "";
	public String opID = "";
	public String opName = "";
	
	public String newAttachWelcomeEmailFile;
	private String editCatID = "";

	public void setList() throws Exception{
		setFromDB();
		li = formsAL.listIterator();
		count = 0;
		totalCount = formsAL.size()/4;
	}//setList

	public String getCategoryName(String catID) throws Exception{
		if (categories.contains(catID))
			return (String)categories.get(categories.indexOf(catID)+1);
		else
			return "";
//			throw new Exception("Invalid catID for Forms: "+catID);
	}//getCategoryName

	public boolean isNextForm(PermissionsBean pBean) throws Exception{
		if (li.hasNext()){
			formID = (String)li.next();
			formName = (String)li.next();
			file = (String)li.next();
			opID = (String)li.next();
			opName = getCategoryName(opID);
			if (pBean.isAdmin() ||
					(pBean.isContractor() && ("0".equals(opID) || pBean.allFacilitiesAL.contains(opID))) ||
					(pBean.isOperator() && (pBean.userID.equals(opID) || pBean.oBean.corporatesAL.contains(opID))) ||
					(pBean.isCorporate() && (pBean.userID.equals(opID) || pBean.oBean.facilitiesAL.contains(opID)))){
				count++;
				return true;
			}//if
			else
				return (isNextForm(pBean));
		}//if
		return false;
	}//isNextForm

	public void closeList(){
		li = null;
	}//closeList

	public String getName(String fID) throws Exception {
		if (formsAL.contains(fID))
			return (String)formsAL.get(formsAL.indexOf(fID)+1);
		else
			throw new Exception("Invalid form ID: "+fID);
	}//getName
	public String getFile(String fID) throws Exception {
		if (formsAL.contains(fID))
			return (String)formsAL.get(formsAL.indexOf(fID)+2);
		else
			throw new Exception("Invalid form ID: "+fID);
	}//getFile
	public String getCategory(String fID) throws Exception {
		if (formsAL.contains(fID))
			return (String)formsAL.get(formsAL.indexOf(fID)+3);
		else
			throw new Exception("Invalid form ID: "+fID);
	}//getCategory

	public void setFromDB() throws Exception {
		if (null != formsAL)
			return;
		String selectQuery = "SELECT id,name FROM accounts WHERE type IN ('Operator','Corporate') ORDER BY name;";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			categories = new ArrayList<String>();
			categories.add("0");
			categories.add("PICS");
			while (SQLResult.next()){
				categories.add(SQLResult.getString("id"));
				categories.add(SQLResult.getString("name"));
			}//while
			SQLResult.close();
			selectQuery = "SELECT * FROM operatorForms LEFT JOIN accounts ON (id=opID) ORDER BY name,formName";
			SQLResult = SQLStatement.executeQuery(selectQuery);
			formsAL = new ArrayList<String>();
			while (SQLResult.next()) {
				formsAL.add(SQLResult.getString("formID"));
				formsAL.add(SQLResult.getString("formName"));
				formsAL.add(SQLResult.getString("file"));
				formsAL.add(SQLResult.getString("opID"));
			}//while
			SQLResult.close();
		}finally{
			DBClose();
		}//finally		
	}//setFromDB

	public int getNumCategories() {
		return categories.size();
	}//getNumCategories

	public String getCategory(int i) {
		if (i <= categories.size())		return (String)categories.get(i-1);
		else							return "";
	}//getCategory

	public void addFormEntry(javax.servlet.jsp.PageContext pageContext) throws Exception {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
		request.setAttribute("directory", "forms");
		request.setAttribute("uploader", String.valueOf(UploadProcessorFactory.FORM));
		UploadConHelper helper = new UploadConHelper();
		helper.init(request, response);
		
		Map<String,String>params = (Map<String,String>)request.getAttribute("uploadfields");
		String newName = params.get("newFormName");
		
		if (newName == null || newName.equals("")) {
			errorMessages.addElement("You must fill in the Form Name field.");
			return;
		}//if	
		
		try{
			String newCatID = params.get("newFormCatID");
			String insertQuery = "INSERT INTO operatorForms (opID) VALUES ("+newCatID+");";
			DBReady();
			SQLStatement.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);
			ResultSet SQLResult = SQLStatement.getGeneratedKeys();
			SQLResult.next();
			String fID = SQLResult.getString("GENERATED_KEY");
			SQLResult.close();
			
			String fileName = (String)request.getAttribute("fileName");			
			String shortName=renameFormFile(fileName, fID);
			
			String updateQuery = "UPDATE operatorForms SET formName='"+Utilities.escapeQuotes(newName)+"',file='"+shortName+
				"',opID="+newCatID+" WHERE formID="+fID+";";
				SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally		
		formsAL = null;
		errorMessages.addElement("Form '"+newName+"' uploaded");
	}//addFormEntry

	public void updateFormEntry(String editFormID, String newFormName, 
			String ftpDir, javax.servlet.jsp.PageContext pageContext) throws Exception {
		
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		
		String newFileName = "";
		if ((null == editFormID) || ("".equals(editFormID)) || (DEFAULT_SELECT_FORM.equals(editFormID))) {
			errorMessages.addElement("Please select a valid form to edit");
			return;
		}//if
		if ("".equals(newFormName) || (null == newFormName))
			newFormName = getName(editFormID);		
				
		String deleteFile = getFile(editFormID);
		java.io.File fileToDelete = new java.io.File(ftpDir + "/forms/"+deleteFile);				
		String fn = (String)request.getAttribute("fileName");  //ftpDir + /forms/form + editFormID + .ext
		if(fn==null || fn.equals(""))
			newFileName = deleteFile;
		else{
			if (fileToDelete.exists())
				fileToDelete.delete();
			else{
				errorMessages.addElement(deleteFile + " does not exist.");
				return;
			}
			int end = deleteFile.lastIndexOf('.');
			newFileName = renameFormFile(fn, deleteFile.substring(4, end));			
		}
		
		String updateQuery = "UPDATE operatorForms SET formName='"+Utilities.escapeQuotes(newFormName)+"',file='"+Utilities.escapeQuotes(newFileName)+
			"' WHERE formID="+editFormID+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally		
		formsAL = null;
		errorMessages.addElement("Form '"+newFormName+"' updated");
	}//updateFormEntry
	
	public void updateAction(javax.servlet.jsp.PageContext pageContext, String path) throws Exception {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
		
		UploadConHelper helper = new UploadConHelper();
		helper.init(request, response);
		
		Map<String,String>params = (Map<String,String>)request.getAttribute("uploadfields");
		String action = params.get("action");
		if(action != null){
			if(action.equals("Update"))
				editCatID = params.get("editCatID");			
			if(action.equals("Delete"))
				deleteForm(params.get("editFormID"), path);
			if(action.equals("Edit")){
				String editFormID = params.get("editFormID");
				String newEditFormName = params.get("newEditFormName");
				updateFormEntry(editFormID, newEditFormName, path, pageContext);
			}
				
		}
		
	}

	public void deleteForm(String deleteFormID, String path) throws Exception {
		
		
		if ((null == deleteFormID) || ("".equals(deleteFormID)) || (DEFAULT_SELECT_FORM.equals(deleteFormID))) {
			errorMessages.addElement("Please select a valid form to delete");
			return;
		}//if
		String deleteFile = getFile(deleteFormID);
		java.io.File fileToDelete = new java.io.File(path+"/forms/"+deleteFile);
		if (fileToDelete.exists())
			fileToDelete.delete();
		String Query = "DELETE FROM operatorForms WHERE formID="+deleteFormID+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(Query);
		}finally{
			DBClose();
		}//finally		
		formsAL = null;
		errorMessages.addElement("Form deleted");
	}//deleteForm

	public int getNumFormEntries() {
		return formsAL.size()/4;
	}//getNumFormEntries

	public String getCategorySelect(String name, String classType, String selectedCatID) {
		return Inputs.inputSelect2(name,classType,selectedCatID,(String[])categories.toArray(new String[0]));
	}//getCategorySelect

	public String getFormSelect(String name, String classType, String selectedCatID, String selectedFormID) throws Exception {
		setFromDB();
		StringBuffer temp = new StringBuffer();		
		if (null==selectedCatID || "".equals(selectedCatID))
			selectedCatID = "0";
		ArrayList<String> tempAL = getFormsInCategory(selectedCatID);
		temp.append(Inputs.inputSelect2First(name,classType,selectedFormID,
				(String[])tempAL.toArray(new String[0]),DEFAULT_SELECT_FORM,DEFAULT_SELECT_FORM));
		return temp.toString();		
	}//getFormSelect

	public ArrayList<String> getFormsInCategory(String searchCatID) {
		ArrayList<String> tempAL = new ArrayList<String>();
		ListIterator li = formsAL.listIterator();
		while (li.hasNext()) {
			String fID = (String)li.next();
			String formName = (String)li.next();
			String file = (String)li.next();
			String catID = (String)li.next();
			if (searchCatID.equals(catID)) {
				tempAL.add(fID);
				tempAL.add(formName);				
			}//if
		}// while
		return tempAL;
	}//getFormsInCategory

	public void uploadNewWelcomeEmail(javax.servlet.jsp.PageContext pageContext) throws Exception {

		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();

		request.setAttribute("welcomeEmailDir", "attachments");
		UploadConHelper helper = new UploadConHelper();
		helper.init(request, response);

		String errorMsg = (String)request.getAttribute("error");
		if(errorMsg != null && !errorMsg.equals("")){
			errorMessages.addElement(errorMsg);
	        return;
		}
		
		Map<String,String>params = (Map<String,String>)request.getAttribute("uploadfields");
		newAttachWelcomeEmailFile = params.get("attachWelcomeEmailFile");
	}

	public void uploadNewUserManual(javax.servlet.jsp.PageContext pageContext) throws Exception {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();

		request.setAttribute("userManualDir", "attachments");
		UploadConHelper helper = new UploadConHelper();
		helper.init(request, response);

		String errorMsg = (String)request.getAttribute("error");
		if(errorMsg != null && !errorMsg.equals("")){
			errorMessages.addElement(errorMsg);
	        return;
		}//if
	}

	public String getEditCatID() {
		return editCatID;
	}

	public void setEditCatID(String editCatID) {
		this.editCatID = editCatID;
	}
	
	private String renameFormFile(String fileName, String fID){
		File file = new File(fileName);
		String shortName = "";
		if(file.exists()){
			String fn= fileName.replaceFirst("X_X.", fID + ".");
			File newFile = new File(fn);
			file.renameTo(newFile);
			shortName = FilenameUtils.getName(fn);
		}
		
		return shortName;
	}
	
}//FormBean
