<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../actionMessages.jsp" />

<h3><s:property value="type" /> (<s:property value="list.size()"/> entries)</h3>
<s:hidden name="sendSize" value="%{list.size()}" />
<table class="report" style="width: 85%; min-width: 400px;">
	<thead>
		<tr>
			<th style="width: 100%;"><s:property value="type" /></th>
			<th>Preview</th>
			<th>Remove</th>
		</tr>
	</thead>
	<s:iterator value="list" id="con" status="stat">				
		<tr>
			<td><s:property value="#con.value" /></td>
			<td class="center"><a href="#" class="preview" onclick="pEmail(<s:property value="#con.key"/>)"></a></td>
			<td class="center"><a href="#" class="remove" onclick="removeCon(<s:property value="#con.key"/>)"></a></td>
		</tr>			
	</s:iterator>
</table>