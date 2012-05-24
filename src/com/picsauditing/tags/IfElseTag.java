package com.picsauditing.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Else;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * The else part of an if tag.
 */
public class IfElseTag extends ComponentTagSupport {
	private static final long serialVersionUID = 8166807953193406785L;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Else(stack);
    }
}
