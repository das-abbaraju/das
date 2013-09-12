<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<div id="search">
	<s:form id="form1" action="%{filter.destinationAction}">
		<s:hidden name="filter.ajax" />
		<s:hidden name="filter.destinationAction" />
		<s:hidden name="showPage" value="1" />
		<s:hidden name="orderBy" />
		
		<div>
			<button id="searchfilter" type="submit" name="button" value="Search" onclick="return clickSearch('form1');" class="picsbutton positive">
				<s:text name="button.Search" />
			</button>
		</div>
		
		<s:if test="filter.showKey">
			<div class="filterOption">
				Key: <s:textfield name="filter.key" size="50" onclick="clearText(this)" />
			</div>
		</s:if>
		
		<div class="clear"></div>
		
		<s:if test="filter.showQualityRating">
			<div class="filterOption" id="qualityRating">
				<a href="javascript:;" class="filterBox">Quality Rating</a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:select
						list="filter.qualityRatingList"
						multiple="true"
						name="filter.qualityRating"
						cssClass="forms"
					/>
					<br />
					<a class="clearLink" href="javascript:;"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
		
		<s:if test="filter.showRestrictToCurrentUser">
			<div class="filterOption">
				Restrict to current user: <s:checkbox name="filter.restrictToCurrentUser" />
			</div>
		</s:if>
	</s:form>
	
	<div class="clear"></div>
</div>