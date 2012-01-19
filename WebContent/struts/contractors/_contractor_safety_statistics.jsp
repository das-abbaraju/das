<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="panel_placeholder contractor-saftey-statistics">
	<div class="panel">
		<div class="panel_header">
			<span>
				<a href="javascipt:;" 
					class="hurdle-rate" 
					data-show-text="<s:text name="ContractorView.ShowHurdleRates" />" 
					data-hide-text="<s:text name="ContractorView.HideHurdleRates" />"
				><s:text name="ContractorView.ShowHurdleRates" /></a>
			</span>
			
			<s:text name="global.Statistics" />
		</div>
		<div class="panel_content">
			<s:iterator value="stats" var="stat">
				<s:if test="#stat.hasData" >
					<table class="table">
						<thead>
							<tr>
								<th>
									<s:property value="#stat.getOshaType()" />
								</th>
								
								<s:iterator value="#stat.auditForSet" id="auditFor">
									<th>
										<s:property value="#auditFor"/>
									</th>
								</s:iterator>
							</tr>
						</thead>
						
                        <s:set var="is_odd" value="true" />
                        
						<s:iterator value="#stat.rateTypeSet" id="rateType" status="rowstatus">
							<tr <s:if test="#rateType.startsWith('P:')">class="hurdle"</s:if><s:elseif test="#is_odd == true">class="odd"</s:elseif>>
								<s:if test="#rateType.startsWith('P:')">
									<td>
										<s:property value="#rateType.substring(2)" escape="false"/>
									</td>
								</s:if>
								<s:else>
									<td>
										<s:property value="#rateType" escape="false"/>
									</td>
								</s:else>
								
								<s:iterator value="#stat.auditForSet" id="auditFor">
									<td>
										<s:property value="#stat.getData(#rateType, #auditFor)" escape="false"/>
									</td>
								</s:iterator>
							</tr>
                            
                            <s:if test="!#rateType.startsWith('P:')">
                                <s:if test="#is_odd == true">
                                    <s:set var="is_odd" value="false" />
                                </s:if>
                                <s:else>
                                    <s:set var="is_odd" value="true" />
                                </s:else>
                            </s:if>
						</s:iterator>
					</table>
				</s:if>
			</s:iterator>
			
			<s:if test="contractor.hasWiaCriteria()">
				<a class="weighted-industry-average"
					href="javascript:;"
					data-url="ContractorView!preview.action?contractor=<s:property value="contractor.id"/>" 
					title="<s:text name="ContractorView.ContractorDashboard.WeightedIndustryAverage" />"
				>*<s:text name="ContractorView.ContractorDashboard.WeightedIndustryAverage" /></a>
			</s:if>
			
		</div>
	</div>
</div>