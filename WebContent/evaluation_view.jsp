<%@ page language="java" import="com.picsauditing.PICS.*"%>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope ="session"/>
<%@ include file="includes/evaluation_data.jsp" %>
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

          <td width="364"><script language="JavaScript" type="text/JavaScript">
<!--
function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
//-->
</script>

<table border="0" cellspacing="0" cellpadding="0" BGCOLOR="#EEEEEE">
  <tr align="center" valign="top">

    <td width="72"><a href="reports.jsp"><img name ="squareReports" height="72" src="images/squareReports.gif" border="0" onMouseOver="MM_swapImage('squareReports','','images/squareReports_2.gif',1)" onMouseOut="MM_swapImgRestore()"></a></td>
 <td width=1><img src=../images/spacer.gif width=1></td>

    <td width="72"><a href="contractor_list.jsp"><img name ="squareContractorList" height="72" src="images/squareContractorList.gif" border="0" onMouseOver="MM_swapImage('squareContractorList','','images/squareContractorList_2.gif',1)" onMouseOut="MM_swapImgRestore()"></a></td>
 <td width=1><img src=../images/spacer.gif width=1></td>

    <td width="72"><a href="services.jsp"><img name ="squareServices" height="72" src="images/squareServices.gif" border="0" onMouseOver="MM_swapImage('squareServices','','images/squareServices_2.gif',1)" onMouseOut="MM_swapImgRestore()"></a></td>
 <td width=1><img src=../images/spacer.gif width=1></td>

    <td width="72"><a href="clients.jsp"><img name ="squareClients" height="72" src="images/squareClients.gif" border="0" onMouseOver="MM_swapImage('squareClients','','images/squareClients_2.gif',1)" onMouseOut="MM_swapImgRestore()"></a></td>
 <td width=1><img src=../images/spacer.gif width=1></td>

    <td width="72"><a href="contact.jsp"><img name ="squareContact" height="72" src="images/squareContact.gif" border="0" onMouseOver="MM_swapImage('squareContact','','images/squareContact_2.gif',1)" onMouseOut="MM_swapImgRestore()"></a></td>

  </tr>
</table></td>

          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center">&nbsp;</td>
          <td valign="top"><script language="JavaScript">
  var j=parseInt(Math.random()*5);
  j=(isNaN(j))?1:j+1;
  document.write("<img useMap='#Map' border='0' hspace='1' src= 'images/squareLogin_" + j + ".gif'>");
</script>
<map name="Map">
  <area shape="rect" coords="73,4,142,70" href="logout.jsp">

</map></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td colspan="3" align="center">
            <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
              <tr align="center" class="blueMain">
                <td width="676">

		
  <center>

	<div class="blueHeader">APAC-Texas, Inc.</div>


<a class=blueMain href=/contractor_detail.jsp?id=1316>Contractor Details</a>
			| <a class=blueMain href=/certificates_view.jsp?id=1316>Insurance Certificates</a>

			| <a class=blueMain href=/con_redFlags.jsp?id=1316>Red Flag Report</a>

<br>

			<a class=blueMain href=/pqf_view.jsp?auditType=PQF&id=1316>View PQF</a>
			| <a class=blueMain href=/pqf_viewAll.jsp?auditType=PQF&id=1316>View Entire PQF</a>
			| <a class=blueMain href=/pqf_printAll.jsp?auditType=PQF&id=1316>Print PQF</a><br>

					<a class=blueMain href=/audit_view.jsp?auditType=Office&id=1316>View Desktop Audit</a>

				| <a class=blueMain href=/audit_view.jsp?id=1316>View Office Audit</a>
				| <a class=blueMain href=/audit_viewRequirements.jsp?id=1316>View Office Audit RQs</a><br>

  </center>
</td>
              </tr>
              <tr align="center" class="blueMain">
                <td class="blueHeader">Contractor Evaluation for Holcim Devil's Slide</td>
              </tr>
              <tr align="center">
                <td class="blueMain">Date Submitted: <strong>11/5/07</strong></td>
              </tr>


              <tr align="center">
                <td class="blueMain">
                </td>
              </tr>
              <tr align="center">
                <td align="left">
                  <table width="657" border="0" cellpadding="1" cellspacing="1">
                    <tr class="blueMain">
                      <td bgcolor="#003366" colspan=3 align="center"><font color="#FFFFFF"><strong>Category 1 - CONTRACTOR PERFORMANCE</strong></font></td>
                    </tr>
                    <tr class="blueMain">
                      <td colspan=3 align="center">Percent Complete: 100%<img src="images/okCheck.gif" width=19 height=15 alt='Not Complete'></td>
                    </tr>
<%
for(ContractorEvaluationCategory category: evalBean.getCategories()) {
	%>
	<tr class="blueMain">
		<td bgcolor="#003366" colspan="3" align="center"><font color="#FFFFFF"><strong>Sub Category <%=category.getDisplayOrder() %> - <%=category.getName() %></strong></font></td>
	</tr>
	<%
	for(ContractorEvaluationQuestion question: category.getQuestions() ) {
		%>
		<tr bgcolor="<%=evalBean.getColor()%>" class="blueMain">
			<td valign="top" width="1%"><%=category.getDisplayOrder() %>.<%=question.getId()%></td>
			<td valign="top"><%=question.getQuestion()%></td>
			<td><strong><%=question.getAnswer()%></strong></td>
		</tr>
		<%
	}
}
%>
                  </table>
                </td>
              </tr>

            </table>
            
            <a href="evaluation_edit.jsp?id=34" class="blueMain">Edit Evaluation</a> | <a href="eval_report.jsp" class="blueMain">View Evaluations</a> </td>

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
