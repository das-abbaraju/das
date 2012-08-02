(function ($) {
    PICS.define('audit.ScheduleAuditAddress', {
        methods: {
            init: function () {
                var that = this;
                
                if ($('#ScheduleAudit__page').length) {
                    $('#verifyButton').bind('click', function (event) {
                        that.addressVerify.apply(that, [event]);
                    });
                    
                    $('.calculated-address input').bind('keydown', PICS.throttle(this.addressUnverify, 250));
                    
                    $('.calculated-address select').bind('change', this.addressUnverify);
                    
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
                }
            },
            
            /**
             * Unverify Address
             * 
             * Reset all fields to be at an 'unverified' status
             */
            addressUnverify: function () {
                $('#conAudit_latitude').val(0);
                $('#conAudit_longitude').val(0);
                
                $('.verify-address-manual').hide();
                
                $('#submitButton').hide();
                $('#verifyButton').show();
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
             * Verify Address
             * 
             * @event
             * 
             * @returns boolean
             */
            addressVerify: function (event) {
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
                        var types = item.types;
                        var is_found = false;
                        
                        $.each(types, function (key, value) {
                            if (value == type) {
                                is_found = true;
                            }
                        });
                        
                        return is_found;
                    });
                    
                    return component.length && component[0][key] ? component[0][key] : null;
                }
                
                var that = this;
                
                // get values to build full address to send to google
                var address = $('#conAudit_address').val();
                var city = $('#conAudit_city').val();
                var countrySubdivision = $('#conAudit_countrySubdivision').val();
                var state = countrySubdivision.substr(string.length-2,2);
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
                            that.addressUnverify();
                            
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
})(jQuery);