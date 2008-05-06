<table border="0" align="center" cellpadding="2" cellspacing="0">
  <tr> 
    <td align="center">
      <select name="type" class="forms">
        <option value="Contractor" <%="Contractor".equals(sBean.searchType)?"selected":""%>>Contractors</option>
        <option value="Corporate" <%="Corporate".equals(sBean.searchType)?"selected":""%>>Corporate</option>
        <option value="Operator" <%="Operator".equals(sBean.searchType)?"selected":""%>>Operators</option>
        <option value="Auditor" <%="Auditor".equals(sBean.searchType)?"selected":""%>>Auditors</option>
      </select>
    </td>
    <td><input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="8" onFocus="clearText(this)"></td>
    <td><%=sBean.getSearchIndustrySelect("industry","forms",sBean.selected_industry)%></td>
    <td><%=tBean.getTradesSelect("trade", "forms", sBean.selected_trade)%></td>
    <td><input name="zip" type="text" class="forms" value="<%=sBean.selected_zip%>" size="5" onFocus="clearText(this)"></td>
    <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
  </tr>
  <tr> 
    <td colspan="2"><%=sBean.getSearchGeneralSelect("generalContractorID", "blueMain", sBean.selected_generalContractorID)%></td>
    <td><%=sBean.getAuditStatusSelect("auditStatus", "blueMain", sBean.selected_auditStatus)%> <input name="city" type="text" class="forms" value="<%=sBean.selected_city%>" size="15" onFocus="clearText(this)"></td>
    <td colspan="2"><%=sBean.getStateSelect("state","forms", sBean.selected_state)%></td>
  </tr>
  <tr> 
    <td colspan=6 class=blueMain><%=Inputs.inputSelect("certsOnly","forms",sBean.selected_certsOnly,sBean.CERTS_SEARCH_ARRAY)%></td>
  </tr>
</table>
