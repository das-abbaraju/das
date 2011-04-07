<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div id="loadingTrade"></div>
<s:if test="trade.id > 0">
<a class="edit translate" href="ManageTranslations.action?button=Search&key=Trade.<s:property value="trade.id"/>."
	target="_BLANK">Manage Translations</a>
</s:if>
<form id="saveTrade" class="form">
	<s:hidden name="trade" value="%{trade.id}" />
	<s:if test="trade.id == 0">
	<s:hidden name="trade.parent" />
	</s:if>
	<fieldset>
	<h2>Trade</h2>
		<ol>
			<s:if test="trade.id > 0">
			<li><label>Trade ID:</label> <s:property value="trade.id"/></li>
			</s:if>
			<li><label>Short Trade Name:</label> <s:textfield name="trade.name"/> <a href=""></a><s:property value="trade.name.locale"/></li>
			<li><label>Full Trade Name (optional):</label> <s:textfield name="trade.name2"/> <s:property value="trade.name2.locale"/></li>
			<li><label>Help Text (optional):</label> <s:textarea name="trade.help"></s:textarea></li>
		</ol>
	</fieldset>
	<fieldset>
	<h2>Attributes</h2>
		<ol>
			<!-- product/service/psmApplies options -->
			<li>
				<label>Product:</label>
				<s:radio theme="pics" name="trade.product" list="trade.radioList"/>
			</li>
			<!--
			<s:if test="trade.parent != null">
				<li><label>Product:</label> <s:checkbox name="trade.product" value="trade.productI" />(Inherited from parent)</li>
				<li><label>Service:</label> <s:checkbox name="trade.service" value="trade.serviceI" />(Inherited from parent)</li>
			</s:if>
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
			-->
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<button class="picsbutton positive save" type="button">Save</button>
		<s:if test="trade.id > 0">
			<button class="picsbutton negative delete" type="button">Delete</button>
		</s:if>
	</fieldset>
</form>
