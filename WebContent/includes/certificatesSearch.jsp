<form name="form1" method="post" action="report_certificates.jsp">
  <table border="0" align="center" cellpadding="2" cellspacing="0">
    <tr> 
        <td><input name="name" type="text" class="forms" value="<%=cerBean.contractor_name%>" size="8" onFocus="clearText(this)">
        </td>
<% if(pBean.isAdmin()) { %>
        <td><%=cerBean.getGeneralSelect4("operator_id","forms", cerBean.operator_id, "" ) %>
      	</td>
<% } %>
         <td class="blueMain">&nbsp;&nbsp;Status</td>
         <td><%=com.picsauditing.PICS.Utilities.inputSelect2("status", "forms", cerBean.getStatus(), statusList) %>
          </td>
      	  <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
    </tr>
  </table>
</form>
