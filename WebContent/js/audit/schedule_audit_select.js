(function ($) {
    PICS.define('audit.ScheduleAuditSelect', {
        methods: {
            init: function () {
                var $select_timeslot_page = $('#ScheduleAudit_select_timeslot_page');
                
                if ($select_timeslot_page.length) {
                	var that = this;
                
	                $select_timeslot_page.on('click', '#show_next', this.showMoreTimeslots);
	                $select_timeslot_page.on('change', '#timezone', this.updateTimeZone);
	                $select_timeslot_page.on('click', 'a.expedite', this.showScheduleAuditExpediteModal);

                	$('window').ready(function (event) {
                		that.updateTimeZone.apply('#timezone', [event]);
            		});
                }
            },

            /**
             * Show More Timeslots
             * 
             * @event
             */
            showMoreTimeslots: function (event) {
                $(event.target).attr('disabled', true);
                
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

                var element = $(event.target);

                event.preventDefault();

                PICS.ajax({
                    url: 'ScheduleAudit!ajaxScheduleAuditExpediteModal.action',
                    success: function (data, textStatus, XMLHttpRequest) {
                        var modal = createModal(data);
                        modal.show();

                        $('.schedule-audit-expedite-modal').on('click', '.accept-expedite', function (event) {
                            window.location.href = element.attr('href');
                        });

                        $('.schedule-audit-expedite-modal').on('click', '.cancel-expedite', function (event) {
                            modal.hide();
                        });
                    }
                });
            },
            
            /**
             * Update Time Zone
             * 
             * @event
             */
            updateTimeZone: function (event) {
                var element = $(event.target),
                	auditID = $('#auditID');
                
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