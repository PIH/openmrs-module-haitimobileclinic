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

public class SputumResultTag extends TagSupport {

	private static final long serialVersionUID = 4201389937632339892L;

	protected final Log log = LogFactory.getLog(getClass());

	private String test=null;
	
	private String tbSuspectEncounterId = null;

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

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
				
				if (tbResult != null) {
					Concept result = null;
					Concept date = null;
					if (getTest().equals("1")) {
						result = Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_1);
						date = Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_DATE_1);
					} else if (getTest().equals("2")) {
						result = Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_2);
						date = Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_DATE_2);
					} if (getTest().equals("3")) {
						result = Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_3);
						date = Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_DATE_3);
					}
					
					DateFormat df = new SimpleDateFormat(HaitiMobileClinicConstants.DATE_FORMAT_DISPLAY, Context.getLocale());
					Iterator<Obs> obses = getObsesFromEncounter(tbResult, Arrays.asList(result)).iterator();
					while (obses.hasNext()) {
						Obs obs = (Obs) obses.next();
						String d = df.format(getObsesFromEncounter(tbResult, Arrays.asList(date)).get(0).getValueDate());
						if (getTest().equals("1")) {
							o.write("<a href='/openmrs/module/htmlformentry/htmlFormEntry.form?encounterId=" + tbResult.getEncounterId() + "'>" + obs.getValueCoded().getName().getName() + "<br/>(" + d + ")" + (obses.hasNext() ? "; " : "") + "</a>");
						} else {
							o.write(obs.getValueCoded().getName().getName() + "<br/>(" + d + ")" + (obses.hasNext() ? "; " : ""));
						}
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

	private List<Obs> getObsesFromEncounter(Encounter e, List<Concept> questions) {
		return Context.getObsService().getObservations(
				Arrays.asList((Person) e.getPatient()),
				Arrays.asList(e), questions, null, null,
				null, null, 1, null, null, null, false);
	}

	public int doEndTag() {
		tbSuspectEncounterId = null;
		test = null;
		return EVAL_PAGE;
	}
}
