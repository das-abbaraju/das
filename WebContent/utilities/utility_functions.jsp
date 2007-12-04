<%!
// String escapeQuotes(String value)
// String escapeHTML(String value)
// boolean isValidInteger(String number)
// boolean isValidInteger2(String number, int min, int max)
// boolean isValidFloat(String number)
// boolean isValidFloat2(String number, float min, float max)
// boolean isValidEmail(String email)
// String printErrorMessages(Vector errorMessages)
// boolean arrayContains(String[] array, String value)



public static String escapeHTML(String value) { 
    if (value == null) return ""; 
    StringBuffer strval = new StringBuffer(); 
    for (int i = 0; i < value.length(); i++) { 
        char ch = value.charAt(i); 
        switch (ch) { 
            case '"': strval.append("&quot;"); break; 
            case '&': strval.append("&amp;"); break; 
            case '<': strval.append("&lt;"); break; 
            case '>': strval.append("&gt;"); break; 
            default: 
                if (ch > 126) 
                    strval.append("&#" + String.valueOf(ch) + ";"); 
                else 
                    strval.append(ch); 
                break; 
        }//switch
    }//for
    return strval.toString(); 
}//escapeHTML

/** 
 * Replaces single quotes in a string with two single quotes. 
 * This formats it properly for use in a SQL statement. 
 * 
 * @param value the string to format. 
 * @return the formatted string. 
 */ 
public static String escapeQuotes(String value) { 
	if (value == null) return ""; 
	StringBuffer strval = new StringBuffer(); 
	for (int i = 0; i < value.length(); i++) { 
		char ch = value.charAt(i); 
		// Found a single quote; replace it with 2 for the SQL statement 
		if (ch == '\'') 
			strval.append("''"); 
		else    // Just append the char to the strval for the complete string 
			strval.append(ch); 
	}//for
	return strval.toString(); 
}//escapeQuotes

public static boolean isValidInteger(String number) {
	try {
		Integer.parseInt(number);
		return true;
	}//try
	catch (Exception e) {
		return false;
	}//catch
}//isValidInteger

public static boolean isValidInteger2(String number, int min, int max) {
	try {
		int i = Integer.parseInt(number);
		return ((i >= min) && (i <= max));
	}//try
	catch (Exception e) {
		return false;
	}//catch
}//isValidInteger2

public static boolean isValidFloat(String number) {
	try {
		Float.parseFloat(number);
		return true;
	}//try
	catch (Exception e) {
		return false;
	}//catch
}//isValidFloat

public static boolean isValidFloat2(String number, float min, float max) {
	try {
		float f = Float.parseFloat(number);
		return ((f >= min) && (f <= max));		
	}//try
	catch (Exception e) {
		return false;
	}//catch
}//isValidFloat2

public static boolean isValidEmail(String email) {
  boolean result = false;
  int index = email.indexOf("@");
  if (index > 0) {
    int pindex = email.indexOf(".",index);
    if ((pindex > index+1) && (email.length() > pindex+1))
	result = true;
  }//if
  return result;
}//isValidEmail

public static String printErrorMessages(Vector errorMessages) {
	StringBuffer out = new StringBuffer();
	for (int i=0; i < errorMessages.size(); i++) {
		out.append(errorMessages.elementAt(i) + "<br>");
	}//for
	return out.toString();
}//printErrorMessages

public static boolean arrayContains(String[] array, String value) {
        boolean isIncluded = false;

        if (array == null || value == null) {
            return false;
        }
        for (int i = 0; i < array.length; i++) {
            if (value.equals(array[i])) {
                isIncluded = true;
                break;
            }
        }
        return isIncluded;
    }

%>