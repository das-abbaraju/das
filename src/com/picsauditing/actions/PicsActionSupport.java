package com.picsauditing.actions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.inject.Context;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;

public class PicsActionSupport extends ActionSupport {
	protected static String LOGIN_AJAX = "LoginAjax";
	protected Permissions permissions = null;
	protected String message;
	
	private User user; // Current logged in user
	private Account account; // Current logged in user's account
	private List<User> auditorList;

	protected void loadPermissions() {
		if (permissions != null)
			// Already set
			return;
		
		permissions = (Permissions) ActionContext.getContext().getSession().get("permissions");
		if (permissions == null) {
			permissions = new Permissions();
			
			// To configure this setting in Eclipse, add "-DautoLogin=USER_ID_TO_AUTO_LOGIN" 
			// to the end of the "start" VMArgument
			// Run...Open Run Dialog...Arguments.VMArguments
			// Then restart Tomcat (also remember to pick the right Server when editing above)
			String autoLogin = System.getProperty("autoLogin");
			
			if( autoLogin != null && autoLogin.length() != 0 )
			{
				try
				{
					com.picsauditing.access.User user = new com.picsauditing.access.User();
					user.setFromDB(autoLogin);
					permissions.login(user);
					ActionContext.getContext().getSession().put("permissions", permissions);
				}
				catch( Exception e )
				{
					System.out.println("Problem autologging in.  Id supplied was: " + autoLogin );
				}
			}
		}
		
	}
	
	protected boolean forceLogin() {
		loadPermissions();
		try {
			if (!permissions.loginRequired(ServletActionContext.getResponse(), ServletActionContext.getRequest())) {
				return false;
			}
		} catch (Exception e) {
			System.out.println("PicsActionSupport: Error occurred trying to login:" + e.getMessage());
			return false;
		}
		return true;
	}
	
	protected void tryPermissions(OpPerms opPerms) throws Exception {
		permissions.tryPermission(opPerms, OpType.View);
	}

	protected void tryPermissions(OpPerms opPerms, OpType opType) throws Exception {
		loadPermissions();
		permissions.tryPermission(opPerms, opType);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public User getUser() {
		if (user == null) {
			UserDAO dao = (UserDAO)SpringUtils.getBean("UserDAO");
			user = dao.find(permissions.getUserId());
		}
		return user;
	}
	
	public Account getAccount() {
		loadPermissions();
		if (account == null) {
			AccountDAO dao = (AccountDAO)SpringUtils.getBean("AccountDAO");
			account = dao.find(permissions.getAccountId(), permissions.getAccountType());
		}
		return account;
	}
	
	public OperatorAccount getOperatorAccount() {
		Account operator = getAccount();
		if (operator.getType().equals("Operator"))
			return (OperatorAccount)operator;
		return null;
	}

	public Permissions getPermissions() {
		try {
			if (permissions == null)
				loadPermissions();
			return permissions;
		} catch (Exception e) {
			return new Permissions();
		}
	}
	
	public String getRequestURI() {
		return ServletActionContext.getRequest().getRequestURI();
	}
	
	public List<User> getAuditorList() {
		if (auditorList == null) {
			auditorList = new ArrayList<User>();
			auditorList.add(new User(User.DEFAULT_AUDITOR));
			UserDAO dao = (UserDAO) SpringUtils.getBean("UserDAO");
			auditorList.addAll(dao.findAuditors());
		}
		return auditorList;
	}
	
	static public String format(float number) {
		return new DecimalFormat("#,##0.00").format(number);
	}

	static public String format(float number, String mask) {
		return new DecimalFormat(mask).format(number);
	}
	
	static protected String getFtpDir() {
		return ServletActionContext.getServletContext().getInitParameter("FTP_DIR");
	}
}
