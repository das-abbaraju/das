PICS.define('report.DirectoryController', {
    methods: {
        init: function () {
            if ($('#ReportTester_directory_page').length > 0) {
                $('#directory_table')
                    .dataTable({
                        fnDrawCallback: function () {
                            $('#directory_table').css('visibility', 'visible');
                        }
                    })
                    .columnFilter({
                        aoColumns: [{
                            type: "text",
                            bRegex:true
                        }, {
                            type: "text",
                            bRegex:true
                        }, {
                            type: "text",
                            bRegex:true
                        }, {
                            type: "text",
                            bRegex:true
                        }, {
                            type: "text",
                            bRegex:true
                        }, {
                            type: "select",
                            values: ['Yes', 'No']
                        }, {
                            type: "select",
                            values: ['Yes', 'No']
                        }, {
                            type: "select",
                            values: ['Yes', 'No']
                    }]
                });
            }
        }
    }
});