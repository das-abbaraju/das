(function ($) {
    PICS.define('audit.ScheduleAuditSelect', {
        methods: {
            init: function () {
                var element = $('#ScheduleAudit_address_page');
                
                if (element.length) {
                    var that = this;
                    
                    var show_more_element = element.find('#show_next');
                    show_more_element.bind('click', this.showMoreTimeslots);
                    
                    var timezone_element = element.find('#timezone');
                    timezone_element.bind('change', this.updateTimeZone);
                    
                    element.delegate('.cal_times a', 'click', this.showScheduleAuditExpediteModal);
                    
                    $('window').ready(function (event) {
                        that.updateTimeZone.apply(timezone_element, [event]);
                    });
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
            
            showScheduleAuditExpediteModal: function (event) {
                function createModal(data) {
                    var modal = PICS.modal({
                        modal_class: 'modal schedule-audit-expedite-modal',
                        title: translate('JS.ScheduleAudit.ExpediteFee.Title'),
                        content: data,
                        buttons: [{
                            html: [
                                   '<a href="javascript:;" class="btn success accept-expedite">' + translate('JS.ScheduleAudit.Btn.AcceptFee') + '</a>',
                                   '<a href="javascript:;" class="btn cancel-expedite">' + translate('JS.ScheduleAudit.Btn.DeclineFee') + '</a>'
                                ].join('')
                        }],
                        width: 650
                    });
                    
                    return modal;
                }
                
                var expedite = $(this).closest('.cal_day').hasClass('rush-date');
                if (expedite) {
                    event.preventDefault();
                    
                    PICS.ajax({
                        url: 'ScheduleAudit!ajaxScheduleAuditExpediteModal.action',
                        success: function (data, textStatus, XMLHttpRequest) {
                            var modal = createModal(data);
                            console.log(modal);
                            modal.show();
                            
                            $('.schedule-audit-expedite-modal').delegate('.accept-expedite', 'click', function (event) {
                                window.location.href = element.attr('href');
                            });
                            
                            $('.schedule-audit-expedite-modal').delegate('.cancel-expedite', 'click', function (event) {
                                modal.hide();
                            });
                        }
                    });
                }
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