<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="../../exception_handler.jsp"%>
<script type="text/javascript">
$().ready(function() {
	$('.datepicker').datepicker({
		showOn: 'both',
		buttonImage: 'images/icon_calendar.gif',
		buttonImageOnly: true
	});
});
</script>

<form id="newForm" action="ManageAssessmentTests.action" method="POST">
<s:hidden name="id" />
<s:hidden name="test.id" />
<input type="hidden" name="testID" value="<s:property value="test.id" />" />
	<table>
		<tbody>
			<tr>
				<td>
					<fieldset class="form">
						<s:if test="test.id == 0">
							<h2 class="formLegend">Add New Assessment Test</h2>
						</s:if>
						<ol>
							<li><label for="qualType">Qualification Type:</label>
								<s:textfield name="test.qualificationType" id="qualType" /></li>
							<li><label for="qualMethod">Qualification Method:</label>
								<s:textfield name="test.qualificationMethod" id="qualMethod" /></li>
							<li><label for="description">Description:</label>
								<s:textfield name="test.description" id="description" /></li>
							<li><label for="effective">Effective Date:</label>
								<input type="text" name="test.effectiveDate" id="effective" class="datepicker"
									value="<s:property value="maskDateFormat(test.effectiveDate)" />" /></li>
							<li><label for="verifiable">Verifiable:</label>
								<s:checkbox name="test.verifiable" id="verifiable" /></li>
							<li><label for="months">Months To Expire:</label>
								<s:textfield name="test.monthsToExpire" id="months" /></li>
						</ol>
					</fieldset>
				</td>
				<s:if test="test.id == 0 && unmapped.size() > 0">
					<td style="vertical-align: top; padding-left: 10px;">
						<fieldset class="form">
							<h2 class="formLegend">Add Unmapped Test</h2>
							<ol>
								<li><label>Unmapped Tests:</label>
									<s:select list="unmapped" name="stageID" listKey="id" headerKey="0" headerValue="- Assessment Test -"
										listValue="%{qualificationType + ' - ' + qualificationMethod + ' - ' + description}" />
								</li>
							</ol>
						</fieldset>
					</td>
				</s:if>
			</tr>
			<tr>
				<td<s:if test="test.id == 0 && unmapped.size() > 0"> colspan="2"</s:if>>
					<fieldset class="form submit">
						<input type="submit" name="button" value="Save" class="picsbutton positive" />
						<s:if test="test.id > 0">
							<input type="submit" name="button" value="Remove" class="picsbutton negative" 
								onclick="return confirm('Are you sure you want to remove this assessment test?');" />
						</s:if>
						<s:if test="test.id == 0">
							<input type="button" value="Cancel" class="picsbutton" 
								onclick="$('#assessmentTest').hide(); $('#newForm').hide(); $('#addLink').show();" />
						</s:if>
					</fieldset>
				</td>
			</tr>
		</tbody>
	</table>
</form>