<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%//@ page language="java" import="com.picsauditing.PICS.*"%>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="pBean" class="com.picsauditing.access.PermissionsBean" scope ="session"/>

<%	String id = request.getParameter("id");
	String ses_id = (String)session.getAttribute("temp_userid");
	if ((null == ses_id) || !ses_id.equals(id)) {
		response.sendRedirect("logout.jsp");
		return;
	}//if
	cBean.setFromDB(id);
	boolean isSubmitted = (null != request.getParameter("submit.x"));
//	boolean isSelected = (null != request.getParameter("billingCycle"));
//	if (isSubmitted && isSelected) {
	if (isSubmitted) {
		session.removeAttribute("temp_userid");
		session.setAttribute("userid",ses_id);
		
//		String billingCycle = request.getParameter("billingCycle");
//		int billingAmount = cBean.calcBillingAmount(cBean.facilitiesCount,billingCycle,"No");
//		if ("E".equals(billingCycle)) {
//			billingAmount = 225;
//			billingCycle = "3";
//			cBean.isExempt="Yes";
//		}//if
		cBean.accountDate = DateBean.getTodaysDate();
// set these when account created
//		cBean.billingCycle = billingCycle;
//		cBean.billingAmount = Integer.toString(billingAmount);
		cBean.writeToDB();
		response.sendRedirect("con_selectFacilities.jsp?id=" + id);
		return;		
	}//if
//	String errMsg = "";
//	if (isSubmitted && !isSelected)
//		errMsg = "Please select your membership subscription";
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
          <td valign="top" align="center"><img src="images/header_welcome.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
		<tr> 
          <td>&nbsp;</td>
          <td colspan="3" align="center"> 
            <form name="form1" method="post" action="con_firstLoginInfo.jsp?id=<%=id%>">
              <table width="0" border="0" cellspacing="0" cellpadding="1">
                <tr> 
                  <td class="redMain"><strong><%//=errMsg%></strong></td> 
                </tr>
                <tr> 
                  <td class="blueMain" align="left">
<!--				  <span class="blueHeader">PICS 
                    Membership</span><br>
					Please choose a subscription time period (<a href=con_pricing.jsp target=_blank class=blueMain>See pricing table</a>)<br>
					<input name=billingCycle type=radio value=3>
                    $<%//=cBean.calcBillingAmount(cBean.facilitiesCount,"3","No", this.getServletContext())%>/3 years (discounted to $<%//=cBean.calcBillingAmount(cBean.facilitiesCount,"3","No",this.getServletContext())/3%>/year)<br>
					<input name=billingCycle type=radio value=1> $<%//=cBean.calcBillingAmount(cBean.facilitiesCount,"1","No",this.getServletContext())%>/1 year<br>
                    <input name=billingCycle type=radio value=E> $225/3 years (Exempt from onsite office audit*)<br><br>
                    <span class="redMain">* In order for you to choose exempt, 
                    you must have prior authorization from the requesting facility. 
                    Only contractors such as gardeners, off-site engineers, janitorial, 
                    some consultants, etc, may qualify as exempt. All exempt contractors 
                    must still complete the prequalification forms each year. 
                    </span><br>
                    <br>
 -->                <span class="blueHeader">Thank you for creating a PICS contractor account<br>Your PICS Membership will include:</span>
                    <ul>
                        <li>Three year onsite office audit available for view by the operators</li>
                        <li>Company name listed on the web site</li>
                        <li>Company logo displayed</li>
                        <li>Link to your company&#8217;s web site</li>
                        <li>Link to your company's email address</li>
                        <li>Full page description of your company&#8217;s services</li>
                        <li>Complete company contact information listed</li>
                        <li>Map to your location</li>
                        <li>Listing of <strong>all trades</strong> your company
                          is capable of performing with search options for the
                          operators</li>
                        <li>Safety Manual upload for viewing by the operators</li>
                        <li>Insurance Certificate viewing by operators that subscribe
                          to the service</li>
                        <li>Free safety training courses</li>
                        <li>Company Brochure upload for viewing by the operators</li>
                  </ul>
				  </td>
                </tr>
                <tr> 
                  <td><span class="redMain"><a href="sample_executive.htm" title="Sample PICS Account" target="_blank" class="redMain">View
                  a Sample Page </a> </span></td>
                </tr>
                <tr> 
                  <td class="blueMain" align="left">We will be sending you an
                    invoice shortly with the option to pay by check or
                    credit card.</td>
                </tr>
                <tr> 
                  <td>&nbsp;</td>
                </tr>
                <tr> 
                  <td><input name="submit" type="image" src="images/button_continue.gif" value="submit"></td>
                </tr>
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
