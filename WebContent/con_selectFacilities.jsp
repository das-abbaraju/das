<%//@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ page language="java" import="com.picsauditing.PICS.*"%>
<%@ include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OperatorBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope ="page"/> 
<jsp:useBean id="FACILITIES" class="com.picsauditing.PICS.Facilities" scope ="application"/>

<%	String id = request.getParameter("id");
	cBean.setFromDB(id);
	aBean.setFromDB(id);
	boolean isSubmitted = (null != request.getParameter("submit.x"));
	boolean removeContractor = ("Remove".equals(request.getParameter("action")));
	if (isSubmitted){
		cBean.setGeneralContractorsFromCheckList(request);
		if (cBean.writeGeneralContractorsToDB(pBean,FACILITIES)){
			pcBean.generateDynamicCategories(id, com.picsauditing.PICS.pqf.Constants.PQF_TYPE, cBean.riskLevel);
			cBean.setPercentComplete(com.picsauditing.PICS.pqf.Constants.PQF_TYPE,pdBean.getPercentComplete(id,com.picsauditing.PICS.pqf.Constants.PQF_TYPE));
			cBean.canEditPrequal="Yes";
			cBean.writeToDB();
			EmailBean.sendUpdateDynamicPQFEmail(id);
		}//if
		if (pBean.isCorporate())
			pBean.setCanSeeSet(pBean.oBean.getFacilitiesCanSeeSet());
		if (pBean.isContractor()) {
			response.sendRedirect("pqf_editMain.jsp?auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE+"&mustFinishPrequal=&id="+aBean.id);
			return;
		}//if
	}//if
	if (pBean.isAdmin() && removeContractor){
		String removeOpID = request.getParameter("opID");
		oBean.removeSubContractor(removeOpID,id);
		AccountBean tempOpBean = new AccountBean();
		tempOpBean.setFromDB(removeOpID);
		cBean.addNote(id,"("+pBean.userName+" from PICS)", "Removed "+aBean.name+" from "+tempOpBean.name+"'s db", DateBean.getTodaysDateTime());
		pcBean.generateDynamicCategories(id,com.picsauditing.PICS.pqf.Constants.PQF_TYPE, cBean.riskLevel);
		cBean.setPercentComplete(com.picsauditing.PICS.pqf.Constants.PQF_TYPE,pdBean.getPercentComplete(id,com.picsauditing.PICS.pqf.Constants.PQF_TYPE));
		cBean.writeToDB();
	}//if
	cBean.setFromDB(id);
	java.util.ArrayList operators = oBean.getOperatorsAL();
	int count = 0;
%>
<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#EEEEEE" background="images/watermark.gif" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
 	  <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_editAccount.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
		<tr> 
          <td>&nbsp;</td>
          <td colspan="3" align="center"> 
			<table width="657" border="0" cellpadding="0" cellspacing="0">
              <tr align="center" class="blueMain">
                <td align="left"><%@ include file="includes/nav/secondNav.jsp"%></td>
              </tr>
            </table>
            <form name="form1" method="post" action="con_selectFacilities.jsp?id=<%=id%>">
              <table width="0" border="0" cellspacing="0" cellpadding="1">
                <tr>
                  <td class="redMain" align="center">
<%	if (pBean.isContractor() || pBean.isAdmin()){%>
				    PICS membership pricing table:
                    <table border="1" cellspacing="1" cellpadding="1">
                      <tr class="whiteTitle">
                        <td bgcolor="#993300" align="center"># of Facilities</td>
                        <td bgcolor="#003366" align="center">Price/yr</td>
                      </tr>
	                  <tr class=blueMain <%=Utilities.getBGColor(count++)%> align="center">
	                    <td>Exempt</td>
	                    <td>$99</td>
	                  </tr>
                      <tr class=blueMain <%=Utilities.getBGColor(count++)%> align="center">
                        <td>1</td>
                        <td>$<%=Billing.calcBillingAmount(1,this.getServletContext())%></td>
                      </tr>
                      <tr class=blueMain <%=Utilities.getBGColor(count++)%> align="center">
                        <td>2-4</td>
                        <td>$<%=Billing.calcBillingAmount(4,this.getServletContext())%></td>
                      </tr>
                      <tr class=blueMain <%=Utilities.getBGColor(count++)%> align="center">
                        <td>5-8</td>
                        <td>$<%=Billing.calcBillingAmount(8,this.getServletContext())%></td>
                      </tr>
                      <tr class=blueMain <%=Utilities.getBGColor(count++)%> align="center">
                        <td>9-12</td>
                        <td>$<%=Billing.calcBillingAmount(12,this.getServletContext())%></td>
                      </tr>
                      <tr class=blueMain <%=Utilities.getBGColor(count++)%> align="center">
                        <td>13-19</td>
                        <td>$<%=Billing.calcBillingAmount(19,this.getServletContext())%></td>
                      </tr>
                      <tr class=blueMain <%=Utilities.getBGColor(count++)%> align="center">
                        <td>20+</td>
                        <td>$<%=Billing.calcBillingAmount(20,this.getServletContext())%></td>
                      </tr>
                    </table>
	               <br><span class="redMain">* There is an account activation fee of $99 for new account registration, and $199 for reactivation</span>
                   <br>
				   <b>Please select all facilities
                    where you work<br>(Currently <%=cBean.facilitiesCount%>)
<!--					<a href="#pricing"class=blueMain>See pricing table below</a>)
-->                  </b><br><br>
<%	}//if
	if (pBean.isAuditor()){%>
				   <strong><%=aBean.name%>'s</strong> facilities:<br><br>
				   
<%	}//if
	if (pBean.isAdmin() || pBean.isCorporate()){%>
				    Assign <strong><%=aBean.name%></strong> to the following facilities:<br><br>
<%	}//if%>				
                    <table border="0" cellpadding="1" cellspacing="1">
                      <tr bgcolor="#003366" class="whiteTitle">
                        <td>Facility</td>
                        <td></td>
                        <td>Status</td>
<%	if (pBean.isAdmin()){%>
                        <td></td>
<%	}//if%>
					  </tr>
<%	count = 0;
	for (java.util.ListIterator li = operators.listIterator();li.hasNext();) {
		String opID = (String)li.next();
		String name = (String)li.next();
		String status = "";
		if (pBean.isCorporate() && !pBean.oBean.facilitiesAL.contains(opID)){%>
                       <input type=hidden name=genID_<%=opID%> value=Yes>
<%//		}else if(!(pBean.isOperator() && !pBean.oBean.facilitiesAL.contains(opID))){%>
<%		}else{%>
					  <tr class=blueMain <%=Utilities.getBGColor(count++)%>>
					    <td><%=name%></td>
						<td align="center">
<%			if (cBean.generalContractors.contains(opID)){
 				oBean.setFromDB(opID);
				status = cBean.calcPICSStatusForOperator(oBean);
%>                         <img src=images/okCheck.gif width=19 height=15>
                          <input type=hidden name=genID_<%=opID%> value=Yes>
<%			}//if
			if (!pBean.isAuditor() && !cBean.generalContractors.contains(opID)){%>
				          <%=Inputs.getCheckBoxInput("genID_"+opID,"forms","","Yes")%>
<%				status = "";
			}//else
%>
					    </td>
						<td class="<%=cBean.getTextColor(status)%>"><%=status%></td>
                        <td>
<%			if (pBean.isAdmin() && cBean.generalContractors.contains(opID)){%>
                          <a href=con_selectFacilities.jsp?id=<%=id%>&action=Remove&opID=<%=opID%>>Remove</a>
<%			}//if%>
                        </td>
					  </tr>
<%		}//else
	}//for
%>
					</table>
				  </td>
                </tr>
                <tr> 
                  <td>&nbsp;</td>
                </tr>
<%	if(pBean.isCorporate()|| pBean.isContractor()|| pBean.isAdmin()|| pBean.isOperator()){%>
                <tr> 
                  <td align="center"><input name="submit" type="image" src="images/button_submit.gif" value="submit"></td>
                </tr>
<%	}//if%>
              </table>
            </form>
          </td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
<%@ include file="includes/statcounter.jsp" %>
</body>
</html>
