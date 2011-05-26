<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../actionMessages.jsp"/>
<s:if test="trade != null">
<s:form id="saveTrade" cssClass="form" action="TradeTaxonomyAjax" method="POST" enctype="multipart/form-data">
	<s:hidden name="trade" value="%{trade.id}" />
	<s:if test="trade.id == 0">
		<s:hidden name="trade.parent" />
	</s:if>
	<fieldset>
	<h2>Trade</h2>
		<s:if test="!isStringEmpty(trade.imageLocationI)">
			<img src="TradeTaxonomy!tradeLogo.action?trade=<s:property value="trade.id"/>" class="trade"/>
		</s:if>
		<ol>
			<s:if test="trade.id > 0">
			<li><label>Trade ID:</label> <s:property value="trade.id"/></li>
			</s:if>
			<li><label>Trade Name:</label> <s:textfield name="trade.name"/> <a href=""></a><s:property value="trade.name.locale"/></li>
			<li><label>Tree Name (optional):</label> <s:textfield name="trade.name2"/> <s:property value="trade.name2.locale"/></li>
			<li><label>Help Text (optional):</label> <s:textarea name="trade.help"></s:textarea></li>
			<li>
				<s:if test="trade.id > 0">
					<a class="edit translate" href="ManageTranslations.action?button=Search&key=Trade.<s:property value="trade.id"/>." target="_BLANK">Manage Translations</a>
				</s:if>
			</li>
			<li>
				<label>Image:</label>
				<s:file name="tradeLogo"></s:file>
				<s:if test="!isStringEmpty(trade.imageExtension)">
					<br/>
					<a id="removelogo" class="remove" href="TradeTaxonomy!removeFileAjax.action?trade=<s:property value="trade.id"/>">Remove File</a>
				</s:if>
			</li>
			<li><s:submit cssClass="picsbutton positive save" method="saveTradeAjax" value="Save" /></li>
		</ol>
	</fieldset>
	<fieldset>
	<h2>Attributes</h2>
		<ol>
			<!-- product/service/psmApplies options -->
			<li>
				<label>Is Product:</label>
				<s:checkbox name="trade.productI" />
				<!-- <s:property value="(trade.product==null)?'null':trade.product" /> -->
			</li>
			<li>
				<label>Is Service:</label>
				<s:checkbox name="trade.serviceI" />
				<!-- <s:property value="(trade.service==null)?'null':trade.service" /> -->
			</li>
			<li>
				<label>PSM Applies:</label>
				<s:checkbox name="trade.psmAppliesI" />
				<!-- <s:property value="(trade.psmApplies==null)?'null':trade.psmApplies" /> -->
			</li>
			<li>
				<label>Product Critical:</label>
				<s:radio theme="pics" name="trade.productRiskI" list="@com.picsauditing.jpa.entities.LowMedHigh@values()"/>
				<!-- (<s:property value="(trade.productRisk==null)?'null':trade.productRisk" />) -->
			</li>
			<li>
				<label>Safety Critical:</label>
				<s:radio theme="pics" name="trade.safetyRiskI" list="@com.picsauditing.jpa.entities.LowMedHigh@values()"/>
				<!-- (<s:property value="(trade.safetyRisk==null)?'null':trade.safetyRisk" />) -->
			</li>
			<li>
				<label>Contractor Count:</label>
				<s:property value="trade.contractorCount" />
			</li>
		</ol>
	</fieldset>
	<s:if test="trade.id > 0">
	<fieldset>
		<h2>Alternate Names</h2>
		<ol>
			<li>
				<label>New Alternate</label>
				<input id="alternateName" type="text"/>
				<s:select list="alternateCategories" id="alternateCategory" />
				<button id="add-alternate" type="button">Add</button>
			</li>
			<li>
				<div id="alternateNames">
					<s:include value="trade_alternates.jsp" />
				</div>
			</li>
		</ol>
	</fieldset>
		<fieldset>
			<h2>Rules</h2>
			<ol>
				<li>
					<label>Manual Audit Category Rules</label>
					<a href="CategoryRuleEditor.action?rule.auditType=2&rule.trade=<s:property value="trade.id" />" class="add">Add New Manual Audit Category Rule</a>
					<div id="tradeCategoryRules"></div>
				</li>
				<li>
					<label>Audit Type Rules</label>
					<a href="AuditTypeRuleEditor.action?rule.trade=<s:property value="trade.id" />" class="add">Add New Audit Type Rule</a>
					<div id="tradeAuditRules"></div>
				</li>
			</ol>
		</fieldset>
	</s:if>
	<fieldset class="form submit">
		<s:submit cssClass="picsbutton positive" method="saveTradeAjax" value="Save"/>
		<s:if test="trade.id > 0">
			<s:submit cssClass="picsbutton negative" method="deleteTradeAjax" value="Delete"/>
		</s:if>
	</fieldset>
</s:form>
</s:if>
<s:else>
<div class="info">Click a trade on the left</div>
</s:else>