<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="panel_placeholder contractor-safety-statistics">
	<div class="panel">
		<div class="panel_header">
			<a href="javascript:;" 
				class="hurdle-rate" 
				data-show-text="<s:text name="ContractorView.ShowHurdleRates" />" 
				data-hide-text="<s:text name="ContractorView.HideHurdleRates" />"
			><s:text name="ContractorView.ShowHurdleRates" /></a>
			
			<s:text name="global.Statistics" />
		</div>
		<div class="panel_content">
			<s:iterator value="oshaDisplay.sortedKeySet" var="stat">
				<s:if test="displayOsha.get(#stat) != null && displayOsha.get(#stat)">
				<table class="table">
					<thead>
						<tr>
							<th>
                                <s:property value="getText(#stat.i18nKey)" />
                            </th>
                            
							<s:iterator value="oshaDisplay.stats.get(#stat).get('columnNames')" var="colname">
								<th>
                                    <s:property value="#colname" />
                                </th>
							</s:iterator>
						</tr>
					</thead>
					<tbody>	
						<s:iterator value="oshaDisplay.stats.get(#stat).get('data')" var="row">
								<tr class="<s:if test="#row.hurdleRate">hurdle</s:if><s:else>rate-type <s:if test="#is_odd == true">odd</s:if></s:else>">
								<td>
									<s:if test="!#row.hurdleRate" >
										<a href="javascript:;" class="rate-type-tooltip"
										title="<s:property value="getText(#row.title + '.helpText')" />"><img src="images/help-icon.png" /></a>
										<s:property value="getText(#row.title)" />
									</s:if>
									<s:else>
										<s:property value="#row.operator.name" />
									</s:else>
								</td>
									<s:iterator value="#row.cells" var="celldata" status="stat">
										<td>
											<span class="rate-type">
	                                            <s:property value="#celldata" escape="false" />
	                                        </span>
										</td>
									</s:iterator>
								</tr>
	                            
								<s:if test="!#row.hurdleRate">
									<s:set var="is_odd" value="%{!#is_odd}" />
								</s:if>
						</s:iterator>
					</tbody>
				</table>
				</s:if>
			</s:iterator>
            
			<a class="weighted-industry-average"
				href="javascript:;"
				data-url="ContractorView!preview.action?contractor=<s:property value="contractor.id"/>" 
				title="<s:text name="ContractorView.ContractorDashboard.WeightedIndustryAverage" />"
			>*<s:text name="ContractorView.ContractorDashboard.WeightedIndustryAverage" /></a>
					
            <div class="clear"></div>
		</div>
	</div>
</div>