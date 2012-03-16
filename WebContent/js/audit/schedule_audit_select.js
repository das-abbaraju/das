(function ($) {
    PICS.define('audit.ScheduleAuditSelect', {
        methods: {
            init: function () {
                var element = $('#ScheduleAudit_address_page');
                
                if (element.length) {
                    $('#timezone', element).bind('change', this.updateTimeZone);
                    $('#show_next', element).bind('click', this.showMoreTimeslots);
                }
            },
            
            /**
             * Show More Timeslots
             * 
             * @event
             */
            showMoreTimeslots: function (event) {
                $(this).attr('disabled', true);
                
                PICS.ajax({
                    url:'ScheduleAudit!viewMoreTimes.action',
                    data: {
                        auditID: $('#ScheduleAudit_auditID').val(),
                        selectedTimeZone: $('#timezone').val(),
                        availabilityStartDate: startDate
                    },
                    success: function(data, textStatus, XMLHttpRequest) {
                        $('#li_availability').append(data);
                    }
                });
            },
            
            /**
             * Update Time Zone
             * 
             * @event
             */
            updateTimeZone: function (event) {
                var element = $(this);
                var auditID = $('#auditID');
                
                PICS.ajax({
                    url: 'ScheduleAudit!changeSelectedTimeZone.action',
                    data: {
                        auditID: auditID.val(),
                        selectedTimeZone: element.val()
                    },
                    success: function (data, textStatus, XMLHttpRequest) {
                        $('#li_availability').html(data);
                    }
                });
            }
        }
    });
})(jQuery);