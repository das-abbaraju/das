PICS.define('report.ListFavoritesController', {
	methods: {
		init: function () {
			if ($('#ManageReports_favoritesList_page').length) {
				$('.dropdown-toggle').dropdown();
			}
		}
	}
});