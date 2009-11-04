function showAudit(auditID) {
	// if this is an ajax call, then get the form elements and then post them through ajax and return the results to a div
	startThinking({div:'verification_audit', type:'large', message: 'Retrieving form...'});
	$('#verification_audit').load('VerifyAuditAjax.action',{'auditID': auditID});
	$(window).scrollTo('#verification_audit',0,0);

}
