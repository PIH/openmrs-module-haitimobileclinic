package org.openmrs.module.haitimobileclinic.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.util.HaitiMobileClinicWebUtil;
import org.openmrs.module.haitimobileclinic.web.taglib.DateWidgetWrapper;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TbResultsController {

	protected final Log log = LogFactory.getLog(getClass());

	@RequestMapping(value = "/module/haitimobileclinic/tbResults.form", method = RequestMethod.GET)
	public ModelAndView referrals(@RequestParam(required = false) String locationId, @RequestParam(required = false) Date fromDate, @RequestParam(required = false) Date toDate,
			ModelAndView mav) {
		Location loc = null;
		if (locationId == null || "".equals(locationId)) {
			loc = Context.getLocationService().getLocation(Integer.parseInt(Context.getAuthenticatedUser().getUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCATION)));
		} else {
			loc = Context.getLocationService().getLocation(locationId);
		}

		Set<Integer> patientIds = HaitiMobileClinicWebUtil.patientIdsWithPendingReferrals("tb", toDate);
		
//		Cohort cohort = Context.getPatientSetService().getAllPatients();
		Cohort cohort = new Cohort("referrals", "referrals", patientIds);
		mav.getModelMap().addAttribute("cohort", cohort);
		mav.getModelMap().addAttribute("memberIds", cohort.getMemberIds());
		return mav;
	}

	@RequestMapping(value = "/module/haitimobileclinic/saveTbResult.form", method = RequestMethod.POST)
	public @ResponseBody String save(@RequestParam String tbSuspectEncounterId, @RequestParam String existingResultEncounterId,
			@RequestParam String sputumResult1, @RequestParam String sputumResult1Date,
			@RequestParam String sputumResult2, @RequestParam String sputumResult2Date,
			@RequestParam String sputumResult3, @RequestParam String sputumResult3Date,
			@RequestParam String status, @RequestParam String statusDate) {
		try {
			Encounter referralEncounter = Context.getEncounterService().getEncounter(Integer.parseInt(tbSuspectEncounterId));
			Encounter existingResultEncounter = null;
			if (existingResultEncounterId != null && !"".equals(existingResultEncounterId)) {
				existingResultEncounter = Context.getEncounterService().getEncounter(Integer.parseInt(existingResultEncounterId));
			} else {
				existingResultEncounter = new Encounter();
			}
			existingResultEncounter.setPatient(referralEncounter.getPatient());
			existingResultEncounter.setProvider(Context.getProviderService().getProvider(HaitiMobileClinicConstants.UNKNOWN_PROVIDER_ID).getPerson());
			existingResultEncounter.setEncounterDatetime(new Date()); 
			existingResultEncounter.setLocation(Context.getLocationService().getLocation(Integer.parseInt(Context.getAuthenticatedUser().getUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCATION))));
			existingResultEncounter.setEncounterType(Context.getEncounterService().getEncounterType(HaitiMobileClinicConstants.ENCOUNTER_TYPE_ID_TB_RESULTS));
			existingResultEncounter.setForm(Context.getFormService().getForm(HaitiMobileClinicConstants.FORM_ID_TB_RESULTS));
			
			// delete all obs
			for (Obs o : existingResultEncounter.getAllObs()) {
				o.setVoided(true);
				existingResultEncounter.removeObs(o);
			}
			
			// (re-) create all obs from input
			Obs o = null;
			if (!isEmpty(sputumResult1)) {
				o = new Obs();
				o.setConcept(Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_1));
				o.setValueCoded(Context.getConceptService().getConcept(Integer.parseInt(sputumResult1)));
				o.setEncounter(existingResultEncounter);
				existingResultEncounter.addObs(o);
			}
			if (!isEmpty(sputumResult1Date)) {
				o = new Obs();
				o.setConcept(Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_DATE_1));
				o.setValueDate(DateWidgetWrapper.parseDate(sputumResult1Date));
				o.setEncounter(existingResultEncounter);
				existingResultEncounter.addObs(o);
			}
			if (!isEmpty(sputumResult2)) {
				o = new Obs();
				o.setConcept(Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_2));
				o.setValueCoded(Context.getConceptService().getConcept(Integer.parseInt(sputumResult2)));
				o.setEncounter(existingResultEncounter);
				existingResultEncounter.addObs(o);
			}
			if (!isEmpty(sputumResult2Date)) {
				o = new Obs();
				o.setConcept(Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_DATE_2));
				o.setValueDate(DateWidgetWrapper.parseDate(sputumResult2Date));
				o.setEncounter(existingResultEncounter);
				existingResultEncounter.addObs(o);
			}
			if (!isEmpty(sputumResult3)) {
				o = new Obs();
				o.setConcept(Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_3));
				o.setValueCoded(Context.getConceptService().getConcept(Integer.parseInt(sputumResult3)));
				o.setEncounter(existingResultEncounter);
				existingResultEncounter.addObs(o);
			}
			if (!isEmpty(sputumResult3Date)) {
				o = new Obs();
				o.setConcept(Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_SPUTUM_RESULT_DATE_3));
				o.setValueDate(DateWidgetWrapper.parseDate(sputumResult3Date));
				o.setEncounter(existingResultEncounter);
				existingResultEncounter.addObs(o);
			}
			if (!isEmpty(status)) {
				o = new Obs();
				o.setConcept(Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_OVERALL_TB_STATUS));
				o.setValueCoded(Context.getConceptService().getConcept(Integer.parseInt(status)));
				o.setEncounter(existingResultEncounter);
				existingResultEncounter.addObs(o);
			}
			if (!isEmpty(statusDate)) {
				o = new Obs();
				o.setConcept(Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_OVERALL_TB_STATUS_DATE));
				o.setValueDate(DateWidgetWrapper.parseDate(statusDate));
				o.setEncounter(existingResultEncounter);
				existingResultEncounter.addObs(o);
			}		
			existingResultEncounter = Context.getEncounterService().saveEncounter(existingResultEncounter);
			
			DateFormat df = new SimpleDateFormat(HaitiMobileClinicConstants.DATE_FORMAT_DISPLAY, Context.getLocale());
			return ("<td colspan='2'><a href='/openmrs/module/htmlformentry/htmlFormEntry.form?encounterId=" + existingResultEncounter.getEncounterId() + "'>" + df.format(existingResultEncounter.getEncounterDatetime()) + "</a></td>");
		} catch (Exception e) {
			log.error(e);
		}
		return "(internal error)";
	}

	private boolean isEmpty(String s) {
		if (s != null && !"".equals(s)) {
			return false;
		}
		return true;
	}
}
