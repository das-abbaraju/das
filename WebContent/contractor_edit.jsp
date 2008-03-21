<%@ page language="java" import="com.picsauditing.access.*" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean" scope ="page"/>
<jsp:useBean id="helper" class="com.picsauditing.servlet.upload.UploadConHelper"/>
<%
	String id = request.getParameter("id");
	if (permissions.isContractor()) {
		id = permissions.getAccountIdString();
	} else {
		permissions.tryPermission(OpPerms.ContractorAccounts, OpType.Edit);
	}

	boolean isSubmitted = "Yes".equals(request.getParameter("isSubmitted"));
	aBean.setFromDB(id);
	cBean.setFromDB(id);
	if (isSubmitted){
//		Process form upload
		request.setAttribute("uploader", String.valueOf(com.picsauditing.servlet.upload.UploadProcessorFactory.CONTRACTOR));
		request.setAttribute("directory", "files");
		helper.init(request, response);			
		
		aBean.setFromUploadRequestClientEdit(request);
		cBean.setFromUploadRequestClientEdit(request);
		
		if (aBean.isOK() && cBean.isOK()) {
			aBean.writeToDB();
			cBean.setUploadedFiles(request);
			cBean.writeToDB();
			response.sendRedirect("contractor_detail.jsp?id="+id);
			return;
		}//if
	}//if
%>

<html>
<head>
<title>Edit Account</title>
<meta name="header_gif" content="header_editAccount.gif" />
</head>
<body>
			  <table width="657" border="0" cellpadding="0" cellspacing="0">
				<tr align="center" class="blueMain">
                  <td align="left"><%@ include file="includes/nav/secondNav.jsp"%></td>
                </tr>
			  </table>
              <table width="657" cellpadding="10" cellspacing="0">
                <tr> 
                  <td align="center" valign="top" class="blueMain"> <table border="0" cellspacing="0" cellpadding="1">
                      <tr align="center" class="blueMain"> 
                        <td colspan="2" class="blueHeader">My Account Information</td>
                      </tr>
                      <tr> 
                        <td colspan="2" class="redMain">
<%	if (isSubmitted)
		out.println(aBean.getErrorMessages() + cBean.getErrorMessages());
%>
                        </td>
                      </tr>
                      <tr class="blueMain"> 
                        <td colspan="2">&nbsp; </td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Username:</td>
                        <td class="blueMain"><b> <%=aBean.username%></b></td>
                      </tr>
                      <tr>
                        <td align="right" valign="top" class="blueMain">Tax ID:</td>
                        <td class="blueMain"><%=cBean.taxID%></td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Company Name</td>
                        <td> <input name="name" type="text" class="forms" size="20" value="<%=aBean.name%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Contact</td>
                        <td> <input name="contact" type="text" class="forms" size="20" value="<%=aBean.contact%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Address</td>
                        <td><input name="address" type="text" class="forms" size="30" value="<%=aBean.address%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">City</td>
                        <td><input name="city" type="text" class="forms" size="15" value="<%=aBean.city%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">State</td>
                        <td><%=com.picsauditing.PICS.Inputs.getStateSelect("state","forms",aBean.state)%></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Zip</td>
                        <td><input name="zip" type="text" class="forms" size="7" value="<%=aBean.zip%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Phone</td>
                        <td><input name="phone" type="text" class="forms" size="15" value="<%=aBean.phone%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Phone 2</td>
                        <td><input name="phone2" type="text" class="forms" size="15" value="<%=aBean.phone2%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Fax</td>
                        <td><input name="fax" type="text" class="forms" size="15" value="<%=aBean.fax%>"></td>
                      </tr>
                      <tr> 
                        <td align="right" valign="top" class="blueMain">Email</td>
                        <td valign="top" class="redMain"> <input name="email" type="text" class="forms" size="30" value="<%=aBean.email%>"> 
                          <br>
                          We send vital account info to this email</td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Second Contact</td>
                        <td><input name="secondContact" type="text" class="forms" size="15" value="<%=cBean.secondContact%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Second Phone</td>
                        <td><input name="secondPhone" type="text" class="forms" size="15" value="<%=cBean.secondPhone%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Second Email</td>
                        <td><input name="secondEmail" type="text" class="forms" size="15" value="<%=cBean.secondEmail%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Billing Contact</td>
                        <td><input name="billingContact" type="text" class="forms" size="15" value="<%=cBean.billingContact%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Billing Phone</td>
                        <td><input name="billingPhone" type="text" class="forms" size="15" value="<%=cBean.billingPhone%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Billing Email</td>
                        <td><input name="billingEmail" type="text" class="forms" size="15" value="<%=cBean.billingEmail%>"></td>
                      </tr>
                      <tr> 
                        <td align="right" valign="top" class="blueMain">Web URL</td>
                        <td class="redMain"> <input name="web_URL" type="text" class="forms" size="26" value="<%=aBean.web_URL%>"> 
                          <br>
                          example: www.site.com</td>
                      </tr>
					  <tr> 
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Industry</td>
                        <td> <%=aBean.getIndustrySelect("industry","forms",aBean.industry)%> </td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right" valign="top">Main Trade</td>
                        <td><%=tBean.getTradesNameSelect("main_trade", "blueMain", cBean.main_trade)%></td>
                      </tr>
<!--                      <tr> 
                        <td class="blueMain" align="right" valign="top">All Trades</td>
                        <td valign="top" class="redMain"> <%//=tBean.getTradesMultipleSelect("trades", "blueMain", cBean.trades)%><br>
                          Hold down 'CTRL' key to select multiple<br>
                          <br>
                        </td>
                      </tr>
-->                    <tr> 
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" valign="top" align="right">Logo</td>
                        <td class="blueMain"><input name="logo_file" type="FILE" class="forms" size="15">
                          (.gif/jpg file)<br>
						  <span class="redMain">Please limit the image size to 240 x 240 <br>pixels for optimal displaying</span></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Co. Brochure</td>
                        <td class="blueMain"><input name="brochure_file" type="FILE" class="forms" size="15">
                          (.pdf file)</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right" valign="top">Description</td>
                        <td><textarea name="description" cols="32" rows="6" class="forms"><%=cBean.description%></textarea></td>
                      </tr>
                    <tr> 
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Password</td>
                        <td><input name="password" type="text" class="forms" size="15" value="<%=aBean.password%>"></td>
                      </tr>
                      <tr> 
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">&nbsp;</td>
                        <td><input name="submit" type="submit" class="forms" value="submit"></td>
                      </tr>
                    </table>
                    <br> </td>
                </tr>
              </table>
</body>
</html>
