<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>

<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript">
	function removeTag(tagId) {
		var pars = "button=RemoveTag&tagId=" + tagId+'&id='+<s:property value="id"/>;
		var divName ='conoperator_tags';
		$(divName).innerHTML="<img src='images/ajax_process.gif' />";
		var myAjax = new Ajax.Updater(divName, 'TagNameEditAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onSuccess: function(transport) { 
				new Effect.Highlight($(divName), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			}
		});
		return false;
	}
	
	function addTag() {
		var tagId = $('tagName').value;
		var pars = "button=AddTag&tagId=" + tagId+'&id='+<s:property value="id"/>;
		var divName ='conoperator_tags';
		$(divName).innerHTML="<img src='images/ajax_process.gif' />";
		var myAjax = new Ajax.Updater(divName, 'TagNameEditAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onSuccess: function(transport) { 
				
				new Effect.Highlight($(divName), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			}
		});
		return false;
	}
</script>
<style>
#content div.column {
	width: 47%;
	margin: 0px 5px;
	padding: 5px;
	float: left;
	border: 1px solid #000;
	position: relative;
}
div.contractor_block {
	border: 1px solid #000;
	float: left;
	width: 98%;
}
div.contractor_info {
	float: left;
}
div.contractor_info ul {
	list-style: none;
	margin-left: 0px;
}
#content div.contractor_description {
	background-color:#797B7A;
	color:#F7F7F7;
	padding:5px 10px;
	clear: left;
	width: 100%
}
img.contractor_logo {
	float: left;
	max-width: 45%;
	/* IE Image max-width */
	width: expression(this.width > 225 ? '45%' : true);
}
</style>
</head>
<body>
<s:include value="conHeader.jsp" />
<s:if test="!contractor.activeB">
	<div id="alert">This contractor is not active.
	<s:if test="contractor.lastPayment != null">They last paid on <s:property value="contractor.lastPayment"/>.</s:if>
	</div>
</s:if>
<s:if test="contractor.acceptsBids">
	<s:if test="canUpgrade">
		<div id="info">This is a Trial Only Account and will expire on <strong><s:date name="contractor.paymentExpires" format="M/d/yyyy" /></strong><br/>
		Click <a href="ContractorView.action?id=<s:property value="id" />" class="picsbutton positive">Upgrade to Full Membership</a> to continue working at your selected facilities.</div>
	</s:if>
	<s:else>
		<div id="alert">This is a Trial Contractor Account.</div>
	</s:else>
</s:if>
<s:elseif test="contractor.paymentOverdue && (permissions.admin || permissions.contractor)">
	<div id="alert">This contractor has an outstanding invoice due</div>
</s:elseif>
<s:if test="permissions.admin && !contractor.mustPayB">
	<div id="alert">This account has a lifetime free membership</div>
</s:if>


<div class="column">
	<div class="contractor_block">
		<img src="ContractorLogo.action?id=<s:property value="id"/>" class="contractor_logo" />
		<div class="contractor_info">
			<div><h4><s:property value="contractor.name"/></h4></div>
			<div><s:property value="contractor.address"/>, <s:property value="contractor.city"/>, <s:property value="contractor.state"/>, <s:property value="contractor.zip"/></div>
			<div>
				<ul>
					<li>Contact: <span class="value"><s:property value="contractor.contact" /></span></li>
					<li>Phone: <span class="value"><s:property value="contractor.phone" /></span></li>
					<s:if test="contractor.phone2"><li>Other Phone: <span class="value"><s:property value="contractor.phone2" /></span></li></s:if>
					<s:if test="contractor.fax"><li>Fax: <span class="value"><s:property value="contractor.fax" /></span></li></s:if>
					<li>Email: <strong><a href="mailto:<s:property value="contractor.email" />" class="value"><s:property value="contractor.email" /></a></strong></li>
					<s:if test="contractor.webUrl.length() > 0"><li>Web site: <strong><a href="http://<s:property value="contractor.webUrl" />" class="value" target="_blank"><s:property value="contractor.webUrl" /></a></strong></li></s:if>
					<s:if test="@com.picsauditing.util.Strings@isEmpty(contractor.brochureFile) == false">
						<li><a href="DownloadContractorFile.action?id=<s:property value="id" />" target="_BLANK">Company Brochure</a></li>
					</s:if>
				</ul>
			</div>
		</div>
		<div class="contractor_info contractor_description"><s:property value="contractor.descriptionHTML" escape="false" /></div>
	</div>
</div>
<div class="column"></div>

<br clear="all" />

</body>
</html>
