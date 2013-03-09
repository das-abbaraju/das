PICS.define('report.manage-report.SearchController', {
    methods: {
        init: function () {
            if ($('#ManageReports_search_page').length > 0) {
                $('#report_search_form input[type=text]').on('keyup', PICS.debounce(this.onSearchKeyup, 250));
            }
        },
        
        onSearchKeyup: function (event) {
            var element = $(this);
            
            PICS.ajax({
                url: 'ManageReports!search.action',
                data: {
                    searchTerm: element.val()
                },
                success: function (data, textStatus, jqXHR) {
                    $('#search_reports_container').html(data);
                }
            });
        }
    }
});