(function ($) {
	PICS.define('contractor.registrationRequest.Report', {
		methods: {
			init: function () {
				$('.ReportNewRequestedContractor-page a.excel').live('click', this.downloadExcelFile);
				$('.ReportNewRequestedContractor-page a.excelUpload').live('click', this.uploadExcelFile);
			},
			
			downloadExcelFile: function(event) {
				event.preventDefault();
				
				var num = $(this).attr('rel');
				
				var confirmed = false;
				if (num > 500)
					confirmed = confirm(translate('JS.ConfirmDownloadAllRows'));
				else
					confirmed = true;
				
				if (confirmed) {
					newurl = 'ReportNewRequestedContractorCSV.action?' + $('#form1').serialize();
					popupWin = window.open(newurl, 'ReportNewRequestedContractor', '');
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