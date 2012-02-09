PICS.define('audit.ScheduleAudit', {
    methods: {
        init: function () {
            var that = this;
            
            // schedule_audit_select
            $('#ScheduleAudit #timezone').bind('change', this.updateTimeZone);
            $('#show_next').bind('click', this.showMoreTimeslots);
            
            // schedule_audit_address
            $('#verifyButton').bind('click', function (event) {
                that.verifyAddress.apply(that, [event]);
            });
            
            $('.calculated-address input').bind('keydown', PICS.throttle(this.unverifyAddress, 250));
            
            $('.calculated-address select').bind('change', this.unverifyAddress);
            
            $('#unverifiedCheckbox').bind('change', function (event) {
                if ($(this).is(':checked')) {
                    $('#submitButton').show();
                    $('#verifyButton').hide();
                } else {
                    $('#submitButton').hide();
                    $('#verifyButton').show();
                }
            });
            
            if (this.isAddressVerified()) {
                $('#submitButton').show();
                $('#verifyButton').hide();
            } else {
                $('#submitButton').hide();
                $('#verifyButton').show();
            }
        },
        
        /**
         * Is Address Verified
         * 
         * Checks to see if the latitude and longitude fields
         * have a value that != 0 (Have been filled out by successful google verification)
         * 
         * @returns boolean
         */
        isAddressVerified: function () {
            if ($('#conAudit_latitude').val() == 0) {
                return false;
            }
                
            if ($('#conAudit_longitude').val() == 0) {
                return false;
            }

            return true;
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
                    auditID: auditID,
                    selectedTimeZone: $('#timezone').val(),
                    availabilityStartDate: startDate
                },
                success: function(text, textStatus) {
                    $('#li_availability').append(text);
                }
            });
        },
        
        /**
         * Unverify Address
         * 
         * Reset all fields to be at an 'unverified' status
         */
        unverifyAddress: function () {
            $('#conAudit_latitude').val(0);
            $('#conAudit_longitude').val(0);
            
            $('.verify-address-manual').hide();
            
            $('#submitButton').hide();
            $('#verifyButton').show();
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
        },
        
        /**
         * Verify Address
         * 
         * @event
         * 
         * @returns boolean
         */
        verifyAddress: function (event) {
            /**
             * Get Address Component From Results By Type
             * 
             * @results
             * @type
             * @key
             * 
             * @returns component value
             */
            function getAddressComponentFromResultsByType(results, type, key) {
                var component = results.address_components.filter(function (item) {
                    return item.types[0] == type;
                });
                
                return component[0][key] ? component[0][key] : null;
            }
            
            var that = this;
            
            // get values to build full address to send to google
            var address = $('#conAudit_address').val();
            var city = $('#conAudit_city').val();
            var state = $('#conAudit_state').val();
            var zip = $('#conAudit_zip').val();
            var country = $('#conAudit_country').val();
            
            var full_address = [address, city, state, zip, country].join(', ');
            
            var geocoder = new google.maps.Geocoder();
            
            if (geocoder) {
                geocoder.geocode({
                    address: full_address 
                }, function(results, status) {
                    if (results[0].address_components.length > 7) {
                        var latlong = results[0].geometry.location;
                        
                        // set lat long values
                        $('#conAudit_latitude').val(latlong.lat());
                        $('#conAudit_longitude').val(latlong.lng());
                        
                        // override address city, state, zip, country with google values
                        $('#conAudit_city').val(getAddressComponentFromResultsByType(results[0], 'locality', 'long_name'));
                        $('#conAudit_state').val(getAddressComponentFromResultsByType(results[0], 'administrative_area_level_1', 'short_name'));
                        $('#conAudit_zip').val(getAddressComponentFromResultsByType(results[0], 'postal_code', 'long_name'));
                        $('#conAudit_country').val(getAddressComponentFromResultsByType(results[0], 'country', 'short_name'));
                        
                        // post success message to corner of the screen
                        $.gritter.add({
                            title: translate('JS.ScheduleAudit.title.AddressVerification'), 
                            text: translate('JS.ScheduleAudit.success.AddressFound', [ full_address ])
                        });
                        
                        // put address in successful state
                        $('.verify-address-manual').hide();
                        $('#verifyButton').hide();
                        $('#submitButton').show();
                    } else {
                        // put address in unsuccessful state
                        that.unverifyAddress();
                        
                        // display checkbox that toggles 'override' unverified address
                        $('.verify-address-manual').show();
                        
                        // post error message to corner of the screen
                        $.gritter.add({
                            title: translate('JS.ScheduleAudit.title.AddressVerification'), 
                            text: translate('JS.ScheduleAudit.error.AddressNotFound', [ full_address ])
                        });
                    }
                });
                
                return false;
            }
        }
    }
});