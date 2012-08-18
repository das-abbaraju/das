PICS.define('report.ListMyReportsController', {
	methods: {
		init: function () {
			if ($('#ManageReports_myReportsList_page').length) {
				$('.dropdown-toggle').dropdown();
			}
		}
	}
});