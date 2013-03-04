package org.openmrs.module.haitimobileclinic.web.taglib;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;

public class ConfirmativeTbDateTag extends TagSupport {

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
				List<Encounter> encounters = Context.getEncounterService().getEncounters(e.getPatient(), e.getLocation(), e.getEncounterDatetime(), null,
				        null, Arrays.asList(Context.getEncounterService().getEncounterType(HaitiMobileClinicConstants.ENCOUNTER_TYPE_ID_TB_RESULTS)), null,
				        null, null, false);
				if (encounters.isEmpty()) {
					o.write("(TB not confirmed)");
				} else {
					Encounter tbResult = encounters.get(encounters.size() - 1);
					DateFormat df = new SimpleDateFormat(HaitiMobileClinicConstants.DATE_FORMAT_DISPLAY, Context.getLocale());
					o.write("<a href='/openmrs/module/htmlformentry/htmlFormEntry.form?encounterId=" + tbResult.getEncounterId() + "'>" + df.format(tbResult.getEncounterDatetime()) + "</a>");
				}
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
