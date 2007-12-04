<form name="form1" method="post" action="">
  <table border="0" align="center" cellpadding="2" cellspacing="0">
    <tr> 
      <td><input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="8" onFocus="clearText(this)">
        <%=sBean.getSearchIndustrySelect("industry","forms",sBean.selected_industry)%>
        <input name="zip" type="text" class="forms" value="<%=sBean.selected_zip%>" size="5" onFocus="clearText(this)">
        <%=sBean.getStateSelect("state","forms", sBean.selected_state)%></td>
      <td></td>
    </tr>
    <tr>
      <td><%=sBean.getSearchGeneralSelect("generalContractorID", "blueMain", sBean.selected_generalContractorID)%>
      <%=Inputs.inputSelect("auditLocation", "forms", sBean.selected_auditLocation, sBean.AUDITLOCATION_SEARCH_ARRAY)%></td>
      <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
    </tr>
  </table>
</form>
