<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<div style="width: 48%; padding: 0; margin: 0 10px 0 0; vertical-align: top; float: left;">
	<table class="report" style="border-collapse: collapse; width: 100%;">
		<thead>
			<tr>
				<th style="width: 85%;">Applied Categories</th>
				<th style="width: 15%;">Remove</th>
			</tr>
		</thead>
			<s:iterator value="conAudit.categories" var="currentAuditCatData">
				<s:set var="currentCat" value="#currentAuditCatData.category"/>
				<s:if test="#currentAuditCatData.applies && #currentCat.parent == NULL">
					<tr>
						<td style="width: 80%;">
							<s:property value="#currentCat.fullNumber"/> <s:property value="#currentCat.name"/>
							<s:if test="#currentCat.subCategories.size() > 0"><br />
							<a style="color: #4C4D4D; padding-left: 20px; font: .90em;" class="clickable sc_link" id="sc-link_<s:property value="#currentCat.id"/>">Show Subcategories</a>
								<ul id="sc_<s:property value="#currentCat.id"/>" style="list-style: none; display: none;">
									<s:iterator value="#currentCat.subCategories">
										<li style="font: .85em; padding-left: 20px;"><s:property value="fullNumber"/> - <s:property value="name"/></li>									
									</s:iterator>
								</ul>
							</s:if>
						</td>
						<td style="width: 20%; vertical-align: top;" class="center">
							<a style="cursor: pointer;" class="remove removeCat" id="category_<s:property value="#currentCat.id"/>"></a>
						</td>
					</tr>
				</s:if>
			</s:iterator>
	</table>
</div>
<div style="width: 48%; padding: 0; margin: 0; vertical-align: top; float: right;">
	<table class="report" style="border-collapse: collapse; width: 100%;">
		<thead>
			<tr>
				<th style="width: 15%;">Add</th>
				<th style="width: 85%;">N/A Categories</th>
			</tr>
		</thead>
		<s:iterator value="conAudit.categories" var="currentAuditCatData">
			<s:set var="currentCat" value="#currentAuditCatData.category"/>
			<s:if test="!#currentAuditCatData.applies && #currentCat.parent == NULL">
				<tr>
					<td style="width: 20%; vertical-align: top;" class="center">
						<a style="cursor: pointer;"  class="add addCat" id="category_<s:property value="#currentCat.id"/>"></a>
					</td>
					<td style="width: 80%;">
						<s:property value="#currentCat.fullNumber"/> <s:property value="#currentCat.name"/>
						<s:if test="#currentCat.subCategories.size() > 1"><br />
						<a style="color: #4C4D4D; padding-left: 20px; font: .90em;" class="clickable sc_link" id="sc-link_<s:property value="#currentCat.id"/>">Show Subcategories</a>
							<ul id="sc_<s:property value="#currentCat.id"/>" style="list-style: none; display: none;">
								<s:iterator value="#currentCat.subCategories">
									<li style="font: .85em; padding-left: 20px;"><s:property value="fullNumber"/> - <s:property value="name"/></li>									
								</s:iterator>
							</ul>
						</s:if>
					</td>
				</tr>
			</s:if>
		</s:iterator>
	</table>
</div>	