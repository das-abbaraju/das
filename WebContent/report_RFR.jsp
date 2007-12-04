<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ include file="utilities/adminGeneral_secure.jsp" %>

<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="session"/>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AccountsQuestionBean" scope ="page"/>
<jsp:useBean id="ctBean" class="com.picsauditing.PICS.CriteriaBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope ="page"/>
<jsp:useBean id="lBean" class="com.picsauditing.PICS.LightBean" scope ="page"/>

<%	try{
	boolean showAll = false;
	sBean.orderBy = "Name";
	sBean.doSearch(request, sBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
	if (!pBean.isAdmin())
		sBean.setCanSeeSet((java.util.HashSet)session.getAttribute("canSeeSet"));
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
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_RedFlagsReport.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
		<td colspan="3" align="center" class="blueMain">
          <table width="657" border="0" cellpadding="0" cellspacing="0">
            <tr> 
              <td height="70" colspan="2" align="center"><%@ include file="includes/selectReport.jsp"%>
                </td>
            </tr>
          </table>
<%	int thisYear = com.picsauditing.PICS.DateBean.getCurrentYear(); %>
		  <form name="form1" method="post" action="report_EMRRates.jsp">
              <br>
              <br><%=sBean.getLinks()%>
              <table width="657" border="0" cellpadding="1" cellspacing="1">
                <tr bgcolor="#003366" class="whiteTitle"> 
                  <td>Contractor</td>
                  <td>Score %</td>
                </tr>
                <%	while (sBean.isNextRecord()) { %>
                <tr <%=sBean.getBGColor()%> class="greenMain"> 
				 <%	String oid = session.getAttribute("userid").toString();	
				  		String id = sBean.aBean.id;	
						String conID = id;
						String color2 = "inactive";
							int green = 95;
							int yellow = 90;
						//id is changing	
						aqBean.setFromDB(oid);//se encarga de buscar las preguntas
						aBean.setFromDB(id);
						cBean.setFromDB(id);
						pdBean.setFromDB(conID,"0");
						int count = 0;
						int max = 0;
						int countQuestions = 0;
						int countOk = 0;
				  for (java.util.ListIterator li=aqBean.accounts.listIterator();li.hasNext();) {
						String Accounts = (String)li.next();
						String Questions = (String)li.next();
						String Weights = (String)li.next();
						String Criterias_id = (String)li.next();
						String Criteria = (String)li.next();
						ctBean.setFromDB(Criterias_id);
						lBean.setFromDB(oid);
						String criteria_desc = ctBean.description;
						if (!lBean.green.equals("")){
							green = java.lang.Integer.parseInt(lBean.green);
							yellow =  java.lang.Integer.parseInt(lBean.yellow);
						}
				  max = max + java.lang.Integer.parseInt(Weights);
				  if (pdBean.getFlag(Questions,criteria_desc,Criteria).equals("<img src=images/okCheck.gif width=19 height=15 alt='Approved'>")){
						countOk = countOk + java.lang.Integer.parseInt(Weights);
						}//if
						countQuestions++;												
					} // for
					if (max==0) max=1;
					//if (countOk==0) countOk=1;
						if ((countOk*100/max)>green) { color2="active";%>
                          <%} else if ((countOk*100/max)>yellow) { color2="warning";%>
                          <%} else {color2="inactive";%> 
                        <%} // color%>
                  <td> <a href="pqf_redFlags.jsp?id=<%=sBean.aBean.id%>&catID=10" class="<%=color2%>" title="view <%=sBean.aBean.name%> details" target="_self"><%=sBean.aBean.name%></a></td>
                 
                  <td><strong><font class ="<%=color2%>">
				 
				  <%=countOk*100/max%><%="%"%>
                    </font></strong> </td>
                 
                </tr>
                <%	} // while %>
              </table>
              <br>
			  <%	sBean.closeSearch(); %>
		  </form>
		  <center><%=sBean.getLinks()%></center>
			<%	sBean.closeSearch(); %>
		  </td>
            <td>&nbsp;</td>
        </tr>
      </table>
      <br><center><%@ include file="utilities/contractor_key.jsp"%></center><br><br>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
</body>
</html>
<%	}finally{
		sBean.closeSearch();
	}//finally
%>