<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="utilities/contractor_edit_secure.jsp"%>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.contractorBean" scope ="page"/>
<jsp:useBean id="vBean" class="com.picsauditing.PICS.VerifyPDFsBean" scope ="page"/>
<%
	String showForm = request.getParameter("showForm");
/*	String id = request.getParameter("id");
	String ses_id = (String)session.getAttribute("userid");
	boolean mustFinishPrequal = (request.getParameter("mustFinishPrequal") != null);
	boolean justSubmitted = (request.getParameter("submit") != null);
	cBean.setFromDB(id);
	if (!isAdmin && !cBean.canEditPrequal()) {
		response.sendRedirect("/login.jsp");
		return;
	}//if
	vBean.verifyForms(id, config);
	if (justSubmitted && vBean.isInfoOK)
		cBean.submitPrequal(id);
*/%>
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
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_prequalification.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        </table>
		 <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
        <tr> 
          <td>&nbsp;</td>
		<td align="center" class="redMain">
<%@ include file="includes/prequal/nav.jsp"%>
Items in red are required. Type "N/A" for 
non-applicable items.<br>
Please do not use your browser's back button on 
these pages.<br>Use the Previous Page button on the form, if there is one.

<%	if ("1".equals(showForm)) {%>
<%@ include file="includes/prequal/form1.jsp"%>
<%	} else if ("2".equals(showForm)) {%>
<%@ include file="includes/prequal/form2.jsp"%>
<%	} else if ("3".equals(showForm)) {%>
<%@ include file="includes/prequal/form3.jsp"%>
<%	} else if ("4".equals(showForm)) {%>
<%@ include file="includes/prequal/form4.jsp"%>
<%	} else if ("5".equals(showForm)) {%>
<%@ include file="includes/prequal/form5.jsp"%>
<%	} else if ("6".equals(showForm)) {%>
<%@ include file="includes/prequal/form6.jsp"%>
<%	} else if ("7".equals(showForm)) {%>
<%@ include file="includes/prequal/form7.jsp"%>
<%	} else if ("8".equals(showForm)) {%>
<%@ include file="includes/prequal/form8.jsp"%>
<%	} else if ("9".equals(showForm)) {%>
<%@ include file="includes/prequal/form9.jsp"%>
<%	} else if ("10".equals(showForm)) {%>
<%@ include file="includes/prequal/form10.jsp"%>
<%	} else if ("11".equals(showForm)) {%>
<%@ include file="includes/prequal/form11.jsp"%>
<%	} else if ("12".equals(showForm)) {%>
<%@ include file="includes/prequal/form12.jsp"%>
<%	} else if ("13".equals(showForm)) {%>
<%@ include file="includes/prequal/form13.jsp"%>
<%	} else if ("14".equals(showForm)) {%>
<%@ include file="includes/prequal/form14.jsp"%>
<%	} else if ("15".equals(showForm)) {%>
<%@ include file="includes/prequal/form15.jsp"%>
<%	} else if ("16".equals(showForm)) {%>
<%@ include file="includes/prequal/form16.jsp"%>
<%	} else if ("17".equals(showForm)) {%>
<%@ include file="includes/prequal/form17.jsp"%>
<%	} else if ("18".equals(showForm)) {%>
<%@ include file="includes/prequal/form18.jsp"%>
<%	} else if ("19".equals(showForm)) {%>
<%@ include file="includes/prequal/form19.jsp"%>
<%	} else if ("20".equals(showForm)) {%>
<%@ include file="includes/prequal/form20.jsp"%>
<%	} else if ("21".equals(showForm)) {%>
<%@ include file="includes/prequal/form21.jsp"%>
<%	} //else%>

          </td>
            <td>&nbsp;</td>
        </tr>
      </table>
        <br>
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
