<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="!permissions.operatorCorporate && trade.id">
	<a href="ContractorTrades!tradeAjax.action?contractor=${contractor.id}&trade=${trade.id}&mode=View" class="trade view"><s:text name="global.View" /></a>
</s:if>

<s:form id="trade-form">
	<s:hidden name="contractor"/>
	<s:hidden name="trade"/>
	<s:hidden name="trade.trade"/>

	<s:if test="trade.id > 0 && trade.activityPercent == 0">
		<div class="alert-message">
			<s:text name="ContractorTrade.activityPercent.missing"/>
		</div>
	</s:if>

	<%-- Trade Activity Percentages --%>
	<div id="activityPercent">
		<s:if test="permissions.operatorCorporate">
			<s:text name="ContractorTrade.businessRepresentationOther">
				<s:param>
					<s:text name="ContractorTrade.activityPercent.%{trade.activityPercent}"/>
				</s:param>
			</s:text>
		</s:if>
		<s:else>
			<s:text name="ContractorTrade.businessRepresentation">
				<s:param>
					<s:select name="trade.activityPercent" list="activityPercentMap" theme="translate"/>
				</s:param>
			</s:text>
		</s:else>
	</div>

	<%-- Trade Options --%>
	<div id="tradeOptions" class="clearfix">
		<s:if test="trade.trade.productI">
			<div>
				<s:if test="permissions.operatorCorporate">
					<s:text name="ContractorTrade.businessProductOther">
						<s:param>
							<s:if test="trade.manufacture">
							    <s:text name="ContractorTrade.manufacture.Manufacture"/>
							</s:if>
							<s:else>
								<s:text name="ContractorTrade.manufacture.Distribute"/>
							</s:else>
						</s:param>
					</s:text>
				</s:if>
				<s:else>
					<s:text name="ContractorTrade.businessProduct">
						<s:param>
							<s:select name="trade.manufacture" theme="translate" list="#{true: 'Manufacture', false:'Distribute' }" />
						</s:param>
					</s:text>
				</s:else>
			</div>
		</s:if>

		<s:if test="trade.trade.serviceI">
			<div>
				<s:if test="permissions.operatorCorporate">
					<s:text name="ContractorTrade.businessServiceOther">
						<s:param>
							<s:if test="trade.selfPerformed">
							    <s:text name="ContractorTrade.selfPerformed.SelfPerform"/>
							</s:if>
							<s:else>
								<s:text name="ContractorTrade.selfPerformed.SubContract"/>
							</s:else>
						</s:param>
					</s:text>
				</s:if>
				<s:else>
					<s:text name="ContractorTrade.businessService">
						<s:param>
							<s:select name="trade.selfPerformed" theme="translate" list="#{true: 'SelfPerform', false:'SubContract' }" />
						</s:param>
					</s:text>
				</s:else>
			</div>
		</s:if>
	</div>

	<s:if test="!permissions.operatorCorporate">
		<s:if test="trade.id == 0">
			<div>
				<s:if test="requiresService">
					<s:if test="!onsite">
						<s:checkbox name="conTypes" fieldValue="Onsite" value="%{contractor.onsiteServices}" cssClass="service" />
						<s:text name="ContractorTrade.onsiteServices" />
						<br />
					</s:if>

					<s:if test="!offsite">
						<s:checkbox name="conTypes" fieldValue="Offsite" value="%{contractor.offsiteServices}" cssClass="service" />
						<s:text name="ContractorTrade.offsiteServices" />
						<br />
					</s:if>
				</s:if>
				<s:elseif test="requiresMaterial">
					<s:checkbox name="conTypes" fieldValue="Supplier" value="%{contractor.materialSupplier}" cssClass="product"/>
					<s:text name="ContractorTrade.materialSupplier" />
				</s:elseif>
			</div>
		</s:if>

		<s:if test="affectedTrades.size > 0">
			<div class="alert-message">
				<s:text name="ContractorTrades.affectedTrades">
					<s:param>
						<ul>
							<s:iterator value="affectedTrades" var="trade">
								<li>
									<a href="ContractorTrades!tradeAjax.action?contractor=<s:property value="contractor.id"/>&trade=<s:property value="#trade.id"/>" class="trade">
										<s:property value="#trade.trade.name"/>
									</a>
								</li>
							</s:iterator>
						</ul>
					</s:param>
				</s:text>
			</div>
		</s:if>

		<div>
			<s:if test="trade.id == 0">
				<s:if test="trade.trade.safetySensitiveI.toString().equals('Yes') && !contractor.safetySensitive">
					<s:set var="affects_safety_sensitive_status">true</s:set>
				</s:if>
				<s:else>
					<s:set var="affects_safety_sensitive_status">false</s:set>
				</s:else>

				<s:submit
					method="saveTradeAjax"
					value="%{getText('button.Add')}"
					cssClass="btn success"
					id="addButton"
					onclick="$('#next_button').show()"
					data-affects-safety-sensitive-status="%{#affects_safety_sensitive_status}"
				/>
			</s:if>
			<s:else>
				<s:submit method="saveTradeAjax" value="%{getText('button.Save')}" cssClass="save btn success" />
				<s:submit method="removeTradeAjax" value="%{getText('button.Remove')}" cssClass="remove btn danger" />
			</s:else>
		</div>
	</s:if>
</s:form>