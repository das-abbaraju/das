package com.picsauditing.tags;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;

public class SecurityCheck implements Tag
{
	protected Tag parent = null;
	protected PageContext pageContext = null;
	
	protected String perm = null;
	protected String type = null;
	protected boolean debug = false;
	
	protected OpPerms opPerms = null;
	protected OpType opType = null;

	

	@Override
	public int doStartTag() throws JspException
	{
		HttpSession session = pageContext.getSession();

		if( session == null )
		{
			System.out.println( "<pics:permissions> is returning false because there was no http session");
			
			return SKIP_BODY;
		}
		
		Permissions permissions = (Permissions) session.getAttribute("permissions");
		
		//this means they're not logged in...no way are they getting access to our resource
		if( permissions == null )
		{
			if( debug )
			{
				System.out.println( "<pics:permissions> is returning false because the permissions object was not in the session");
			}
			
			return SKIP_BODY;
		}
		
		//build the opPerms object based on the perm string from the tag call
		setOpPerms();
		
		//build the opType object based on the "type" string from the tag call
		setOpType();

		//default to "view".  This will happen if nothing was specified in the "type" attribute 
		if( opType == null )
		{
			opType = OpType.View;
		}

		
		if( debug )
		{
			System.out.println( "<pics:permission> tag debug information: ");
			System.out.println("\tOpPerms = " + opPerms.name() );
			System.out.println("\tOpType = " + opType.name() );
		}
		
		//run the check
		if( permissions.hasPermission(opPerms, opType))
		{
			return EVAL_BODY_INCLUDE;
		}
		else
		{
			return SKIP_BODY;
		}
	}


	private void setOpType() throws JspException {
		if( type != null )
		{
			try
			{
				opType = OpType.valueOf(type);
			}
			catch( Exception e )
			{
				throw new JspException("OpType does not exist: " + type);
			}
		}
	}


	private void setOpPerms() throws JspException {
		if( perm != null )
		{
			try
			{
				opPerms = OpPerms.valueOf(perm);
			}
			catch( Exception e )
			{
				throw new JspException("OpPerm does not exist: " + perm);
			}
		}
	}

	
	@Override
	public int doEndTag() throws JspException
	{
		return EVAL_PAGE;
	}

	@Override
	public Tag getParent()
	{
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public void release()
	{
	// TODO Auto-generated method stub
	}

	@Override
	public void setPageContext( PageContext arg0 )
	{
		this.pageContext = arg0;
	}

	@Override
	public void setParent( Tag arg0 )
	{
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
	
	
	
	
}
