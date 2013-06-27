<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
    <head>
        <title>Manage Question</title>
        
        <link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
        <link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
        <link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
        <link rel="stylesheet" type="text/css" media="screen" href="js/jquery/mcdropdown/css/jquery.mcdropdown.min.css?v=${version}" />
        
        <s:include value="../jquery.jsp"/>
        
        <script type="text/javascript" src="js/jquery/jquery.bgiframe.min.js?v=${version}"></script>
        <script type="text/javascript" src="js/jquery/mcdropdown/jquery.mcdropdown.min.js?v=${version}"></script>
        
        <script type="text/javascript">
            $(function(){
            	$('select[name="question.questionType"]').change(function () {
            		if($(this).val() == 'MultipleChoice') {
            			$('.scoreWeight').show();
            			$('#optionTypes').show();
            		} else {
            			$('.scoreWeight').hide();
            			$('.scoreWeight input[name="question.scoreWeight"]').val(0);
            			$('#optionTypes').hide();
            		}
            	}).trigger('change');
            
            	showFlags();
            	showAuditTypeRules();
            	showCategoryRules();
            	toggleOtherOptionType();

                $('.datepicker').datepicker({
                    changeMonth : true,
                    changeYear : true,
                    yearRange : '1950:2049',
                    showOn : 'button',
                    buttonImage : 'images/icon_calendar.gif',
                    buttonImageOnly : true,
                    buttonText : translate('JS.ChooseADate'),
                    constrainInput : true,
                    showAnim : 'fadeIn',
                    minDate: 1,
                    dateFormat: 'yy-mm-dd'
                });
            });
            
            function copyQuestion(atypeID) {
            	$('#copy_audit').load('ManageQuestionCopyAjax.action', {
            	    button: 'text', 
            	    id: atypeID
        	    }, function () {
            			$(this).dialog({
            				modal: true,
            				title: 'Copy Question',
            				width: '55%',
            				close: function (event, ui) {
            					$(this).dialog('destroy');
            					location.reload();
            				},
            				buttons: {
            					Cancel: function () {
            						$(this).dialog('close');
            					},
            					'Copy Question': function () {
            						var data = $('form#textForm').serialize();
            						
            						data += "&button=Copy&originalID="+atypeID;
            						
            						startThinking({
            						    div: 'copy_audit', 
            						    message: 'Copying Question...'
        						    });
            						
            						$.ajax(
            							{
            								url: 'ManageQuestionCopyAjax.action',
            								data: data,
            								complete: function () {
            									stopThinking({
            									    div: 'copy_audit'
        									    });
            									
            									$(this).dialog('close');
            									
            									location.reload();
            								}
            							}
            						);
            					}
            				}
            			});
            		}
            	);
            }
            
            function moveQuestion(atypeID) {
            	$('#copy_audit').load('ManageQuestionMoveAjax.action', {
            	    button: 'text', 
            	    'id': atypeID
        	    }, function () {
            			$(this).dialog({
            				modal: true,
            				title: 'Move Question',
            				width: '55%',
            				close: function (event, ui) {
            					$(this).dialog('destroy');
            				},
            				buttons: {
            					Cancel: function() {
            						$(this).dialog('close');
            					},
            					'Move Question': function () {
            						var data = $('form#textForm').serialize();
            						
            						data += "&button=Move&originalID="+atypeID;
            						
            						startThinking({
            						    div: 'copy_audit', 
            						    message: 'Moving Question...'
        						    });
            						
            						$.ajax({
        								url: 'ManageQuestionMoveAjax.action',
        								data: data,
        								complete: function () {
        									stopThinking({
        									    div: 'copy_audit'
    									    });
        									
        									$(this).dialog('close');
        									
        									location.reload();
        								}
        							});
            					}
            				}
            			});
            		}
            	);
            }
            
            function showFlags() {
            	var data = {
    				questionID: <s:property value="id"/>
            	};
            	
            	startThinking({
            	    div: "flags", 
            	    message: "Loading Related Flag Criteria"
        	    });
            	
            	$('#flags').load('FlagCriteriaListAjax.action', data);
            }
            
            function showAuditTypeRules() {
            	var data = {
        			'comparisonRule.question.id': <s:property value="id"/>
            	};
            	
            	$('#auditrules').think({
            	    message: "Loading Related Audit Rules..."
        	    }).load('AuditTypeRuleTableAjax.action', data);
            }
            
            function showCategoryRules() {
            	var data = {
        			'comparisonRule.question.id': <s:property value="id"/>
            	};
            	
            	$('#categoryrules').think({
            	    message: "Loading Related Category Rules..."
        	    }).load('CategoryRuleTableAjax.action', data);
            }
            
            function toggleOtherOptionType() {
            	if ($('#optionTypes').val() == 0 || $('#optionTypes').val() == null) {
            		$('#optionTypeOther_text').show();
            	} else {
            		$('#optionTypeOther_text').hide();
            		$('#optionTypeOther_text').val('');
            		$('#optionTypeOther_hidden').val($('#optionTypes').val());
            	}
            }
            
            function showOption() {
            	if ($('#questionTypes select').val() == 'MultipleChoice' || 
            	        $('#questionTypes select').val() == 'Tagit' ||
            	        $('#questionTypes select').val() == 'MultiSelect') {
            		$('#optionTypesArea').show();
            		toggleOtherOptionType();
            	} else {
            		$('#optionTypesArea').hide();
            		$('#optionTypes').val('');
            		toggleOtherOptionType();
            	}
            }
        </script>
    </head>
    <body>
        <s:include value="manage_audit_type_breadcrumbs.jsp" />
        <s:include value="../config_environment.jsp" />
        <s:include value="../actionMessages.jsp" />
        
        <s:form id="save" cssClass="form">
        	<s:hidden name="id" />
        	<s:hidden name="parentID" value="%{question.category.id}"/>
            
        	<fieldset class="form">
            	<h2 class="formLegend">Question</h2>
                
            	<ol>
            		<li>
                        <label>ID:</label>
            			
                        <s:if test="question.id > 0">
            				<s:property value="question.id" />
            				<s:set var="o" value="question" />
            				<s:include value="../who.jsp" />
            			</s:if>
            			<s:else>
            				NEW
            			</s:else>
            		</li>
            		<li>
            			<a class="edit translate" href="ManageTranslations.action?button=Search&key=AuditQuestion.<s:property value="question.id"/>." target="_BLANK">Manage Translations</a>
            		</li>
            		<li>
                        <label>Question Type:</label>
            			<div id="questionTypes">
            				<s:select list="questionTypes" 
                                name="question.questionType" 
                                headerKey="" 
                                headerValue="" 
                                onchange="showOption()" />
            			</div>
            		</li>
            		<li class="required">
            			<div id="optionTypesArea" style="display: none; clear: left;">
            				<label>Option Type:</label>
            				<s:select list="optionTypes" 
                                headerKey="0" 
                                headerValue="- Other -" 
                                listKey="id" 
                                value="%{question.option.id}" 
                                listValue="name" 
                                id="optionTypes" 
                                onchange="toggleOtherOptionType();" />
            				
                            <pics:autocomplete action="OptionGroupAutocomplete" name="question.option" htmlName="question.option" value="question.option" htmlId="optionTypeOther" />
                            
            				<a href="ManageOptionGroup.action?question=<s:property value="question.id" />&group.id=0&editOnly=true" class="add">Create Option</a>
            				<a href="ManageOptionGroup.action?question=<s:property value="question.id" />" class="preview">See all Option Types</a>
            			</div>
                        
            			<pics:fieldhelp title="Option Type">
            				<p>The type of widget to use on the user interface.</p>
                            
            				<ul>
            					<li>Yes/No</li>
            				</ul>
                            
            				<p>Commonly used option types are listed in the dropdown.</p>
            				<p>If an option type is not listed, select "- Other -" and search by the option name.</p>
            				<p>If an option type does not exist, click on the "Create Option" link to create a new option type.</p>
            			</pics:fieldhelp>
            		</li>
            		<li>
                        <label>Question Text:</label>
            			<s:textarea name="question.name" rows="8" />
                        
                        <s:include value="/struts/translation/_listAllTranslationsForKey.jsp">
                            <s:param name="translation_key">AuditQuestion.${question.id}.name</s:param>
                            <s:param name="include_locale_static">true</s:param>
                        </s:include>
            		</li>
            		<li>
	                    <label>Title:</label>
						<s:textfield
							name="question.title"
							size="65"
							value="%{(question.title != null && !question.title.equals(question.getI18nKey('title'))) ? question.title : ''}" />
					</li>
            		<li>
                        <label>Required:</label>
            			<s:checkbox name="question.required" />
						<pics:fieldhelp title="Required">
            				<p>If checked, the contractor will be required to answer this question.</p>
            			</pics:fieldhelp>
            		</li>
            		<li>
                        <label>Effective Date:</label>
                        <s:date name="question.effectiveDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" var="question_effective_date" />
            			<s:textfield name="question.effectiveDate" value="%{#question_effective_date}" cssClass="datepicker" />
            		</li>
                    
            		<s:if test="question.id > 0">
            			<li>
                            <label>Expiration Date:</label>
                            <s:date name="question.expirationDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" var="question_expiration_date"/>
                            <s:textfield name="question.expirationDate" value="%{#question_expiration_date}" cssClass="datepicker" />
            			</li>
            			<li>
                            <label>Added:</label>
            				<s:date name="question.creationDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
            			</li>
            			<li>
                            <label>Updated:</label>
            				<s:date name="question.updateDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
            			</li>
            		</s:if>
                    
            		<li>
                        <label>Column Header:</label>
						<s:textfield
							name="question.columnHeader"
							size="20"
							maxlength="30"
							value="%{(question.columnHeader != null && !question.columnHeader.equals(question.getI18nKey('columnHeader')) ? question.columnHeader : ''}" />
					</li>
            		<li>
                        <label>Field Identifier:</label>
            			<s:textfield name="question.uniqueCode" size="20" maxlength="50"/>
            		</li>
                    
            		<s:if test="question.category.languages.size > 1">
            			<li>
            				<label><s:text name="ManageAuditType.RequiredLanguages" />:</label>
            				<s:optiontransferselect
            					name="requiredLanguagesName"
            					list="getAvailableLocalesFrom(question.category)"
            					listKey="language"
            					listValue="%{displayLanguage + (displayCountry == '' ? '' : ' (' + displayCountry + ')')}"
            					doubleName="question.languages"
            					doubleList="selectedLocales"
            					doubleListKey="language"
            					doubleListValue="%{displayLanguage + (displayCountry == '' ? '' : ' (' + displayCountry + ')')}"
            					leftTitle="%{getText('ManageAuditType.AvailableLanguages')}"
            					rightTitle="%{getText('ManageAuditType.SelectedLanguages')}"
            					addToLeftLabel="%{getText('button.Remove')}"
            					addToRightLabel="%{getText('button.Add')}"
            					allowAddAllToLeft="false"
            					allowAddAllToRight="false"
            					allowSelectAll="false"
            					allowUpDownOnLeft="false"
            					allowUpDownOnRight="false"
            					buttonCssClass="arrow"
            					theme="pics"
            				>
            					<s:param name="sort" value="'false'" />
            				</s:optiontransferselect>
            			</li>
            		</s:if>
            	</ol>
        	</fieldset>
            
        	<s:if test="question.functions.size() > 0">
            	<fieldset>
            		<h2>Functions</h2>
            		<table class="report">
                		<thead>
                    		<tr>
                    			<th>
                                    Type
                                </th>
                    			<th>
                                    Function
                                </th>
                    			<th>
                                    Watchers (Operands)
                                </th>
                    		</tr>
                		</thead>
                        
                		<s:iterator value="question.functions">
                			<tr>
                				<td>
                                    <s:property value="type"/>
                                </td>
                				<td>
                                    <s:property value="function"/>
                                </td>
                				<td>
                					<table class="inner">
                    					<s:iterator value="watchers" var="w">
                    						<tr>
                    							<td>
                                                    <s:property value="#w.question.id"/>
                                                </td>
                    							<td>
                                                    <a href="ManageQuestion.action?id=<s:property value="#w.question.id"/>"><s:property value="#w.question.name"/></a>
                                                </td>
                    						</tr>
                    					</s:iterator>
                					</table>
                				</td>
                			</tr>
                		</s:iterator>
            		</table>
            	</fieldset>
        	</s:if>
            
        	<s:if test="question.functionWatchers.size() > 0">
            	<fieldset>
            		<h2>Function Watchers</h2>
            		<table class="report">
                		<thead>
                    		<tr>
                    			<th>
                                    Type
                                </th>
                    			<th>
                                    Function
                                </th>
                    			<th>
                                    Affected Questions
                                </th>
                    		</tr>
                		</thead>
                        
                		<s:iterator value="question.functionWatchers" var="w">
                			<tr>
                				<td>
                                    <s:property value="#w.function.type"/>
                                </td>
                				<td>
                                    <s:property value="#w.function.function"/>
                                </td>
                				<td>
                					<table class="inner">
                						<tr>
                							<td>
                                                <s:property value="#w.function.question.id"/>
                                            </td>
                							<td>
                                                <a href="ManageQuestion.action?id=<s:property value="#w.function.question.id"/>"><s:property value="#w.function.question.name"/></a>
                                            </td>
                						</tr>
                					</table>
                				</td>
                			</tr>
                		</s:iterator>
            		</table>
            	</fieldset>
        	</s:if>
            
        	<fieldset class="form">
            	<h2 class="formLegend">Additional Options</h2>
                
            	<ol>
            		<li>
                        <label>Has Requirement:</label>
            			<s:checkbox name="question.hasRequirement"/>
            			
                        <pics:fieldhelp title="Has Requirement">
            				<p>If this is question has a requirement, you MUST make the question required as well.</p>
            			</pics:fieldhelp>
            		</li>
            		<li>
                        <label>OK Answer:</label>
            			<s:textfield name="question.okAnswer" />
            		</li>
            		<li>
                        <label>Requirement</label>
            			<s:textarea
            				name="question.requirement"
            				value="%{(question.requirement != null && !question.requirement.equals(question.getI18nKey('requirement'))) ? question.requirement : ''}" />
                        
                        <s:include value="/struts/translation/_listAllTranslationsForKey.jsp">
                            <s:param name="translation_key">AuditQuestion.${question.id}.requirement</s:param>
                            <s:param name="include_locale_static">true</s:param>
                        </s:include>
            		</li>
                    
            		<s:if test="auditType.scoreable">
            			<li class="scoreWeight" <s:if test="!(question.questionType.equals('MultipleChoice'))">style="display: none;"</s:if>>
                            <label>Score Weight:</label>
            				<s:textfield name="question.scoreWeight" />
                            
            				<pics:fieldhelp title="Score Weight">
            					<p>This number will affect the strength of the score</p>
            				</pics:fieldhelp>
            			</li>
            		</s:if>
                    
            		<li>
                        <label>Required by Question:</label>
            			<s:textfield name="requiredQuestionID" />
            			
                        <s:if test="requiredQuestionID > 0">
                            <a href="?id=<s:property value="requiredQuestionID" />">Show</a>
                        </s:if>
                        
            			<pics:fieldhelp title="Required by Question">
            				<p>The question the contractor must answer in order for this question to become required.</p>
            			</pics:fieldhelp>
            		</li>
            		<li>
                        <label>Required Answer:</label>
            			<s:textfield name="question.requiredAnswer" />
                        
            			<pics:fieldhelp title="Required Answer">
            				<p>The answer the contractor must give on the "Required By" question in order for this question to become required.</p>
            			</pics:fieldhelp>
            		</li>
            		<li>
                        <label>Visible Question:</label>
            			<s:textfield name="visibleQuestionID" />
            			
                        <pics:fieldhelp title="Visible Question">
            				<p>The question the contractor must answer in order for this question to be displayed.</p>
            			</pics:fieldhelp>
            		</li>
            		<li>
                        <label>Visible Answer:</label>
            			<s:textfield name="question.visibleAnswer" />
            			
                        <pics:fieldhelp title="Visible Answer">
            				<p>The answer the contractor must give on the "Visible By" question in order for this question to be displayed.</p>
            			</pics:fieldhelp>
            		</li>
            		<li>
                        <label>Risk Level:</label>
            			<s:select list="@com.picsauditing.jpa.entities.LowMedHigh@values()" name="question.riskLevel" />
            		</li>
            		<li>
                        <label>Grouped with Previous:</label>
            			<s:checkbox name="question.groupedWithPrevious"/>
            		</li>
            	</ol>
        	</fieldset>
            
        	<fieldset class="form">
            	<h2 class="formLegend">Help</h2>
                
            	<ol>
            		<li>
                        <label>Show Comments:</label>
            			<s:checkbox name="question.showComment" value="question.showComment"/>
            		</li>
            		<li>
                        <label>Help Page:</label>
            			<div>
            				<s:textfield name="question.helpPage" size="30" maxlength="100" />
            				
                            <s:if test="!isStringEmpty(question.helpPage)">
                                <a href="http://help.picsauditing.com/wiki/<s:property value="question.helpPage"/>">Help Center</a>
                            </s:if>
            				<s:else>
                                help.picsauditing.com/wiki/???
                            </s:else>
            			</div>
            		</li>
            		<li>
                        <label>Help Text:</label>
						<s:textarea
							name="question.helpText"
							rows="4"
							value="%{(question.helpText != null && !question.helpText.equals(question.getI18nKey('helpText'))) ? question.helpText : ''}" />
	
						<s:include value="/struts/translation/_listAllTranslationsForKey.jsp">
                            <s:param name="translation_key">AuditQuestion.${question.id}.helpText</s:param>
                            <s:param name="include_locale_static">true</s:param>
                        </s:include>
            		</li>
            	</ol>
        	</fieldset>
            
        	<s:if test="extractable">
        		<fieldset class="form">
            		<h2 class="formLegend">Extract Options</h2>
                    
            		<ol>
            			<li>
                            <label>Define Extract Options:</label>
            				<s:checkbox name="extractOptionDefined" value="extractOptionDefined"/>
            			</li>
            			<li>
                            <label>Start Search at Beginning:</label>
            				<s:checkbox name="startAtBeginning" value="startAtBeginning"/>
            			</li>
            			<li>
                            <label>Start Searching For Question After:</label>
            				<s:textfield name="startingPoint" />
            			</li>
            			<li>
                            <label>Collect Response As Lines:</label>
            				<s:checkbox name="collectAsLines" value="collectAsLines"/>
            			</li>
            			<li>
                            <label>Stop Collecting Response At:</label>
            				<s:select name="stopAt" list="stopAtOptions" value="stopAt"></s:select>
            			</li>
            			<li>
                            <label>Stop Processing Question At This Text:</label>
            				<s:textfield name="stoppingPoint" />
            			</li>
            		</ol>
        		</fieldset>
                
        		<fieldset class="form">
        		<h2 class="formLegend">Transforms</h2>
        		<ol>
        		<s:if test="transformOptions.size > 0">
        			<li>
        				<table class="report">
        				<thead>
        				<tr>
        					<th>Description</th>
        					<th>Dest. Question</th>
        					<th></th>
        				</tr>
        				</thead>
        				<s:iterator value="transformOptions" var="t">
        					<tr>
        						<td><s:property value="#t.description.length()>70 ? #t.description.substring(0,67) + '...' : #t.description"/></td>
        						<td><a href="ManageQuestion.action?id=<s:property value="#t.destinationQuestion.id"/>"><s:property value="#t.destinationQuestion.name.toString().length()>70 ? #t.destinationQuestion.name.toString().substring(0,67) + '...' : #t.destinationQuestion.name"/></a></td>
        						<td><a href="ManageTransformOption.action?id=<s:property value="question.id"/>&transformId=<s:property value="#t.id"/>">Edit</a></td>
        					</tr>
        				</s:iterator>
        				</table>
        			</li>
        		</s:if>
        		<li>
        			<a href="ManageTransformOption.action?id=<s:property value="id"/>" class="add">Add Transform</a>
        		</li>
        		</ol>
        		</fieldset>
        	</s:if>
        	<fieldset class="form submit">
        		<div>
        			<button id="save" class="picsbutton positive" name="button" type="submit" value="save">Save</button>
        			<input type="button" id="copyQuestion" class="picsbutton" value="Copy" onclick="copyQuestion(<s:property value="id"/>)"/>
        			<input type="button" id="moveQuestion" class="picsbutton" value="Move" onclick="moveQuestion(<s:property value="id"/>)"/>
        		<s:if test="question.id > 0">
        			<input type="submit" name="button" class="picsbutton negative" value="Delete"
        				onclick="return confirm('Are you sure you want to delete this question?');" />
        		</s:if>
        		</div>
        	</fieldset>
        </s:form>
        
        <ul id="allCategories" style="display: none" class="mcdropdown_menu">
        	<s:iterator value="category.ancestors.get(0).auditType.categories">
        		<s:if test="id != category.id">
        			<li rel="<s:property value="id" />"><s:property value="number" />. <s:property value="name" />
        				<s:include value="manage_category_subcategories.jsp" />
        			</li>
        		</s:if>
        	</s:iterator>
        </ul>
        
        <div id="copy_audit"></div>
        
        <br/>
        
        <h3>Related Flag Criteria</h3>
        <div id="flags"></div>
        <a href="ManageFlagCriteria!edit.action?criteria.displayOrder=999&criteria.question=<s:property value="question.id" />&criteria.requiredStatus=Submitted&criteria.category=Paperwork<s:if test="question.columnHeader" >&criteria.label=<s:property value="auditType.name" />%3A%20<s:property value="question.columnHeader" />&criteria.description=<s:property value="auditType.name" />%3A%20<s:property value="question.columnHeader" /></s:if>" class="add">Add New Question Flag Criteria</a>
        
        <br />
        <br />
        
        <h3>Related Audit Type Rules</h3>
        <div id="auditrules"></div>
        
        <pics:permission perm="ManageAuditTypeRules" type="Edit" >
        	<a href="AuditTypeRuleEditor.action?button=New&ruleQuestionId=<s:property value="id" />" class="add">Add New Audit Type Rule</a>
        </pics:permission>
        
        <br/>
        <br/>
        
        <h3>Related Category Rules</h3>
        <div id="categoryrules"></div>
        
        <pics:permission perm="ManageCategoryRules" type="Edit" >
        	<a href="CategoryRuleEditor.action?button=New&ruleQuestionId=<s:property value="id" />" class="add">Add New Category Rule</a>
        </pics:permission>
    </body>
</html>