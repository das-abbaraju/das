package com.picsauditing.oshadisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.PICS.OshaOrganizer;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaRateType;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.Numbers;
import com.picsauditing.util.Strings;
import com.picsauditing.util.YearList;

/**
 * This class is used for displaying safety statistics on the contractor dashboard.
 * 
 * TODO: Turn this class into a bean and make it stateless.
 */
public class OshaDisplay {

	private static final String EMPTY_CELL = ""; 
	
	private OshaOrganizer oshaOrganizer;
	private ContractorAccount contractor;
	private Locale locale;
	private List<ContractorOperator> contractorOperators;
	private I18nCache i18nCache;
	private List<String> columnNames;
	private YearList yearList;
	
	@Autowired
	private NaicsDAO naicsDao;

	private static final MultiYearScope[] YEAR_SCOPES = {
			MultiYearScope.ThreeYearsAgo, MultiYearScope.TwoYearsAgo,
			MultiYearScope.LastYearOnly, MultiYearScope.ThreeYearAverage };

	public OshaDisplay(OshaOrganizer oshaOrganizer, Locale locale,
			List<ContractorOperator> contractorOperators,
			ContractorAccount contractor, NaicsDAO naicsDao) {
		this.oshaOrganizer = oshaOrganizer;
		this.locale = locale;
		this.contractorOperators = contractorOperators;
		this.contractor = contractor;
		this.naicsDao = naicsDao;
		this.i18nCache = I18nCache.getInstance();
	}

	private List<String> getColumnNames(OshaType oshaType) {
		columnNames = new ArrayList<String>();
		yearList = oshaOrganizer.mostRecentThreeYears(oshaType);
		StringBuilder yearsForAverageLabel = new StringBuilder();
		for (MultiYearScope yearScope : YEAR_SCOPES) {
			Integer year = yearList.getYearForScope(yearScope);
			if (year != null) {
				columnNames.add(year.toString());
				yearsForAverageLabel.append(", ");
				yearsForAverageLabel.append(year);
			}
		}
		yearsForAverageLabel.delete(0, 1);
		columnNames.add(i18nCache.getText(
				"ContractorView.ContractorDashboard.AverageLabel", locale,
				yearsForAverageLabel.toString()));
		columnNames.add(i18nCache.getText(
				"ContractorView.ContractorDashboard.Industry", locale));

		return columnNames;
	}

	@SuppressWarnings("unchecked")
	private Map getInfoForParticularOshaType(OshaType oshaType) {
		Map info = new HashMap();
		info.put("columnNames", getColumnNames(oshaType));
		info.put("data", getData(oshaType));

		return info;
	}

	private List<OshaDisplayRow> getData(OshaType oshaType) {
		List<OshaDisplayRow> rows = new ArrayList<OshaDisplayRow>();
		for (OshaRateType rateType : oshaType.rates) {
			if (!isShowRow(rateType))
				continue;
			StatisticsDisplayRow rateRow = new StatisticsDisplayRow();

			rateRow.setOshaRateType(rateType);

			for (MultiYearScope scope : YEAR_SCOPES) {
				Double answer = oshaOrganizer.getRate(oshaType, scope, rateType);
				if (yearList.getYearForScope(scope) != null
					|| scope == MultiYearScope.ThreeYearAverage) {
					if (answer != null && answer >= 0) {
						rateRow.addCell(Numbers.printDouble(answer));
					} else {
						rateRow.addCell(EMPTY_CELL);
					}
				}
			}

			if (rateType.isHasIndustryAverage()) {
				String industryAverage = getIndustryAverage(rateType);
//				String.valueOf(Utilities
//						.getIndustryAverage(
//								rateType == OshaRateType.LwcrAbsolute,
//								contractor.getNaics()));
				rateRow.addCell(industryAverage);
			} else {
				rateRow.addCell(EMPTY_CELL);
			}

			rows.add(rateRow);
			if (rateType != OshaRateType.Fatalities) {
				rows.addAll(generateHurdleRates(rateType, oshaType));
			}
		}

		return rows;
	}

	private boolean isShowRow(OshaRateType rateType) {
		if (!rateType.equals(OshaRateType.SeverityRate))
			return true;
		for (ContractorOperator conOp:contractorOperators) {
			if (conOp.getOperatorAccount().isOrIsDescendantOf(1436)) // Tesoro
				return true;
		}
		
		return false;
	}
	
	private String getIndustryAverage(OshaRateType rateType) {
		if (rateType == OshaRateType.LwcrAbsolute) {
			return String.valueOf(Utilities.getIndustryAverage(true, contractor.getNaics()));
		}
		else if (rateType == OshaRateType.TrirAbsolute) {
			if (contractor.hasWiaCriteria()) {
				return String.format("%.2g%n", contractor.getWeightedIndustryAverage()) + "*";
			}
			else {
				return String.valueOf(Utilities.getIndustryAverage(false, contractor.getNaics()));
			}
		}
		return null;
	}

	private List<OshaDisplayRow> generateHurdleRates(
			OshaRateType oshaRateType, OshaType oshaType) {
		List<OshaDisplayRow> hurdleRateRows = new ArrayList<OshaDisplayRow>();

		Set<OperatorAccount> inheritedOperators = new LinkedHashSet<OperatorAccount>();
		for (ContractorOperator co : contractorOperators) {
			inheritedOperators.add(co.getOperatorAccount()
					.getInheritFlagCriteria());
		}

		for (OperatorAccount o : inheritedOperators) {
			if (oshaType == o.getOshaType()) {
				Map<MultiYearScope, Set<FlagCriteriaOperator>> flagCriteriaForYear = generateOperatorFlagCriteriaMap(
						oshaRateType, oshaType, o);
				HurdleRateDisplayRow hurdleRow = new HurdleRateDisplayRow();
				hurdleRow.setOperator(o);

				boolean hasFlagCriteria = false;

				for (MultiYearScope scope : YEAR_SCOPES) {
					Set<FlagCriteriaOperator> flagCriteriaForThisYear = flagCriteriaForYear
							.get(scope);
					if (yearList.getYearForScope(scope) != null
							|| scope == MultiYearScope.ThreeYearAverage) {
						if (flagCriteriaForThisYear == null
								|| flagCriteriaForThisYear.size() == 0) {
							hurdleRow.addCell(EMPTY_CELL);
						} else if (yearList.getYearForScope(scope) != null
									|| scope == MultiYearScope.ThreeYearAverage) {
							StringBuilder display = new StringBuilder();
							for (FlagCriteriaOperator fco : flagCriteriaForThisYear) {
								display.append(", ");
								display.append(getFlagDescription(fco));
							}
							display.delete(0, 1);
							hurdleRow.addCell(display.toString());
	
							hasFlagCriteria = true;
						}
					}
				}
				if (hasFlagCriteria) {
					hurdleRateRows.add(hurdleRow);
				}
			}
		}

		return hurdleRateRows;
	}

	private Map<MultiYearScope, Set<FlagCriteriaOperator>> generateOperatorFlagCriteriaMap(
			OshaRateType oshaRateType, OshaType oshaType, OperatorAccount o) {
		Map<MultiYearScope, Set<FlagCriteriaOperator>> flagCriteriaForYear = new HashMap<MultiYearScope, Set<FlagCriteriaOperator>>();
		for (FlagCriteriaOperator fco : o.getFlagCriteriaInherited()) {
			if (fco.getCriteria().getOshaType() == oshaType
					&& isEquivalentRateTypes(fco.getCriteria().getOshaRateType(), oshaRateType)) {
				MultiYearScope scope = fco.getCriteria().getMultiYearScope();
				Set<FlagCriteriaOperator> flagCriteria = flagCriteriaForYear
						.get(scope);
				if (flagCriteria == null) {
					flagCriteria = new HashSet<FlagCriteriaOperator>();
					flagCriteriaForYear.put(scope, flagCriteria);
				}
				flagCriteria.add(fco);
			}
		}

		return flagCriteriaForYear;
	}

	private boolean isEquivalentRateTypes(OshaRateType flagCriteriaRateType,
			OshaRateType requestedRateType) {
		if (flagCriteriaRateType == requestedRateType)
			return true;
		
		if (flagCriteriaRateType.isTrir() && requestedRateType.isTrir())
			return true;
		
		return false;
	}

	private String getFlagDescription(FlagCriteriaOperator fco) {
		if (OshaRateType.TrirWIA.equals(fco.getCriteria().getOshaRateType())) {
			float hurdle = (fco.getHurdle() != null) ? Float.valueOf(fco
					.getHurdle()) : 100;
			return "<nobr class=\""
					+ fco.getFlag()
					+ "\">"
					+ fco.getCriteria().getComparison()
					+ " "
					+ Strings.formatDecimalComma(Double.toString(hurdle / 100
							* contractor.getWeightedIndustryAverage()))
					+ "</nobr>";
		} else if (OshaRateType.TrirNaics.equals(fco.getCriteria()
				.getOshaRateType())) {
			float hurdle = (fco.getHurdle() != null) ? Float.valueOf(fco
					.getHurdle()) : 100;
			return "<nobr class=\""
					+ fco.getFlag()
					+ "\">"
					+ fco.getCriteria().getComparison()
					+ " "
					+ Strings.formatDecimalComma(Double.toString(hurdle
							/ 100
							* naicsDao.getIndustryAverage(false, contractor
									.getNaics()))) + "</nobr>";
		} else if (OshaRateType.DartNaics.equals(fco.getCriteria()
				.getOshaRateType())) {
			float hurdle = (fco.getHurdle() != null) ? Float.valueOf(fco
					.getHurdle()) : 100;
			return "<nobr class=\""
					+ fco.getFlag()
					+ "\">"
					+ fco.getCriteria().getComparison()
					+ " "
					+ Strings.formatDecimalComma(Double.toString(hurdle
							/ 100
							* naicsDao.getDartIndustryAverage(contractor
									.getNaics()))) + "</nobr>";
		} else
			return "<nobr class=\"" + fco.getFlag() + "\">"
					+ fco.getShortDescription() + "</nobr>";
	}

	@SuppressWarnings("unchecked")
	public Map getStats() {
		Map stats = new HashMap();
		for (OshaType oshaType : OshaType.values()) {
			try {
				oshaOrganizer.hasOshaType(oshaType);
				if (oshaOrganizer.hasOshaType(oshaType)) {
					stats.put(oshaType, getInfoForParticularOshaType(oshaType));
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return stats;
	}
}
