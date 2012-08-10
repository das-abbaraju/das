(function ($) {
    PICS.define('audit.ScheduleAuditEdit', {
        methods: {
            init: function () {
                var that = this;
                var processing = false;
                
                if ($('#ScheduleAudit__page').length) {
                    var form = $('.schedule-audit-edit-form');
                    
                    // show expedite warning
                    form.delegate('#scheduledDateDay', 'change', this.showScheduleAuditExpediteModal);
                    
                    // if the form is flagged to have a reschedule-fee hijack date + time fields
                    if (form.hasClass('reschedule-fee')) {
                        $("#scheduledDateTime, #scheduledDateDay").bind('click', this.showRescheduleFeeNotification);
                    } else {
                        this.initDateTimePickers();
                    }
                    
                    // display google map preview
                    this.showGoogleMap();
                    
                    // update location with primary contact
                    $('.update-location-contractor-info').bind('click', this.updateLocationWithContractorInformation);
                    
                    // fee continue
                    $('.reschedule-fee-continue').bind('click', function (event) {
                        that.rescheduleFee.apply(that, [false]);
                    });
                    
                    // fee override
                    $('.reschedule-fee-override').bind('click', function (event) {
                        that.rescheduleFee.apply(that, [true]);
                    });
                    
                    // prevent double submit
                    form.bind('submit', function(event) {
                        if (processing === false) {
                            processing = true;
                        } else {
                            return false;
                        }
                    });
                }
            },
            
            // init date + time pickers
            initDateTimePickers: function () {
                var audit_date_input = $('#scheduledDateDay');
                var audit_time_input = $('#scheduledDateTime');
                
                audit_date_input.datepicker({
                    minDate: new Date(), 
                    numberOfMonths: [1, 2]
                });
                
                audit_time_input.timeEntry({
                    ampmPrefix: ' ',
                    spinnerImage: 'images/spinnerDefault.png' 
                });
            },
            
            /**
             * Reschedule Fee
             *  
             * @override:
             */
            rescheduleFee: function (override) {
                var notification = $('#needsReschedulingFee');
                var audit_date_input = $('#scheduledDateDay');
                var audit_time_input = $('#scheduledDateTime');
                
                // display override or fee messages
                if (override) {
                    var override_input = $('input[name="feeOverride"]');
                    override_input.val(true);
                    
                    notification.text(translate('JS.ScheduleAudit.message.NoReschedulingFee'));
                } else {
                    notification.text(translate('JS.ScheduleAudit.message.ReschedulingFee'));
                }
            
                // disable pickers for editting
                audit_date_input.attr('disabled', false);
                audit_time_input.attr('disabled', false);
                
                // enable pickers
                audit_date_input.datepicker({
                    minDate: new Date(), 
                    numberOfMonths: [1, 2]
                });
                
                audit_date_input.datepicker('show');
                
                audit_time_input.timeEntry({
                    ampmPrefix: ' ',
                    spinnerImage: 'images/spinnerDefault.png' 
                });
            },
            
            showGoogleMap: function () {
                var lat = $('#conAudit_latitude').val();
                var lng = $('#conAudit_longitude').val();
                
                var map = new google.maps.Map(document.getElementById("mappreview"), {
                    center: new google.maps.LatLng(lat, lng),
                    zoom: 8,
                    mapTypeId: google.maps.MapTypeId.ROADMAP
                });
                
                return map;
            },
            
            // one time schedule fee notification message
            showRescheduleFeeNotification: function (event) {
                var notification = $('#needsReschedulingFee'); 
                notification.slideDown();
                
                var audit_date_input = $('#scheduledDateDay');
                audit_date_input.attr('disabled', true);
                
                var audit_time_input = $('#scheduledDateTime');
                audit_time_input.attr('disabled', true);
                
                audit_date_input.unbind('click');
                audit_time_input.unbind('click');
            },
            
            showScheduleAuditExpediteModal: function (event) {
                function createModal(data) {
                    var modal = PICS.modal({
                        modal_class: 'modal schedule-audit-expedite-modal',
                        title: translate('JS.ScheduleAudit.ExpediteFee.Title'),
                        content: data,
                        buttons: [{
                            html: [
                                '<a href="javascript:;" class="btn cancel-expedite">' + translate('JS.button.Cancel') + '</a>',
                                '<a href="javascript:;" class="btn success accept-expedite">' + translate('JS.button.Accept') + '</a>'
                            ].join('')
                        }]
                    });
                    
                    return modal;
                }
                
                var element = $(this);
                var original_date = element.attr('data-date');
                var date = element.val();
                
                var new_date = new Date(element.val());
                var today = new Date();
                var difference = new_date - today;
                var day_difference = Math.ceil(difference/(1000 * 60 * 60 * 24));
                
                if (day_difference <= 10 && date < original_date) {
                    PICS.ajax({
                        url: 'ScheduleAudit!ajaxScheduleAuditExpediteModal.action',
                        success: function (data, textStatus, XMLHttpRequest) {
                            var modal = createModal(data);
                            modal.show();
                            
                            $('.schedule-audit-expedite-modal').delegate('.accept-expedite', 'click', function (event) {
                                modal.hide();
                            });
                            
                            $('.schedule-audit-expedite-modal').delegate('.cancel-expedite', 'click', function (event) {
                                modal.hide();
                                
                                element.val(original_date);
                            });
                        }
                    });
                }
            },
            
            updateLocationWithContractorInformation: function (event) {
                var contractor_id = $('#ScheduleAudit_conID').val();
                
                if (!contractor_id) {
                    throw 'updateLocationWithContractorInformation missing contractor_id';
                }
                
                PICS.ajax({
                    url: 'ContractorJson.action',
                    data: {
                        id: contractor_id
                    },
                    success: function (data, textStatus, XMLHttpRequest) {
                        var contractor = data;
                        
                        $('form [name="conAudit.contractorContact"]').val(contractor.primaryContact.name);
                        $('form [name="conAudit.phone"]').val(contractor.primaryContact.phone);
                        $('form [name="conAudit.phone2"]').val(contractor.primaryContact.email);
                        $('form [name="conAudit.address"]').val(contractor.address);
                        $('form [name="conAudit.city"]').val(contractor.city);
                        $('form [name="conAudit.countrySubdivision"]').val(contractor.countrySubdivision);
                        $('form [name="conAudit.zip"]').val(contractor.zip);
                        $('form [name="conAudit.country"]').val(contractor.country);
                        
                        // TODO: method does not exist
                        //recenterMap();
                    }
                });
            }
        }
    });
})(jQuery);