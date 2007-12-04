<script language="JavaScript" SRC="js/ImageSwap.js"></script>
<%	String navString = "contractor_new_instructions,Register,login,Contractors,services,Services,clients,Clients,contact,Contact";
	String [] LINKS_ARRAY = navString.split(",");
  	boolean firstSquare = true;
	int count = 0;
%>
<table border="0" cellspacing="0" cellpadding="0" BGCOLOR="#EEEEEE">
  <tr align="center" valign="top">
<%	for (int i=0;i<LINKS_ARRAY.length;i+=2) {
		if (!firstSquare) { %>
    <td width=1><img src=../../images/spacer.gif width=1></td>
<%		}//if %>
    <td width="72">
<%		if (i==2*whichPage-2) { %>
      <img src="/images/square<%=LINKS_ARRAY[i+1]%>_<%=whichPage%>.gif" height="72" border="0"><%="</td>"%>
<%		} else { %>
      <a href="<%=LINKS_ARRAY[i]%>.jsp"><img name ="square<%=LINKS_ARRAY[i+1]%>" height="72" src="/images/square<%=LINKS_ARRAY[i+1]%>.gif" border="0" onMouseOver="MM_swapImage('square<%=LINKS_ARRAY[i+1]%>','','images/square<%=LINKS_ARRAY[i+1]%>_<%=whichPage%>.gif',1)" onMouseOut="MM_swapImgRestore()"></a></td>
<%		} //else
		firstSquare = false;
		count +=1;
	} //for
	for (int i=count;i<5;i+=1) { %>
    <td width="72" BGCOLOR="#003366"> </td>
<%	} //for %>
  </tr>
</table>
<a href="contractor_new_instructions.jsp"><img name ="squareRegister" height="72" src="/images/squareRegister.gif" border="0" onMouseOver="MM_swapImage('squareRegister','','images/squareRegister_2.gif',1)" onMouseOut="MM_swapImgRestore()"></a></td>
