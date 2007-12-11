<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%//@ page language="java" import="com.picsauditing.PICS.*"%>
<%@include file="utilities/contractor_secure.jsp"%>
<%	int count = 0;
	ContractorBean c = new ContractorBean();
%>

<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#EEEEEE" background="images/watermark.gif" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
  <table border="1" cellspacing="1" cellpadding="1">
	<tr class="whiteTitle">
	  <td colspan="3" bgcolor="#993300" align="center">PICS Annual Membership Price</td>
	</tr>
	<tr class="whiteTitle">
	  <td bgcolor="#993300" align="center">Facilities</td>
	  <td bgcolor="#003366" align="center">Audited</td>
	  <td bgcolor="#003366" align="center"><nobr>PQF-Only</nobr></td>
	</tr>
	<tr class=blueMain <%=Utilities.getBGColor(count++)%> align="center">
	  <td>1</td>
	  <td>$<%=Billing.calcBillingAmount(1)%></td>
	  <td>$99</td>
	</tr>
	<tr class=blueMain <%=Utilities.getBGColor(count++)%> align="center">
	  <td>2-4</td>
	  <td>$<%=Billing.calcBillingAmount(4)%></td>
	  <td>$99</td>
	</tr>
	<tr class=blueMain <%=Utilities.getBGColor(count++)%> align="center">
	  <td>5-8</td>
	  <td>$<%=Billing.calcBillingAmount(8)%></td>
	  <td>$99</td>
	</tr>
	<tr class=blueMain <%=Utilities.getBGColor(count++)%> align="center">
	  <td>9-12</td>
	  <td>$<%=Billing.calcBillingAmount(12)%></td>
	  <td>$99</td>
	</tr>
	<tr class=blueMain <%=Utilities.getBGColor(count++)%> align="center">
	  <td>13-19</td>
	  <td>$<%=Billing.calcBillingAmount(19)%></td>
	  <td>$99</td>
	</tr>
	<tr class=blueMain <%=Utilities.getBGColor(count++)%> align="center">
	  <td>20+</td>
	  <td>$<%=Billing.calcBillingAmount(20)%></td>
	  <td>$99</td>
	</tr>
  </table>
	  <br><span class="redMain">* There is an account activation fee of $99 for new account registration, and $199 for reactivation</span>
</body>
</html>
