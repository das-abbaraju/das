<%//@ page language="java" errorPage="exception_handler.jsp"%>
<%@ page language="java" %>
<%@ include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OSHABean" scope ="page"/>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AccountsQuestionBean" scope ="page"/>
<jsp:useBean id="ctBean" class="com.picsauditing.PICS.CriteriaBean" scope ="page"/>
<jsp:useBean id="lBean" class="com.picsauditing.PICS.LightBean" scope ="page"/>
<%
	//11/29/05 jj report created
	int green = 95;
	int yellow = 90;
	String color2 = "inactive";
	String conID = request.getParameter("id");
	String id = request.getParameter("id");
	String oid = session.getAttribute("userid").toString();
	aqBean.setFromDB(oid);//se encarga de buscar las preguntas
	aBean.setFromDB(conID);
	cBean.setFromDB(conID);
	pdBean.setFromDB(conID,"0");

	int count = 0;
	int max = 0;
	int countQuestions = 0;
	int countOk = 0;
		
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
          <td valign="top" align="center"><img src="images/header_RedFlagsReport.gif" width="321" height="72" border="0"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td colspan="3" align="center">
			<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
              <tr align="center" class="blueMain">
			    <td width="721">
  <%@ include file="includes/nav/secondNav.jsp"%>
				</td>
			  </tr>
    		  <tr align="center" class="blueMain">
                <td class="blueHeader">PQF Snapshot for <%=aBean.name%></td>
    		  </tr>
	  		  <tr align="center">
                <td class="blueMain">Date Submitted: <span class="redMain"><strong><%=cBean.pqfSubmittedDate%></strong></span></td>
    		  </tr>
	  	      <tr align="center">
      	      </tr>
    		  <tr align="center" class="blueMain">
                <td class="redMain">&nbsp;</td>
   			  </tr>
  			  <tr align="center">
				<td align="left">
  				  <table width="657" border="0" cellpadding="1" cellspacing="1">
				     <%	for (java.util.ListIterator li=aqBean.accounts.listIterator();li.hasNext();) {
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
						}
						
						countQuestions++;												
					}//for%>
                    <%if (countQuestions==0) countQuestions=1;%>
                    <tr align="left" class="blueMain"> 
                      <td colspan="5" bgcolor="#DDDDDD"><div align="right"><strong> 
                          <%if (max==0) {
						     max=1;
						  }
						  if ((countOk*100/max)>green) { 
						  color2="active";
                          } else if ((countOk*100/max)>yellow) {
						  color2="warning";
                          } else {
						  color2="inactive";
						  }%>
                          * <%=aBean.name%> score is <font class=<%=color2%>><%=countOk*100/max%>%</font></strong></div></td>
                      
                    </tr>

                    <tr class="whiteTitle">
                      <td width="280" align="center" bgcolor="#003366">Question</td>
					  <td width="59" align="center" bgcolor="#003366">Weight</td>
                      <td width="49" align="center" bgcolor="#003366">Criteria</td>
                      <td width="209" align="center" bgcolor="#003366">Answer</td>
                      <td width="24" align="center" bgcolor="#003366">OK?</td>
					</tr>  
<%					for (java.util.ListIterator li=aqBean.accounts.listIterator();li.hasNext();) {
						String Accounts = (String)li.next();
						String Questions = (String)li.next();
						String Weights = (String)li.next();
						String Criterias_id = (String)li.next();
						String Criteria = (String)li.next();						
						ctBean.setFromDB(Criterias_id);
						
						String criteria_desc = ctBean.description;
						max = max + 10;
%>                   
					<tr class="blueMain" <%=com.picsauditing.PICS.Utilities.getBGColor(count)%>>
					  <td><%=pqBean.getQuestion(Questions)%></td>
					  <td><%=Weights%></td>
                      <td><%=criteria_desc%> <%=Criteria%></td>					  
                      <td><a href=pqf_view.jsp?id=<%=conID%>&catID=<%=pqBean.getCategoryID(Questions)%> target=_blank><%=pdBean.getAnswer(Questions)%></a></td>
                      <td><%=pdBean.getFlag(Questions,criteria_desc,Criteria)%></td>                    
                    </tr>
<%			}//for
%>					  
					<tr align="left" class="blueMain"> 
						
                      <td colspan="6" bgcolor="#003366">&nbsp;</td>
			  		</tr>
                  </table>
				</td>
		      </tr>
			</table>
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
</body>
</html>