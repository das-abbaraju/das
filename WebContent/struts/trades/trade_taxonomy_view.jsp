<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div id="loadingTrade"></div>
<form id="saveTrade" class="form">
	<s:hidden name="trade.id" />
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
			<li><label>Product:</label> <s:checkbox name="trade.product" value="trade.productI" />
				<s:if test="trade.parent != null && trade.product == null">Inherited from parent</s:if>
			</li>
			<li><label>Service:</label> <s:checkbox name="trade.service" value="trade.serviceI" />
				<s:if test="trade.parent != null && trade.service == null">Inherited from parent</s:if>
			</li>
			<s:if test="trade.productI">
				<li><label>Product Critical:</label> <s:checkbox name="trade.riskLevel" value="trade.riskLevelI" />
					<s:if test="trade.parent != null && trade.riskLevel == null">Inherited from parent</s:if>
				</li>
			</s:if>
			<s:if test="trade.serviceI">
				<p><label>Safety Critical:</label> <s:property value="trade.riskLevelI"/></p>
			</s:if>
			<li><label>PSM Critical:</label> <s:checkbox name="trade.psmApplies" value="trade.psmAppliesI" />
				<s:if test="trade.parent != null && trade.psmApplies == null">Inherited from parent</s:if>
			</li>
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<button class="picsbutton positive" type="button">Save</button>
		<button class="picsbutton negative" type="button">Delete</button>
	</fieldset>
</form>
