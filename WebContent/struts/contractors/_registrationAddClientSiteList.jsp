<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ul class="client-site-list">
	<s:iterator value="#client_site_list" var="result" status="status">
		<li>
			<a href="javascript:;" data-id="${result.id}">
				<span class="name">${result.name}</span>
				<span class="location">
					<s:if test="#result.city != ''">
						${result.city},
					</s:if>
					
					${result.state}
					
					<s:if test="permissions.country != #result.country.isoCode">
						, ${result.country.isoCode}
					</s:if>
				</span>
				
				<s:if test="#client_site_list_position == 'left'">
					<span class="add btn success">+ Add Site</span>
				</s:if>
				<s:else>
					<span class="remove btn error">- Remove Site</span>
				</s:else>
				
				<s:if test="#result.description.length() > 0">
					<span class="info btn">?</span>
					<span class="client-site-info">${result.description}</span>
				</s:if>
			</a>
		</li>
	</s:iterator>
</ul>

<s:if test="#client_site_list_position == 'right'">
	<s:if test="!#client_site_list || #client_site_list.size() > 3">
		<s:set name="display_client_site_help" value="%{'none'}" />
	</s:if>
	<s:else>
		<s:set name="display_client_site_help" value="%{'block'}" />
	</s:else>
	
	<div class="client-site-help" style="display: ${display_client_site_help}">
		<section>
			<h1>Are there any other client sites you work for?</h1>
			<h2>Start by using the filter above or using the suggest feature.</h2>
			
			<s:submit type="button" cssClass="btn info suggest-client-site" value="Suggest Client Sites" />
		</section>
	</div>
</s:if>