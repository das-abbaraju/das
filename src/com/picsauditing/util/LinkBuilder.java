package com.picsauditing.util;

import java.util.Locale;

import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;

/**
 * This class will build all of our dynamic links found in reports
 * 
 * @author jshelley
 * 
 */
public class LinkBuilder {
	/**
	 * Builds a list of "Starts With" links, using the new javascript model of
	 * filtering/sorting.
	 * 
	 * @return Html String containing the links
	 */
	public static String getStartsWithLinks() {
		StringBuffer temp = new StringBuffer("");
		TranslationService translationService = TranslationServiceFactory.getTranslationService();
		Locale locale = TranslationActionSupport.getLocaleStatic();

		temp.append(String
				.format("<ul class=\"alpha\"><li>%s:</li><li><a href=\"javascript: changeStartsWith('form1', '');\" class=\"blueMain\">%s</a></li>",
						translationService.getText("Filters.paging.StartsWith", locale),
						translationService.getText("JS.Filters.status.All", locale)));
		for (char c = 'A'; c <= 'Z'; c++) {
			temp.append("<li><a href=\"javascript: changeStartsWith('form1', '" + c + "');\" class=\"blueMain\">" + c
					+ "</a></li>");
		}
		temp.append("</ul>");
		return temp.toString();
	}

	/**
	 * Builds a list of links, used for paging search results, using the new
	 * javascript model of filtering/sorting.
	 * 
	 * @return Html String containing the links
	 */
	public static String getPageNOfXLinks(int numResults, int showNum, int beginResults, int endResults, int showPage) {
		TranslationService translationService = TranslationServiceFactory.getTranslationService();
		Locale locale = TranslationActionSupport.getLocaleStatic();

		if (numResults == 0 && showPage == 1) {
			return translationService.getText("Filters.paging.NoResultsFound", locale);
		}

		int SHOW_PAGES = 4;
		int lastPage = (numResults - 1) / showNum + 1;

		StringBuffer sb = new StringBuffer("<ul class=\"paging\">");
		sb.append(translationService.getText("Filters.paging.ShowingResults", locale, new Object[] { beginResults,
				endResults, numResults }));

		int startIndex = 1;
		if (showPage - 1 > SHOW_PAGES) {
			startIndex = showPage - SHOW_PAGES;
			sb.append("<li><a href=\"javascript: changePage('form1','1');\">1</a></li>");
			sb.append("<li>...</li>");
		}

		int endIndex = lastPage;
		if (lastPage - showPage > SHOW_PAGES) {
			endIndex = showPage + SHOW_PAGES;
		}
		for (int i = startIndex; i <= endIndex; i++) {
			sb.append("<li><a " + ((i == showPage) ? "class=\"current\"" : "")
					+ "href=\"javascript: changePage('form1','" + i + "');\">" + i + "</a></li>");
		}

		if (lastPage - showPage > SHOW_PAGES) {
			sb.append("<li>...</li>");
			sb.append("<li><a href=\"javascript: changePage('form1','" + lastPage + "');\">" + lastPage + "</a></li>");
		}
		sb.append("</ul>");
		return sb.toString();
	}

}
