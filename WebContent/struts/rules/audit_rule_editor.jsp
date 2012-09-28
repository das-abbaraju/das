<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page language="java" errorPage="/exception_handler.jsp" %>

<html>
	<head>
		<title><s:property value="ruleType"/> Rule Editor</title>
		
		<link rel="stylesheet" href="css/reports.css?v=${version}" />
		<link rel="stylesheet" href="css/forms.css?v=${version}" />
		
		<s:include value="../jquery.jsp"/>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
		
		<script type="text/javascript">
			$(function() {
				startThinking({div:'moreRelated', message: "Loading"});
				startThinking({div:'lessRelated', message: "Loading"});
				$('#moreRelated').load('<s:property value="urlPrefix"/>RuleTableAjax.action',{id: <s:property value="id"/>, button: 'moreGranular'});
				$('#lessRelated').load('<s:property value="urlPrefix"/>RuleTableAjax.action',{id: <s:property value="id"/>, button: 'lessGranular'});
				$('#thisRuleTable').load('<s:property value="urlPrefix"/>RuleTableAjax.action',{id: <s:property value="id"/>});
				$('#operator').change(function() {
					if ($(this).blank()) {
						$('#operator_display').html('');
						$('#tag').find('option').remove();
						$('#opTagli').hide();
					}
				}).autocomplete('OperatorAutocomplete.action', {
					formatItem  : function(data,i,count) {
						return data[1];
					},
					formatResult: function(data,i,count) {
						return data[0];
					},
					max	: 50
				}).result(function(event, data) {
					$('#operator_display').html("<a target='_BLANK' href=\"OperatorConfiguration.action?id=" + data[0] + "\">" + data[1] + " Configuration</a>");
					$.getJSON('OperatorTagAutocomplete!json.action',{'q': data[0]},
						function(json) {
							if (json) {
								$('#tag').html('');
								var tags = json.result;
								$('#tag').append($('<option>').attr('value', 0).text("Any"));
								for(var i=0; i<tags.length; i++) {
									$('#tag').append($('<option>').attr('value', tags[i].id).text(tags[i].tag));
								}
								$('#opTagli').show();
							}
						}
					);
				});
				$('#question').change(function() {
					if ($(this).blank()) {
						$('#question_display').html('');
						$('.requiresQuestion').hide();
						$('.requiresComparator').hide();
					}
				}).autocomplete('AuditQuestionAutocomplete.action', {
					formatItem  : function(data,i,count) {
						return data[1];
					},
					formatResult: function(data,i,count) {
						return data[0];
					}
				}).result(function(event, data) {
					$('#question_display').html(data[1]);
					$('.requiresQuestion').show();
				});
				<s:if test="!auditTypeRule">
				$('#category').change(function() {
					if ($(this).blank())
						$('#category_display').html('');
				}).autocomplete('CategoryAutocomplete.action', {
					extraParams: {auditTypeID: $('#auditType').val()},
					formatItem  : function(data,i,count) {
						return data[1];
					},
					formatResult: function(data,i,count) {
						return data[0];
					}
				}).result(function(event, data) {
					$('#category_display').html(data[1]);
				});
				</s:if>
				$('#dependentAudit').change(function() {
					AJAX.request({
						url:'AuditTypeRuleEditor!dependentAuditStatusSelect.action',
						data:{
							id: $('#rule_form_id').val(),
							audit_id: $(this).val()
						},
						success: function(data, textStatus, XMLHttpRequest) {
							$('.requiresDependentAudit').html(data);
						}
					});
					if ($(this).val() == '') {
						$('.requiresDependentAudit').hide();
					} else {
						$('.requiresDependentAudit').show();
					}
				});
				$('#comparator').change(function() {
					if ($(this).blank()) {
						$('.requiresComparator').hide().find('input').val('');
					} else {
						$('.requiresComparator').show();
					}
				});
				$('#ruleEditCheckbox').change(function() {
					if ($(this).is(':checked'))
						$("div.buttons .picsbutton").removeAttr("disabled");
					else
						$("div.buttons .picsbutton").attr("disabled", "disabled");
				});
			});
		</script>
		
		<style>
			.ruleEditor {
				display: none;
			}
			
			<s:if test="rule == null || rule.question == null">
			.requiresQuestion {
				display: none;
			}
			</s:if>
			
			<s:if test="rule == null || rule.dependentAuditStatus == null">
			.requiresDependentAudit {
				display: none;
			}
			</s:if>
			
			<s:if test="rule == null || rule.operatorAccount == null">
			.requiresOperator {
				display: none;
			}
			</s:if>
			
			<s:if test="rule == null || rule.questionComparator == null">
			.requiresComparator {
				display: none;
			}
			</s:if>
			
			#related {
				display: none;
			}
		</style>
	</head>
	<body>
		<h1><s:property value="ruleType"/> Rule Editor</h1>
		
		<s:include value="../actionMessages.jsp"/>
		<s:include value="../config_environment.jsp" />
		
		<div>
			<s:if test="rule.id > 0 && canEditRule">
				<a class="add" href="<s:property value="urlPrefix"/>RuleEditor.action?button=New">Create new rule</a>
			</s:if>
			<s:else>
				<script type="text/javascript">
					$(function(){$('.ruleEditor').show();});
				</script>
			</s:else>
		</div>
		
		<s:if test="rule != null">
			<s:if test="rule.effectiveDate.after(new java.util.Date())">
				<div class="alert">
					This rule will not go into effect until <s:date name="rule.effectiveDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/>.
				</div>
			</s:if>
			<s:elseif test="!rule.current">
				<div class="alert">
					This rule is no longer in effect, it was removed by <s:property value="rule.updatedBy.name"/>.
				</div>
			</s:elseif>
		</s:if>
		
		<s:form method="post" id="rule_form">
			<s:hidden name="id"/>
			
			<div id="detail">
				<s:if test="rule.id > 0">
					<fieldset class="form lessGran">
						<h2 class="formLegend">Less Granular</h2>
						<div id="lessRelated" style="padding-top:10px;"></div>
					</fieldset>
					
					<fieldset class="form">
						<a name="edit"></a>
						
						<h2 class="formLegend">Summary</h2>
						
						<ol>
							<li>
								<h4><s:property value="rule.toString()"/></h4>
							</li>
							<li id="thisRuleTable"></li>
							
							<s:if test="canEditRule">
								<li>
									<a href="#edit" class="edit" onclick="$('.ruleEditor').toggle();">Edit Rule</a>
								</li>
							</s:if>
							<s:else>
								<li>
									<s:if test="rule.operatorAccount != null">
										<a href="FacilitiesEdit.action?operator=<s:property value="rule.operatorAccount.id" />">
											<s:property value="rule.operatorAccount.name" />
										</a>
										<br />
									</s:if>
									
									<s:if test="rule.auditType != null">
										<a href="ManageAuditType.action?id=<s:property value="rule.auditType.id" />">
											<s:property value="rule.auditType.name" />
										</a>
										<br />
									</s:if>
									
									<s:if test="rule.auditCategory != null">
										<a href="ManageCategory.action?id=<s:property value="rule.auditCategory.id" />">
											<s:property value="rule.auditCategory.name" />
										</a>
										<br />
									</s:if>
								</li>
							</s:else>
						</ol>
						
						<s:if test="!canEditRule">
							<div class="alert">
								This rule is not specific to one of your Accounts. Please contact one of the Audit Rule Administrators if this rule needs to be changed.
							</div>
						</s:if>
					</fieldset>
				</s:if>
				
				<s:if test="canEditRule">
					<fieldset class="form ruleEditor">
						<h2 class="formLegend">Rule</h2>
						
						<ol>
							<li>
								<label>Rule Type</label>
								<div class="nobr">
									<s:radio 
										list="#{true:'Include',false:'Exclude'}" 
										name="ruleInclude" 
										value="rule.include"
										theme="pics"
										cssClass="inline"
									/>
								</div>
								<pics:fieldhelp title="Rule Type">Include means that matching rules will include the Audit Type or Category. Use exclude if you want to remove the Type or Category when the conditions below exist.</pics:fieldhelp>
							</li>
							<li>
								<label>Level</label>
								<s:property value="%{rule.level-rule.levelAdjustment}" default="0"/> + <s:textfield name="rule.levelAdjustment" />
								<pics:fieldhelp title="Level Adjustment">
									<i>This is an advanced feature that enables users to alter the system generated level and should only be used in rare cases.</i>
									<br />
									The level and priority determine the order in which the rules are evaluated. Higher levels are more granular and take precedence over lower levels.
									Enter a positive number to have this rule run earlier or more important.
									Enter a negative number to have this rule run later or less important.
								</pics:fieldhelp>
							</li>
							<li>
								<label>Priority</label>
								<s:property value="rule.priority"/>
							</li>
						</ol>
					</fieldset>
					
					<fieldset class="form ruleEditor">
						<h2 class="formLegend">Options</h2>
						
						<ol>
							<li>
								<label>Audit Type</label>
								<s:select id="auditType" name="ruleAuditTypeId" value="rule.auditType.id" list="{}" headerKey="0" headerValue="Any Audit Type">
									<s:iterator value="auditTypeMap" var="aType">
										<s:optgroup label="%{#aType.key}" list="#aType.value" listKey="id" listValue="name"/>
									</s:iterator>
								</s:select>
								<div id="auditType_display"></div>
								<pics:fieldhelp>Choose the Audit Type. This is field required.</pics:fieldhelp>
							</li>
							
							<s:if test="auditTypeRule">
								<li>
									<label>Auto Add Audit</label>
									<div class="nobr">
										<s:radio 
											list="#{false:'Auto Add',true:'Manually Added'}" 
											name="rule.manuallyAdded"
											theme="pics"
											cssClass="inline"
										/>
									</div>
									<pics:fieldhelp title="Auto Add Audit">
										<p>Auto Add (default) - a single audit is added to each contractor account that matches this rule.</p>
										<p>Manually Added - audits are available to be manually added to a contractor's account. Examples include: Field Audit and Integrity Management</p>
									</pics:fieldhelp>
								</li>
							</s:if>
							
							<s:if test="!auditTypeRule">
								<li>
									<label>Category</label>
									<s:textfield cssClass="autocomplete" id="category" value="%{rule.auditCategory.id}" name="ruleAuditCategoryId"/>
									<div id="category_display">
										<s:if test="rule.auditCategory != null">
											<s:iterator value="rule.auditCategory.ancestors" status="stat">
												<a href="ManageCategory.action?id=<s:property value="id"/>"><s:property value="name"/></a>
												
												<s:if test="!stat.last">
													&gt;
												</s:if>
											</s:iterator>
										</s:if>
									</div>
								</li>
								<li>
									<label>Top or Sub Category</label>
									<s:select list="#{'':'Any',false:'Sub Categories',true:'Top Categories'}" name="rule.rootCategory"/> 
									<pics:fieldhelp title="Top or Sub Category">
										<p>Only set this field if category is blank. This is auto selected by the system if a category has been selected.</p>
									</pics:fieldhelp>
								</li>
							</s:if>
							<li><label>Account Level</label>
								<div class="nobr">
									<s:radio 
										name="ruleAccountLevel" 
										list="#{'':'Any','Full':'Full Account','BidOnly':'Bid Only','ListOnly':'List Only'}" 
										value="rule.accountLevel"
										theme="pics"
										cssClass="inline"
									/>
								</div>
								<pics:fieldhelp title="Account Level">
									<p>Full Account (default) - Regular paying contractor account.</p>
									<p>Bid Only - A trial contractor account that is used for bidding.</p>
									<p>List Only - A renewable account with a subset of the PQF. Currently for Low Risk Material Suppliers.</p>
								</pics:fieldhelp>
							</li>
							<li>
								<label>Sole Proprietor</label>
								<div class="nobr">
									<s:radio 
										name="rule.soleProprietor" 
										list="#{'':'Any',false:'Not a Sole Proprietor',true:'Sole Proprietor'}"
										theme="pics"
										cssClass="inline" 
									/>
								</div>
							</li>
							<li>
								<label>Account Type</label>
								<s:radio
									name="rule.contractorType"
									list="accountTypeList"
									theme="pics"
									cssClass="inline"
								/>
							</li>
							<li>
								<label>Safety Risk</label>
								<div class="nobr">
									<s:radio 
										name="rule.safetyRisk" 
										list="#{'':'Any','Low':'Low','Med':'Medium','High':'High'}"
										theme="pics"
										cssClass="inline"
									/>
								</div>
							</li>
							<li>
								<label>Product Risk</label>
								<div class="nobr">
									<s:radio 
										name="rule.productRisk" 
										list="#{'':'Any','Low':'Low','Med':'Medium','High':'High'}"
										theme="pics"
										cssClass="inline"
									/>
								</div>
							</li>
							<li <s:if test="operatorRequired">class="required"</s:if>>
								<label>Operator</label>
								<s:textfield cssClass="autocomplete" id="operator" name="ruleOperatorAccountId" value="%{rule.operatorAccount.id}"/>
								<div id="operator_display">
									<s:if test="rule.operatorAccount != null">
										<a href="OperatorConfiguration.action?id=<s:property value="rule.operatorAccount.id"/>"><s:property value="rule.operatorAccount.name"/> Configuration</a>
									</s:if>
								</div>
								
								<s:if test="operatorRequired">
									<pics:fieldhelp title="Operator">
										<p>You must specify the Operator that this rule will apply to</p>
									</pics:fieldhelp>
								</s:if>
							</li>
							<li id="opTagli" class="requiresOperator"<s:if test="rule.operatorAccount==null">style="display: none;"</s:if>>
								<label>Tag</label>
								<s:select list="operatorTagList" name="ruleOperatorTagId" listKey="id" listValue="tag" id="tag" headerKey="0" headerValue="Any" value="rule.tag.id" />
							</li>
							<s:if test="auditTypeRule">
								<li><label>Dependent Audit</label>
									<s:select id="dependentAudit" name="ruleDependentAuditTypeId" value="rule.dependentAuditType.id" list="{}" headerKey="" headerValue="Any Audit Type">
										<s:iterator value="auditTypeMap" var="aType">
											<s:optgroup label="%{#aType.key}" list="#aType.value" listKey="id" listValue="name"/>
										</s:iterator>
									</s:select>
									<div id="dependentAudit_display"></div>
								</li>
								<li class="requiresDependentAudit">
									<s:include value="/struts/rules/_dependent_audit_status_select.jsp" />
								</li>					
							</s:if>
							<li>
								<label>Trade</label>
								<pics:autocomplete action="TradeAutocomplete" name="rule.trade" />
							</li>
							<li>
								<label>Question</label>
								<s:textfield cssClass="autocomplete" id="question" name="ruleQuestionId" value="%{rule.question.id}"/>
								<div id="question_display">
									<s:if test="rule.question != null">
										<a href="ManageAuditType.action?id=<s:property value="rule.question.auditType.id"/>"><s:property value="rule.question.auditType.name"/></a> &gt;
										<s:iterator value="rule.question.category.ancestors">
											<a href="ManageCategory.action?id=<s:property value="id"/>"><s:property value="name"/></a> &gt;
										</s:iterator>
										<a href="ManageQuestion.action?id=<s:property value="rule.question.id"/>"><s:property value="rule.question.name"/></a>
									</s:if>
								</div>
							</li>
							<li class="requiresQuestion">
								<label>Question Comparator</label>
								<s:select id="comparator" name="rule.questionComparator" list="@com.picsauditing.jpa.entities.QuestionComparator@values()" headerKey="" headerValue="Comparator"/>
							</li>
							<li class="requiresComparator">
								<label>Answer</label>
								<s:textfield name="rule.questionAnswer" />
							</li>
						</ol>
					</fieldset>
					
					<fieldset class="form ruleEditor submit" style="margin-bottom: 0px;">
						<s:if test="rule.id > 0 && ((!auditTypeRule && rule.priority < 300) || (auditTypeRule && rule.priority < 230))" >
							<s:checkbox label="label" id="ruleEditCheckbox" name="ruleEditCheckbox" value="false" fieldValue="false" />I understand that I am changing a rule with potentially broad reaching affects.
							<br />
							<div class="buttons">
								<input type="submit" class="picsbutton positive" name="button" value="Save" disabled="disabled"/>
								
								<s:if test="'New' != button">
									<input type="submit" class="picsbutton" name="button" value="Copy" disabled="disabled"/>
									<input type="submit" class="picsbutton negative" name="button" value="Delete" disabled="disabled"/>
								</s:if>
							</div>
						</s:if>
						<s:else>
							<input type="submit" class="picsbutton positive" name="button" value="Save"/>
							
							<s:if test="rule.id > 0">
								<input type="submit" class="picsbutton" name="button" value="Copy"/>
								<input type="submit" class="picsbutton negative" name="button" value="Delete"/>
							</s:if>
						</s:else>
					</fieldset>
				</s:if>
				
				<s:if test="rule.id > 0">
					<fieldset class="form moreGran">
						<h2 class="formLegend">More Granular</h2>
						<div id="moreRelated" style="padding-top:10px;"></div>
					</fieldset>
				</s:if>
			</div>
		</s:form>
	</body>
</html>