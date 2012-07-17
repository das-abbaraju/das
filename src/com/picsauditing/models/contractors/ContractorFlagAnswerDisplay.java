package com.picsauditing.models.contractors;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;
import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.jpa.entities.AmBest;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.OshaRateType;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorFlagAnswerDisplay extends PicsActionSupport {

	@Autowired
	protected FlagCriteriaOperatorDAO flagCriteriaOperatorDao;

	@Autowired
	protected NaicsDAO naicsDao;

	protected ContractorAccount contractor;

	protected ContractorOperator contractorOperator;

	public String getContractorAnswer(FlagData f, boolean addLabel) {
		FlagCriteriaContractor fcc = null;
		for (FlagCriteriaContractor contractorCriteria : contractor
				.getFlagCriteria()) {
			if (contractorCriteria.getCriteria().getId() == f.getCriteria()
					.getId()) {
				fcc = contractorCriteria;
				break;
			}
		}
		if (fcc == null)
			return "";

		return getContractorAnswer(fcc, f, addLabel);
	}

	public String getContractorAnswer(FlagCriteriaContractor fcc, FlagData f,
			boolean addLabel) {
		FlagCriteria fc = f.getCriteria();
		String answer = fcc.getAnswer();

		if (fc.getCategory().equals("Insurance AMB Class"))
			answer = getAmBestClass(answer);
		else if (fc.getCategory().equals("Insurance AMB Rating"))
			answer = getAmBestRating(answer);
		else if (fc.getQuestion() != null
				&& fc.getQuestion().getId() == AuditQuestion.EMR) {
			addLabel = false;
			answer = getTextParameterized("ContractorFlag.EMRAnswer", fcc
					.getAnswer2().split("<br/>")[0],
					format(Float.parseFloat(answer), "#,##0.000"));
		} else if (fc.getOshaRateType() != null) {
			addLabel = false;
			String rate = answer;
			answer = getTextParameterized("ContractorFlag.OshaAnswer", fc
					.getOshaType().name(), getText(fc.getOshaRateType()
					.getDescriptionKey()), fcc.getAnswer2().split("<br/>")[0]);
			if (fc.getOshaRateType().equals(OshaRateType.Fatalities)) {
				Double value = Double.parseDouble(rate);
				answer += value.intValue();
			} else {
				answer += Strings.formatDecimalComma(rate);
			}

			if (fc.getOshaRateType().equals(OshaRateType.LwcrNaics)
					|| fc.getOshaRateType().equals(OshaRateType.TrirNaics)) {
				for (FlagCriteriaOperator fco : contractorOperator
						.getOperatorAccount().getFlagCriteriaInherited()) {
					if (fco.getCriteria().equals(fc)
							&& fco.getCriteria().equals(f.getCriteria())) {
						answer += getText("ContractorFlag.OshaAnswer2");
						if (fc.getOshaRateType().equals(OshaRateType.LwcrNaics))
							answer += (getBroaderNaics(true,
									f.getContractor().getNaics()).getLwcr() * Float
									.parseFloat(fco.criteriaValue())) / 100;
						if (fc.getOshaRateType().equals(OshaRateType.TrirNaics)) {
							answer += (getBroaderNaics(false,
									f.getContractor().getNaics()).getTrir() * Float
									.parseFloat(fco.criteriaValue())) / 100;
						}
					}
				}
				answer += getText("ContractorFlag.OshaAnswer3")
						+ f.getContractor().getNaics().getCode();
			}
		} else if (fc.isInsurance()) {
			int operatorIdOfInheritedFlagCriteria = contractorOperator
					.getOperatorAccount().getInheritFlagCriteria().getId();
			FlagCriteriaOperator fco = flagCriteriaOperatorDao
					.findByOperatorAndCriteriaId(
							operatorIdOfInheritedFlagCriteria, fc.getId());
			answer = getText("Insurance.RequiredLimit")
					+ Strings.formatDecimalComma(fco.getHurdle()) + " "
					+ getText("Insurance.YourLimit")
					+ Strings.formatDecimalComma(answer);
		} else if (fc.getDataType().equals(FlagCriteria.NUMBER))
			answer = Strings.formatDecimalComma(answer);
		else if (fc.getQuestion() != null
				&& fc.getQuestion().getOption() != null)
			answer = getText(fc.getQuestion().getOption().getI18nKey() + "."
					+ answer);

		answer = Utilities.escapeHTML(answer);

		if (addLabel)
			answer = fc.getLabel() + " - " + answer;

		if (!Strings.isEmpty(fcc.getAnswer2())) {
			String[] exploded = fcc.getAnswer2().split("<br/>");
			String year = null;
			String conAnswer = null;
			String verified = null;

			for (String token : exploded) {
				if (token.contains("Year"))
					year = token;
				if (token.contains("Contractor"))
					conAnswer = token;
				if (token.contains("Verified"))
					verified = token;
			}

			if (verified != null)
				answer += "&nbsp;" + verified;
			if (year != null) {
				String front = "<span title=\"" + year;

				if (conAnswer != null)
					answer += " " + conAnswer;

				answer = front + "\">" + answer + "</span>";
			}
		}

		return answer;
	}

	private Naics getBroaderNaics(boolean lwcr, Naics naics) {
		if ((lwcr && naics.getLwcr() > 0) || (!lwcr && naics.getTrir() > 0)
				|| naics == null)
			return naics;
		else {
			Naics naics2 = naicsDao.find(naics.getCode().substring(0,
					naics.getCode().length() - 1));
			return getBroaderNaics(lwcr, naics2);
		}
	}

	private String getAmBestRating(String value) {
		int rating = (int) Float.parseFloat(value);
		return AmBest.ratingMap.get(rating);
	}

	private String getAmBestClass(String value) {
		int classValue = (int) Float.parseFloat(value);
		return AmBest.financialMap.get(classValue);
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public void setContractorOperator(ContractorOperator co) {
		this.contractorOperator = co;
	}

}