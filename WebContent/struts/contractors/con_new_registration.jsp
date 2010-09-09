<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<html>
<head>
<title>Registration Request</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<script type="text/javascript" src="js/jquery/fancybox/jquery.fancybox-1.3.1.pack.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/fancybox/jquery.fancybox-1.3.1.css" />
<style type="text/css">
#operatorForms {
	overflow: auto;
	background-color: white;
}

#hidden #operatorForms {
	display: none;
}

#attachment {
	display: inline-block;
	vertical-align: top;
}

#email_preview {
	display: none;
	border: 1px solid #C3C3C3;
	margin-top: 10px;
	background-color: #FBFBF8;
	padding: 5px;
}

#email_preview td {
	padding: 5px;
}

.normal:hover {
	color: #4C4D4D;
	text-decoration: none;
}

#notesPreview, #addHere {
	background-color: transparent;
}

pre {
	white-space: pre-wrap; /* css-3 */
	white-space: -moz-pre-wrap !important; /* Mozilla, since 1999 */
	white-space: -pre-wrap; /* Opera 4-6 */
	white-space: -o-pre-wrap; /* Opera 7 */
	word-wrap: break-word; /* Internet Explorer 5.5+ */
}

#requestTable tr td {
	width: 50%;
	vertical-align: top;
}

#addToNotes {
	margin-bottom: 10px;
}

#notesHere{
	background: #FFFF99;
	padding: 10px 5px 10px 5px;
	border-top: 2px solid #4686BF;
	border-bottom: 2px solid #4686BF;
	min-width: 400px;
}

<s:if test="newContractor.city == null ||newContractor.city.length == 0">
.address-zip {
	display: none;
}
</s:if>
</style>

<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>

<script type="text/javascript">
var show=false;
$(document).ready(function() {
	$('#notesHere').hide();
	<s:if test="newContractor.notes.length() > 0">
		show = true;
		$('#notesHere').show();		
	</s:if>
	$('.fancybox').fancybox();
	$('.cluetip').cluetip({
		closeText: "<img src='images/cross.png' width='16' height='16'>",
		arrows: true,
		cluetipClass: 'jtip',
		local: true,
		clickThrough: false
	});
});

$(function() {
	$('.checkReq').change(function() {
		var ele = $(this);
		var term = ele.val();
		var fType = ele.attr('name').substr(ele.attr('name').indexOf('.')+1, ele.attr('name').length);
		$('#_'+fType).hide();
		startThinking( {div: 'think_'+fType, message: 'Checking for matches', type: 'small' } );
		if(fType=='name' || fType=='phone' || fType=='taxID') var type = 'C';
		else if(fType=='contact' || fType=='email') var type = 'U';
		$.getJSON(
			'RequestNewContractorAjax.action',
			{term: term, type: type, button: 'ajaxcheck'},
			function(json){
				if(json==null)
					return;
				var result = json.result;
				if(result!=null) {
					var used = result[2];
					var usedList = $('<div>');
					var usedStr = '';
					for(var i=0; i<used.length; i++){
						usedStr += used[i].used+' ' ;
					}
					usedList.append('Matching on these words:').append('<br/>');
					usedList.append($('<div>').append(usedStr).css('font','italic').css('color','#A84D10'));
					var unused = result[1];
					var matchList = $('<div>');
					if(unused.length>0){
						var unusedList =$('<div>');
						var uStr = '';
						for(var i=0; i<unused.length; i++){
							uStr += unused[i].unused+', ' ;
						}
						uStr = uStr.substr(0, uStr.length-2);
						unusedList.append('These words found no matches, you might want to check to see if they are misspelled').append('<br/>');
						unusedList.append(uStr).append('<br/>'); 
						matchList.append(unusedList);
					}
					matchList.append(usedList);
					matchList.append('If you see the company below then you do not need to request for them to register.  Click on their name to be taken to their page')
					.append('<br/>');
					var ul = $('<ul>');
					for(var i=3; i<result.length; i++){
						var id=result[i].id;
						var name=result[i].name;
						if(result[i].add)
							ul.append($('<li>').append($('<a>').attr('href','ContractorFacilities.action?id='+id).append(name)));
						else
							ul.append($('<li>').append($('<a>').attr('href','ContractorView.action?id='+id).append(name)));
					}
					matchList.append(ul);
					var hasResults = $('#match_'+fType).attr('matched');
					if(hasResults!=null)
						$('#match_'+fType).html(' ');
					$('#match_'+fType).attr('matched', 'true').css('width','600px').append($('<h2>').text('Potential Matches'))
						.append($('<div>').attr('id','inner_'+fType).append(matchList)).hide();
					var link = $('#_'+fType);
					if(!link.length>0){
						link = $('<div>').attr('id','_'+fType).append($('<a>').attr('href','#').css('float', 'left').text('Click to view possible matches').click(function(e){
							e.preventDefault();
							$.facebox({div: '#match_'+fType});
						}));
					}
					ele.parent().append(link);
					link.show();
				}
				
			}
		);
		stopThinking( {div: 'think_'+fType} );
	});
	changeState($("#newContractorCountry").val());
	$('.datepicker').datepicker({
		showOn: 'button',
		buttonImage: 'images/icon_calendar.gif',
		buttonImageOnly: true,
		buttonText: 'Choose a date...',
		showAnim: 'fadeIn',
		minDate: new Date()
	});
	$('#matchedContractor').autocomplete('ContractorSelectAjax.action', 
		{
			minChars: 3,
			extraParams: {'filter.accountName': function() {return $('#matchedContractor').val();}},
			formatResult: function(data,i,count) { return data[0]; }
	}).result(function(event, data){
		$('input#conID').val(data[1]);
	});

	$('#matchedContractor').blur(function() {
		if ($('#matchedContractor').val() == '')
			$('input#conID').val(0);
	});
	
	$('.show-address').keyup(function() {
		if (!$(this).blank())
			$('.address-zip').show();
		else
			$('.address-zip').hide();
	});
	$('#addToNotes').keyup(function() {
		if(show==false){
			show=true;
			$('#notesHere').show();
		}			
		var d = new Date();
		var dateString = (d.getMonth() + 1 < 10 ? "0" : "") + (d.getMonth() + 1) + "/" +
				(d.getDate() < 10 ? "0" : "") + (d.getDate()) + "/" + d.getFullYear();
		$('#addHere').html(dateString + " - <s:property value="permissions.name" /> - " + 
				$(this).val() + "\n\n");

		if ($('#addToNotes').val() == '')
			$('#addHere').text('');
	});
});

function fillNotes(){
	var r = $('#noteReason').val();
	if(r==0)
		return;
	var n = $('#addToNotes').val();
	if(n.length > 1)
		r = r +"\n"+ n;
	$('#addToNotes').val(r);
	$('#addToNotes').trigger('keyup');
	
}

function countryChanged(country) {
	changeState(country);
}

function changeState(country) {
	$('#state_li').load('StateListAjax.action',{countryString: $('#newContractorCountry').val(), stateString: '<s:property value="newContractor.state.isoCode"/>'});
}

function updateUsersList() {
	$('#loadUsersList').load('OperatorUserListAjax.action',{opID: $('#saveContractorForm_requestedOperator').val(),
		requestedUser: <s:property value="newContractor.requestedByUser == null ? 0 : newContractor.requestedByUser.id" />,
		requestID: <s:property value="requestID" />}, checkUserOther);
}

function checkUserOther() {
	if ($("#requestedUser").val() == 0)
		$("#requestedOther").show();
	else
		$("#requestedOther").hide();
}

function checkDate(){
	var date = $('.datepicker').val();
	date = new Date(date);
	if(date==null){
		var newDate = $.datepicker.formatDate("mm/dd/yy", new Date()) 
		$('.datepicker').val(newDate);
	}
	if(date < new Date()){
		var newDate = $.datepicker.formatDate("mm/dd/yy", new Date()) 
		$('.datepicker').val(newDate);
	}
}

function addAttachment(formName, filename) {
	$.fancybox.close();
	var id = filename.substring(0, filename.indexOf('.'));
	
	var attachment = '<span id="' + id + '"><a href="#" class="remove" onclick="removeAttachment(\'' + id
		+ '\'); return false;">' + formName + '</a><input type="hidden" id="' + id + '_input" name="filenames" value="'
		+ filename + '" /><br /></span>';
	
	$('#attachment').append(attachment);
	$('#'+id+'_input').val(filename);
}

function removeAttachment(id) {
	$('span#'+id).remove();
}

function addToken(token) {
	$('#email_body').val($('#email_body').val() + "<" + token + ">");
	$('#email_body').focus();
}

function getMatches(requestID) {
	var data = {
		button: 'MatchingList',
		requestID: requestID
	};

	$('#potentialMatches').append('<img src="images/ajax_process.gif" style="border: none;" />');
	$('#potentialMatches').load('RequestNewContractorAjax.action', data);
}
</script>
</head>
<body>
<h1>Registration Request</h1>

<pics:permission perm="RequestNewContractor">
	<a href="ReportNewRequestedContractor.action">&lt;&lt; Back to Registration Requests</a>
</pics:permission>

<s:include value="../actionMessages.jsp"></s:include>

<s:if test="newContractor.contractor != null || !newContractor.open">
	<div class="info">
		<s:if test="newContractor.contractor != null">
			<s:property value="newContractor.contractor.name" /> has registered an account with PICS on <strong><s:date name="newContractor.contractor.creationDate" format="M/d/yyyy" /></strong><br/>
		</s:if>
		<s:if test="newContractor.open">
			Click here to <a href="RequestNewContractor.action?requestID=<s:property value="newContractor.id" />&button=Close Request" class="picsbutton positive">Close the Request</a>
		</s:if>
		<s:else>
			This request is closed.
		</s:else>
	</div>
</s:if>

<s:form id="saveContractorForm">
	<s:hidden name="requestID"/>
	<s:hidden name="conID" id="conID" />
	<table id="requestTable" style="width: 100%;">
		<tr>
			<td>
				<fieldset class="form">
				<h2 class="formLegend">Company Information</h2>
				<ol>
					<li><label>Company Name:</label>
						<s:textfield cssClass="checkReq" name="newContractor.name" size="35" />
						<div id="think_name"></div>
						<div id="match_name"></div>
					</li>
					<li><label>Contact Name:</label>
						<s:textfield cssClass="checkReq" name="newContractor.contact" />
						<div id="think_contact"></div>
						<div id="match_contact"></div>
						<br />
					</li>
					<s:if test="newContractor.phone == null && newContractor.email == null">
						<li>
							<span style="margin-left: 11em; padding-bottom: 0px; clear: right;">
								(Phone <i>or</i> Email is required)
							</span>
						</li>
					</s:if>
					<li>
						<label>Phone:</label>
						<s:textfield cssClass="checkReq" name="newContractor.phone" size="20" />
						<s:if test="newContractor.id > 0 && newContractor.phone != null && newContractor.phone.length() > 0 && !permissions.operatorCorporate">
							<input type="submit" class="picsbutton" name="button" value="Contacted By Phone" />
						</s:if>
						<div id="think_phone"></div>
						<div id="match_phone"></div>
					</li>
					<li><label for="email">Email:</label>
						<s:textfield cssClass="checkReq" name="newContractor.email" size="30" id="email" />
						<s:if test="newContractor.id > 0 && newContractor.email.length() > 0 && !permissions.operatorCorporate">
							<input type="button" onclick="$('#email_preview').toggle(); return false;" class="picsbutton" value="Edit Email" />
							<table id="email_preview">
								<tr>
									<td>
										<s:if test="formsViewable && attachment == null && forms.size() > 0">
											<a href="#operatorForms" class="add fancybox" title="Add Attachment" onclick="return false;">Add Attachment</a>
										</s:if>
									</td>
									<td>
										<s:if test="formsViewable && attachment == null && forms.size() > 0">
											<div id="attachment"></div>
										</s:if>
									</td>
								</tr>
								<tr>
									<td>Subject: <input id="email_subject" name="emailSubject" value="<s:property value="emailSubject" />" size="30"/></td>
									<td>Fields: <s:select list="tokens" onchange="addToken(this.value);"></s:select></td>
								</tr>
								<tr><td colspan="2">
									<s:textarea cols="120" rows="10" name="emailBody" id="email_body" cssStyle="width: 600px;"></s:textarea>
								</td></tr>
								<tr><td colspan="2"><input type="submit" name="button" class="picsbutton positive" value="Send Email" /></td></tr>
							</table>
						</s:if>
						<div id="think_email"></div>
						<div id="match_email"></div>
					</li>
					<li><label for="taxID">Tax ID:</label>
						<s:textfield cssClass="checkReq" name="newContractor.taxID" size="9" maxLength="9" id="taxID" />
						<div class="fieldhelp">
						<h3>Tax ID</h3>
						<p>Optional field for Tax ID</p>
						</div>
						<div id="think_tax"></div>
						<div id="match_tax"></div>
					</li>
					<s:if test="assignedCSR != null">
						<li><label>Assigned PICS CSR:</label>
							<nobr><s:property value="assignedCSR.name" /> / <s:property value="assignedCSR.phone"/></nobr>
						</li>
					</s:if>	
				</ol>
				</fieldset>
				<fieldset class="form">
				<h2 class="formLegend">Primary Address</h2>
				<ol>
					 <li><label for="newContractorCountry">Country:</label>
						<s:select
							list="countryList" name="country.isoCode" id="newContractorCountry"
							listKey="isoCode" listValue="name" value="%{newContractor.country.isoCode}"
							onchange="countryChanged(this.value)" />
					</li>
					<li id="state_li"></li>
					<li>
						<label for="city">City:</label>
						<s:textfield name="newContractor.city" size="20" id="city" cssClass="show-address"/>
						<div class="fieldhelp">
						<h3>City</h3>
						<p>Optional field for City</p>
						</div>
					</li>
					<li class="address-zip">
						<label for="address">Address:</label>
						<s:textfield name="newContractor.address" size="35" id="address" />
						<div class="fieldhelp">
						<h3>Address</h3>
						<p>Optional field for Address</p>
						</div>
					</li>
					<li class="address-zip">
						<label for="zip">Zip:</label><s:textfield name="newContractor.zip" size="7" id="zip" />
						<div class="fieldhelp">
						<h3>Zip</h3>
						<p>Optional field for Zip</p>
						</div>
					</li>		
				</ol>
				</fieldset>
				<fieldset class="form">
				<h2 class="formLegend">Request Summary</h2>
				<ol>
					<li><label>Requested By Account:</label>
						<s:select list="operatorsList" headerKey="0" headerValue="- Select a Operator -"
							name="requestedOperator" onchange="updateUsersList();" listKey="id" listValue="name"
							value="%{newContractor.requestedBy.id}" />
					</li>
					<s:if test="newContractor.requestedByUser != null || newContractor.requestedByUserOther != null">
						<li id="loadUsersList"><script type="text/javascript">updateUsersList();</script></li>
					</s:if>
					<s:else>
						<li id="loadUsersList"></li>
					</s:else>
					<s:if test="newContractor.requestedByUser != null && newContractor.id > 0">
						<li><label>Add to Watchlist:</label>
							<s:checkbox name="newContractor.watch" />
							<div class="fieldhelp">
								<h3>Add To Watchlist</h3>
								When a contractor in the PICS database is associated with this request, <s:property value="newContractor.requestedByUser.name" /> will be able to watch <s:property value="newContractor.name" /> on their watchlist.
							</div>
							<s:if test="!contractorWatch && newContractor.watch">
								<div class="alert">This user does not have the Contractor Watch permission.</div>
							</s:if>
						</li>
					</s:if>
					<li><label>Registration Deadline:</label> <input id="regDate" name="newContractor.deadline" type="text"
						class="datepicker" size="10"
						value="<s:date name="newContractor.deadline" format="MM/dd/yyyy" />" onchange="checkDate()" />
					</li>
				</ol>
				</fieldset>
				<fieldset class="form">
				<h2 class="formLegend">Contact Summary</h2>
				<ol>
					<s:if test="newContractor.id > 0">
						<li><label>Last Contacted By:</label>
							<s:property value="newContractor.lastContactedBy.name" /><br /></li>
						<li><label>Date	Contacted:</label>
							<s:date name="newContractor.lastContactDate" format="MM/dd/yyyy" /><br /></li>
					</s:if>
					<s:if test="newContractor.id > 0">
						<s:if test="permissions.admin">
							<li><label>Who should follow up?:</label>
								<s:radio list="#{'PICS':'PICS','Operator':'Operator'}" name="newContractor.handledBy" theme="pics"/>
							</li>
						</s:if>
						<li><label># of Times Contacted:</label>
							<s:property value="newContractor.contactCount"/></li>
						<li><label>Matches Found in PICS:</label>
							<s:if test="newContractor.matchCount > 0 && newContractor.open">
								<a href="#potentialMatches" onclick="getMatches(<s:property value="newContractor.id" />);"><s:property value="newContractor.matchCount"/></a>
							</s:if>
							<s:else>
								<s:property value="newContractor.matchCount" />
							</s:else>
						</li>
						<li><label>PICS Contractor:</label>
							<s:if test="permissions.admin">
								<s:textfield value="%{newContractor.contractor.name}" id="matchedContractor" size="20" />
								<div class="fieldhelp">
									<h3>Return To Operator</h3>
									After you enter in a contractor and save, this request will be automatically returned to the operator.
								</div>
							</s:if>
							<s:if test="newContractor.contractor != null">
								<a href="ContractorView.action?id=<s:property value="newContractor.contractor.id"/>">
								<s:property value="newContractor.contractor.name"/></a>
							</s:if>
						</li>
					</s:if>
					<li><label>Notes:</label>
						<s:if test="permissions.admin">
							<s:select headerKey="0" headerValue="Select a preformatted note to add it to the Notes section" 
								list="noteReason" id="noteReason" onchange="fillNotes()" cssStyle="margin-bottom: 10px;" />
						</s:if>
						<div>
							<s:textarea name="addToNotes" cols="30" rows="3" id="addToNotes" />
						</div>
						<div class="fieldhelp">
						<h3>Notes</h3>
						<p>Information about this registration request</p>
						</div>
					</li>
					<li>
						<div id="notesHere">
							<pre id="addHere"></pre>
							<s:if test="newContractor.notes.length() > 0">
								<pre id="notesPreview"><s:property value="newContractor.notes" /></pre>
							</s:if>
						</div>					
					</li>
				</ol>
				</fieldset>
			</td>
		</tr>
	</table>
	<fieldset class="forms submit">
	  	<input type="submit" class="picsbutton positive" name="button" value="Save" />
	  	<s:if test="newContractor.contractor != null || (permissions.operatorCorporate && newContractor.id > 0) || newContractor.handledBy.toString() == 'Operator'">
		  	<input type="submit" class="picsbutton negative" name="button" value="Close Request" />
		</s:if>
		<s:elseif test="permissions.admin && newContractor.id > 0 && newContractor.handledBy.toString() == 'PICS'">
			<input type="submit" class="picsbutton" name="button" value="Return To Operator" />
		</s:elseif>
	</fieldset>
</s:form>

<div style="display: none" id="load"></div>

<s:if test="formsViewable && forms.size() > 0">
<div id="hidden"><div id="operatorForms">
	<table class="report">
		<thead>
			<tr>
				<th colspan="2">Forms</th>
				<th>Facility</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="forms" status="stat">
				<tr>
					<td class="right"><s:property value="#stat.index + 1" /></td>
					<td><a href="#" onclick="addAttachment('<s:property value="formName" />','<s:property value="file" />'); return false;">
						<s:property value="formName" /></a></td>
					<td><s:property value="account.name" /></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</div></div>
</s:if>

</body>
</html>
