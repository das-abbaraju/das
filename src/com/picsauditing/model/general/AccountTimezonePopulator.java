package com.picsauditing.model.general;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.util.Strings;
import com.picsauditing.util.TimeZoneUtil;

public class AccountTimezonePopulator implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(AccountTimezonePopulator.class);
	public static final String limitPropertyName = "GoogleMapApiDailyCount";
	public static final int lookupLimit = 2400;
	public static final long millsIn24Hours = 1000 * 60 * 60 * 24;

	private volatile boolean isPopulatorRunning;
	private volatile int totalAccounts;
	private volatile int accountsConverted;
	private volatile int totalAccountsWillRun = 10;
	private volatile String infoMessage = "Nothing to report";
	
	@Autowired
	private ContractorAccountDAO contractorAccountDAO;
	@Autowired
	private AppPropertyDAO appPropertyDAO;
	@Autowired
	private TimezoneFinder timezoneFinder;
	
	@Override
	public void run() {
		if (exceededOurDailyMapApiCount()) {
			infoMessage = "Exceeded the 24 hour period limit of Google Maps API. We can't run.";
			isPopulatorRunning = false;
		} else {
			totalAccounts = 0;
			infoMessage = "Nothing to report";
			doRun();
		}
	}

	private void doRun() {
		isPopulatorRunning = true;
		accountsConverted = 0;
		totalAccounts = contractorAccountDAO.findCountWhere("c.status = 'Active' and c.timezone is null");
		List<ContractorAccount> contractors = contractorsWithNullTimezones();
		populateContractorTimezones(contractors);
		isPopulatorRunning = false;
		infoMessage = "Done.";
		if (exceededOurDailyMapApiCount()) {
			infoMessage += " We exceeded the google daily allotment, so we only completed those that did not need lookup.";
		}
	}

	private boolean exceededOurDailyMapApiCount() {
		JSONObject json = limitJson();
		if (json == null) {
			return false;
		} else {
			try {
				String dateString = json.getString("date");
				if (Strings.isNotEmpty(dateString)) {
					Date date = new SimpleDateFormat().parse(dateString);
					long now = new Date().getTime();
					if (now - date.getTime() < millsIn24Hours) {
						return json.getInt("count") > lookupLimit;
					}
				}
			} catch (ParseException e) {
				logger.error("Bad date format in google maps limit property: {}", e.getMessage());
			}
			return false;
		}
	}

	private JSONObject limitJson() {
		String googleMapApiCount = appPropertyDAO.getProperty(limitPropertyName);
		if (Strings.isNotEmpty(googleMapApiCount)) {
			return JSONObject.fromObject(googleMapApiCount);
		} else {
			return null;
		}
	}

	private void populateContractorTimezones(List<ContractorAccount> contractors) {
		for (ContractorAccount contractor : contractors) {
			Country country = contractor.getCountry();
			CountrySubdivision countrySub = contractor.getCountrySubdivision();
			if (country != null && TimeZoneUtil.COUNTRY_TO_TIME_ZONE.containsKey(country.getIsoCode())) {
				setTimezoneFromCountry(contractor, country);
			} else if (countrySub != null && TimeZoneUtil.COUNTRY_SUB_TO_TIME_ZONE.containsKey(countrySub.getIsoCode())) {
				setTimezoneFromCountrySubdivision(contractor, countrySub);
			} else {
				if (!exceededOurDailyMapApiCount()) {
					setTimezoneFromLookup(contractor);
					incrementLookupCount();
				} else {
					logger.error("Google timezone lookup has exceeded the daily lookup count. We'll keep running, but only for ones we don't need to lookup via google");
				}
			}
		}
	}

	private void setTimezoneFromLookup(ContractorAccount contractor) {
		TimeZone tz = timezoneFinder.timezoneFromAddress(contractor.getFullAddress());
		if (tz != null) {
			manageTimezone(contractor, tz);
		}
	}

	private void incrementLookupCount() {
		JSONObject json = limitJson();
		if (json == null || dateIsGreaterThan24HoursAgo(json)) {
			json = new JSONObject();
			json.element("count", 1);
			json.element("date", new SimpleDateFormat().format(new Date()));
		} else {
			json.element("count", json.getInt("count") + 1);
		}
		appPropertyDAO.setProperty(limitPropertyName, json.toString());
	}

	private boolean dateIsGreaterThan24HoursAgo(JSONObject json) {
		if (json == null || Strings.isEmpty(json.getString("date"))) {
			return false;
		} else {
			String dateString = json.getString("date");
			try {
				Date date = new SimpleDateFormat().parse(dateString);
				long now = new Date().getTime();
				return (now - date.getTime() > millsIn24Hours);
			} catch (ParseException e) {
				logger.error("Bad date format in google maps limit property: {}", e.getMessage());
			}
			return false;
		}
	}

	private void setTimezoneFromCountrySubdivision(ContractorAccount contractor, CountrySubdivision countrySub) {
		manageTimezone(contractor,
				TimeZone.getTimeZone(
						TimeZoneUtil.COUNTRY_SUB_TO_TIME_ZONE.get(countrySub.getIsoCode())
						)
				);
	}

	private void setTimezoneFromCountry(ContractorAccount contractor, Country country) {
		manageTimezone(contractor,
				TimeZone.getTimeZone(
						TimeZoneUtil.COUNTRY_TO_TIME_ZONE.get(country.getIsoCode())
						)
				);
	}

	private void manageTimezone(ContractorAccount contractor, TimeZone tz) {
		contractor.setTimezone(tz);
		saveAndIncrementConvertedCount(contractor);
	}

	private void saveAndIncrementConvertedCount(ContractorAccount contractor) {
		logger.debug("Saving timezone {} to contractor {}", contractor.getTimezone(), contractor.getId());
		contractorAccountDAO.save(contractor);
		accountsConverted++;
	}

	private List<ContractorAccount> contractorsWithNullTimezones() {
		List<ContractorAccount> contractors = contractorAccountDAO
				.findWhere("a.status = 'Active' and a.timezone is null", totalAccountsWillRun);
		return contractors;
	}

	public int getTotalAccounts() {
		return totalAccounts;
	}

	public void setTotalAccounts(int totalAccounts) {
		this.totalAccounts = totalAccounts;
	}

	public int getAccountsConverted() {
		return accountsConverted;
	}

	public void setAccountsConverted(int accountsConverted) {
		this.accountsConverted = accountsConverted;
	}

	public int getTotalAccountsWillRun() {
		return totalAccountsWillRun;
	}

	public void setTotalAccountsWillRun(int totalAccountsWillRun) {
		this.totalAccountsWillRun = totalAccountsWillRun;
	}

	public boolean isPopulatorRunning() {
		return isPopulatorRunning;
	}

	public String getInfoMessage() {
		return infoMessage;
	}

	public void setInfoMessage(String infoMessage) {
		this.infoMessage = infoMessage;
	}
}
