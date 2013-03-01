package org.openmrs.module.haitimobileclinic.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntryContext.Mode;
import org.openmrs.module.htmlformentry.widget.Widget;


public class DatePickerTag extends TagSupport {

	private static final long serialVersionUID = 4201389937632339892L;

	protected final Log log = LogFactory.getLog(getClass());

	private String id = null;
	
	private String initialValue = null;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter o = pageContext.getOut();
			
			// re-use HFE datewidget
			DateWidgetWrapper dw  = new DateWidgetWrapper();
			FormEntryContext fec = new FormEntryContext(Mode.EDIT){
			    public String getFieldName(Widget widget) {
			    	return id;
			    }
			};
			if (getInitialValue() != null && !"".equals(getInitialValue())) {
				dw.setInitialValue(dw.parseDate(getInitialValue()));
			}
			o.write(dw.generateHtml(fec));
			
		} catch (Exception e) {
			log.error(e);
			
		}
		release();
		
		return SKIP_BODY;
	}

	public int doEndTag() {
		id = null;
		initialValue = null;
		return EVAL_PAGE;
	}
  
}
