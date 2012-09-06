<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<head>
<title><s:text name="GetLcCorQuote.title" /></title>
<style type="text/css">
      .leftside { float: left; padding: 8px; }
      .rightside { float: right; padding: 8px; }
    </style>
</head>

<body>
<h1>
	<s:text name="GetLcCorQuote.title"></s:text>
	<span class="sub">
		<s:text name="GetLcCorQuote.subtitle"></s:text>
	</span>
</h1>
<s:include value="../actionMessages.jsp" />
<table width="100%">
	<tr>
		<td style="vertical-align:top; width: 48%">
		<div><h2><s:text name="GetLcCorQuote.header" /></h2><br /></div>
		<div style="border-style:solid; border-width:1px; padding:5px;" ><s:text name="GetLcCorQuote.info" /></div>
		<div><br/><a href="resources/PTS_B004V4_LCCOR_Brochure.pdf" ><s:text name="GetLcCorQuote.document" /></a></div>
		</td>
		<td width="15px"></td>
		<td style="vertical-align:top; width: 48%">
		<s:if test="!confirmQuote">
			<h3><s:text name="GetLcCorQuote.form.title" /></h3>
			<s:form id="save" method="POST" enctype="multipart/form-data">
				<s:hidden name="id" />
				<ol style="list-style-type:none" >
					<li>
						<table>
							<tr>
								<td><s:text name="GetLcCorQuote.Name" /><br/><s:textfield name="user.name" size="35" /></td>
								<td width="15px"></td>
								<td><s:text name="GetLcCorQuote.Company" /><br/><s:textfield name="contractor.name" size="35" /></td>
							</tr>
							<tr>
								<td><s:text name="GetLcCorQuote.Email" /></br><s:textfield name="user.email" size="35" /></td>
								<td width="15px"></td>
								<td><s:text name="GetLcCorQuote.TotalEmployees" /><br/><s:textfield name="totalEmployees" size="35" /></td>
							</tr>
							<tr>
								<td><s:text name="GetLcCorQuote.Phone" /><br/><s:textfield name="user.phone" size="35" /></td>
								<td width="15px"></td>
								<td></td>
							</tr>
						</table>
					</li>
					<li><br />
						<table>
							<thead>
								<tr>
									<th><s:text name="GetLcCorQuote.Provinces" /><th>
									<th><s:text name="GetLcCorQuote.Partners" /><th>
									<th><s:text name="GetLcCorQuote.Employees" /><th>
								</tr>
							</thead>
						
							<tbody>
								<s:iterator value="provinces" status="provinceStatus">
								<tr>
									<td><s:checkbox name="provIndex"  fieldValue="%{#provinceStatus.index}" value="false" /> <s:property value="%{provinces[#provinceStatus.index]}" /></td>
									<td width="15px"></td>
									<td><s:textfield name="partners" value="%{partners[#provinceStatus.index]}" /></td>
									<td width="15px"></td>
									<td><s:textfield name="employees" value="%{employees[#provinceStatus.index]}" /></td>
								</tr>
								</s:iterator>
						
							</tbody>
						</table>
					</li>
					<li>
					<br />
					<div class="leftside" >
						<s:if test="contractor.id != 0">
							<s:submit action="GetLcCorQuote!remindMeLater" cssClass="picsbutton" value="%{getText('GetLcCorQuote.RemindMeLater')}" />
						</s:if>
					</div>
					<div class="rightside" >
						<s:submit action="GetLcCorQuote!noThanks" cssClass="picsbutton" value="%{getText('GetLcCorQuote.NoThanks')}" />
						<s:submit action="GetLcCorQuote!generateQuote" cssClass="picsbutton positive" value="%{getText('GetLcCorQuote.GetQuote')}" />
					</div>
					</li>
				</ol>
			</s:form>
		</s:if>
		<s:else>
			<br/>
			<s:text name="GetLcCorQuote.Confirmation" />
			<br/>
			<s:if test="id != 0" >
				<a href="ContractorView.action?id=<s:property value='id' />" ><s:text name="GetLcCorQuote.Continue" /></a>
			</s:if>
			<s:else>
				<a href="http://www.picsauditing.com/" ><s:text name="GetLcCorQuote.Continue" /></a>
			</s:else>
		</s:else>
		
		</td>
	</tr>
</table>
</body>