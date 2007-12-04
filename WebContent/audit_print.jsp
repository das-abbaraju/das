<%//@ page language="java" errorPage="exception_handler.jsp"%>
<%@ page language="java"%>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AuditQuestionBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<%
try{
	boolean isContractor = "Contractor".equalsIgnoreCase((String)session.getAttribute("usertype"));
	String orderby = request.getParameter("orderby");
	String showReq = request.getParameter("showReq");
	if (showReq == null)
		showReq = "";
%>
<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body vlink="#003366" alink="#003366" leftmargin="4" topmargin="6" marginwidth="4" marginheight="4">
  <table width="670" height="100%" border="0" cellpadding="0" cellspacing="0">
    <tr align="center" class="blueMain">
    <td class="blueHeader">PICS Office Audit<br>
<%	if (showReq.equals("true")) { %>
	    <a href="?showReq=false"  class="redmain">Hide Requirements</a>
<%	} else { %>	
		<a href="?showReq=true"  class="redmain">Show Requirements</a>
<%	}//else %>
      </td>
	</tr>
	<tr>
	  <td class="blueMain">
	    <strong>Contractor Audited:<br>
	      Date:</strong>
	  </td>
	</tr>
	<tr align="center">
	  <td>
		<table width="657" border="1" bordercolor="#003366" cellpadding="1" cellspacing="0">
          <tr class="active"> 
            <td width="30"><font color="#000000"><strong>#</strong></font></td>
            <td><font color="#000000"><strong>Question</strong></font></td>
<%	if (isContractor) { %>
			<td colspan="3">NOTES</td>
<%	} else { %>
			<td><font color="#000000"><strong>YES</strong></font></td>
			<td><font color="#000000"><strong>NO</strong></font></td>
			<td><font color="#000000"><strong>NA</strong></font></td>
<%	}//else %>
          </tr>
<%	aqBean.setList(orderby,"Office");
	while (aqBean.isNextRecord()) {
%>
          <tr class="blueMain" <%=aqBean.getBGColor()%>> 
		    <td valign="top"><%=aqBean.num%></td>
			<td>(<%=aqBean.getCategoryName()%>) <%=aqBean.question%> <%=aqBean.getLinksShow()%></td>
<%		if (isContractor) { %>
			<td colspan="3">&nbsp;</td>
<%		} else { %>	
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
<%		}//else %>
		  </tr>
<%		if (showReq.equals("true")) { 
			if (!"".equals(aqBean.getAllRequirements())) { %>
		  <tr class="redMain" <%=aqBean.getBGColor()%>> 
			<td valign="top"><nobr>Req:</nobr></td>
			<td colspan="4"><strong><%=aqBean.getAllRequirements()%></strong></td>
		  </tr>
<%			} //if "" <> req
		} //if showReq
	}//while
%>				
        </table>
        <br><br><br>
	  </td>
    </tr>
  </table>
</body>
</html>
<%	}finally{
		aqBean.closeList();
	}//finally
%>