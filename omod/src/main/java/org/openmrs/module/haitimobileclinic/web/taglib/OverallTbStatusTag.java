package org.openmrs.module.haitimobileclinic.web.taglib;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.util.LookupHelper;

public class OverallTbStatusTag extends TagSupport {

	private static final long serialVersionUID = 4201389937632339892L;

	protected final Log log = LogFactory.getLog(getClass());

	private String tbSuspectEncounterId = null;

	public String getTbSuspectEncounterId() {
		return tbSuspectEncounterId;
	}

	public void setTbSuspectEncounterId(String encounterId) {
		this.tbSuspectEncounterId = encounterId;
	}

	public int doStartTag() throws JspException {
		JspWriter o = pageContext.getOut();
		try {
			if (!"".equals(getTbSuspectEncounterId())) {
				Integer encounterId = Integer
						.parseInt(getTbSuspectEncounterId());
				Encounter e = Context.getEncounterService().getEncounter(
						encounterId);
				Encounter tbResult = LookupHelper.getMatchingTbResultsEncounter(e, null);

				if (tbResult != null){
					Iterator<Obs> obses = getObsesFromEncounter(tbResult, Arrays.asList(Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_CONFIRMATIVE_TB_STATUS))).iterator();
					if (obses.hasNext()) {
						Obs obs = (Obs) obses.next();
						o.write(displayValue(tbResult, obs));
					} else {
						o.write("updateValue(	");
					}
				} else {
					o.write("(none found)");
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

	private String displayValue(Encounter tbResult, Obs obs) throws IOException {
		DateFormat df = new SimpleDateFormat(HaitiMobileClinicConstants.DATE_FORMAT_DISPLAY, Context.getLocale());
		String d = df.format(getObsesFromEncounter(tbResult, Arrays.asList(Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_CONFIRMATIVE_TB_STATUS_DATE))).get(0).getValueDate());
		return obs.getValueCoded().getName().getName() + " (" + d + ")";
	}

	private List<Obs> getObsesFromEncounter(Encounter e, List<Concept> questions) {
		return Context.getObsService().getObservations(
				Arrays.asList((Person) e.getPatient()),
				Arrays.asList(e), questions, null, null,
				null, null, 1, null, null, null, false);
	}

	public int doEndTag() {
		tbSuspectEncounterId = null;
		return EVAL_PAGE;
	}
}
