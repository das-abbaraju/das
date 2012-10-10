PICS.define('report.Tester', {
    methods: {
        init: function () {
            $('#models td.results').each(function(index) {
                var modelType = $(this).text();
                $.getJSON("ReportDynamic!availableFields.action", {"report.modelType": modelType}, function(data) {
                    var resultTD = $('#models-row-' + data.modelType + ' td.results');
                    if (data.success) {
                        resultTD.text("OK - " + data.fields.length + " fields");
                        resultTD.addClass("success");
                    } else {
                        resultTD.text(data.message);
                        resultTD.addClass("fail");
                    };
                });
            });
            $('#reports td.results').each(function(index) {
                var reportID = $(this).text();
                $.getJSON("ReportData!report.action", {"report": reportID}, function(data) {
                    var resultTD = $('#reports-row-' + data.reportID + ' td.results');
                    resultTD.removeClass("waiting");
                    if (data.success) {
                        resultTD.text("OK");
                        resultTD.addClass("success");
                    } else {
                        resultTD.text(data.message);
                        resultTD.addClass("fail");
                    };
                });
            });
        }
    }
});
