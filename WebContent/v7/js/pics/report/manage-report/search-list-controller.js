PICS.define('report.manage-report.SearchListController', {
    methods: {
        init: function () {
            if ($('#ManageReports_searchList_page').length) {
                $('#report_search_form input[type=text]').on('keyup', PICS.debounce(this.onSearchKeyup, 250));
            }
        },
        
        onSearchKeyup: function (event) {
            var element = $(this);
            
            PICS.ajax({
                url: 'ManageReports!searchList.action',
                type: 'GET',
                data: {
                    searchTerm: element.val()
                },
                success: function (data, textStatus, jqXHR) {
                    $('#report_search_list').replaceWith(data);
                }
            });
        }
    }
});