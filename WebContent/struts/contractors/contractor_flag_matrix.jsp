<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<div class="panel_placeholder contractor-flag-matrix">
	<div class="panel">
		<div class="panel_header">
			<s:url action="ContractorView" method="printFlagMatrix" var="print_flag_matrix">
				<s:param name="id">
					${id}
				</s:param>
			</s:url>
            <a href="${print_flag_matrix}" class="print-contractor-flag-matrix"><s:text name="global.print" /></a>
            
			<s:text name="ContractorView.FlagMatrix" /> - <s:property value="contractor.name" />
		</div>
		<div class="panel_content">
			<table class="table">
				<tr>
					<th class="client-site">
                        <s:text name="global.Operator" />
                    </th>
					<th class="green-flag">
                        <s:text name="FlagColor.Green" />
                    </th>
					<th class="amber-flag">
                        <s:text name="FlagColor.Amber" />
                    </th>
					<th class="red-flag">
                        <s:text name="FlagColor.Red" />
                    </th>
				</tr>
                
                <s:set var="is_odd" value="true" />
                
				<s:iterator value="activeOperatorsMap">
					<s:iterator value="value">
						<s:if test="!operatorAccount.generalContractorFree" >
							<tr class="<s:if test="#is_odd == true">odd</s:if>">
								<td class="client-site">
	                                <s:property value="operatorAccount.name" />
	                            </td>
								<td class="green-flag">
	                                <s:if test="flagColor.isGreen()">
	                                    <img src="images/icon_greenFlag.gif" />
	                                </s:if>
	                            </td>
								<td class="amber-flag">
	                                <s:if test="flagColor.isAmber()">
	                                    <img src="images/icon_amberFlag.gif" />
	                                </s:if>
	                            </td>
								<td class="red-flag">
	                                <s:if test="flagColor.isRed()">
	                                    <img src="images/icon_redFlag.gif" />
	                                </s:if>
	                            </td>
							</tr>
	                        <s:if test="#is_odd == true">
	                            <s:set var="is_odd" value="false" />
	                        </s:if>
	                        <s:else>
	                            <s:set var="is_odd" value="true" />
	                        </s:else>
                        </s:if>
					</s:iterator>
				</s:iterator>
			</table>
		</div>
	</div>
</div>