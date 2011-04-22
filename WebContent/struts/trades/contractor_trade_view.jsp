<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<h3><s:property value="trade.trade.name"/></h3>

<div class="clearfix">
	<s:if test="trade.trade.productI">
		<div class="fieldoption left">
			<s:radio name="trade.manufacture" theme="translate" list="#{true: 'Manufacture', false:'Distribute' }"/>
		</div>
	</s:if>
	<s:if test="trade.trade.serviceI">
		<div class="fieldoption left">
			<s:radio name="trade.selfPerformed" theme="translate" list="#{true: 'SelfPerform', false:'SubContract' }"/>
		</div>
	</s:if>
</div>


<div>
	<s:text name="ContractorTrade.businessRepresentation">
		<s:param><s:select name="trade.activityPercent" list="activityPercentMap" theme="translate"/></s:param>
	</s:text>
</div>

<div>
	<ol class="form-style">
		<s:if test="!isStringEmpty(trade.trade.name2.toString())">
			<li>
				<label><s:text name="Trade.name2"/></label>
				<s:property value="trade.trade.name2"/>	
			</li>
		</s:if>
		<li>
			<label><s:text name="Trade"/></label>
			<div class="hierarchy">
				<div id="trade-hierarchy"></div>
				<script type="text/javascript">
					$(function() {
						$('#trade-hierarchy').jstree({
							"themes": {
								theme: "classic"	
							},
							"json_data": {
								"ajax": {
									"url": 'TradeTaxonomy!hierarchyJson.action',
									"dataType": "json",
									"success": function(json) {
										return json.result;
									},
									"data": function(node) {
										return {
											trade: '<s:property value="trade.trade.id"/>'
										};
									}
								}
							},
							"plugins": ['themes', "json_data"]
						});
					});
				</script>
			</div>
		</li>
		<s:if test="!isStringEmpty(trade.trade.help.toString())">
			<li>
				<label><s:text name="Trade.help"/></label>
				<s:property value="trade.trade.help"/>
			</li>
		</s:if>
		<li>
			<label><s:text name="Trade.alternateSearch"/></label>
			<s:if test="trade.trade.alternates.size() > 0">
				<ul>
					<s:iterator value="trade.trade.alternates">
						<li><s:property value="name"/></li>
					</s:iterator>
				</ul>
			</s:if>
		</li>
	</ol>
</div>