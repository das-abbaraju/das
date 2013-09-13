<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../actionMessages.jsp" />

<h3>
	<s:text name="%{type.i18nKey}" />
	<s:text name="EmailWizard.Entries">
		<s:param value="%{list.size}" />
	</s:text>
</h3>
<s:hidden name="sendSize" id="sendSize" value="%{list.size()}" />
<table class="report" style="width: 85%; min-width: 400px;">
	<thead>
		<tr>
			<th style="width: 100%;"><s:text name="%{type.i18nKey}" /></th>
			<th><s:text name="EmailWizard.Preview" /></th>
			<th><s:text name="button.Remove" /></th>
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