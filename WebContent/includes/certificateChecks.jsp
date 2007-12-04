<% if(pBean.isAdmin() || pBean.isCorporate()) { %>
	<td align="right"><%=java.text.NumberFormat.getInstance().format(certDO.getLiabilityLimit())%></td>             
	<td><%=com.picsauditing.PICS.Utilities.convertNullString(certDO.getNamedInsured(), "None")%></td>
	<td><%=certDO.getSubrogationWaived()%></td> 
<% } %> 
<% if(pBean.isOperator()) { %>           
	<td id="liability" align="right"><%=java.text.NumberFormat.getInstance().format(certDO.getLiabilityLimit())%>
	</td>             
	<td id="namedInsured"><%=com.picsauditing.PICS.Utilities.convertNullString(certDO.getNamedInsured(), "None")%>
	</td>
	<td id="subrogation"><%=certDO.getSubrogationWaived()%>
	</td> 
<% }%>                      
