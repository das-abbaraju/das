(function($) {
    PICS.define('subcontractor.Report', {
        methods : {
            init : function() {
                $('#ReportSubcontractors-page table.report input[type=checkbox].selectAll').live('click', this.toggleAllSelectable);
                $('#ReportSubcontractors-page form .save').live('click', this.toggleAllSelectable);
            },

            toggleAllSelectable : function(event) {
                var isChecked = $(this).is(':checked');
                $('#ReportSubcontractors-page table.report input[type=checkbox].selectable').attr('checked', isChecked);
            },

            submitForm : function(event) {
                event.preventDefault();
                
                var formElement = $(this).closest('form');
                console.log(formElement.serialize());
            }
        }
    });
})(jQuery);