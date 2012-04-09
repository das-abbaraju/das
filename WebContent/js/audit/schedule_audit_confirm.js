(function ($) {
    PICS.define('audit.ScheduleAuditConfirm', {
        methods: {
            init: function () {
                var that = this;
                var processing = false;
                
                if ($('#ScheduleAudit_selectTime_page').length) {
                    $('#ScheduleAudit_readInstructions, #ScheduleAudit_confirmed').bind('click', this.confirmScheduledAudit);
                    
                    // prevent double submit
                    $('.schedule-audit-confirm-form').bind('submit', function(event) {
                        if (processing === false) {
                            processing = true;
                        } else {
                            return false;
                        }
                    });
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