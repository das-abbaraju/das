package com.picsauditing.util;

/**
 * This class will build all of our dynamic links found in reports
 * @author jshelley
 *
 */
public class LinkBuilder 
{

	/**
	 * Builds a list of "Starts With" links, using the new javascript model of filtering/sorting.
	 * @return Html String containing the links
	 */
	public static String getStartsWithLinks() {
		StringBuffer temp = new StringBuffer("<span class=\"blueMain\">Starts with: ");

		temp.append( "<a href=\"javascript: changeStartsWith('form1', '');\" class=\"blueMain\">All</a> " );
		for (char c = 'A';c<='Z';c++)
			temp.append( "<a href=\"javascript: changeStartsWith('form1', '" + c + "');\" class=\"blueMain\">"+c+"</a> " );
		temp.append( "</span>" );
		return temp.toString();
	}
	

	/**
	 * Builds a list of links, used for paging search results, using the new javascript model of filtering/sorting.
	 * @return Html String containing the links
	 */
	public static String getPageNOfXLinks(int numResults, int showNum, int beginResults, int endResults, int showPage ){
		int SHOW_PAGES = 4;
		int lastPage = (numResults-1)/showNum+1;
		String orderByQuery = "";
		
		
		StringBuffer sb = new StringBuffer("<span class=\"redMain\">");
		sb.append("Showing "+beginResults+"-"+endResults+" of <b>"+numResults+"</b> results | ");
		
		int startIndex = 1;
		if (showPage-1 > SHOW_PAGES){
			startIndex = showPage-SHOW_PAGES;
			sb.append("<a href=\"javascript: changePage('form1','1');\">1</A> << ");
		}
		
		int endIndex = lastPage;
		if (lastPage-showPage > SHOW_PAGES)
			endIndex = showPage+SHOW_PAGES;
		for (int i=startIndex;i<=endIndex;i++){
			if (i==showPage)
				sb.append(" <strong>"+i+"</strong> ");
			else{
				sb.append("<a href=\"javascript: changePage('form1','" + i + "');\">" + i + "</A> ");
			}
		}
		
		if (lastPage-showPage > SHOW_PAGES)
			sb.append(">> <a href=\"javascript: changePage('form1','" + lastPage + "');\">" + lastPage + "</A>");
			sb.append("</span>");
		return sb.toString();
	}

	
}
