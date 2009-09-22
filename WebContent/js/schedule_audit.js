// Statuses:
// 0 = No address entered
// 1 = Verified address, ready to submit
// 10 = Can't communicate with GMail
// 11 = Incompatible browser
// 12 = GeoCoder couldn't instantiate
// 13 = Invalid address

var status = 0;

$(document).ready(function() {
	if (isVerified()) {
		$(".calculatedAddress").show("slow");
		$("#submitButton").show();
	}
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
	$("#submitButton").hide();
	$("#unverifiedLI").show();
}

function verifyAddress() {
	if (GBrowserIsCompatible()) {
		var geocoder = new GClientGeocoder();
		if (geocoder) {
			startThinking({message: "Validating Address"});
			var address = $("#conAudit_address").val() + ", " + $("#conAudit_zip").val();
			geocoder.getLocations(
				address,
				function(matches) {
					try {
						// $.gritter.removeAll();
						if (matches.Placemark.length == 0) {
							throw("Address (" + address + ") could not be found");
						}
						
						var detail = matches.Placemark[0].AddressDetails;
						var latlong = matches.Placemark[0].Point.coordinates;
						if (detail.Accuracy < 8) {
							throw("Address (" + address + ") could not be found accurately");
						}

						$("#conAudit_latitude").val(latlong[1]);
						$("#conAudit_longitude").val(latlong[0]);
						$("#conAudit_country").val(detail.Country.CountryNameCode);
						$("#conAudit_state").val(detail.Country.AdministrativeArea.AdministrativeAreaName);
						var city = null;
						if (detail.Country.AdministrativeArea.SubAdministrativeArea != null)
							city = detail.Country.AdministrativeArea.SubAdministrativeArea.Locality;
						else
							city = detail.Country.AdministrativeArea.Locality;
						$("#conAudit_city").val(city.LocalityName);
						$("#conAudit_address").val(city.Thoroughfare.ThoroughfareName);

						$("#unverifiedLI").hide();
						$("#submitButton").show();
					} catch(err) {
						unVerify();
						$.gritter.add({title: 'Address Verification', text: err});
					}
					$(".calculatedAddress").show("slow");
					stopThinking();
				}
			);
			return false;
		}
	} else {
		$(".calculatedAddress").show("slow");
		$("#submitButton").show();
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
