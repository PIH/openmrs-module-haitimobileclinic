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
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.util.HaitiMobileClinicWebUtil;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntryContext.Mode;
import org.openmrs.module.htmlformentry.widget.Widget;

public class TbResultAndStatusTag extends TagSupport {

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
				Integer encounterId = Integer.parseInt(getTbSuspectEncounterId());
				Encounter e = Context.getEncounterService().getEncounter(encounterId);
				Encounter tbResult = HaitiMobileClinicWebUtil.getMatchingTbResultsEncounter(e, null);

				// sputum 1
				Obs s1Obs = getObsFromEncounter(tbResult, (Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_1)));
				String s1 = inputResult(s1Obs, e.getPatientId(), "sputum1");
				Obs s1ObsDate = getObsFromEncounter(tbResult, (Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_DATE_1)));
				String s1Date = dateInput(s1ObsDate, e.getPatientId(), "sputumdate1");
				
				// sputum 2
				Obs s2Obs = getObsFromEncounter(tbResult, (Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_2)));
				String s2 = inputResult(s2Obs, e.getPatientId(), "sputum2");
				Obs s2ObsDate = getObsFromEncounter(tbResult, (Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_DATE_2)));
				String s2Date = dateInput(s2ObsDate, e.getPatientId(), "sputumdate2");
				
				// sputum 3
				Obs s3Obs = getObsFromEncounter(tbResult, (Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_3)));
				String s3 = inputResult(s3Obs, e.getPatientId(), "sputum3");
				Obs s3ObsDate = getObsFromEncounter(tbResult, (Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_DATE_3)));
				String s3Date = dateInput(s3ObsDate, e.getPatientId(), "sputumdate3");

				// overall status
				Obs statusObs = getObsFromEncounter(tbResult, (Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_OVERALL_TB_STATUS)));
				String status = inputStatus(statusObs, e.getPatientId(), "status");
				Obs statusObsDate = getObsFromEncounter(tbResult, (Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_OVERALL_TB_STATUS_DATE)));
				String statusDate = dateInput(statusObsDate, e.getPatientId(), "statusdate");

				o.write("<table><tr>");
				o.write("<td>" + s1 + "</td>");
				o.write("<td>" + s2 + "</td>");
				o.write("<td>" + s3 + "</td>");
				o.write("<td>" + status + "</td>");
				if (tbResult != null) {
					DateFormat df = new SimpleDateFormat(HaitiMobileClinicConstants.DATE_FORMAT_DISPLAY, Context.getLocale());
					o.write("<td rowspan='2'><a href='/openmrs/module/htmlformentry/htmlFormEntry.form?encounterId=" + tbResult.getEncounterId() + "'>" + df.format(e.getEncounterDatetime()) + "</a></td>");
				} else {
					o.write("<td rowspan='2'></td>");
				}
				o.write("</tr><tr>");
				o.write("<td>" + s1Date + "</td>");
				o.write("<td>" + s2Date + "</td>");
				o.write("<td>" + s3Date + "</td>");
				o.write("<td>" + statusDate + "<input type='hidden' name='resultEncounterId' id='resultEncounterId-"+ e.getPatientId() + "' value='" + (tbResult != null ? tbResult.getEncounterId() : "") + "'></td>");
				o.write("</tr></table>");
			} else {
				o.write("(no TB suspect)");
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

	private String inputStatus(Obs obs, Integer patientId, String field) {
		String id = field + "-" + patientId;
		String s = "";
		s += "<select id='"+ id + "' name='" + field + "'>";
		s += "<option value='' " + ((obs == null) ? " selected='true'" : "" ) + "></option>";
		s += "<option value='6780' " + ((obs != null && obs.getValueCoded().getConceptId() == 6780) ? " selected='true'" : "" ) + ">Confirmed</option>";
		s += "<option value='6781' " + ((obs != null && obs.getValueCoded().getConceptId() == 6781) ? " selected='true'" : "" ) + ">Not confirmed</option>";
		s += "</select>";
		return s;
	}

	private String inputResult(Obs obs, Integer patientId, String field) {
		String id = field + "-" + patientId;
		String s = "";
		s += "<select id='"+ id + "' name='" + field + "'>";
		s += "<option value='' " + ((obs == null) ? " selected='true'" : "" ) + "></option>";
		s += "<option value='6773' " + ((obs != null && obs.getValueCoded().getConceptId() == 6773) ? " selected='true'" : "" ) + ">0</option>";
		s += "<option value='6774' " + ((obs != null && obs.getValueCoded().getConceptId() == 6774) ? " selected='true'" : "" ) + ">+</option>";
		s += "<option value='6775' " + ((obs != null && obs.getValueCoded().getConceptId() == 6775) ? " selected='true'" : "" ) + ">++</option>";
		s += "<option value='6776' " + ((obs != null && obs.getValueCoded().getConceptId() == 6776) ? " selected='true'" : "" ) + ">+++</option>";
		s += "</select>";
		return s;
	}

	private String dateInput(Obs obsDate, Integer patientId, String field) {
		try {
			final String id = field + "-" + patientId;
			// re-use HFE datewidget
			DateWidgetWrapper dw  = new DateWidgetWrapper();
			FormEntryContext fec = new FormEntryContext(Mode.EDIT){
			    public String getFieldName(Widget widget) {
			    	return id;
			    }
			};
			if (obsDate != null && obsDate.getValueDate() != null) {
				dw.setInitialValue(obsDate.getValueDate());
			}
			String s = dw.generateHtml(fec);
			// brain dead stupid way to get rid of the pattern text just for this Results entering part
			// take both possible locales (of US, UK, HA, FR) into account
			s = s.replace(" (mm/dd/yyyy)", "");
			s = s.replace(" (dd/mm/yyyy)", "");
			return s;
		} catch (Exception e) {
			log.error(e);
		}
		return "(error)";
	}

	private List<Obs> getObsesFromEncounter(Encounter e, List<Concept> questions) {
		if (e == null) {
			return null;
		}
		return Context.getObsService().getObservations(
				Arrays.asList((Person) e.getPatient()),
				Arrays.asList(e), questions, null, null,
				null, null, 1, null, null, null, false);
	}

	private Obs getObsFromEncounter(Encounter e, Concept question) {
		if (e == null) {
			return null;
		}
		List<Obs> os = getObsesFromEncounter(e, Arrays.asList(question));
		if (os.size() == 1) {
			return os.get(0);
		}
		return null;
	}

	public int doEndTag() {
		tbSuspectEncounterId = null;
		return EVAL_PAGE;
	}
}
