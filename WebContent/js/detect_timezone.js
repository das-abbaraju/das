/* 
 * Original script by Josh Fraser (http://www.onlineaspect.com)
 * Continued by Jon Nylander, (jon at pageloom dot com)
 * According to both of us, you are absolutely free to do whatever 
 * you want with this code.
 * 
 * This code is  maintained at bitbucket.org as jsTimezoneDetect.
 */

/**
 * Namespace to hold all the code for timezone detection.
 */
var jzTimezoneDetector = new Object();

jzTimezoneDetector.HEMISPHERE_SOUTH = 'SOUTH';
jzTimezoneDetector.HEMISPHERE_NORTH = 'NORTH';
jzTimezoneDetector.HEMISPHERE_UNKNOWN = 'N/A';
jzTimezoneDetector.olson = {};

/**
 * A simple object containing information of utc_offset, which olson timezone key to use, 
 * and if the timezone cares about daylight savings or not.
 * 
 * @constructor
 * @param {string} offset - for example '-11:00'
 * @param {string} olson_tz - the olson Identifier, such as "US/Mountain"
 * @param {boolean} uses_dst - flag for whether the time zone somehow cares about daylight savings.
 */
jzTimezoneDetector.TimeZone = function (offset, olson_tz, uses_dst) {
	this.utc_offset = offset;
	this.olson_tz = olson_tz;
	this.uses_dst = uses_dst;
}

/**
 * Prints out the result.
 * But before it does that, it calls this.ambiguity_check.
 */
jzTimezoneDetector.TimeZone.prototype.display = function() {
	this.ambiguity_check();
	var response_text = '<b>UTC-offset</b>: ' + this.utc_offset + '<br/>';
	response_text += '<b>Zoneinfo key</b>: ' + this.olson_tz + '<br/>';
	response_text += '<b>Zone uses DST</b>: ' + (this.uses_dst ? 'yes' : 'no') + '<br/>';
	
	return response_text;
}

/**
 * Checks if a timezone has possible ambiguities. I.e timezones that are similar.
 * 
 * If the preliminary scan determines that we're in US/Mountain. We double check
 * here that we're really there and not in America/Mazatlan.
 * 
 * This is done by checking known dates for when daylight savings start for different
 * timezones.
 */
jzTimezoneDetector.TimeZone.prototype.ambiguity_check = function() {
	var local_ambiguity_list = jzTimezoneDetector.olson.ambiguity_list[this.olson_tz];
	
	if (typeof(local_ambiguity_list) == 'undefined') {
		return;
	}
	
	var length = local_ambiguity_list.length;
	
	for (var i = 0; i < length; i++) {
		var tz = local_ambiguity_list[i]

		if (jzTimezoneDetector.date_is_dst(jzTimezoneDetector.olson.dst_start_dates[tz])) {
			this.olson_tz = tz;
			return;
		}	
	}
}

/**
 * Checks whether a given date is in daylight savings time.
 * 
 * If the date supplied is after june, we assume that we're checking
 * for southern hemisphere DST.
 * 
 * @param {Date} date
 * @returns {boolean}
 */
jzTimezoneDetector.date_is_dst = function (date) {
	var base_offset = ( (date.getMonth() > 5 ? jzTimezoneDetector.get_june_offset() : jzTimezoneDetector.get_january_offset()) )
	
	var date_offset = jzTimezoneDetector.get_date_offset(date);
	
	return (base_offset - date_offset) != 0;
}

/** 
 * Gets the offset in minutes from UTC for a certain date.
 * 
 * @param date
 * @returns {number}
 */
jzTimezoneDetector.get_date_offset = function (date) {
	return -date.getTimezoneOffset();
}

/**
 * This function does some basic calculations to create information about 
 * the user's timezone.
 * 
 * Returns a primitive object on the format
 * {'utc_offset' : -9, 'dst': 1, hemisphere' : 'north'}
 * where dst is 1 if the region uses daylight savings.
 * 
 * @returns {Object}  
 */
jzTimezoneDetector.get_timezone_info = function () {
	var january_offset = jzTimezoneDetector.get_january_offset();
	
	var june_offset = jzTimezoneDetector.get_june_offset();
	
	var diff = january_offset - june_offset;

	if (diff < 0) {
	    return {'utc_offset' : january_offset,
	    		'dst':	1,
	    		'hemisphere' : jzTimezoneDetector.HEMISPHERE_NORTH}
	}
	else if (diff > 0) {
        return {'utc_offset' : june_offset,
        		'dst' : 1,
        		'hemisphere' : jzTimezoneDetector.HEMISPHERE_SOUTH}
	}

    return {'utc_offset' : january_offset, 
    		'dst': 0, 
    		'hemisphere' : jzTimezoneDetector.HEMISPHERE_UNKNOWN}
}

jzTimezoneDetector.get_january_offset = function () {
	return jzTimezoneDetector.get_date_offset(new Date(2011, 0, 1, 0, 0, 0, 0));
}

jzTimezoneDetector.get_june_offset = function () {
	return jzTimezoneDetector.get_date_offset(new Date(2011, 5, 1, 0, 0, 0, 0));
}

/**
 * Uses get_timezone_info() to formulate a key to use in the olson.timezones dictionary.
 * 
 * Returns a primitive object on the format:
 * {'timezone': TimeZone, 'key' : 'the key used to find the TimeZone object'}
 * 
 * @returns Object 
 */
jzTimezoneDetector.determine_timezone = function () {
	var timezone_key_info = jzTimezoneDetector.get_timezone_info();
	
	var hemisphere_suffix = ''
		
	if (timezone_key_info.hemisphere == jzTimezoneDetector.HEMISPHERE_SOUTH) {
		hemisphere_suffix = ',s';
	}
	
	var tz_key = timezone_key_info.utc_offset + ',' + timezone_key_info.dst + hemisphere_suffix
	
	return {'timezone' : jzTimezoneDetector.olson.timezones[tz_key], 'key' : tz_key}
}

/**
 * The keys in this dictionary are comma separated as such:
 * 
 * First the offset compared to UTC time in minutes.
 *  
 * Then a flag which is 0 if the timezone does not take daylight savings into account and 1 if it does.
 * 
 * Thirdly an optional 's' signifies that the timezone is in the southern hemisphere, only interesting for timezones with DST.
 * 
 * The values of the dictionary are TimeZone objects.
 */
jzTimezoneDetector.olson.timezones = {
	    '-720,1,s' : new jzTimezoneDetector.TimeZone('-12:00','Etc/GMT+12', true), // unused timezone, defaulted to another timezone
	    '-720,1'   : new jzTimezoneDetector.TimeZone('-13:00','Etc/GMT+12', true), // unused timezone, defaulted to another timezone
	    '-720,0'   : new jzTimezoneDetector.TimeZone('-12:00','Etc/GMT+12', false),
	    '-660,1,s' : new jzTimezoneDetector.TimeZone('-11:00','Pacific/Samoa', true), // unused timezone, defaulted to another timezone
	    '-660,1'   : new jzTimezoneDetector.TimeZone('-12:00','Pacific/Samoa', true), // unused timezone, defaulted to another timezone
	    '-660,0'   : new jzTimezoneDetector.TimeZone('-11:00','Pacific/Samoa', false),
	    '-600,1,s' : new jzTimezoneDetector.TimeZone('-10:00','Pacific/Tahiti', true),
	    '-600,1'   : new jzTimezoneDetector.TimeZone('-11:00','US/Aleutian',true),
	    '-600,0'   : new jzTimezoneDetector.TimeZone('-10:00','Pacific/Honolulu', false),
	    '-570,1,s' : new jzTimezoneDetector.TimeZone('-09:30','Pacific/Marquesas', true), // unused timezone, defaulted to another timezone
	    '-570,1'   : new jzTimezoneDetector.TimeZone('-10:30','Pacific/Marquesas', true), // unused timezone, defaulted to another timezone
	    '-570,0'   : new jzTimezoneDetector.TimeZone('-09:30','Pacific/Marquesas',false),
	    '-540,1,s' : new jzTimezoneDetector.TimeZone('-09:00','US/Alaska', true), // unused timezone, defaulted to another timezone
	    '-540,1'   : new jzTimezoneDetector.TimeZone('-10:00','US/Alaska', true),
	    '-540,0'   : new jzTimezoneDetector.TimeZone('-09:00','SystemV/YST9',false),
	    '-480,1,s' : new jzTimezoneDetector.TimeZone('-08:00','US/Pacific', true), // unused timezone, defaulted to another timezone
	    '-480,1'   : new jzTimezoneDetector.TimeZone('-09:00','US/Pacific', true),
	    '-480,0'   : new jzTimezoneDetector.TimeZone('-08:00','Pacific/Pitcairn',false),
	    '-420,1,s' : new jzTimezoneDetector.TimeZone('-07:00','America/Bahia_Banderas', true),
	    '-420,1'   : new jzTimezoneDetector.TimeZone('-08:00','US/Mountain', true),
	    '-420,0'   : new jzTimezoneDetector.TimeZone('-07:00','US/Arizona', false),
	    '-360,1,s' : new jzTimezoneDetector.TimeZone('-06:00','Pacific/Galapagos', true),
	    '-360,1'   : new jzTimezoneDetector.TimeZone('-07:00','US/Central', true),
	    '-360,0'   : new jzTimezoneDetector.TimeZone('-06:00','Canada/Saskatchewan', false), // between saskatchewan and costa rica
	    '-300,1,s' : new jzTimezoneDetector.TimeZone('-05:00','Pacific/Easter', true),
	    '-300,1'   : new jzTimezoneDetector.TimeZone('-06:00','US/Eastern', true),
	    '-300,0'   : new jzTimezoneDetector.TimeZone('-05:00','America/Port-au-Prince', false), // between cayman and port-au-prince
	    '-270,1,s' : new jzTimezoneDetector.TimeZone('-04:30','America/Caracas', true), // unused timezone, defaulted to another timezone
	    '-270,1'   : new jzTimezoneDetector.TimeZone('-05:30','America/Caracas', true), // unused timezone, defaulted to another timezone
	    '-270,0'   : new jzTimezoneDetector.TimeZone('-04:30','America/Caracas', false), // unused timezone, defaulted to another timezone
	    '-240,1,s' : new jzTimezoneDetector.TimeZone('-04:00','America/Santiago', true),
	    '-240,1'   : new jzTimezoneDetector.TimeZone('-05:00','Atlantic/Bermuda', true),
	    '-240,0'   : new jzTimezoneDetector.TimeZone('-04:00','America/La_Paz', false),
	    '-210,1,s' : new jzTimezoneDetector.TimeZone('-03:30','Canada/Newfoundland', true), // unused timezone, defaulted to another timezone
	    '-210,1'   : new jzTimezoneDetector.TimeZone('-04:30','Canada/Newfoundland', true),
	    '-210,0'   : new jzTimezoneDetector.TimeZone('-03:30','Canada/Newfoundland', false), // unused timezone, defaulted to another timezone
	    '-180,1,s' : new jzTimezoneDetector.TimeZone('-03:00','America/Sao_Paulo', true),
	    '-180,1'   : new jzTimezoneDetector.TimeZone('-04:00','America/Godthab', true),
	    '-180,0'   : new jzTimezoneDetector.TimeZone('-03:00','America/Argentina/Buenos_Aires', false),
	    '-120,1,s' : new jzTimezoneDetector.TimeZone('-02:00','Brazil/DeNoronha', true), // unused timezone, defaulted to another timezone
	    '-120,1'   : new jzTimezoneDetector.TimeZone('-03:00','Brazil/DeNoronha', true), // unused timezone, defaulted to another timezone
	    '-120,0'   : new jzTimezoneDetector.TimeZone('-02:00','Brazil/DeNoronha', false),
	    '-60,0'    : new jzTimezoneDetector.TimeZone('-01:00','Atlantic/Cape_Verde', false),
	    '-60,1'    : new jzTimezoneDetector.TimeZone('-02:00','Atlantic/Azores', true),
	    '-60,1,s'  : new jzTimezoneDetector.TimeZone('-01:00','Atlantic/Azores', true), // unused timezone, defaulted to another timezone
	    '0,0'      : new jzTimezoneDetector.TimeZone('00:00','Greenwich', false),
	    '0,1'      : new jzTimezoneDetector.TimeZone('-01:00','Europe/London', true),
	    '0,1,s'    : new jzTimezoneDetector.TimeZone('00:00','Etc/UTC', true),
	    '60,0'     : new jzTimezoneDetector.TimeZone('+01:00','Africa/Lagos', false),
	    '60,1'     : new jzTimezoneDetector.TimeZone('00:00','Europe/Paris', true),
	    '60,1,s'   : new jzTimezoneDetector.TimeZone('+01:00','Africa/Windhoek', true),
	    '120,0'    : new jzTimezoneDetector.TimeZone('+02:00','Africa/Tripoli', false),
	    '120,1'    : new jzTimezoneDetector.TimeZone('+01:00','Asia/Jerusalem', true),
	    '120,1,s'  : new jzTimezoneDetector.TimeZone('+02:00','Asia/Jerusalem', true), // unused timezone, defaulted to another timezone
	    '180,0'    : new jzTimezoneDetector.TimeZone('+03:00','Asia/Riyadh', false),
	    '180,1'    : new jzTimezoneDetector.TimeZone('+02:00','Europe/Moscow', true),
	    '180,1,s'  : new jzTimezoneDetector.TimeZone('+03:00','Europe/Moscow', true), // unused timezone, defaulted to another timezone
	    '210,0'    : new jzTimezoneDetector.TimeZone('+03:30','Asia/Tehran', false),
	    '210,1'    : new jzTimezoneDetector.TimeZone('+02:30','Asia/Tehran', true), // unused timezone, defaulted to another timezone
	    '210,1,s'  : new jzTimezoneDetector.TimeZone('+03:30','Asia/Tehran', true), // unused timezone, defaulted to another timezone
	    '240,0'    : new jzTimezoneDetector.TimeZone('+04:00','Asia/Dubai', false),
	    '240,1'    : new jzTimezoneDetector.TimeZone('+03:00','Asia/Tbilisi', true),
	    '240,1,s'  : new jzTimezoneDetector.TimeZone('+04:00','Asia/Tbilisi', true), // unused timezone, defaulted to another timezone
	    '270,0'    : new jzTimezoneDetector.TimeZone('+04:30','Asia/Kabul', false),
	    '270,1'    : new jzTimezoneDetector.TimeZone('+03:30','Asia/Kabul', true), // unused timezone, defaulted to another timezone
	    '270,1,s'  : new jzTimezoneDetector.TimeZone('+04:30','Asia/Kabul', true), // unused timezone, defaulted to another timezone
	    '300,0'    : new jzTimezoneDetector.TimeZone('+05:00','Asia/Karachi', false),
	    '300,1'    : new jzTimezoneDetector.TimeZone('+04:00','Asia/Bishkek', true),
	    '300,1,s'  : new jzTimezoneDetector.TimeZone('+05:00','Asia/Bishkek', true), // unused timezone, defaulted to another timezone
	    '330,0'    : new jzTimezoneDetector.TimeZone('+05:30','Asia/Kolkata', false),
	    '330,1'    : new jzTimezoneDetector.TimeZone('+04:30','Asia/Kolkata', true), // unused timezone, defaulted to another timezone
	    '330,1,s'  : new jzTimezoneDetector.TimeZone('+05:30','Asia/Kolkata', true), // unused timezone, defaulted to another timezone
	    '345,0'    : new jzTimezoneDetector.TimeZone('+05:45','Asia/Kathmandu', false),
	    '345,1'    : new jzTimezoneDetector.TimeZone('+04:45','Asia/Kathmandu', true), // unused timezone, defaulted to another timezone
	    '345,1,s'  : new jzTimezoneDetector.TimeZone('+05:45','Asia/Kathmandu', true), // unused timezone, defaulted to another timezone
	    '360,0'    : new jzTimezoneDetector.TimeZone('+06:00','Asia/Colombo', false),
	    '360,1'    : new jzTimezoneDetector.TimeZone('+05:00','Asia/Almaty', true),
	    '360,1,s'  : new jzTimezoneDetector.TimeZone('+06:00','Asia/Almaty', true), // unused timezone, defaulted to another timezone
	    '390,0'    : new jzTimezoneDetector.TimeZone('+06:30','Asia/Rangoon', false),
	    '390,1'    : new jzTimezoneDetector.TimeZone('+05:30','Asia/Rangoon', true), // unused timezone, defaulted to another timezone
	    '390,1,s'  : new jzTimezoneDetector.TimeZone('+06:30','Asia/Rangoon', true), // unused timezone, defaulted to another timezone
	    '420,0'    : new jzTimezoneDetector.TimeZone('+07:00','Asia/Bangkok', false),
	    '420,1'    : new jzTimezoneDetector.TimeZone('+06:00','Asia/Hovd', true),
	    '420,1,s'  : new jzTimezoneDetector.TimeZone('+07:00','Asia/Hovd', true), // unused timezone, defaulted to another timezone
	    '480,0'    : new jzTimezoneDetector.TimeZone('+08:00','Asia/Shanghai', false),
	    '480,1'    : new jzTimezoneDetector.TimeZone('+07:00','Asia/Irkutsk', true),
	    '480,1,s'  : new jzTimezoneDetector.TimeZone('+08:00','Asia/Irkutsk', true), // unused timezone, defaulted to another timezone
	    '525,0'    : new jzTimezoneDetector.TimeZone('+08:45','Australia/Eucla', false),
	    '525,1'    : new jzTimezoneDetector.TimeZone('+07:45','Australia/Eucla', true), // unused timezone, defaulted to another timezone
	    '525,1,s'  : new jzTimezoneDetector.TimeZone('+08:45','Australia/Eucla', true), // unused timezone, defaulted to another timezone
	    '540,0'    : new jzTimezoneDetector.TimeZone('+09:00','Asia/Tokyo', false),
	    '540,1'    : new jzTimezoneDetector.TimeZone('+08:00','Asia/Yakutsk', true),
	    '540,1,s'  : new jzTimezoneDetector.TimeZone('+09:00','Asia/Yakutsk', true), // unused timezone, defaulted to another timezone
	    '570,0'    : new jzTimezoneDetector.TimeZone('+09:30','Australia/Darwin', false),
	    '570,1'    : new jzTimezoneDetector.TimeZone('+08:30','Australia/Adelaide', true), // unused timezone, defaulted to another timezone
	    '570,1,s'  : new jzTimezoneDetector.TimeZone('+09:30','Australia/Adelaide', true),
	    '600,0'    : new jzTimezoneDetector.TimeZone('+10:00','Australia/Brisbane', false),
	    '600,1'    : new jzTimezoneDetector.TimeZone('+09:00','Asia/Vladivostok', true),
	    '600,1,s'  : new jzTimezoneDetector.TimeZone('+10:00','Australia/Sydney', true),
	    '630,0'    : new jzTimezoneDetector.TimeZone('+10:30','Australia/Lord_Howe', true), // unused timezone, defaulted to another timezone
	    '630,1'    : new jzTimezoneDetector.TimeZone('+09:30','Australia/Lord_Howe', true), // unused timezone, defaulted to another timezone
	    '630,1,s'  : new jzTimezoneDetector.TimeZone('+10:30','Australia/Lord_Howe', true),
	    '660,0'    : new jzTimezoneDetector.TimeZone('+11:00','Pacific/Pohnpei', false),
	    '660,1'    : new jzTimezoneDetector.TimeZone('+10:00','Asia/Magadan', true),
	    '660,1,s'  : new jzTimezoneDetector.TimeZone('+11:00','Asia/Magadan', true), // unused timezone, defaulted to another timezone
	    '690,0'    : new jzTimezoneDetector.TimeZone('+11:30','Pacific/Norfolk', false),
	    '690,1'    : new jzTimezoneDetector.TimeZone('+10:30','Pacific/Norfolk', true), // unused timezone, defaulted to another timezone
	    '690,1,s'  : new jzTimezoneDetector.TimeZone('+11:30','Pacific/Norfolk', true), // unused timezone, defaulted to another timezone
	    '720,0'    : new jzTimezoneDetector.TimeZone('+12:00','Pacific/Fiji', false),
	    '720,1'    : new jzTimezoneDetector.TimeZone('+11:00','Asia/Anadyr', true),
	    '720,1,s'  : new jzTimezoneDetector.TimeZone('+12:00','Pacific/Auckland', true),
	    '765,0'    : new jzTimezoneDetector.TimeZone('+12:45','Pacific/Chatham', false), // unused timezone, defaulted to another timezone
	    '765,1'    : new jzTimezoneDetector.TimeZone('+11:45','Pacific/Chatham', true), // unused timezone, defaulted to another timezone
	    '765,1,s'  : new jzTimezoneDetector.TimeZone('+12:45','Pacific/Chatham', true),
	    '780,0'    : new jzTimezoneDetector.TimeZone('+13:00','Pacific/Tongatapu', false),
	    '780,1'    : new jzTimezoneDetector.TimeZone('+12:00','Pacific/Tongatapu', true), // unused timezone, defaulted to another timezone
	    '780,1,s'  : new jzTimezoneDetector.TimeZone('+13:00','Pacific/Tongatapu', true), // unused timezone, defaulted to another timezone
	    '840,0'    : new jzTimezoneDetector.TimeZone('+14:00','Pacific/Kiritimati', false),
		'840,1'    : new jzTimezoneDetector.TimeZone('+13:00','Pacific/Kiritimati', true), // unused timezone, defaulted to another timezone
		'840,1,s'  : new jzTimezoneDetector.TimeZone('+14:00','Pacific/Kiritimati', true) // unused timezone, defaulted to another timezone
}

/**
 * This object contains information on when daylight savings starts for
 * different timezones.
 * 
 * The list is short for a reason. Often we do not have to be very specific
 * to single out the correct timezone. But when we do, this list comes in
 * handy.
 * 
 * Each value is a date denoting when daylight savings starts for that timezone.
 */
jzTimezoneDetector.olson.dst_start_dates = {
    'US/Mountain' : new Date(2011, 2, 13, 3, 0, 0, 0),
    'America/Mazatlan' : new Date(2011, 3, 3, 3, 0, 0, 0),
    'US/Central' : new Date(2011, 2, 13, 3, 0, 0, 0),
    'America/Mexico_City' : new Date(2011, 3, 3, 3, 0, 0, 0),
    'Atlantic/Stanley' : new Date(2011, 8, 4, 7, 0, 0, 0),
    'America/Santiago' : new Date(2011, 9, 2, 3, 0, 0, 0),
    'America/Santiago' : new Date(2011, 9, 9, 3, 0, 0, 0),
    'America/Campo_Grande' : new Date(2011, 9, 16, 5, 0, 0, 0),
    'America/Sao_Paulo' : new Date(2011, 9, 2, 3, 0, 0, 0),
    'America/Sao_Paulo' : new Date(2011, 9, 16, 5, 0, 0, 0),
    'US/Pacific' : new Date(2011, 2, 13, 8, 0, 0, 0),
    'America/Santa_Isabel' : new Date(2011, 3, 5, 8, 0, 0, 0),
    'America/Havana' : new Date(2011, 2, 13, 2, 0, 0, 0),
    'US/Eastern' : new Date(2011, 2, 13, 7, 0, 0, 0),
    'Asia/Gaza' : new Date(2011, 2, 26, 23, 0, 0, 0),
    'Asia/Jerusalem' : new Date(2011, 2, 27, 1, 0, 0, 0),
    'Europe/Minsk' : new Date(2011, 2, 27, 3, 0, 0, 0),
    'Europe/Istanbul' : new Date(2011, 2, 27, 7, 0, 0, 0),
    'Asia/Damascus' : new Date(2011, 3, 1, 2, 0, 0, 0),
    'Asia/Jerusalem' : new Date(2011, 3, 1, 6, 0, 0, 0),
    'Africa/Cairo' : new Date(2011, 3, 29, 4, 0, 0, 0),
    'Asia/Yerevan' : new Date(2011, 2, 27, 4, 0, 0, 0),
    'Asia/Baku'    : new Date(2011, 2, 27, 8, 0, 0, 0),
    'Pacific/Auckland' : new Date(2011, 8, 26, 7, 0, 0, 0),
    'Pacific/Fiji' : new Date(2010, 11, 29, 23, 0, 0, 0),
    'Atlantic/Bermuda' : new Date(2011, 2, 13, 6, 0, 0, 0),
    'America/Goose_Bay' : new Date(2011, 2, 13, 2, 1, 0, 0),
    'America/Miquelon' : new Date(2011, 2, 13, 5, 0, 0, 0),
    'America/Godthab' : new Date(2011, 2, 27, 1, 0, 0, 0)
}

/**
 * The keys in this object are timezones that we know may be ambiguous after
 * a preliminary scan through the olson_tz object.
 * 
 * The array of timezones to compare must be in the order that daylight savings
 * starts for the regions.
 */
jzTimezoneDetector.olson.ambiguity_list = {
    'US/Mountain' : ['US/Mountain','America/Mazatlan'],
    'US/Central' : ['US/Central','America/Mexico_City'],
    'America/Santiago' : ['Atlantic/Stanley', 'America/Santiago', 'America/Santiago','America/Campo_Grande'],
    'America/Sao_Paulo' : ['America/Sao_Paulo', 'America/Sao_Paulo'],
    'Asia/Jerusalem' : ['Asia/Gaza','Asia/Jerusalem', 'Europe/Minsk', 'Europe/Istanbul', 'Asia/Damascus', 'Asia/Jerusalem','Africa/Cairo'],
    'Asia/Yerevan' : ['Asia/Yerevan', 'Asia/Baku'],
    'Pacific/Auckland' : ['Pacific/Auckland', 'Pacific/Fiji'],
    'US/Pacific' : ['US/Pacific', 'America/Santa_Isabel'],
    'US/Eastern' : ['America/Havana','US/Eastern'],
    'Atlantic/Bermuda' : ['America/Goose_Bay','Atlantic/Bermuda'],
    'America/Godthab' : ['America/Miquelon', 'America/Godthab']
}