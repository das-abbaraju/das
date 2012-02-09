PICS.define('audit.ScheduleAudit', {
    methods: {
        init: function () {
            $('#ScheduleAudit #timezone').bind('change', this.updateTimeZone);
        },
        
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