(function($) {
    PICS.define('subcontractor.Report', {
        methods : {
            init : function() {
                var page = $('.ReportSubcontractors-page');
                
                if (page.length) {
                    page.find('table.report input[type=checkbox].selectAll').live('click', this.toggleAllSelectable);
                    page.find('form .save').live('click', this.submitForm);
                }
            },

            toggleAllSelectable : function(event) {
                var isChecked = $(this).is(':checked');
                $('.ReportSubcontractors-page table.report input[type=checkbox].selectable').attr('checked', isChecked);
            },

            submitForm : function(event) {
                event.preventDefault();

                var url = $(this).attr('data-url');
                var formElement = $(this).closest('form');
                var reportData = formElement.find('#report_data');
                var serializedParameters = formElement.serialize();
                var htmlData = null;
                
                reportData.html('<img src="images/ajax_process2.gif" />' + translate('JS.Loading'));
                
                PICS.ajax({
                   url: url + "?" + serializedParameters,
                   success: function(data, textStatus, XMLHttpRequest) {
                       htmlData = data;
                   },
                   error: function(XMLHttpRequest, textStatus, errorThrown) {
                       htmlData = errorThrown;
                   },
                   complete: function(XMLHttpRequest, textStatus) {
                       reportData.html(htmlData);
                   }
                });
            }
        }
    });
})(jQuery);