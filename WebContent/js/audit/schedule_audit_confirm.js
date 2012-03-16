(function ($) {
    PICS.define('audit.ScheduleAuditConfirm', {
        methods: {
            init: function () {
                var that = this;
                
                if ($('#ScheduleAudit_selectTime_page').length) {
                    $('#ScheduleAudit_readInstructions, #ScheduleAudit_confirmed').bind('click', this.confirmScheduledAudit);
                }
            },
            
            confirmScheduledAudit: function (event) {
                var check_read_instructions = $('#ScheduleAudit_readInstructions');
                var check_confirmed = $('#ScheduleAudit_confirmed');
                var confirm = $('.schedule-audit-confirm-form #ScheduleAudit__confirm');
                
                if (check_read_instructions.is(':checked') && check_confirmed.is(':checked')) {
                    confirm.show();
                } else {
                    confirm.hide();
                }
            }
        }
    });
})(jQuery);