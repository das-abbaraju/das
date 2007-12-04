<%//@ page language="java" import="com.picsauditing.PICS.*,com.jspsmart.upload.*" errorPage="exception_handler.jsp"%>
<%@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.access.OpPerms"%>
<%@ include file="utilities/op_edit_secure.jsp" %>

<jsp:useBean id="fBean" class="com.picsauditing.PICS.FormBean" scope ="page"/>
<jsp:useBean id="eBean" class="com.picsauditing.PICS.EmailBean" scope ="page"/>

<%	String path = application.getInitParameter("FTP_DIR");
	boolean isSubmitted = "Yes".equals(request.getParameter("isSubmitted"));
	fBean.setFromDB();
	String editCatID = "";
	if (!pBean.isAdmin())
		editCatID = pBean.userID;
	
	String opID = request.getParameter("id");
	String editFormID = "";
	String newFormCatID = "";
	
	if (isSubmitted){
		String action = request.getParameter("action");
		System.out.println("action: "+action);
		request.setAttribute("uploader", String.valueOf(com.picsauditing.servlet.upload.UploadProcessorFactory.FORM));
		request.setAttribute("directory","forms");
		if ("Update".equals(action)){
			fBean.updateAction(pageContext, path);
			if(fBean.getEditCatID()!= "")
		    	editCatID = fBean.getEditCatID();
		}
		if ("Add".equals(action))
			fBean.addFormEntry(pageContext);		
		if ("Upload".equals(action)) {
			fBean.uploadNewWelcomeEmail(pageContext);
			eBean.writeWelcomeEmailValuesToDB(
					fBean.newWelcomeEmailSubject,
					fBean.newWelcomeEmailGreeting,
					fBean.newWelcomeEmailBody,
					fBean.newAttachWelcomeEmailFile);
		}
		if ("UploadUserManual".equals(action)) {
			fBean.uploadNewUserManual(pageContext);
		}
		fBean.setFromDB();

	}//if
	eBean.setFromDB();
%>
<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <META Http-Equiv="Cache-Control" Content="no-cache">
  <META Http-Equiv="Pragma" Content="no-cache">
  <META Http-Equiv="Expires" Content="0">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
	  <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr> 
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" rowspan="2" valign="top"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td valign="top" align="center"><a href="accounts_manage.jsp"><img src="images/header_manageAccounts.gif" width="252" height="72" border="0"></a></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td colspan="3" align=center>
            <table width="657" cellpadding="2" cellspacing="0">
              <tr>
                <td colspan="2" align="center" class="blueMain">
                  <%@ include file="includes/nav/opSecondNav.jsp"%>
	              <span class="blueHeader">Manage Forms & Documents</span><br>
                </td>
              </tr>
			  <tr>
			    <td colspan="2" align="center" valign="top" class="redMain">
                  <b><%=fBean.getErrorMessages()%></b>
				</td>
			  </tr>
<%	if (pBean.isAdmin() || pBean.userAccess.hasAccess(OpPerms.EditForms)){%>
              <tr> 
                <td width="50%" align="center" valign="top" bgcolor="#DDDDDD" class="blueMain"> 
                  <form name="form1" method="post" action="manage_forms.jsp?isSubmitted=Yes&action=Add&id=<%=opID%>" enctype="multipart/form-data">
                    <table border="0" cellpadding="0" cellspacing="0" class="blueMain">
                      <tr> 
                        <td colspan="2" align="center" class="redMain"><strong>Add 
                          a New Form</strong></td>
                      </tr>
<%		if (pBean.isAdmin()){%>
                      <tr> 
                        <td align="right">Category</td>
                        <td><%=fBean.getCategorySelect("newFormCatID", "blueMain",newFormCatID)%> 
                        </td>
                      </tr>
<%		}else
			out.println("<input type=hidden name=newFormCatID value='"+pBean.userID+"'>");
%>
                      <tr> 
                        <td align="right"><nobr>Form Name</nobr></td>
                        <td> <input name="newFormName" type="text" class="forms" size="26"> 
                        </td>
                      </tr>
                      <tr> 
                        <td align="right">File&nbsp; </td>
                        <td> <input name="newFormFile" type="file" class="forms" size="15"></td>
                      </tr>
                      <tr> 
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td>&nbsp;</td>
                        <td><input name="action" type="submit" class="forms" value="Add"></td>
                      </tr>
                    </table>
                  </form>
                </td>
                <td align="center" valign="top" bgcolor="#FFFFFF" class="blueMain">
				  <form action="manage_forms.jsp?isSubmitted=Yes&action=Update&id=<%=opID%>" method="post" name="form2" id="form2" enctype="multipart/form-data">
                    <table border="0" cellpadding="0" cellspacing="0" class="blueMain">
                      <tr> 
                        <td colspan="2" class="redMain"><strong>Edit an Existing Form</strong></td>
                      </tr>
					  <tr>
					    <td colspan="2">
<%		if (pBean.isAdmin()){%>
						  <nobr><%=fBean.getCategorySelect("editCatID", "blueMain",editCatID)%>
                          <input name="action" type=submit class=forms value=Update></nobr>
<%		}else
			out.println("<input type=hidden name=editCatID value='"+pBean.userID+"'>");
%>
						  <%=fBean.getFormSelect("editFormID","blueMain",editCatID,editFormID)%>
						</td>
                      </tr>
                      <tr> 
                        <td align="right">&nbsp;</td>
                        <td align="right">&nbsp;</td>
                      </tr>
                      <tr> 
                        <td align="right">Edit Name&nbsp;</td>
                        <td> <input name="newEditFormName" type="text" class="forms" size="26">
                        </td>
                      </tr>
                      <tr> 
                        <td align="right"><nobr>Update File&nbsp;</nobr></td>
                        <td> <input name="newFile" type="file" class="forms" size="15"></td>
                      </tr>
                      <tr> 
                        <td align="right">&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td align="right">&nbsp;</td>
                        <td>
						  <input name="action" type="submit" class="forms" value="Edit"> 
                          <input name="action" type="submit" class="forms" value="Delete">
						</td>
                      </tr>
                    </table>
                  </form>
                </td>
              </tr>
<%		if (pBean.isAdmin()){%>
              <tr>
                <td align="center" valign="top" bgcolor="#CCCCCC" class="blueMain" colspan="2">
				  <form action="manage_forms.jsp?isSubmitted=Yes&action=Upload" method="post" name="form3" id="form3" enctype="multipart/form-data">
                    <table border="0" cellpadding="1" cellspacing="0">
                      <tr> 
                        <td colspan="2" align="center" class="redMain"><strong>Update
                            the Welcome E-mail</strong></td>
                      </tr>
                      <tr> 
                        <td align="right" valign="top" class="redMain">Subject:</td>
                        <td><input name="welcomeEmailSubject" type="text" class="forms" size="80" value="<%=eBean.welcomeEmailSubject%>"></td>
                      </tr>
                      <tr> 
                        <td align="right" valign="top" class="redMain">Text:</td>
                        <td><textarea name="welcomeEmailGreeting" cols="80" rows="4" class="forms" id="welcomeEmailGreeting"><%=eBean.welcomeEmailGreeting%></textarea></td>
                      </tr>
                      <tr> 
                        <td width="80" align="right" valign="top" class="redMain">Fixed:</td>
                        <td class="blueMain">&lt;CompanyName&gt;. Please click 
                          on this link to confirm your receipt of this email:<br>
						  <u>Confirm Email Receipt</u><br>
						  Because we send important account info to this email, your 
						  account will not be activated until you have confirmed receipt
						  of this email.  If the link does not work, please cut and past
						  the url into your web browser.  After that, you will be able to
						  log into your account at www.picsauditing.com. Your username is 
						  "username" and your password is "yourpass".</td>
                      </tr>
                      <tr> 
                        <td align="right" valign="top" class="redMain">Text:</td>
                        <td><textarea name="welcomeEmailBody" cols="80" rows="6" class="forms" id="welcomeEmailBody"><%=eBean.welcomeEmailBody%></textarea></td>
                      </tr>
                      <tr> 
                        <td width="80" align="right" valign="top" class="redMain">Fixed:</td>
                        <td class="blueMain"><%=Utilities.escapeHTML(eBean.EMAIL_FOOTER)%></td>
                      </tr>
	                  <tr> 
                        <td align="right" class="redMain">File:</td>
                        <td> <input name="welcomeEmailFile" type="file" class="forms" size="15"></td>
                      </tr>
	                  <tr> 
                        <td align="right" class="redMain">Attach File:</td>
                        <td class="blueMain">
						<input type="radio" name="attachWelcomeEmailFile" value="Yes" <%=Utilities.checked(eBean.attachWelcomeEmailFile,"Yes")%>> Yes
                        <input type="radio" name="attachWelcomeEmailFile" value="No" <%=Utilities.checked(eBean.attachWelcomeEmailFile,"No")%>> No</td>
                      </tr>
                      <tr> 
                        <td>&nbsp;</td>
                        <td><input type="submit" class="forms" value="Upload"></td>
                      </tr>
                    </table>
                  </form></td>
              </tr>
              <tr>
                <td align="center" valign="top" bgcolor="#DDDDDD" class="blueMain" colspan="2">
				  <form action="manage_forms.jsp?isSubmitted=Yes&action=UploadUserManual" method="post" name="form4" id="form4" enctype="multipart/form-data">
                    <table border="0" cellpadding="1" cellspacing="0">
                      <tr> 
                        <td colspan="2" align="center" class="redMain"><strong>Upload New User Manual
                            </strong></td>
                      </tr>
	                  <tr> 
                        <td align="right" class="redMain">File:</td>
                        <td> <input name="userManualFile" type="file" class="forms" size="15"></td>
                      </tr>
                      <tr> 
                        <td>&nbsp;</td>
                        <td><input type="submit" class="forms" value="Upload"></td>
                      </tr>
                    </table>
                  </form></td>
              </tr>
<%		}//if
	}//if%>
              <tr>
                <td class="redMain" align="center" colspan=2>
                  <table border="0" cellpadding="1" cellspacing="1">
                    <tr class="whiteTitle">
                      <td bgcolor="#003366" align="center" colspan="2">Form</td>
                      <td bgcolor="#993300" align="center">Facility</td>
                    </tr>
<%		fBean.setList();
		while (fBean.isNextForm(pBean)){		
%>
                    <tr class=blueMain align="center" <%=Utilities.getBGColor(fBean.count)%>>
                      <td align="right"><%=fBean.count%>.</td>
                      <td align="left"><a href='/forms/<%=fBean.file%>' target=_blank><%=fBean.formName%></a></td>
                      <td align="left"><%=fBean.opName%></td>
                    </tr>
<%		}//while%>
                  </table>
				</td>
              </tr>
              <tr> 
                <td>&nbsp;</td>
              </tr>
			</table>
		  </td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br>
      <br>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
</body>
</html>
