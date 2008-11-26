function showAudit(auditID) {
	// if this is an ajax call, then get the form elements and then post them through ajax and return the results to a div
	$('verification_audit').innerHTML = "<img src='images/ajax_process2.gif' width='48' height='48' /> retrieving form";
	var pars = 'auditID=' + auditID;
	
	var myAjax = new Ajax.Updater('verification_audit','VerifyAuditAjax.action', 
		{method: 'post',parameters: pars});
}
