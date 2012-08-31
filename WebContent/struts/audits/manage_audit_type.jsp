<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
    <title>
        <s:if test="auditType.id > 0">
            <s:property value="auditType.name" />
        </s:if>
        <s:else>
            Create New Audit Type
        </s:else>
    </title>
    
    <link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
    
    <s:include value="../jquery.jsp"/>
    
    <script type="text/javascript">
        $(function(){
            var sortList = $('#list').sortable({
                update: function() {
                    $('#list-info').load('OrderAuditChildrenAjax.action?id=<s:property value="auditType.id"/>&type=AuditType', 
                        sortList.sortable('serialize').replace(/\[|\]/g,''), 
                        function() {sortList.effect('highlight', {color: '#FFFF11'}, 1000);}
                    );
                }
            });
            
            showFlags();
            showRules();
            showWorkFlowSteps();
            
            $('.cluetip').cluetip({
                closeText: "<img src='images/cross.png' width='16' height='16'>",
                arrows: true,
                cluetipClass: 'jtip',
                local: true,
                clickThrough: false
            });
            
            <s:if test="">
                var jsonObj = <s:property value="" />
            </s:if>    
        
            $('#save_workFlowID').live('change', function(){
                var id = $(this).find(':selected').val(); 
                
                if(id > 0){
                    var link = $('<a>').attr({'href': 'ManageAuditWorkFlow.action?id='+id, 'class': 'go'}).append('Go');
                    $('.workflow_go').html(link);
                } else {
                    $('.workflow_go').html('');
                }
                
                showWorkFlowSteps();
            });
        });
        
        function showFlags() {
            var data = {
                auditTypeID: '<s:property value="id"/>'
            };
            
            startThinking({ div: "flags", message: "Loading Related Flag Criteria" });
            $('#flags').load('FlagCriteriaListAjax.action', data);
        }
        
        function showRules() {
            var data = {
                    'comparisonRule.auditType.id': '<s:property value="id"/>'
            };
            startThinking({ div: "rules", message: "Loading Related Rules" });
            $('#rules').load('AuditTypeRuleTableAjax.action', data);
        }
        
        function showWorkFlowSteps(){
            var data = {
                workFlowID: $('#save_workFlowID').val()
            };
            startThinking({ div: "workflow_step_area", message: "Loading Work Flow Steps" });
            $('.workflow_step_area').load('ManageAuditType!workFlowSteps.action',data);
        }
    </script>
</head>
<body>
    <s:include value="manage_audit_type_breadcrumbs.jsp" />
    
    <s:include value="../config_environment.jsp" />
    
    <s:if test="auditType.id > 0">
        <a class="edit" href="ManageAuditTypeHierarchy.action?id=<s:property value="auditType.id"/>">Manage Hierarchy</a>
    </s:if>
    
    <s:form id="save">
        <s:hidden name="id"></s:hidden>
        
        <div>
            <fieldset class="form">
                <h2 class="formLegend">
                    Audit Type
                </h2>
                
                <ol>
                    <li>
                        <label>ID:</label>
                        
                        <s:if test="auditType.id > 0">
                            <s:property value="auditType.id" />
                            <s:set var="o" value="auditType" />
                            <s:include value="../who.jsp" />
                        </s:if>
                        <s:else>
                            NEW
                        </s:else>
                    </li>
                    <li>
                        <label>Name:</label>
                        <s:textfield name="auditType.name" />
                        
                        <s:include value="/struts/translation/_listAllTranslationsForKey.jsp">
                            <s:param name="translation_key">AuditType.${auditType.id}.name</s:param>
                            <s:param name="include_locale_static">true</s:param>
                        </s:include>
                        
                        <pics:fieldhelp title="Audit Name">
                            <p>
                                The name of the document, policy type, audit, or operator specific form
                            </p>
                        </pics:fieldhelp>
                    </li>
                    <li>
                        <a
                            class="edit translate" 
                            href="ManageTranslations.action?button=Search&key=AuditType.<s:property value="auditType.id"/>."
                            target="_BLANK">
                            Manage Translations
                        </a>
                    </li>
                    <li>                    
                        <a
                            class="edit translate" 
							href="ManageAuditType!findTranslations.action?id=<s:property value="auditType.id"/>">
                            Translation Portal
                        </a>
                    </li>
                    <li>
                        <label>Class:</label>
                        <s:select list="classList" name="auditType.classType" />
                    </li>
                    <li>
                        <label>Sort Order:</label>
                        <s:textfield name="auditType.displayOrder" />
                    </li>
                    <li>
                        <label>Description:</label>
                        <s:textfield name="auditType.description" />
                        
                        <pics:fieldhelp title="Description">
                            <p>
                                An optional description used for reference. Currently this is not used anywhere.
                            </p>
                        </pics:fieldhelp>
                    </li>
                    <li>
                        <label>Has Multiple:</label>
                        <s:checkbox name="auditType.hasMultiple" />
                        
                        <pics:fieldhelp title="Has Multiple">
                            <p>
                                Check this box if a given contractor can have more than one of these types of audits active at the same time.
                                This is usually NOT checked.
                            </p>
                        </pics:fieldhelp>
                    </li>
                    <li>
                        <label>Can Renew:</label>
                        <s:checkbox name="auditType.renewable" />
                        
                        <pics:fieldhelp title="Can Renew">
                            <p>
                                Check this box if the document or audit is reusable at the end of its life.
                                For example, PQF is renewable because we don't make them fill out a whole new PQF each year.
                                GL Policy is NOT renewable because we force them to fill out a brand new policy each time.
                                One major drawback to renewable audit types is they don't maintain a history of past audits.
                            </p>
                        </pics:fieldhelp>
                    </li>
                    <li>
                        <label>Score Type:</label>
                        <s:select
                            name="auditType.scoreType"
                            list="@com.picsauditing.jpa.entities.ScoreType@values()"
                            headerKey=""
                            headerValue="- Score Type -"
                        />
                        
                        <pics:fieldhelp title="Scoreable">
                            <p>
                                This field is for scoreable audits.
                            </p>
                            <ol>
                                <li>
                                    ACTUAL - score is a calculation of the questions only.
                                    [Calculation = SUM(question.scoreWeight * (question.option.scoreValue / question.option.maxValue) )]
                                </li>
                                <li>
                                    PERCENT - score is a calculation of the questions only expressed as a percentage.
                                    [Calculation = MAX(SUM(question.scoreWeight * (question.option.scoreValue / question.option.maxValue) ), 100)]
                                </li>
                                <li>
                                    WEIGHTED - score is a calculation of the questions weighted by category.
                                    [Calculation = RAW_SCORE * (category.scoreWeight / SUM(siblingCategories.scoreWeight)]
                                </li>
                            </ol>
                        </pics:fieldhelp>
                    </li>
                    <li>
                        <label>Is Scheduled:</label>
                        <s:checkbox name="auditType.scheduled" />
                        
                        <pics:fieldhelp>
                            Check this box if this can be scheduled to be performed at a specific date and time
                        </pics:fieldhelp>
                    </li>
                    <li>
                        <label>Has Safety Professional:</label>
                        <s:checkbox name="auditType.hasAuditor" />
                    </li>
                    <li>
                        <label>Contractor Can View:</label>
                        <s:checkbox name="auditType.canContractorView" />
                    </li>
                    <li>
                        <label>Contractor Can Edit:</label>
                        <s:checkbox name="auditType.canContractorEdit" />
                    </li>
                    <li>
                        <label>Operator Can View:</label>
                        <s:checkbox name="auditType.canOperatorView" />
                    </li>
                    <li>
                        <label>Permission to Edit:</label>
                        <s:select
                            name="editPerm"
                            list="@com.picsauditing.access.OpPerms@values()"
                            listValue="%{getText(getI18nKey('description'))}" 
                            headerKey=""
                            headerValue="Only PICS"
                            listKey="name()"
                            value="editPerm"
                        />
                        
                        <pics:fieldhelp title="Permission to Edit">
                            For Operators and PICS Admins this will restrict the ability to edit this audit 
                            type to that permission.
                        </pics:fieldhelp>
                    </li>                
                    <li>
                        <label>Workflow:</label>
                        <s:select
                            list="workFlowList"
                            name="workFlowID"
                            listKey="id"
                            listValue="name"
                            value="auditType.workFlow.id" 
                            headerKey="0"
                            headerValue="- Select Workflow -"
                        />
    
                        <div class="workflow_step_area"/></div>
                        
                        <div class="workflow_go">
                            <s:if test="auditType.workFlow.id > 0">
                                <a href="ManageAuditWorkFlow.action?id=<s:property value="auditType.workFlow.id" />" class="go">
                                    Go
                                </a>
                            </s:if>
                        </div>
                        
                        <pics:fieldhelp title="Workflow">
                            Required: This describes the workflow steps or lifecycle that this audit goes through.
                            If you're not sure, then start with Single Step Workflow, which is the simplest.
                        </pics:fieldhelp>
                    </li>
                    <li>
                        <label>Months to Expire:</label>
                        <s:textfield name="auditType.monthsToExpire" />
                        
                        <pics:fieldhelp>Determines when the audit will expire.
                            <ul>
                                <li>
                                    Set to zero to never expire.
                                </li>
                                <li>
                                    Leave blank to set to March 1st of the following year.
                                </li>
                                <li>
                                    If greater than 0 and class type is PQF, then this number of months is added to
                                    March 1st of this year.
                                    For example, 12 would be the same as leaving it blank.
                                    Setting it to 36 means that all audits completed during 2010 would expire March 1, 2013.
                                    Never set this number to less than 10.
                                </li>
                                <li>
                                    If greater than 0 and class type is not PQF, then this number is added to today's date.
                                </li>
                            </ul>
                            <p>
                               	Note: Changing this field will NOT update existing audits.  Please contact the
                               	Software Development Team for a data conversion.
                            </p>
                        </pics:fieldhelp>
                    </li>
                    <li>
                        <label><s:text name="ManageAuditType.RequiredLanguages" />:</label>
                        <s:optiontransferselect
                            name="requiredLanguagesName"
                            list="availableLocales"
                            listKey="language"
                            listValue="%{displayLanguage + (displayCountry == '' ? '' : ' (' + displayCountry + ')')}"
                            doubleName="auditType.languages"
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
                </ol>
            </fieldset>
            
            <fieldset class="form submit">
                <input type="submit" class="picsbutton positive" name="button" value="Save" id="save" />

                <s:if test="auditType.id > 0 && auditType.categories.size == 0">
                    <input id="deleteButton" type="submit" class="picsbutton negative" name="button" value="Delete" id="delete" />
                </s:if>
                
                <input type="submit" class="picsbutton" name="button" value="UpdateAllAudits" id="updateAllAudits" />
            </fieldset>
        </div>
    </s:form>
    
    <s:if test="id > 0">
        <div>
            <ul id="list" class="list">
                <s:iterator value="auditType.topCategories">
                    <li id="item_<s:property value="id"/>" title="Drag and drop to change order">
                        <s:property value="number"/>.
                        <a href="ManageCategory.action?id=<s:property value="id"/>">
                            <s:property value="name.toString().trim().length() == 0 ? 'empty' : name"/>
                        </a>
                        <a
                            class="preview"
                            href="AuditCatPreview.action?categoryID=<s:property value="id" />&button=PreviewCategory"
                            title="Preview Category">
                        </a>
                    </li>
                </s:iterator>
            </ul>
            
            <a
                id="manage_audit_types_add_new_category"
                class="add"
                href="ManageCategory.action?button=AddNew&auditType=<s:property value="auditType.id"/>&category.auditType.id=<s:property value="auditType.id"/>">
                Add New Category
            </a>
            
            <div id="list-info"></div>
        </div>
        
        <s:if test="auditType.categories.size > 1">
            <div class="info">
                Drag and drop categories to change their order
            </div>
            
            <br clear="all" />
        </s:if>
        
        <br/>
        
        <h3>
            Related Flag Criteria
        </h3>
        
        <div id="flags"></div>
        
        <pics:permission perm="EditFlagCriteria">
            <a href="ManageFlagCriteria!edit.action?criteria.displayOrder=999&criteria.dataType=boolean&criteria.comparison=%3d&criteria.defaultValue=false&criteria.allowCustomValue=false&criteria.requiredStatus=Complete&criteria.category=Paperwork&criteria.auditType=<s:property value="auditType.id" />&criteria.label=<s:property value="auditType.name" />&criteria.description=<s:property value="auditType.name" />%20is%20missing"
                class="add" id="manageFlagCriteria">
                Add New Audit Type Flag Criteria
            </a>
        </pics:permission>
        
        <br/>
        <br/>
        
        <h3>
            Related Rules
        </h3>
        
        <div id="rules"></div>
        
        <pics:permission perm="ManageAuditTypeRules" type="Edit" >
            <a href="AuditTypeRuleEditor.action?button=New&ruleAuditTypeId=<s:property value="id" />"
                class="add" id="auditTypeRuleEditor">
                Add New Audit Type Rule
            </a>
        </pics:permission>
    </s:if>
</body>