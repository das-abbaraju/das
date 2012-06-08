package com.picsauditing.tags;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;

public class SecurityCheck implements Tag {
	protected Tag parent = null;
	protected PageContext pageContext = null;

	protected String perm = null;
	protected String type = null;
	protected boolean debug = false;
	protected boolean negativeCheck = false;
	
	
	protected OpPerms opPerms = null;
	protected OpType opType = null;

	public int doStartTag() throws JspException {
		HttpSession session = pageContext.getSession();

		if (session == null) {
			System.out
					.println("<pics:permissions> is returning doesNotHavePermission() because there was no http session");

			return doesNotHavePermission();
		}

		Permissions permissions = (Permissions) session
				.getAttribute("permissions");

		// this means they're not logged in...no way are they getting access to
		// our resource
		if (permissions == null) {
			if (debug) {
				System.out
						.println("<pics:permissions> is returning doesNotHavePermission() because the permissions object was not in the session");
			}

			return doesNotHavePermission();
		}

		// build the opPerms object based on the perm string from the tag call
		setOpPerms();

		// build the opType object based on the "type" string from the tag call
		setOpType();

		// default to "view". This will happen if nothing was specified in the
		// "type" attribute
		if (opType == null) {
			opType = OpType.View;
		}

		if (debug) {
			System.out.println("<pics:permission> tag debug information: ");
			System.out.println("\tisNegativeCheck = " + negativeCheck);
			System.out.println("\tOpPerms = " + opPerms.name());
			System.out.println("\tOpType = " + opType.name());
		}

		// run the check
		if (permissions.hasPermission(opPerms, opType)) {
			if (debug) {
				System.out.println("\t\t\t\tUser has Permission");
			}

			return hasPermission();
		} else {
			if (debug) {
				System.out.println("\t\t\t\tUser does not have Permission");
			}
			return doesNotHavePermission();
		}
	}

	private void setOpType() throws JspException {
		if (type != null) {
			try {
				opType = OpType.valueOf(type);
			} catch (Exception e) {
				throw new JspException("OpType does not exist: " + type);
			}
		}
	}

	private void setOpPerms() throws JspException {
		if (perm != null) {
			try {
				opPerms = OpPerms.valueOf(perm);
			} catch (Exception e) {
				throw new JspException("OpPerm does not exist: " + perm);
			}
		}
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	public Tag getParent() {
		return parent;
	}

	public void release() {
		// TODO Auto-generated method stub
	}

	public void setPageContext(PageContext arg0) {
		this.pageContext = arg0;
	}

	public void setParent(Tag arg0) {
		this.parent = arg0;
	}

	public String getPerm() {
		return perm;
	}

	public void setPerm(String perm) {
		this.perm = perm;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isNegativeCheck() {
		return negativeCheck;
	}

	public void setNegativeCheck(boolean negativeCheck) {
		this.negativeCheck = negativeCheck;
	}

	
	//these methods make the jump from having a permission to whether or not to display the body of the tag.
	protected int hasPermission()
	{
		if( negativeCheck )
		{
			return SKIP_BODY;
		}
		
		return EVAL_BODY_INCLUDE;
	}
	protected int doesNotHavePermission()
	{
		if( negativeCheck )
		{
			return EVAL_BODY_INCLUDE;
		}
		return SKIP_BODY;
	}
	
	
	
}
