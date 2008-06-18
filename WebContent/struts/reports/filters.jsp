<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">
function toggleBox(name) {
	var box = $(name+'_select');
	var result = $(name+'_query');
	result.hide();
	box.toggle();
	if (box.visible())
		return;

	updateQuery(name);
	result.show();
}

function clearSelected(name) {
	var box = $(name);
	for(i=0; i < box.length; i++)
		box.options[i].selected = false
	updateQuery(name);
}

function updateQuery(name) {
	var box = $(name);
	var result = $(name+'_query');
	var queryText = '';
	var values = $F(box);
	for(i=0; i < box.length; i++) {
		if (box.options[i].selected) {
			if (queryText != '') queryText = queryText + ", ";
			queryText = queryText + box.options[i].text;
		}
	}
	
	if (queryText == '') {
		queryText = 'ALL';
	}
	result.update(queryText);
}
</script>

<style type="text/css">
   a.clearLink {
     border-width: 1px;
     font-size: 10px;
     line-height: 10px;
     padding: 0px;
     margin: 0px;
   }
</style>

<div id="search">
<div id="showSearch" onclick="showSearch()" <s:if test="filtered">style="display: none"</s:if> ><a href="#">Show Filter Options</a></div>
<div id="hideSearch" <s:if test="!filtered">style="display: none"</s:if> ><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
<s:form id="form1" method="post" cssStyle="background-color: #F4F4F4; %{filtered ? '' : 'display: none;'}">
	<s:hidden name="showPage" value="1"/>
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />
	
	<s:submit type="button" label="Search" onclick="runSearch( 'form1')" cssStyle="float: right;" />
	<br clear="all" />

	<div class="filterOption"><s:textfield name="accountName" cssClass="forms" size="10" onfocus="clearText(this)"  /></div>

<s:if test="filterAddress">
	<div class="filterOption">Address:
		<s:textfield name="city" cssClass="forms" size="15" onfocus="clearText(this)"  />
		<s:select list="stateList" cssClass="forms" name="state" />
		<s:textfield name="zip" cssClass="forms" size="5" onfocus="clearText(this)" />
	</div>
</s:if>

<s:if test="filterIndustry">
	<div class="filterOption">
		<a href="#" onclick="toggleBox('form1_industry'); return false;">Industry</a> =
		<span id="form1_industry_query">ALL</span><br />
		<span id="form1_industry_select" style="display: none" class="clearLink">
		  <s:select name="industry" list="industryList" cssClass="forms" multiple="true" size="5"/>
		  <script type="text/javascript">updateQuery('form1_industry');</script>
		  <br/>
		  <a class="clearLink" href="#" onclick="clearSelected('form1_industry'); return false;">Clear</a>
		</span>
	</div>
</s:if>

<s:if test="filterTrade">
	<div class="filterOption">
		<a href="#" onclick="toggleBox('form1_trade'); return false;">Trade</a>
		<s:select list="tradePerformedByList" cssClass="forms" name="performedBy" /> =
		<span id="form1_trade_query">ALL</span><br />
		<span id="form1_trade_select" style="display: none" class="clearLink">
		  <s:select list="tradeList" cssClass="forms" name="trade" multiple="true" size="5" />
		  <script type="text/javascript">updateQuery('form1_trade');</script>
		  <br/>
		  <a class="clearLink" href="#" onclick="clearSelected('form1_trade'); return false;">Clear</a>
		</span>
	</div>
</s:if>

<s:if test="filterFlagStatus">
	<div class="filterOption">
		<s:select list="flagStatusList" cssClass="forms" name="flagStatus" />
	</div>
</s:if>

<s:if test="filterAuditType">
	<div class="filterOption">
		<a href="#" onclick="toggleBox('form1_auditTypeID'); return false;">Audit Type</a> =
		<span id="form1_auditTypeID_query">ALL</span><br />
		<span id="form1_auditTypeID_select" style="display: none" class="clearLink">
		  <s:select list="auditTypeList" cssClass="forms" name="auditTypeID" listKey="auditTypeID" listValue="auditName" multiple="true" size="5" />
		  <script type="text/javascript">updateQuery('form1_auditTypeID');</script>
		  <br/>
		  <a class="clearLink" href="#" onclick="clearSelected('form1_auditTypeID'); return false;">Clear</a>
		</span>
	</div>
</s:if>

<s:if test="filterAuditStatus">
	<div class="filterOption">
		<a href="#" onclick="toggleBox('form1_auditStatus'); return false;">Audit Status</a> =
		<span id="form1_auditStatus_query">ALL</span><br />
		<span id="form1_auditStatus_select" style="display: none" class="clearLink">
		  <s:select list="auditStatusList" cssClass="forms" name="auditStatus" multiple="true" size="5" />
		  <script type="text/javascript">updateQuery('form1_auditStatus');</script>
		  <br/>
		  <a class="clearLink" href="#" onclick="clearSelected('form1_auditStatus'); return false;">Clear</a>
		</span>
	</div>
</s:if>

<s:if test="filterAuditor">
	<div class="filterOption">
		<a href="#" onclick="toggleBox('auditorId'); return false;">Auditors</a> =
		<span id="auditorId_query">ALL</span><br />
		<span id="auditorId_select" style="display: none" class="clearLink">
		  <s:action name="AuditorsGet" executeResult="true">
			<s:param name="controlName" value="%{'auditorId'}"/>
			<s:param name="presetValue" value="auditorId"/>
		  </s:action>
		  <script type="text/javascript">updateQuery('auditorId');</script>
		  <br/>
		  <a class="clearLink" href="#" onclick="clearSelected('auditorId'); return false;">Clear</a>
		</span>
	</div>
</s:if>


<s:if test="filterOperator">
	<div class="filterOption">
		<a href="#" onclick="toggleBox('form1_operator'); return false;">Operators</a> =
		<span id="form1_operator_query">ALL</span><br />
		<span id="form1_operator_select" style="display: none" class="clearLink">
		  <s:select list="operatorList" cssClass="forms" name="operator" listKey="id" listValue="name" multiple="true" size="5"/>
		  <script type="text/javascript">updateQuery('form1_operator');</script>
		  <br/>
		  <a class="clearLink" href="#" onclick="clearSelected('form1_operator'); return false;">Clear</a>
		</span>
	</div>
</s:if>

<s:if test="filterCerts">
	<div class="filterOption">
		<s:select list="certsOptions" cssClass="forms" name="certsOnly" />
	</div>
</s:if>
<s:if test="filterVisible">
	<div class="filterOption">
		<s:select list="visibleOptions" cssClass="forms" name="visible" />
	</div>
</s:if>
<s:if test="filterTaxID">
	<div class="filterOption">
		<s:textfield name="taxID" cssClass="forms" size="9" onfocus="clearText(this)"  /><br/>
		<span class="redMain">*must be 9 digits</span>
	</div>
</s:if>
<s:if test="filterLicensedIn">
	<div class="filterOption">
		<a href="#" onclick="toggleBox('form1_stateLicensedIn'); return false;">Licensed In</a> =
		<span id="form1_stateLicensedIn_query">ALL</span><br />
		<span id="form1_stateLicensedIn_select" style="display: none" class="clearLink">
		  <s:select list="stateLicensesList" cssClass="forms" name="stateLicensedIn" multiple="true" size="5" />
		  <script type="text/javascript">updateQuery('form1_stateLicensedIn');</script>
		  <br/>
		  <a class="clearLink" href="#" onclick="clearSelected('form1_stateLicensedIn'); return false;">Clear</a>
		</span>
	</div>
</s:if>

<s:if test="filterWorksIn">
	<div class="filterOption">
		<a href="#" onclick="toggleBox('form1_worksIn'); return false;">Works In</a> =
		<span id="form1_worksIn_query">ALL</span><br />
		<span id="form1_worksIn_select" style="display: none" class="clearLink">
		  <s:select list="worksInList" cssClass="forms" name="worksIn" multiple="true" size="5" />
		  <script type="text/javascript">updateQuery('form1_worksIn');</script>
		  <br/>
		  <a class="clearLink" href="#" onclick="clearSelected('form1_worksIn'); return false;">Clear</a>
		</span>
	</div>
</s:if>

<s:if test="filterWorksIn">
	<div class="filterOption">
		<a href="#" onclick="toggleBox('form1_officeIn'); return false;">Office In</a> =
		<span id="form1_officeIn_query">ALL</span><br />
		<span id="form1_officeIn_select" style="display: none" class="clearLink">
		  <s:select list="officeInList" cssClass="forms" name="officeIn" multiple="true" size="5" />
		  <script type="text/javascript">updateQuery('form1_officeIn');</script>
		  <br/>
		  <a class="clearLink" href="#" onclick="clearSelected('form1_officeIn'); return false;">Clear</a>
		</span>
	</div>
</s:if>

	<br clear="all"/>
	<div class="alphapaging">
		<s:property value="report.startsWithLinksWithDynamicForm" escape="false" />
	</div>
</s:form>
</div>
