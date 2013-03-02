package org.openmrs.module.haitimobileclinic.web.taglib;

import java.util.Date;

import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.htmlformentry.widget.DateWidget;

public class DateWidgetWrapper extends DateWidget {

	public DateWidgetWrapper() {
	}

    public static Date parseDate(String value) {
    	return (Date) HtmlFormEntryUtil.convertToType(value, Date.class);
    }
}
