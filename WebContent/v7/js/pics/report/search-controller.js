PICS.define('report.SearchController', {
    methods: {
        init: function () {
            $('#report_search_form input[type=text]').on('keyup', PICS.debounce(this.onSearchKeyup, 250));
        },
        
        onSearchKeyup: function (event) {
            var element = $(this);
            
            PICS.ajax({
                url: 'ManageReports!searchList.action',
                type: 'get',
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