<%@ page language="java" import="com.jspsmart.upload.*, com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%//errorPage="exception_handler.jsp"%>
<%//@ page language="java" import="com.jspsmart.upload.*"%>
<%@ include file="utilities/adminGeneral_secure.jsp"%>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="uBean" class="com.picsauditing.PICS.UserBean" scope ="page"/>
<%  String id = request.getParameter("id"); //it gets the parameter from another web page
    String username = "";
	String password = "";
	String email ="";
	if (null==id) 
		id = pBean.userID;// gets the parameter from the permissionBean variable
	aBean.setFromDB(id);   // gets the info for the variables
	if (pBean.isMainAccount){ //check if this Main Account
		username= aBean.username;  
		password= aBean.password;
		email = aBean.email;
	}else{
	    uBean.setFromDB(pBean.uBean.id); // gets the infor from the userBean
	    username= uBean.username;
		password= uBean.password;
		email = uBean.email;
	}//if
	if (null != request.getParameter("Submit")){ 
		if (pBean.isMainAccount) 
			aBean.setFromRequest(request);
		else 
		    uBean.setFromEditRequest(request);
		if (aBean.isOK() && aBean.usernameCanBeChanged(request)){// usernameCanBeCange also get the error message
			if (pBean.isMainAccount) 
			  	aBean.writeToDB();
			else if (uBean.isOK())
			   	uBean.writeToDB(pBean.uBean.id);
		}//if 	
		username=request.getParameter("username"); // user or Main after submit
		password=request.getParameter("password");
		email=request.getParameter("email");			
	}//if Submit
%>
<html>
<head>
<title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <META Http-Equiv="Cache-Control" Content="no-cache">
  <META Http-Equiv="Pragma" Content="no-cache">
  <META Http-Equiv="Expires" Content="0">
  <link href="PICS.css" rel="stylesheet" type="text/css">
<script language="JavaScript" SRC="js/ImageSwap.js"></script>
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
	  <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr> 
          <td width="50%" bgcolor="#993300"><div align="center"><span class="blueMain"><a href="operator_edit.jsp?id=<%=16%>"><font color="#CCCCCC"></font></a></span></div></td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
		  <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"> <%@ include file="utilities/rightUpperNav.jsp"%> </td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_EditMyAccount.gif" width="321" height="72"></td>
          <td valign="top"> 
            <%@ include file="utilities/rightLowerNav.jsp"%>
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td colspan="3"> <table width="657" border="0" cellpadding="0" cellspacing="0">
              <tr> 
                <td height="35" colspan="2" align="center"> 
                  <form name="form1" method="post" action="operator_edit.jsp">
				    <table width="75%" border="0">
                      <tr> 
                        <td colspan="2" align="center" valign="top" class="blueHeader"></td>
                      </tr>
                      <tr> 
                        <td colspan="2" align="center" valign="top" class="redMain"><%=aBean.getErrorMessages()%><%=uBean.getErrorMessages()%></td>
                      </tr>
                      <tr> 
                        <td colspan="2" align="center" valign="top">&nbsp;</td>
                      </tr>
                      <tr> 
                        <td align="right" class="blueMain">Login:</td>
                        <td align="" valign="top"><input type="text" name="username" class="loginForms" value="<%=username%>">
                          <input type="hidden" name="oldUsername" value="<%=username%>">
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">password:</td>
                        <td align="" valign="top"><input type="text" name="password" class="loginForms" value="<%=password%>"></td>
                      </tr>
<%	if (pBean.isMainAccount){%>					  
                      <tr> 
                        <td colspan="2" align="right" class="blueMain"><div align="center"> 
                          </div></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Contact: </td>
                        <td> <input name="contact" type="text" class="forms" size="20" value="<%=aBean.contact%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Address: </td>
                        <td><input name="address" type="text" class="forms" size="30" value="<%=aBean.address%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">City: </td>
                        <td><input name="city" type="text" class="forms" size="15" value="<%=aBean.city%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">State/Province:</td>
                        <td><%=Inputs.getStateSelect("state","forms",aBean.state)%></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Zip: </td>
                        <td><input name="zip" type="text" class="forms" size="7" value="<%=aBean.zip%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Phone: </td>
                        <td><input name="phone" type="text" class="forms" size="15" value="<%=aBean.phone%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Phone 2: </td>
                        <td><input name="phone2" type="text" class="forms" size="15" value="<%=aBean.phone2%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Fax:</td>
                        <td><input name="fax" type="text" class="forms" size="15" value="<%=aBean.fax%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Web URL: </td>
                        <td><input name="web_URL" type="text" class="forms" size="30" value="<%=aBean.web_URL%>"></td>
                      </tr>
<%	}//if%>					  
                      <tr> 
                        <td class="blueMain" align="right">Email: </td>
                        <td><input name="email" type="text" class="forms" size="30" value="<%=email%>"></td>
                      </tr>					  
                      <tr> 
                        <td class="blueMain" align="right">&nbsp;</td>
                        <td> 
                          <input name="name" type="hidden" value="<%=aBean.name%>">
                          <input type="hidden" name="industry" value="<%=aBean.industry%>"> 
                          <input type="hidden" name="active" value="<%=aBean.active%>"> 
                          <input type="hidden" name="createdBy" value="<%=aBean.createdBy%>"> 
                          <input type="hidden" name="type" value="<%=aBean.type%>"></td>
                      </tr>
                      <tr> 
                        <td colspan="2" align="center" class="blueMain"><input name="Submit" type="Submit" class="forms" value="Save"></td>
                      </tr>
                    </table>
                  </form></td>
              </tr>
              <tr> 
                <td height="35" colspan="2" align="center">&nbsp; </td>
              </tr>
              <tr> 
                <td height="40"></td>
                <td height="40" align="center">&nbsp;</td>
              </tr>
            </table>
          </td>
          <td>&nbsp;</td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
</body>
</html>