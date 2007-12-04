<script language="JavaScript" type="text/JavaScript">
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
<%	String navString = "";
	String whichPage = "";
	if (pBean.isAdmin())
		navString = "accounts_manage,ManageAccounts,manage_forms,ManageFormsTrade,report_auditors,Audits,reports,Reports";
	else if (pBean.isOperator() || pBean.isCorporate()){
		if (pBean.userAccess.hasAccess(com.picsauditing.access.OpPerms.StatusOnly))
			navString = "contractor_list_limited,ContractorList";
		else
			navString = "reports,Reports,contractor_list,ContractorList,services,Services,clients,Clients,contact,Contact";
	} else if (pBean.isContractor()) {
		navString = "contractor_new_instructions,Register,contractor_detail,Contractors,services,Services,clients,Clients,contact,Contact";
		whichPage = "Contractors";
	} else if (pBean.isAuditor()) {
		navString = " , ";
	} else {
		navString = "contractor_new_instructions,Register,contractor_detail,Contractors,services,Services,clients,Clients,contact,Contact";
	} //else
	String [] LINKS_ARRAY = navString.split(",");
%>
<table border="0" cellspacing="0" cellpadding="0" BGCOLOR="#EEEEEE">
  <tr align="center" valign="top">
<%	boolean firstSquare = true;
	int thiscount = 0;
	for (int i=0;i<LINKS_ARRAY.length;i+=2) {
		if (!" ".equals(LINKS_ARRAY[i])) {
			if (!firstSquare) {
%> <td width=1><img src=../images/spacer.gif width=1></td>
<%			}//if %>
    <td width="72"><%
			if (LINKS_ARRAY[i+1].equals(whichPage)) { 
    %><img src="images/square<%=LINKS_ARRAY[i+1]%>_2.gif" height="72" border="0"><%			} else {
    %><a href="<%=LINKS_ARRAY[i]%>.jsp"><img name ="square<%=LINKS_ARRAY[i+1]%>" height="72" src="images/square<%=LINKS_ARRAY[i+1]%>.gif" border="0" onMouseOver="MM_swapImage('square<%=LINKS_ARRAY[i+1]%>','','images/square<%=LINKS_ARRAY[i+1]%>_2.gif',1)" onMouseOut="MM_swapImgRestore()"></a><%			} //else 
  %></td>
<%			firstSquare = false;
			thiscount +=1;
		}//if
	} //for
	for (int i=thiscount;i<5;i+=1) {
%>		<td width="1"><img src="images/spacer.gif" width="1"></td>
		<td width="72" BGCOLOR="#003366"><img src="images/spacer.gif" width="72"></td>
<%	} //for %>
  </tr>
</table>