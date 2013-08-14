(function ($) {
	PICS.define('contractor.registrationRequest.Report', {
		methods: {
			init: function () {
				$('.NewContractorSearch-page').on('click', '#ImportRegistrationRequests', $.proxy(this.importRegistrationRequest, this));
				$('.ReportNewRequestedContractor-page').on('click', '#ImportRegistrationRequests', $.proxy(this.importRegistrationRequest, this));
				$('.ReportRegistrationRequests-page').on('click', '#ImportRegistrationRequests', $.proxy(this.importRegistrationRequest, this));

				$('.NewContractorSearch-page').on('click', '.download-search-results', $.proxy(this.getDownloadParameters, this));
				$('.ReportNewRequestedContractor-page').on('click', '.download-search-results', $.proxy(this.getDownloadParameters, this));
				$('.ReportRegistrationRequests-page').on('click', '.download-search-results', $.proxy(this.getDownloadParameters, this));
            },


            confirmDownload: function(results, url) {
				var confirmed = true;

				if (results > 500) {
					confirmed = confirm(translate('JS.ConfirmDownloadAllRows'));
				}
				
				if (confirmed) {
					this.downloadResults(url);
				}
			},
			
			downloadResults: function(download_url) {
				var $contractor_search_form = $('#form1'),
					default_action = $contractor_search_form.attr('action');

				// must make non-ajax post request to ensure foreign characters display correctly and do not break the request
				$contractor_search_form.attr('action', download_url);
				$contractor_search_form.submit();
				$contractor_search_form.attr('action', default_action);
			},

			getDownloadParameters: function(event) {
            	var $element = $(event.target),
					number_of_results = $element.attr('data-number-of-results'),
					url = $element.attr('data-url');
				
				this.confirmDownload(number_of_results, url);
            },

			importRegistrationRequest: function(event) {
				var $element = $(event.target),
					url = $element.attr('data-url'),
					title = 'Upload',
					parameters = 'scrollbars=yes,resizable=yes,width=650,height=400,toolbar=0,directories=0,menubar=0';
				
				fileUpload = window.open(url,title,parameters);
				fileUpload.focus();
			}
		}
	});
})(jQuery);