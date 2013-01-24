(function ($) {
	PICS.define('contractor.registrationRequest.Report', {
		methods: {
			init: function () {
			    function performDelegation(element, reportRegistrationRequests) {
			        if (element.length) {
			            element.delegate('a.excel', 'click', reportRegistrationRequests.downloadExcelFile);
			            element.delegate('a.excelUpload', 'click', reportRegistrationRequests.uploadExcelFile);
			        }
			    }
			    
			    performDelegation($('.ReportNewRequestedContractor-page'), this);
			    performDelegation($('.ReportRegistrationRequests-page'), this);
			},
			
			downloadExcelFile: function(event) {
				event.preventDefault();
				
				var num = $(this).attr('rel');
				var url = $(this).attr('data-url');
				
				if (!url) {
				    url = 'ReportNewRequestedContractorCSV.action';
				}
				
				var confirmed = false;
				if (num > 500)
					confirmed = confirm(translate('JS.ConfirmDownloadAllRows'));
				else
					confirmed = true;
				
				if (confirmed) {
					newurl = url + '?' + $('#form1').serialize();
					popupWin = window.open(newurl, 'ReportNewRequestedContractorCSV', '');
				}
			},
			
			uploadExcelFile: function(event) {
				event.preventDefault();

				var url = $(this).attr('data-url');
				var title = 'Upload';
				var pars = 'scrollbars=yes,resizable=yes,width=650,height=400,toolbar=0,directories=0,menubar=0';
				
				fileUpload = window.open(url,title,pars);
				fileUpload.focus();
			}
		}
	});
})(jQuery);