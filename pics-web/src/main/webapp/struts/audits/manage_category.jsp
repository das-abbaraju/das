<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ page import="com.picsauditing.toggle.FeatureToggle" %>
<%@ page import="com.picsauditing.service.i18n.TranslateUI" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title>Manage Category</title>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/manage_category.css?v=<s:property value="version"/>" />
		
		<s:include value="../jquery.jsp"/>
		
		<script type="text/javascript">
			$(function(){
				var sortList = $('#list').sortable({
					update: function() {
						$('#list-info').load('OrderAuditChildrenAjax.action?id=<s:property value="category.id"/>&type=AuditCategory', 
							sortList.sortable('serialize').replace(/\[|\]/g,''), 
							function() {sortList.effect('highlight', {color: '#FFFF11'}, 1000);}
						);
					},
					axis: 'y'
				});
			
				var sortListQ = $('#listQ').sortable({
					update: function() {
						$('#listQ-info').load('OrderAuditChildrenAjax.action?id=<s:property value="category.id"/>&type=AuditCategoryQuestions', 
							sortListQ.sortable('serialize').replace(/\[|\]/g,''), 
							function() {sortListQ.effect('highlight', {color: '#FFFF11'}, 1000);}
						);
					},
					axis: 'y'
				});

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

				showRules();
			});
			
			function showRules() {
				var data = {
					'comparisonRule.auditCategory': '<s:property value="id"/>'
				};
				
				$('#rules').think({message: "Loading Related Rules..." }).load('CategoryRuleTableAjax.action', data);
			}
		</script>
	</head>
	<body>
		<s:include value="manage_audit_type_breadcrumbs.jsp" />
        <s:include value="../config_environment.jsp" />
        <pics:toggle name="<%=FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER%>">
            <div class="alert">
                This system is currently set to use the new translations service. Changing translations from ManageTranslations will not have an
                effect while this is active. Please use <a href="https://translate.picsorganizer.com/" target="_blank"><%=TranslateUI.SHOW_TRANSLATION_URL_LINK_TEXT%></a>
            </div>
        </pics:toggle>
        <pics:toggleElse>
            <pics:toggle name="<%=FeatureToggle.TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE%>">
                <div class="alert">
                    This system is currently set to use the new translations datasource. Changing translations from ManageTranslations will
                    not have an effect while this is active. Please use <a href="https://translate.picsorganizer.com/" target="_blank"><%=TranslateUI.SHOW_TRANSLATION_URL_LINK_TEXT%></a>
                </div>
            </pics:toggle>
        </pics:toggleElse>

		<a class="preview" href="AuditCatPreview.action?categoryID=<s:property value="category.id" />&button=PreviewCategory">Preview Category</a>
		
		<s:form id="save">
			<s:hidden name="id" />
			
			<s:if test="category.auditType != null">
				<s:hidden name="category.auditType.id" />
			</s:if>
			
			<s:if test="categoryParent != null">
				<s:hidden name="categoryParent.id" />
			</s:if>
			
			<s:if test="category.auditType != null">
				<s:hidden name="parentID" value="%{category.auditType.id}" />
			</s:if>
			<s:elseif test="categoryParent != null">
				<s:hidden name="parentID" value="%{categoryParent.ancestors.get(0).auditType.id}" />
			</s:elseif>
			
			<fieldset class="form">
				<h2 class="formLegend">Category</h2>
				
				<ol>
					<li>
						<label>ID:</label>
						
						<s:if test="category.id > 0">
							<s:property value="category.id" />
							<s:set var="o" value="category" />
							<s:include value="../who.jsp" />
						</s:if>
						<s:else>
							NEW
						</s:else>
					</li>
					<li>
						<label>Category Name:</label>
						<s:textfield name="category.name" />
                        
                        <s:include value="/struts/translation/_listAllTranslationsForKey.jsp">
                            <s:param name="translation_key">AuditCategory.${category.id}.name</s:param>
                            <s:param name="include_locale_static">true</s:param>
                        </s:include>
					</li>
                    <pics:toggle name="<%=FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER%>"/>
                    <pics:toggleElse>
                        <pics:toggle name="<%=FeatureToggle.TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE%>"/>
                        <pics:toggleElse>
                            <li>
                                <a class="edit translate" href="ManageTranslations.action?button=Search&key=AuditCategory.<s:property value="category.id"/>." target="_BLANK">
                                    Manage Translations
                                </a>
                            </li>
                            <li>
                                <a
                                    class="edit translate"
                                    href="ManageCategory!findTranslations.action?id=<s:property value="category.id"/>">
                                    Translation Portal
                                </a>
                            </li>
                        </pics:toggleElse>
                    </pics:toggleElse>
					<li>
						<label>Unique Code:</label>
						<s:textfield name="category.uniqueCode" />
					</li>
					<li>
						<label># of columns</label>
						<s:textfield name="category.columns" />
						
						<pics:fieldhelp title="Multi-Column Layout">
							<p>
								This enables the multi-column layout for a category.
								The default is 1 column, and can support up to 3.
							</p>
							<p>
								This field disables the question's "Grouped With Previous" property.
								All questions will be shaded the same color.
							</p>
						</pics:fieldhelp>
					</li>
					<li>
						<label>Score Weight</label>
						<s:textfield name="category.scoreWeight"/>
					</li>
                    <li>
                        <label>Effective Date:</label>
                        <s:date name="category.effectiveDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" var="category_effective_date" />
                        <s:textfield name="category.effectiveDate" value="%{#category_effective_date}" cssClass="datepicker" />
                    </li>

                    <s:if test="category.id > 0">
                        <li>
                            <label>Expiration Date:</label>
                            <s:date name="category.expirationDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" var="category_expiration_date"/>
                            <s:textfield name="category.expirationDate" value="%{#category_expiration_date}" cssClass="datepicker" />
                        </li>
                    </s:if>

                    <li>
						<label># of Questions:</label>
						<s:property value="category.numQuestions"/>
					</li>
					<li>
						<label># Required:</label>
						<s:property value="category.numRequired"/>
					</li>
					<li>
						<label>Help Text:</label>
						<s:textarea 
							name="category.helpText"
							cssStyle="width: 480px;"
							rows="5"
							value="%{(category.helpText != null && !category.helpText.equals('') && !category.helpText.equals(category.getI18nKey('helpText')))  ? category.helpText : ''}" />
					</li>
					
					<s:if test="category.auditType.dynamicCategories">
						<li>
							<label>Apply on Question:</label>
							<s:textfield name="applyOnQuestionID" />
							
							<s:if test="applyOnQuestionID > 0">
								<a href="ManageQuestion.action?id=<s:property value="applyOnQuestionID" />">Show</a>
							</s:if>
							
							<pics:fieldhelp title="Apply on Question">
								<p>This field is only available on audits with dynamic categories</p>
							</pics:fieldhelp>
						</li>
						<li>
							<label>When Answer is:</label>
							<s:textfield name="category.applyOnAnswer" />
						</li>
					</s:if>
                    
					<s:if test="category.auditType.languages.size > 1">
						<li>
							<label>
								<s:text name="ManageAuditType.RequiredLanguages" />:
							</label>
							<s:optiontransferselect
								name="requiredLanguagesName"
								list="getAvailableLocalesFrom(category.parent)"
								listKey="language"
								listValue="%{displayLanguage + (displayCountry == '' ? '' : ' (' + displayCountry + ')')}"
								doubleName="category.languages"
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
			
			<fieldset class="form submit">
                <pics:permission perm="ManageAudits" type="Edit" >
				<div>
					<input type="submit" class="picsbutton positive" name="button" value="Save" id="save"/>
					
					<s:if test="category.subCategories.size == 0 && category.questions.size == 0">
						<input type="submit" class="picsbutton negative" name="button" value="Delete" onclick="return confirm('Are you sure you want to delete this category?');" id="delete" />
					</s:if>
					
					<input type="submit" class="picsbutton" name="button" value="UpdateAllAuditsCategories" id="updateAllAuditsCategories"/>	
				</div>
                </pics:permission >
            </fieldset>
		</s:form>
		
		<s:if test="id != 0">
			<div>
				<h3>Child Categories</h3>
				
				<ul class="list" id="list" title="Drag and drop to change order">
					<s:iterator value="category.subCategories">
						<li id="item_<s:property value="id"/>" title="Drag and drop to change order" <s:if test="!current">style="font-style: italic"</s:if>>
							<s:property value="number" />.
					    	<a href="ManageCategory.action?id=<s:property value="id"/>"><s:property value="name.toString().trim().length() == 0 ? 'empty' : name"/></a>
						</li>
					</s:iterator>
				</ul>

                <pics:permission perm="ManageAudits" type="Edit" >
				    <a id="addNewSubCategory" class="add" href="ManageCategory.action?button=AddNew&parentID=<s:property value="category.parentAuditType.id"/>&categoryParent.id=<s:property value="category.id" />">Add New Sub Category</a>
                </pics:permission >

				<div id="list-info"></div>
			</div>
			
			<br clear="all" />
			
			<div>
				<h3>Questions</h3>
				
				<ul class="list" id="listQ" title="Drag and drop to change order">
					<s:iterator value="category.questions">
					    <li id="item_<s:property value="id"/>" <s:if test="!current">style="font-style: italic"</s:if>><s:property value="number"/>.
					    <a href="ManageQuestion.action?id=<s:property value="id"/>"><s:if test="name != null"><s:property value="name.toString().length()>100 ? name.toString().substring(0,97) + '...' : name"/></s:if><s:else>EMPTY</s:else></a></li>
					</s:iterator>
				</ul>

                <pics:permission perm="ManageAudits" type="Edit" >
				    <a id="addNewQuestion" class="add" href="ManageQuestion.action?button=AddNew&parentID=<s:property value="category.id"/>&categoryParent.id=<s:property value="category.id"/>&question.category.id=<s:property value="category.id"/>">Add New Question</a>
                </pics:permission >

                <div id="listQ-info"></div>
			</div>
			<br/>
			
			<h3>Related Rules</h3>
			<div id="rules"></div>
			
			<pics:permission perm="ManageCategoryRules" type="Edit">
				<a id="addNewCategoryRule" href="CategoryRuleEditor.action?button=New&ruleAuditTypeId=<s:property value="category.auditType.id" />&ruleAuditCategoryId=<s:property value="category.id" />" class="add">Add New Category Rule</a>
			</pics:permission>
		</s:if>
	</body>
</html>