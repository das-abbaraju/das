<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<table>
	<tr>
		<s:if test="role.jobCompetencies.size > 0">
			<td>
				<table class="report" id="<s:property value="role.id" />">
					<thead>
						<tr>
							<td colspan="3"><s:text name="ManageJobRoles.label.RequiredHSECompetencies" /></td>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="role.jobCompetencies">
							<tr id="<s:property value="competency.id"/>">
								<td>
									<span><s:property value="competency.label"/></span>
								</td>
								<td class="center">
									<img src="images/help.gif" alt="<s:property value="competency.label" />" title="<s:property value="competency.category" />: <s:property value="competency.description" />" />
								</td>
								<td class="center">
									<a href="#" class="compEditor removeCompetency"><img alt="Delete" src="images/icon-16-remove.png" border="0"></a>
								</td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</td>
			<td>&nbsp;</td>
		</s:if>
		<td>
			<s:if test="role.jobCompetencies.size == 0">
				<div class="alert">
					<s:text name="ManageJobRoles.message.SelectCompetencies">
						<s:param value="%{role.name}" />
					</s:text>
				</div>
			</s:if>
			<table class="report" id="<s:property value="role.id" />">
				<thead>
					<tr>
						<td colspan="2"><s:text name="ManageJobRoles.label.PotentialHSECompetencies" /></td>
						<td></td>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="otherCompetencies">
					<tr id="<s:property value="id" />">
						<td>
							<span><s:property value="label"/></span>
						</td>
						<td class="center">
							<img src="images/help.gif" alt="<s:property value="label" />" title="<s:property value="category" />: <s:property value="description" />" />
						</td>
						<td class="center">
							<a href="#" class="compEditor add addCompetency"></a>
						</td>
					</tr>
					</s:iterator>
				</tbody>
			</table>
		</td>
	</tr>
</table>
<div class="info">
	<a href="resources/HSECompetencyReview.pdf">
		<s:text name="ManageJobRoles.link.QuestionReviewPDF" />
	</a>
	<br />
	<s:text name="ManageJobRoles.help.QuestionReviewPDF" />
</div>