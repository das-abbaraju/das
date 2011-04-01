<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div id="loadingTrade"></div>
<form id="saveTrade" class="form">
	<s:hidden name="trade" value="%{trade.id}" />
	<fieldset>
	<h2>Trade</h2>
		<ol>
			<li><label>Trade ID:</label> <s:property value="trade.id"/></li>
			<li><label>Short Trade Name:</label> <s:textfield name="trade.name"/></li>
			<li><label>Full Trade Name (optional):</label> <s:textfield name="trade.name2"/></li>
			<li><label>Help Text (optional):</label> <s:textarea name="trade.help"></s:textarea></li>
		</ol>
	</fieldset>
	<fieldset>
	<h2>Attributes</h2>
		<ol>
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<button class="picsbutton positive" type="button" onclick="saveTrade()">Save</button>
		<s:if test="trade.id > 0">
			<button class="picsbutton negative" type="button" onclick="deleteTrade(<s:property value="trade.id"/>)">Delete</button>
		</s:if>
	</fieldset>
</form>
