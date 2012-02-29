<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/exception_handler.jsp" %>
<s:if test="selectedCategories.size > 0 && selectedItems.size > 0">
	<table class="report">
		<thead>
			<tr>
				<s:if test="editTable">
					<th>
						<a href="#" class="preview">
							<img
								src="images/preview.gif"
								alt="<s:text name="button.View" />"
								title="<s:text name="button.View" />"
							/>
						</a>
					</th>
				</s:if>
				<s:else>
					<th>
						<a href="#" class="edit">
							<img
								src="images/edit_pencil.png"
								alt="<s:text name="button.Edit" />"
								title="<s:text name="button.Edit" />"
							/>
						</a>
					</th>
				</s:else>
				<s:iterator value="pivot ? selectedItems : selectedCategories">
					<th style="width: 100%">
						<s:property value="name" />
					</th>
				</s:iterator>
			</tr>
		</thead>
		<tbody>
			<s:if test="pivot">
				<s:iterator value="selectedCategories" var="cat">
					<tr>
						<td>
							<s:property value="name" />
						</td>
						<s:iterator value="selectedItems" var="item">
							<td class="center">
								<s:if test="!editTable && matrix.get(#cat.id, #item.id)">
									<img alt="Checked" src="images/okCheck.gif" />
								</s:if>
								<s:if test="editTable">
									<input
										type="checkbox"
										class="toggle"
										<s:if test="matrix.get(#cat.id, #item.id)">
											checked="checked"
										</s:if>
										data-audittype="<s:property value="auditType.id" />"
										data-item="<s:property value="#item.id" />"
										data-category="<s:property value="#cat.id" />"
									/>
								</s:if>
							</td>
						</s:iterator>
					</tr>
				</s:iterator>
			</s:if>
			<s:else>
				<s:iterator value="selectedItems" var="item">
					<tr>
						<td>
							<s:property value="name" />
						</td>
						<s:iterator value="selectedCategories" var="cat">
							<td class="center">
								<s:if test="!editTable && matrix.get(#cat.id, #item.id)">
									<img alt="Checked" src="images/okCheck.gif" />
								</s:if>
								<s:if test="editTable">
									<input
										type="checkbox"
										class="toggle"
										<s:if test="matrix.get(#cat.id, #item.id)">
											checked="checked"
										</s:if>
										data-audittype="<s:property value="auditType.id" />"
										data-item="<s:property value="#item.id" />"
										data-category="<s:property value="#cat.id" />"
									/>
								</s:if>
							</td>
						</s:iterator>
					</tr>
				</s:iterator>
			</s:else>
		</tbody>
	</table>
</s:if>
<s:else>
	<div class="info">
		<s:text name="AuditCategoryMatrix.SelectCategoryAndOrCompetency"/>
	</div>
</s:else>