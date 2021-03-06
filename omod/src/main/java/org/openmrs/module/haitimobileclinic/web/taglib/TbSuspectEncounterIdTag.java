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
import org.openmrs.module.haitimobileclinic.util.LookupHelper;
import org.openmrs.util.OpenmrsConstants;

public class TbSuspectEncounterIdTag extends TagSupport {

	protected final Log log = LogFactory.getLog(getClass());

	private static final long serialVersionUID = 4201389937632339892L;

	private Integer patientId;

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public int doStartTag() throws JspException {
		JspWriter o = pageContext.getOut();
		try {
			Date fromDate = null;
			Date toDate = null;
			EncounterType consultation = Context.getEncounterService()
					.getEncounterType(HaitiMobileClinicConstants.ENCOUNTER_TYPE_ID_MOBILE_CLINIC_CONSULTATION);
			Location location = Context.getLocationService().getLocation(Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCATION_NAME));
			Patient patient = Context.getPatientService().getPatient(
					getPatientId());
			Concept question = Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_REFERRAL_REASON);
			Concept answer = LookupHelper.referralReasonAnswer("tb");
			List<Encounter> encounters = Context.getEncounterService()
					.getEncounters(patient, location, fromDate, toDate, null,
							Arrays.asList(consultation), null, null, null,
							false);
			if (encounters != null && encounters.size() > 0) {
				Encounter e = encounters.get(encounters.size() - 1);
				List<Obs> obses = Context.getObsService().getObservations(
						Arrays.asList((Person) e.getPatient()),
						Arrays.asList(e), Arrays.asList(question),
						Arrays.asList(answer), null, null, null, 1, null, null,
						null, false);
				if (obses == null || obses.size() != 1) {
					o.write("");
				} else {
					o.write("" + obses.get(0).getEncounter().getEncounterId());
				}
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
		return EVAL_PAGE;
	}
}
