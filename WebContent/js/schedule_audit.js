// Statuses:
// 0 = No address entered
// 1 = Verified address, ready to submit
// 10 = Can't communicate with GMail
// 11 = Incompatible browser
// 12 = GeoCoder couldn't instantiate
// 13 = Invalid address

if (!window.SCHEDULE_AUDIT) {
	SCHEDULE_AUDIT = {};
}

/**
 * SCHEDULE AUDIT
 * 
 * Base Class
 */
SCHEDULE_AUDIT = {
	/**
	 * Base Class
	 * 
	 * isVerified()
	 * submit()
	 * unVerify()
	 * verifyAddress()
	 */
	Base: {
		/**
		 * Is Verified
		 * 
		 * comments...
		 */
		isVerified: function () {
			if ($("#conAudit_latitude").val() == 0) {
				return false;
			}
				
			if ($("#conAudit_longitude").val() == 0) {
				return false;
			}

			return true;
		},
		
		/**
		 * Submit
		 * 
		 * comments...
		 */
		submit: function (event) {
			if (!this.isVerified()) {
				
				if ($("#unverifiedCheckbox").val()) {
					return true;
				}
					
				this.verifyAddress();
				
				return false;
			}
			
			return true;
		},
		
		/**
		 * Unverified
		 * 
		 * comments...
		 */
		unVerify: function () {
			$("#conAudit_latitude").val(0);
			$("#conAudit_longitude").val(0);
			$("#submitButton").hide();
			$("#unverifiedLI").show();
		},
		
		/**
		 * Verify Address
		 * 
		 * comments...
		 */
		verifyAddress: function () {
//			var that = this;
//			
//			if (GBrowserIsCompatible()) {
//				var geocoder = new GClientGeocoder();
//				
//				if (geocoder) {
//					startThinking({
//						message: translate('JS.ScheduleAudit.message.ValidatingAddress')
//					});
//					
//					var address = $("#conAudit_address").val() + ", " + $("#conAudit_city").val() + ", " + $("#conAudit_state").val() + ", " + $("#conAudit_zip").val();
//					
//					geocoder.getLocations(address, function(matches) {
//						try {
//							// $.gritter.removeAll();
//							if (matches.Placemark.length == 0) {
//								throw(translate('JS.ScheduleAudit.error.AddressNotFound', [ address ]));
//							}
//							
//							var detail = matches.Placemark[0].AddressDetails;
//							var latlong = matches.Placemark[0].Point.coordinates;
//							
//							if (detail.Accuracy < 8) {
//								// Submitting city, state, zip, address, and country
//								// Accepting Google's best guess on approximate matches
//								throw(translate('JS.ScheduleAudit.error.AddressNotFound', [ address ]));
//							}
//
//							$("#conAudit_latitude").val(latlong[1]);
//							$("#conAudit_longitude").val(latlong[0]);
//							$("#conAudit_country").val(detail.Country.CountryNameCode);
//							$("#conAudit_state").val(detail.Country.AdministrativeArea.AdministrativeAreaName);
//							
//							var city = null;
//							
//							if (detail.Country.AdministrativeArea.SubAdministrativeArea != null) {
//								city = detail.Country.AdministrativeArea.SubAdministrativeArea.Locality;
//							} else {
//								city = detail.Country.AdministrativeArea.Locality;
//							}
//							
//							$("#conAudit_city").val(city.LocalityName);
//							$("#conAudit_address").val(city.Thoroughfare.ThoroughfareName);
//
//							$("#unverifiedLI").hide();
//							$("#submitButton").show();
//						} catch(err) {
//							that.unVerify();
//							
//							$.gritter.add({
//								title: translate('JS.ScheduleAudit.title.AddressVerification'), 
//								text: err
//							});
//						}
//						
//						$(".calculatedAddress").show("slow");
//						
//						stopThinking();
//					});
//					
//					return false;
//				}
//			} else {
//				$(".calculatedAddress").show("slow");
//				$("#submitButton").show();
//			}
		}
	},
	
	address: function() {
		
	},
	
	edit: function() {
		var that = Object.create(SCHEDULE_AUDIT.Base);
		
		that.init = function () {
			var processing = false;
			
			$('.schedule-audit-edit-form').bind('submit', function(event) {
				if (processing === false) {
					processing = true;
					
					return that.submit.apply(that, [event]);
				} else {
					return false;
				}
			});
		};
		
		return that;
	},
	
	confirm: function() {
		return {
			init: function () {
				var processing = false;
				
				$('.schedule-audit-confirm-form').bind('submit', function(event) {
					if (processing === false) {
						processing = true;
					} else {
						return false;
					}
				});
			}
		};
	}
};

$(function() {
	SCHEDULE_AUDIT.edit().init();
	SCHEDULE_AUDIT.confirm().init();
});

var status = 0;

$(function() {
	if (isVerified()) {
		$(".calculatedAddress").show("slow");
		$("#submitButton").show();
	}
	
	$('#show_next').live('click', function(e) {
		$(this).attr('disabled', true);
		$.ajax({
			 url:'ScheduleAudit!viewMoreTimes.action',
			 data:{availabilityStartDate:startDate,auditID:auditID},
			 success: function(text, textStatus) {
			   $('#li_availability').append(text);
			 }
		});
	});
});

function isVerified() {
	if ($("#conAudit_latitude").val() == 0)
		return false;
	if ($("#conAudit_longitude").val() == 0)
		return false;

	return true;
}

function unVerify() {
	$("#conAudit_latitude").val(0);
	$("#conAudit_longitude").val(0);
	$("#unverifiedCheckbox").attr('checked', false);
	$("#submitButton").hide();
	$("#unverifiedLI").show();
}

function verifyAddress() {
	function getAddressComponentFromResultsByType(results, type, key) {
		var component = results.address_components.filter(function (item) {
			return item.types[0] == type;
		});
		return component[0][key] ? component[0][key] : null;
	}

	var address = $("#conAudit_address").val() + ", " + $("#conAudit_city").val() + ", " + $("#conAudit_state").val() + ", " + $("#conAudit_zip").val();
	var geocoder = new google.maps.Geocoder();
	if (geocoder) {
		startThinking({message: translate('JS.ScheduleAudit.message.ValidatingAddress')});

		geocoder.geocode( { 'address': address }, function(results, status) {
			if (results[0].address_components.length > 7){
				var latlong = results[0].geometry.location;

				$("#conAudit_latitude").val(latlong.lat());
				$("#conAudit_longitude").val(latlong.lng());
				//$("#conAudit_country").val(getAddressComponentFromResultsByType(results[0], 'country', 'short_name'));
				//$("#conAudit_state").val(getAddressComponentFromResultsByType(results[0], 'administrative_area_level_1', 'short_name'));
				//$("#conAudit_city").val(getAddressComponentFromResultsByType(results[0], 'locality', 'long_name'));
				//var addr = getAddressComponentFromResultsByType(results[0], 'street_number', 'long_name') + " " +
				//getAddressComponentFromResultsByType(results[0], 'route', 'long_name');
				//$("#conAudit_address").val(addr);
				//$("#conAudit_address2").val(getAddressComponentFromResultsByType(results[0], 'subpremise', 'long_name'));

				$("#unverifiedLI").hide();
				$("#submitButton").show();

				$(".calculatedAddress").show("slow");

			} else {
				unVerify();
				throw(translate('JS.ScheduleAudit.error.AddressNotFound', [ address ]));
			}
		});
		stopThinking();
		return false;
	}
}

function submitForm() {
	if (!isVerified()) {
		if ($("#unverifiedCheckbox").val())
			return true;
		verifyAddress();
		return false;
	}
	return true;
}