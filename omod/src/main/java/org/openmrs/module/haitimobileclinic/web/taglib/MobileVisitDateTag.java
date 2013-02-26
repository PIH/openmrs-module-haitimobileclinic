package org.openmrs.module.haitimobileclinic.web.taglib;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;

public class MobileVisitDateTag extends TagSupport {

	private static final long serialVersionUID = 4201389937632339892L;

	protected final Log log = LogFactory.getLog(getClass());

	private String referralEncounterId = null;

	public String getReferralEncounterId() {
		return referralEncounterId;
	}

	public void setReferralEncounterId(String encounterId) {
		this.referralEncounterId = encounterId;
	}

	public int doStartTag() throws JspException {
		JspWriter o = pageContext.getOut();
		try {
			if (!"".equals(getReferralEncounterId())) {
				Integer encounterId = Integer
						.parseInt(getReferralEncounterId());
				Encounter e = Context.getEncounterService().getEncounter(
						encounterId);
				DateFormat df = new SimpleDateFormat(HaitiMobileClinicConstants.DATE_FORMAT_DISPLAY, Context.getLocale());
				o.write("<a href='/module/htmlformentry/htmlFormEntry.form?encounterId=" + encounterId + ">" + df.format(e.getEncounterDatetime()) + "</a>");
			} else {
				o.write("(none provided)");
			}
		} catch (Exception e) {
			log.error(e);
			try {
				o.write("(error: " + e.getMessage());
			} catch (IOException e1) {
			}
		}

		// reset the objects to null because taglibs are reused
		release();

		return SKIP_BODY;
	}

	public int doEndTag() {
		referralEncounterId = null;
		return EVAL_PAGE;
	}
}
