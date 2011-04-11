<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:if test="importTranslations">
	<table class="report">
		<thead>
			<tr>
				<th>New Translation</th>
				<th>Old Translation</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="importedTranslations.keySet()" id="key">
				<tr>
					<td><s:property value="importedTranslations.get(#key).get(0).value" /></td>
					<td><s:property value="importedTranslations.get(#key).get(1).value" /></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>
<s:else>
	<style type="text/css">
	#clipButton { 
		padding-left: 20px;
		background-image: url("images/plus.png");
		background-position: left center;
		background-repeat: no-repeat;
	}
	</style>
	<div id="exportOptions">
		<div class="right"><s:property value="foundRows" /> entries found</div>
		<div id="clipDiv" style="position:relative"> 
			<div id="clipButton">Copy to clipboard...</div> 
		</div>
	</div>
	<s:textarea name="translations" id="translationsArea" rows="20" cssStyle="width: 100%;" />
</s:else>