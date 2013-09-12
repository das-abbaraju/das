package com.picsauditing.tags;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import java.io.IOException;
import java.io.Writer;

import org.apache.struts2.components.Component;
import org.apache.velocity.app.event.implement.EscapeHtmlReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.jpa.entities.Autocompleteable;
import com.picsauditing.util.Strings;

public class Autocomplete extends Component {

	private String action;
	private String name;
	private String htmlId;
	private String htmlName;
	private String value;
	private String hiddenValue;
	private String textValue;
	private String extraParams;
	private int minChars = 1;
	private int cacheLength = 10;
    
	public Autocomplete(ValueStack stack) {
		super(stack);
	}

	public boolean start(Writer writer) {
		try {
			// Please don't format this section
			String result = "<div>"
					+ "<input type=\"hidden\" name=\"%1$s\" value=\"%3$s\" id=\"%2$s_hidden\" class=\"ac_hidden\"/>"
					+ "<input type=\"text\" value=\"%4$s\" id=\"%2$s_text\" class=\"ac_text\"/>"
					+ "<script>"
					+ 	"$(function() {"
					+ 		"$('#%2$s_text').change(function() {"
					+			"var me = $(this);"
					+ 			"if (me.blank())"
					+ 				"$('#%2$s_hidden').val('');"
					+ 		"}).autocomplete('%5$s.action', {"
					+ 			"formatItem  : function(data,i,count) {"
					+ 				"return data[1];"
					+ 			"},"
					+ 			"formatResult: function(data,i,count) {"
					+ 				"return data[2];"
					+ 			"}," 
					+			"extraParams: %6$s,"
					+			"minChars: %7$d,"
					+			"cacheLength: %8$d"
					+ 		"}).result(function(event, data) {"
					+ 			"$('#%2$s_hidden').val(data[0]);"
					+ 		"});"
					+	"});"
					+ "</script>"
					+ "</div>";

			String hiddenValue = "";
			String textValue = "";

			if (!Strings.isEmpty(name) || !Strings.isEmpty(value)) {
				String searchWith = "";
				if (!Strings.isEmpty(name))
					searchWith = name;
				else
					searchWith = value;
				try {
					Autocompleteable b = (Autocompleteable) stack.findValue(searchWith);
					if (b != null) {

						hiddenValue = escapeHtml4(b.getAutocompleteResult());
						textValue = escapeHtml4(b.getAutocompleteValue());
					}
				} catch (ClassCastException e) {
					hiddenValue = textValue = escapeHtml4((String) stack.findValue(searchWith));
				}
			}

			if (!Strings.isEmpty(this.hiddenValue)) {
				hiddenValue = this.hiddenValue;
			}

			if (!Strings.isEmpty(this.textValue)) {
				textValue = this.textValue;
			}

			String htmlName = this.name;
			if (Strings.isEmpty(htmlName)) {
				htmlName = this.value;
			}

			if (!Strings.isEmpty(this.htmlName)) {
				htmlName = this.htmlName;
			}

			String htmlId = this.htmlId;
			if (htmlId == null) {
				htmlId = htmlName.replaceAll("\\.", "") + System.nanoTime();
			}
			
			String extra = "''";
			if (!Strings.isEmpty(extraParams)) {
				extra = extraParams;
			}

			result = String.format(result, htmlName, htmlId, hiddenValue, textValue, action, extra, minChars, cacheLength);

			writer.write(result);
		} catch (IOException e) {
			Logger logger = LoggerFactory.getLogger(Autocomplete.class);
			logger.error(e.getMessage());
		}
		return true;
	};

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHtmlId() {
		return htmlId;
	}

	public void setHtmlId(String htmlId) {
		this.htmlId = htmlId;
	}

	public String getHtmlName() {
		return htmlName;
	}

	public void setHtmlName(String htmlName) {
		this.htmlName = htmlName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getHiddenValue() {
		return hiddenValue;
	}

	public void setHiddenValue(String hiddenValue) {
		this.hiddenValue = hiddenValue;
	}

	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}

	public String getExtraParams() {
		return extraParams;
	}

	public void setExtraParams(String extraParams) {
		this.extraParams = extraParams;
	}

	public int getMinChars() {
		return minChars;
	}

	public void setMinChars(int minChars) {
		this.minChars = minChars;
	}

	public int getCacheLength() {
		return cacheLength;
	}

	public void setCacheLength(int cacheLength) {
		this.cacheLength = cacheLength;
	}

}
