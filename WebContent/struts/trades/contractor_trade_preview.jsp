<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="trade != null">
	<div class="trade-preview">
		<s:if test="!isStringEmpty(trade.trade.imageLocationI)">
			<img src="TradeTaxonomy!tradeLogo.action?trade=<s:property value="trade.trade.id"/>" class="trade"/>
		</s:if>
		
		<s:if test="trade.trade.parent != null">
			<div class="trade-section">
				<s:iterator value="tradeClassification" var="atrade" status="status">
					<s:if test="#atrade.name2 != null && !#atrade.name2.equals('') && !#atrade.name2.equals(#atrade.getI18nKey('name2'))">
						<s:property value="#atrade.name2"/>
					</s:if>
					<s:else>
						<s:property value="#atrade.name"/>
					</s:else>
					
					<s:if test="#status.last != true">
						&gt;
					</s:if>
				</s:iterator>
			</div>
		</s:if>
	
		<%-- Trade Activity Percentages --%>
		<div id="activityPercent">
			<p>
				<s:if test="permissions.operatorCorporate">
					<s:text name="ContractorTrade.businessRepresentationOther">
						<s:param>
							<b><s:text name="ContractorTrade.activityPercent.%{trade.activityPercent}"/></b>
						</s:param>
					</s:text>
				</s:if>
				<s:else>
					<s:text name="ContractorTrade.businessRepresentation">
						<s:param>
							<b><s:text name="ContractorTrade.activityPercent.%{activityPercentMap.get(trade.activityPercent)}" /></b>
						</s:param>
					</s:text>
				</s:else>
			</p>
		</div>
		
		<%-- Trade Options --%>
		<div id="tradeOptions" class="clearfix">
			<s:if test="trade.trade.productI">
				<p>
					<s:text name="ContractorTrade.businessProduct">
						<s:param>
							<b>
								<s:if test="trade.manufacture">
								    <s:text name="ContractorTrade.manufacture.Manufacture"/>
								</s:if>
								<s:else>
									<s:text name="ContractorTrade.manufacture.Distribute"/>
								</s:else>
							</b>
						</s:param>
					</s:text>
				</p>
			</s:if>
			
			<s:if test="trade.trade.serviceI">
				<p>
					<s:text name="ContractorTrade.businessService">
						<s:param>
							<b>
								<s:if test="trade.selfPerformed">
								    <s:text name="ContractorTrade.selfPerformed.SelfPerform"/>
								</s:if>
								<s:else>
									<s:text name="ContractorTrade.selfPerformed.SubContract"/>
								</s:else>
							</b>
						</s:param>
					</s:text>
				</p>
			</s:if>
		</div>
	</div>
</s:if>