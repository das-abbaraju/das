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
			<s:iterator value="oshaDisplay.stats.keySet()" var="stat">
				<table class="table">
					<thead>
						<tr>
							<th><s:property value="getText(#stat.i18nKey)" /></th>
							<s:iterator value="oshaDisplay.stats.get(#stat).get('columnNames')" var="colname">
								<th><s:property value="#colname" /></th>
							</s:iterator>
						</tr>
					</thead>
					<tbody>	
				 	<s:set var="is_odd" value="true" />			
						<s:iterator value="oshaDisplay.stats.get(#stat).get('data')" var="row">
							<tr <s:if test="#row.hurdleRates">class="hurdle"</s:if> <s:elseif test="#is_odd == true">class="odd"</s:elseif>>
								<s:iterator value="#row.cells" var="celldata">
									<td>
										<s:property value="#celldata" escape="false"/>
									</td>
								</s:iterator>
							</tr>
							<s:if test="!#row.hurdleRates">
								<s:set var="is_odd" value="%{!#is_odd}" />
							</s:if>
						</s:iterator>
					</tbody>
				</table>
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