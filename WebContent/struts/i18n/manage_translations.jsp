<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<title>Manage Translations</title>

<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />

<script type="text/javascript" src="js/jquery/translate/jquery.translate-1.4.7-debug-all.js"></script>
<script type="text/javascript" src="js/ReportSearch.js?v=<s:property value="version"/>"></script>
<script type="text/javascript" src="js/filters.js?v=<s:property value="version"/>"></script>

<script type="text/javascript" src="js/translation_manage.js?v=<s:property value="version"/>"></script>

<h1>Manage Translations</h1>

<s:include value="../actionMessages.jsp" />
<s:include value="../config_environment.jsp" />

<s:if test="tracingOn">
	<div class="alert">
		<s:if test="showDoneButton">
			<div class="right">
				<input type="button" class="picsbutton positive" value="Done with this page" id="doneButton" />
			</div>
		</s:if>
        
		Text Tracing for Internationalization is turned ON.
		
        <s:form id="formTracingOff">
			<s:hidden name="button" value="tracingOff" />
			<s:submit value="Turn Tracing Off" />
		</s:form>
		
        <s:form id="formTracingClear">
			<s:hidden name="button" value="tracingClear" />
			<s:submit value="Clear Tracing Cache" />
		</s:form>
	</div>
</s:if>
<s:else>
	<s:form id="formTracingOn">
		<s:hidden name="button" value="tracingOn" />
		<s:submit value="Turn Tracing On" />
	</s:form>
</s:else>

<div id="search">
    <s:form id="form1">
    	<s:hidden name="filter.ajax" value="false" />
    	<s:hidden name="showPage" value="1" />
        
    	From:
    	<s:select
    		list="@com.picsauditing.jpa.entities.AppTranslation@getLocales()"
    		name="localeFrom"
    		listValue="displayName" />

    	To:
    	<s:select
    		list="@com.picsauditing.jpa.entities.AppTranslation@getLocales()"
    		name="localeTo"
    		listValue="displayName" />
            
    	Key:
    	<s:textfield name="key" />
        
    	Search:
    	<s:textfield name="search" />
        
    	<br />
        
    	<div class="filterOption">
    		Custom:
    		<s:select headerKey="" headerValue=""
    			list="#{
    				'Common':'Commonly Used '+localeFrom.displayName+' Phrases', 
    				'MissingTo':'Missing '+localeTo.displayName+' Translations', 
    				'MissingFrom':'Missing '+localeFrom.displayName+' Translations', 
    				'Updated':'Recently Updated '+localeFrom.displayName+' Phrases', 
    				'Unused':'Unused Keys'}"
    			name="searchType" />
    	</div>
    	<div class="filterOption">
    		<a href="#" class="filterBox">Source Language</a> =
    		<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
            <br />
            
    		<span class="clearLink q_box select">
    			<s:select
    				list="@com.picsauditing.jpa.entities.AppTranslation@getLocaleLanguages()"
    				multiple="true"
    				name="fromSourceLanguages"
    			/>
    			<br />
                
    			<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
    		</span>
    	</div>

    	<div class="clear"></div>
        
    	<div class="filterOption">
    		<a href="#" class="filterBox">From Quality Rating</a> =
    		<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
            <br />
            
    		<span class="clearLink q_box select">
    			<s:select
    				list="@com.picsauditing.jpa.entities.TranslationQualityRating@values()"
    				listKey="ordinal()"
    				listValue="name()"
    				multiple="true"
    				name="fromQualityRatings"
    			/>
    			<br />
                
    			<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
    		</span>
    	</div>
    	<div class="filterOption">
    		From Applicable:
    		<s:radio
    			list="#{'':'Any','false':'Not Applicable','true':'Applicable'}"
    			name="fromShowApplicable"
    		/>
    	</div>

    	<div class="clear"></div>

    	<div class="filterOption">
    		<a href="#" class="filterBox">To Quality Rating</a> =
    		<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
            <br />
            
    		<span class="clearLink q_box select">
    			<s:select
    				list="@com.picsauditing.jpa.entities.TranslationQualityRating@values()"
    				listKey="ordinal()"
    				listValue="name()"
    				multiple="true"
    				name="toQualityRatings"
    			/>
    			<br />
                
    			<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
    		</span>
    	</div>
    	<div class="filterOption">
    		To Applicable:
    		<s:radio
    			list="#{'':'Any','false':'Not Applicable','true':'Applicable'}"
    			name="toShowApplicable"
    		/>
    	</div>
    	<div class="clear"></div>
        
    	<s:submit name="button" id="searchfilter" value="Search" cssClass="picsbutton positive" />
    	<div class="clear"></div>
    </s:form>
</div>
<div class="right">
	<a
		class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
		href="javascript: download('ManageTranslations');"
	 	title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>">
	 	<s:text name="global.Download" />
	</a>
</div>
<div>
	<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<table class="table translation-list">
	<thead>
		<tr>
			<th class="translation-key">
				Key
			</th>
			<th class="translation-from">
				From (<s:property value="localeFrom.displayName"/>)
			</th>
			<th class="translation-to">
				To (<s:property value="localeTo.displayName"/>)
			</th>
		</tr>
	</thead>
    
	<s:iterator value="list" status="rowstatus">
		<tr id="row<s:property value="from.id"/>" class="translate <s:if test="#rowstatus.odd == true">odd</s:if><s:else>even</s:else>">
			<td class="translation-key">
				<span class="key"><s:property value="from.key" /></span>
			</td>
            
			<s:iterator value="items" status="status">
                <s:if test="#status.index == 0">
                    <s:set var="translation_class" value="'translation-from'" />
                    <s:set var="radio_id" value="%{'quality_from_' + id + '_'}" />
                    <s:set var="checkbox_id" value="%{'applicable_from_' + id + '_'}" />
                </s:if>
                <s:else>
                    <s:set var="translation_class" value="'translation-to'" />
                    <s:set var="radio_id" value="%{'quality_to_' + id + '_'}" />
                    <s:set var="checkbox_id" value="%{'applicable_to_' + id + '_'}" />
                </s:else>
                
				<td class="${translation_class}">
                    <s:form cssClass="translationValue" onsubmit="return false;" theme="pics">
                        <s:hidden name="translation" value="%{id}" />
                        <s:hidden name="localeTo" />
                        <s:hidden name="localeFrom" />
                        <s:hidden name="button" value="save" />
                        
                        <s:if test="!(id > 0)">
                            <s:hidden name="translation.locale" value="%{localeTo}" />
                            <s:hidden name="translation.key" value="%{from.key}" />
                        </s:if>
                        
                        <div class="content view-mode">
                            <div class="view">
                                <div class="text">
                                    <s:property value="value" escape="false" />
                                </div>
                                
                                <%-- <s:if test="!(id > 0)">
                                    <a href="javascript:;" class="btn small success suggestTranslation">Suggest</a>
                                </s:if> --%>
                                
                                <a href="javascript:;" class="edit btn small primary">Edit</a>
                            </div>
                            
                            <div class="edit">
                                <s:textarea name="translation.value" value="%{value}" />
                                
                                <div class="actions">
                                    <button name="button" class="save btn small success">Save</button>
                                    <button class="cancel btn small">Cancel</button>
                                    
                                    <s:if test="!sourceLanguage.empty">
                                        <span class="source">
                                            Src: <s:property value="getLanguageNameFromISOCode(sourceLanguage)" />
                                        </span>
                                    </s:if>
                                </div>
                                
                                <div class="rate">
                                    <div class="applicable">
                                        <s:checkbox id="%{checkbox_id}" name="translation.applicable" value="%{applicable}" cssClass="is-applicable"/>
                                    </div>
                                    
                                    <s:if test="applicable == false">
                                        <s:set var="radio_display" value="'display: none'" />
                                    </s:if>
                                    <s:else>
                                        <s:set var="radio_display" value="" />
                                    </s:else>
                                    
                                    <div class="quality" style="${radio_display}">
                                        <s:radio
                                            list="@com.picsauditing.jpa.entities.TranslationQualityRating@values()"
                                            name="translation.qualityRating"
                                            id="%{radio_id}"
                                            cssClass="qualityRating"
                                            value="%{qualityRating}"
                                        />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </s:form>
				</td>
			</s:iterator>
		</tr>
	</s:iterator>
</table>

<div>
	<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<s:form>
	<input type="hidden" name="translation.locale" value="<s:property value="localeFrom"/>">
	Add New Key <s:textfield name="translation.key" /> for <s:property value="localeFrom.displayName"/>
	<br />
    
	<s:textarea name="translation.value" cols="50" />
	<br />
    
	<button name="button" class="save" value="save">Save</button>
</s:form>