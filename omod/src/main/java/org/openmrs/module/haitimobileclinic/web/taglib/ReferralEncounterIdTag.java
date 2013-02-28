package org.openmrs.module.haitimobileclinic.web.taglib;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.util.HaitiMobileClinicWebUtil;
import org.openmrs.util.OpenmrsConstants;

public class ReferralEncounterIdTag extends TagSupport {

	protected final Log log = LogFactory.getLog(getClass());

	private static final long serialVersionUID = 4201389937632339892L;

	private Integer patientId;

	private String referralType;

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String getReferralType() {
		return referralType;
	}

	public void setReferralType(String referralType) {
		this.referralType = referralType;
	}

	public int doStartTag() throws JspException {
		JspWriter o = pageContext.getOut();
		try {
			Date fromDate = null;
			Date toDate = null;
			Concept answer = HaitiMobileClinicWebUtil.referralReasonAnswer(referralType);
			Encounter e = HaitiMobileClinicWebUtil.mostRecentReferralEncounter(fromDate, toDate, Context.getPatientService().getPatient(getPatientId()), answer);
			if (e != null) {
				o.write("" + e.getEncounterId());
			} else {
				o.write("");
			}
		} catch (Exception e) {
			log.error(e);
			try {
				e.printStackTrace();
				o.write("(error: " + e.getMessage());
			} catch (IOException e1) {
			}
		}

		// reset the objects to null because taglibs are reused
		release();

		return SKIP_BODY;
	}

	public int doEndTag() {
		patientId = null;
		referralType = null;
		return EVAL_PAGE;
	}
}
