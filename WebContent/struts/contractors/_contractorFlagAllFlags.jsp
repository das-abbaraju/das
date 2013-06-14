<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<table class="flagCategories details">
	<tr>
		<s:iterator id="flagData" value="flagDataMap">
			<td>
				<table class="report">
					<thead>
						<tr>
							<td><s:text name="ContractorFlag.Flag" /></td>
							<%--<td><s:property value="#flagData.key" /></td>--%>
                            <td><s:property value="%{getText('FlagCriteria.Category.' + #flagData.key)}"/></td>
							<s:if test="displayCorporate">
								<td><s:text name="ContractorFlag.Operator" /></td>
							</s:if>
						</tr>
					</thead>
					<s:iterator id="data" value="#flagData.value">
						<tr>
							<td class="center">
								<s:property value="flag.smallIcon" escape="false" />
							</td>
							<td>
								<s:if test="criteria.auditType != null">
									<s:property value="criteria.auditType.name" />
								</s:if>
								<s:else>
									<s:property value="getContractorAnswer(#data, true)" escape="false" />
								</s:else>
							</td>
							<s:if test="displayCorporate">
								<td>
									<s:property value="#data.operator.name" escape="false" />
								</td>
							</s:if>
						</tr>
					</s:iterator>
				</table>
			</td>
		</s:iterator>
	</tr>
</table>