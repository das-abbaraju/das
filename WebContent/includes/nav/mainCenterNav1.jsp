<script language="JavaScript" SRC="js/ImageSwap.js"></script>
<%
String navString = "contractor_new_instructions,Register,login,Contractors,services,Services,clients,Clients,contact,Contact";
String [] LINKS_ARRAY = navString.split(",");
%>
<table border="0" cellspacing="0" cellpadding="0" BGCOLOR="#EEEEEE" valign="top">
  <tr align="center" valign="top">
  <%
  boolean firstSquare = true;
  int count = 0;
  for (int i=0;i<LINKS_ARRAY.length;i+=2) {
     if (!firstSquare)
	 	out.println("<td width=\"1\"><img src=\"../../images/spacer.gif\" width=\"1\"></td>");
	%>
		<td width="72"><% if (i==2*whichPage-2) {%>
		<img src="/images/square<%=LINKS_ARRAY[i+1]%>_<%=whichPage%>.gif" height="72" border="0"></td>
	<% } else { %><a href="<%=LINKS_ARRAY[i]%>.jsp"><img name ="square<%=LINKS_ARRAY[i+1]%>" height="72" src="/images/square<%=LINKS_ARRAY[i+1]%>.gif" border="0" onMouseOver="MM_swapImage('square<%=LINKS_ARRAY[i+1]%>','','images/square<%=LINKS_ARRAY[i+1]%>_<%=whichPage%>.gif',1)" onMouseOut="MM_swapImgRestore()"></a></td>
	<%} //if
		firstSquare = false;
		count +=1;
	} //for
	for (int i=count;i<5;i+=1) {%>
		<td width="72"   BGCOLOR="#003366">
    	</td>
	<%} //for
	%></tr>
	<tr>
	  <td colspan="4"></td>
	  <td height="12">
	    <table cellpadding="0" cellspacing="0" border="0">
		  <tr>
		    <td width="100%"><img src="/images/spacer.gif" width="33" height="12"></td>
		    <td width="1" class="mainBG"><img src="spacer.gif" width="1" height="12"></td>
		    <td width="3" class="brownBG"><img src="spacer.gif" width="3" height="12"></td>
		    <td width="1" class="mainBG"><img src="spacer.gif" width="1" height="12"></td>
		    <td width="100%"><img src="/images/spacer.gif" width="34" height="12"></td>
		  </tr>
		</table>
	  </td>
	  <td colspan="4" height="12"></td>
	</tr>
	<tr>	
	  <td colspan="9">
	    <table cellpadding="0" cellspacing="0" border="0">
		  <tr>
			<td rowspan="4"><img src="/images/spacer.gif" height="1"  width="70" border="0"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="1" width="1"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="1" width="3"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="1" width="1"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="1" width="68"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="1" width="1"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="1" width="3"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="1" width="1"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="1" width="31"></td>
			<td class="brownBG"><img src="/images/spacer.gif" height="1" width="3"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="1" width="34"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="1" width="1"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="1" width="3"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="1" width="1"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="1" width="68"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="1" width="1"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="1" width="3"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="1" width="1"></td>
			<td rowspan="4"><img src="/images/spacer.gif" height="1"  width="70" border="0"></td>
	      </tr>
		  <tr>
			<td class="mainBG"><img src="/images/spacer.gif" height="3" width="1"></td>
			<td class="brownBG" colspan="15"><img src="/images/spacer.gif" height="3" width="4"></td>		
			<td class="mainBG"><img src="/images/spacer.gif" height="3" width="1"></td>
	      </tr>
	      <tr>
			<td class="mainBG"><img src="/images/spacer.gif" height="12" width="1"></td>
			<td class="brownBG"><img src="/images/spacer.gif" height="12" width="3"></td>		
			<td class="mainBG"><img src="/images/spacer.gif" height="12" width="1"></td>
			<td><img src="/images/spacer.gif" height="12" width="68"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="12" width="1"></td>
			<td class="brownBG"><img src="/images/spacer.gif" height="12" width="3"></td>		
			<td class="mainBG"><img src="/images/spacer.gif" height="12" width="1"></td>
			<td colspan="3"><img src="/images/spacer.gif" height="12" width="68"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="12" width="1"></td>
			<td class="brownBG"><img src="/images/spacer.gif" height="12" width="3"></td>		
			<td class="mainBG"><img src="/images/spacer.gif" height="12" width="1"></td>
			<td><img src="/images/spacer.gif" height="12" width="68"></td>
			<td class="mainBG"><img src="/images/spacer.gif" height="12" width="1"></td>
			<td class="brownBG"><img src="/images/spacer.gif" height="12" width="3"></td>		
			<td class="mainBG"><img src="/images/spacer.gif" height="12" width="1"></td>
	      </tr>
	   </table>
	 </td>
   </tr>
   <tr>
	  <td colspan="9" valign="top">
	    <table cellpadding="0" cellspacing="0" valign="top">
		  <tr>
			<td><img src="/images/spacer.gif" height="55" width="40" border="0"></td>
			<td>
			  <table cellpadding="0" cellspacing="0" valign="top">
			    <td><img src="/images/spacer.gif" height="1" width="30" border="0"></td>
			    <td class="mainBG"><img src="/images/spacer.gif" height="55" width="1"></td>
			    <td class="brownBG"><img src="/images/spacer.gif" height="55" width="3"></td>		
			    <td class="mainBG"><img src="/images/spacer.gif" height="55" width="1"></td>
				<td  valign="top"><img src="/images/spacer.gif" height="1" width="30" border="0"></td>
			</table></td>
			<td><img src="/images/spacer.gif" height="1"  width="8" border="0"></td>
			<td  valign="top"><img src="/images/sqServices_2.gif "border="0"  name="sqServices_2" onMouseOver="MM_swapImage('sqServices_2','','/images/sqServices_2B.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
			<td><img src="/images/spacer.gif" height="1"  width="8" border="0"></td>
			<td  valign="top"><img src="/images/sqServices_3.gif" border="0" name="sqServices_3" onMouseOver="MM_swapImage('sqServices_3','','/images/sqServices_3B.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
			<td><img src="/images/spacer.gif" height="1"  width="8" border="0"></td>
			<td  valign="top"><img src="/images/sqServices_4.gif" border="0" name="sqServices_4" onMouseOver="MM_swapImage('sqServices_4','','/images/sqServices_4B.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
			<td><img src="/images/spacer.gif" height="1"  width="40" border="0"></td>
		   </tr>		
		</table>
	 </td>
   </tr>
   <tr>
	  <td colspan="9">
			<table cellpadding="0" cellspacing="0">
				<tr>
					<td><img src="/images/spacer.gif" height="1" width="10" border="0"></td>
					<td><img src="/images/sqServicesMain_1.gif" border="0">
			</td>
			<td>
			</td>
			</table>
			</td>
		</tr>
</table>