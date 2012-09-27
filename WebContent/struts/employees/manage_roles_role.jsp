<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:form id="role_form">
	<s:hidden name="account" value="%{account.id}" />
	<s:hidden name="audit" value="%{audit.id}" />
	<s:hidden name="questionId" value="%{questionId}" />
	<s:hidden name="role" />
	
	<fieldset class="form">
		<h2 class="formLegend">
			<s:text name="ManageJobRoles.label.DefineRole" />
		</h2>
		<ol>
			<li>
				<label>
					<s:text name="ManageJobRoles.label.JobRole" />:
				</label>
				<s:textfield
					id="role_name"
					name="role.name"
					size="35"
				/>
			</li>
			<li>
				<label>
					<s:text name="global.Active" />:
				</label>
				<s:checkbox
					name="role.active"
					value="role.active"
				/>
			</li>
		</ol>
	</fieldset>
	<fieldset class="form">
		<h2 class="formLegend">
			<s:text name="global.HSECompetencies" />
		</h2>
		
		<s:if test="role.jobCompetencies.size == 0">
			<tr>
				<td colspan="2">
					<div class="alert">
						<s:text name="ManageJobRoles.message.SelectCompetencies">
							<s:param>
								<s:if test="role.id > 0">
									${role.name}
								</s:if>
								<s:else>
									<s:text name="global.NEW" />
								</s:else>
							</s:param>
						</s:text>
					</div>
				</td>
			</tr>
		</s:if>
		
		<ol>
			<li class="fill">
				<table class="competencies fill">
					<s:set name="halfway" value="operatorCompetencies.size / 2" />
					<s:iterator value="operatorCompetencies" begin="0" end="#halfway - 1" status="index" var="operator_competency">
						<tr>
							<td>
								<input
									type="checkbox"
									name="competenciesToAdd"
									<s:if test="role.id > 0 && isPreviouslySelected(#operator_competency)">checked="checked"</s:if>
									value="${operator_competency.id}"
								/>
							</td>
							<td>
								${operator_competency.label}
							</td>
							<td class="center">
								<img
									src="images/help.gif"
									alt="${operator_competency.label}"
									title="${operator_competency.category}: ${operator_competency.description}"
								/>
							</td>
							<td class="spacer"></td>
							<s:set name="second_column_competency" value="%{operatorCompetencies.get(#index.index + #halfway)}" />
							<s:if test="#second_column_competency != null">
								<td>
									<input
										type="checkbox"
										name="competenciesToAdd"
										<s:if test="role.id > 0 && isPreviouslySelected(#second_column_competency)">checked="checked"</s:if>
										value="${second_column_competency.id}"
									/>
								</td>
								<td>
									${second_column_competency.label}
								</td>
								<td class="center">
									<img
										src="images/help.gif"
										alt="${second_column_competency.label}"
										title="${second_column_competency.category}: ${second_column_competency.description}"
									/>
								</td>
							</s:if>
							<s:else>
								<td colspan="3"></td>
							</s:else>
						</tr>
					</s:iterator>
				</table>
			</li>
		</ol>
		
		<div class="info">
			<a href="resources/HSECompetencyReview.pdf">
				<s:text name="ManageJobRoles.link.QuestionReviewPDF" />
			</a>
			<br />
			<s:text name="ManageJobRoles.help.QuestionReviewPDF" />
		</div>
	</fieldset>
	<fieldset class="form submit">
		<s:submit
			method="save"
			value="%{getText('button.Save')}"
			cssClass="picsbutton positive"
		/>
		<input
			type="button"
			class="picsbutton cancel"
			value="<s:text name="button.Cancel" />"
		/>
		<s:if test="role.id != 0">
			<s:submit
				method="delete"
				value="%{getText('button.Delete')}"
				cssClass="picsbutton negative"
			/>
		</s:if>
	</fieldset>
</s:form>