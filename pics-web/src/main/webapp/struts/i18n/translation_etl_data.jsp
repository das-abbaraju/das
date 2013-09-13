<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp" />
<s:if test="importTranslations">
	<s:include value="translation_etl_table.jsp" />
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
	Page Count:<br />
	<s:select name="pagesToInclude" list="pageCount" listKey="key" listValue="%{key + ' (' + value + ')'}" multiple="true"></s:select> 
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