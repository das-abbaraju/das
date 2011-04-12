<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp" />
<s:if test="importTranslations">
	<table class="report">
		<thead>
			<tr>
				<s:iterator value="allLocales">
					<td colspan="3"><s:property /></td>
				</s:iterator>
			</tr>
			<tr>
				<s:iterator value="allLocales">
					<td>Key</td>
					<td>New</td>
					<td>Old</td>
				</s:iterator>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="allKeys" id="key">
				<tr>
					<s:iterator value="allLocales" id="locale">
						<td><s:property value="importedTranslations.get(#key, #locale).get(0).key" /></td>
						<td><s:property value="importedTranslations.get(#key, #locale).get(0).value" /></td>
						<td><s:property value="importedTranslations.get(#key, #locale).get(1).value" /></td>
					</s:iterator>
				</tr>
			</s:iterator>
			<tr>
				<td colspan="<s:property value="allLocales.size * 3" />" class="center" style="font-weight: bold;">
					<s:property value="allKeys.size" /> total changes
				</td>
			</tr>
		</tbody>
	</table>
	
	<s:submit action="TranslationETL!save" value="Save" cssClass="picsbutton positive" />
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
		<s:if test="download">
			<div class="info">
				Found <s:property value="foundRows" /> rows. A download is provided instead of printing to screen.<br />
				<s:submit action="TranslationETL!download" value="Download" cssClass="picsbutton positive" />
			</div>
		</s:if>
		<s:else>
			<div class="right"><s:property value="foundRows" /> entries found</div>
			<div id="clipDiv" style="position:relative"> 
				<div id="clipButton">Copy to clipboard...</div> 
			</div>
		</s:else>
	</div>
	<s:textarea name="translations" id="translationsArea" rows="20" cssStyle="width: 100%;%{download ? ' display: none;' : ''}" />
</s:else>