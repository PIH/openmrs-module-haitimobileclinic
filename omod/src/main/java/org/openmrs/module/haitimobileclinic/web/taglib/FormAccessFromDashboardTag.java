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

public class FormAccessFromDashboardTag extends TagSupport {

	private static final long serialVersionUID = 4201389937632339892L;

	protected final Log log = LogFactory.getLog(getClass());

	private String patientId = null;

	private String formId = null;
	
	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public int doStartTag() throws JspException {
		JspWriter o = pageContext.getOut();
		try {
			o.write("<a href='/openmrs/module/htmlformentry/htmlFormEntry.form?personId=" + getPatientId() + "&patientId=" + getPatientId() +"&returnUrl=&formId=" + getFormId() + "'>Enter new</a><br/>");
			o.write("Previous: ");
			DateFormat df = new SimpleDateFormat(HaitiMobileClinicConstants.DATE_FORMAT_DISPLAY, Context.getLocale());
			List<Encounter> encounters = Context.getEncounterService().getEncounters(Context.getPatientService().getPatient(Integer.parseInt(getPatientId())), null, null, null, Arrays.asList(Context.getFormService().getForm(Integer.parseInt(getFormId()))), null, false);
			// get the last 3 encounters
			int startIndex = 0;
			if (encounters.size() > 3) {
				startIndex = encounters.size() - 3;
			}
			for (int i = startIndex; i < encounters.size(); i++){
				Encounter e = encounters.get(i);
				o.write("<a href='/openmrs/module/htmlformentry/htmlFormEntry.form?encounterId=" + e.getId() + "'>" + df.format(e.getEncounterDatetime()) + "</a> ");
			}
			o.write(" (see Encounters tab to show all)");
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
		patientId = null;
		formId = null;
		return EVAL_PAGE;
	}
}
