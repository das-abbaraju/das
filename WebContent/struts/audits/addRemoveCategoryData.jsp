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
		<s:if test="categories.keySet().size > 1">
			<s:iterator value="categories" var="currentCat">
				<s:if test="#currentCat.key.parent == NULL && #currentCat.value.applies">
					<tr>
						<td style="width: 80%;">
							<s:property value="#currentCat.key.fullNumber"/> <s:property value="#currentCat.key.name"/>
							<s:if test="#currentCat.key.subCategories.size() > 1"><br />
							<a style="color: #4C4D4D; padding-left: 20px; font: .90em;" class="clickable sc_link" id="sc-link_<s:property value="#currentCat.key.fullNumber.replace('.','_')"/>">Show Subcategories</a>
								<ul id="sc_<s:property value="#currentCat.key.fullNumber.replace('.','_')"/>" style="list-style: none; display: none;">
									<s:iterator value="#currentCat.key.subCategories">
										<li style="font: .85em; padding-left: 20px;"><s:property value="fullNumber"/> - <s:property value="name"/></li>									
									</s:iterator>
								</ul>
							</s:if>
						</td>
						<td style="width: 20%; vertical-align: top;" class="center">
							<a style="cursor: pointer;" class="remove removeCat" id="category_<s:property value="#currentCat.key.id"/>"></a>
						</td>
					</tr>
				</s:if>
			</s:iterator>
		</s:if>
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
		<s:if test="categories.keySet().size > 1">
			<s:iterator value="categories" var="currentCat">
				<s:if test="#currentCat.key.parent == NULL && !#currentCat.value.applies && permissions.picsEmployee">
					<tr>
						<td style="width: 20%; vertical-align: top;" class="center">
							<a style="cursor: pointer;"  class="add addCat" id="category_<s:property value="#currentCat.key.id"/>"></a>
						</td>
						<td style="width: 80%;">
							<s:property value="#currentCat.key.fullNumber"/> <s:property value="#currentCat.key.name"/>
							<s:if test="#currentCat.key.subCategories.size() > 1"><br />
							<a style="color: #4C4D4D; padding-left: 20px; font: .90em;" class="clickable sc_link" id="sc-link_<s:property value="#currentCat.key.fullNumber.replace('.','_')"/>">Show Subcategories</a>
								<ul id="sc_<s:property value="#currentCat.key.fullNumber.replace('.','_')"/>" style="list-style: none; display: none;">
									<s:iterator value="#currentCat.key.subCategories">
										<li style="font: .85em; padding-left: 20px;"><s:property value="fullNumber"/> - <s:property value="name"/></li>									
									</s:iterator>
								</ul>
							</s:if>
						</td>
					</tr>
				</s:if>
			</s:iterator>
		</s:if>
	</table>
</div>	