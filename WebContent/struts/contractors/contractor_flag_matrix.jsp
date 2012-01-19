<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<div class="panel_placeholder widget locations">
	<div class="panel" id="all">
		<div class="panel_header">
			<s:text name="ContractorView.FlagMatrix" /> - <s:property value="contractor.name" />
			<a href="ContractorView!printFlagMatrix.action?id=${id}"><s:text name="global.print" /></a>
		</div>
		<div class="panel_content">
			<table border="1">
				<tr>
					<th><s:text name="global.Operator" /></th>
					<th><s:text name="FlagColor.Green" /></th>
					<th><s:text name="FlagColor.Amber" /></th>
					<th><s:text name="FlagColor.Red" /></th>
				</tr>
				<s:iterator value="activeOperatorsMap">
					<s:iterator value="value">
						<tr>
							<td><s:property value="operatorAccount.name" /></td>
							<td><s:if test="flagColor.isGreen()"><img src="images/tick.png" /></s:if></td>
							<td><s:if test="flagColor.isAmber()"><img src="images/tick.png" /></s:if></td>
							<td><s:if test="flagColor.isRed()"><img src="images/tick.png" /></s:if></td>
						</tr>
					</s:iterator>
				</s:iterator>
			</table>
			<div class="clear"></div>
		</div>
	</div>
</div>