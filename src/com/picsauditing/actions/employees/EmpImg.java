package com.picsauditing.actions.employees;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.terracotta.agent.repkg.de.schlichtherle.io.FileInputStream;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.util.FileUtils;

public class EmpImg extends PicsActionSupport{
	
	private EmployeeDAO employeeDAO;
	
	protected InputStream inputStream;
	protected Employee employee;
	
	private int employeeID;	

	public EmpImg(EmployeeDAO employeeDAO){
		this.employeeDAO = employeeDAO;
	}
	
	public String execute(){
		
		if(button!=null){
			if(button.equals("photo")){
				try{
					File photo = new File(getFtpDir() + "/files/"+FileUtils.thousandize(employeeID)+getFileName(employeeID)+".jpg");
					if(photo.exists()){
							inputStream = new FileInputStream(photo);
							return "photo";		
					} else 
						return BLANK;					
				}catch(Exception e){
					addActionError("Failed to load img");
					return BLANK;
				}
			}
		}
		
		return SUCCESS;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}	
	
	public String getFileName(int eID) {
		return PICSFileType.emp + "_" + eID;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

}
