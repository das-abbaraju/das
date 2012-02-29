<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/exception_handler.jsp" %>
<div class="filterOption" id="select_categories">
	<a href="#" data-name="form1_categories" class="filterBox">
		<s:text name="global.Category" />
	</a> =
	<span id="form1_categories_query">
		<s:text name="JS.Filters.status.None"/>
	</span>
	<br />
	<span id="form1_categories_select" class="hide">
		<s:select
			list="auditType.topCategories"
			multiple="true"
			cssClass="forms"
			name="categoryIDs"
			id="form1_categories"
			listKey="id"
			listValue="name"
		/>
		<br />
		<a class="allLink" href="#" data-name="form1_categories">
			<s:text name="JS.Filters.status.All" />
		</a>
		<br />
		<a class="clearLink" href="#" data-name="form1_categories">
			<s:text name="button.Clear"/>
		</a>
	</span>
</div>
<div class="clear"></div>
<div class="filterOption" id="select_items">
	<a href="#" data-name="form1_items" class="filterBox">
		<s:text name="OperatorCompetency" />
	</a> =
	<span id="form1_items_query">
		<s:text name="JS.Filters.status.None"/>
	</span>
	<br />
	<span id="form1_items_select" class="hide">
		<s:if test="auditType.desktop">
			<s:select
				list="desktopQuestions"
				multiple="true"
				cssClass="forms"
				name="itemIDs"
				id="form1_items"
				listKey="id"
				listValue="name"
			/>
		</s:if>
		<s:else>
			<s:select
				list="operatorCompetencies"
				multiple="true"
				cssClass="forms"
				name="itemIDs"
				id="form1_items"
				listKey="id"
				listValue="label"
			/>
		</s:else>
		<br />
		<a class="allLink" href="#" data-name="form1_items">
			<s:text name="JS.Filters.status.All"/>
		</a>
		<br />
		<a class="clearLink" href="#" data-name="form1_items">
			<s:text name="button.Clear"/>
		</a>
	</span>
</div>
<div class="clear"></div>
<div class="filterOption">
	<s:checkbox name="pivot" />
	<s:text name="AuditCategoryMatrix.SwapRowsandColumns"/>
</div>
<div class="clear"></div>
<div>
	<a href="#" id="update" class="picsbutton positive">
		<s:text name="button.Search" />
	</a>
</div>