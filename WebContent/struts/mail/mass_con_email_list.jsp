<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../actionMessages.jsp" />

<h3><s:property value="type" /> (<s:property value="list.size()"/> entries)</h3>
<s:hidden name="sendSize" value="%{list.size()}" />
<table class="report" style="width: 100%;">
	<thead>
		<tr>
			<th>Contractor</th>
			<th>Preview</th>
			<th>Remove</th>
		</tr>
	</thead>
	<s:iterator value="list" id="con" status="stat">				
		<tr>
			<td><s:property value="#con.value" /></td>
			<td><a href="#" onclick="pEmail(<s:property value="#con.key"/>)">Preview</a></td>
			<td><a href="#" onclick="removeCon(<s:property value="#con.key"/>)">Remove</a></td>
		</tr>			
	</s:iterator>
</table>