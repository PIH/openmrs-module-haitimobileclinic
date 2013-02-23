package org.openmrs.module.haitimobileclinic.web.taglib;

import java.io.IOException;
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

public class ChwNamesTag extends TagSupport {

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
				Iterator<Obs> obses = getObsesFromEncounter(e, Arrays.asList(Context.getConceptService().getConcept(6768), Context.getConceptService().getConcept(6770), Context.getConceptService().getConcept(6771))).iterator();
				while (obses.hasNext()) {
					Obs obs = (Obs) obses.next();
					o.write(obs.getValueText() + (obses.hasNext() ? "; " : ""));
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
		referralEncounterId = null;
		return EVAL_PAGE;
	}
}
