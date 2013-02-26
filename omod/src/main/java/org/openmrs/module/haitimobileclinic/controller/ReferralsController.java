package org.openmrs.module.haitimobileclinic.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ReferralsController {

	protected final Log log = LogFactory.getLog(getClass());

	@RequestMapping(value = "/module/haitimobileclinic/referralsHiv.form", method = RequestMethod.GET)
	public ModelAndView referralsHiv(@RequestParam(required = false) String country,
			ModelAndView mav) {
		Cohort cohort = Context.getPatientSetService().getAllPatients();
//		Cohort cohort = new Cohort("hivReferrals", "hivReferrals", arg2))
		mav.getModelMap().addAttribute("cohort", cohort);
		mav.getModelMap().addAttribute("memberIds", cohort.getMemberIds());
		return mav;
	}


	@RequestMapping(value = "/module/haitimobileclinic/enrollHiv.form", method = RequestMethod.POST)
	public @ResponseBody String enrollInHiv(@RequestParam String referralEncounterId, @RequestParam String enrollmentDate) {
		try {
			Encounter referralEncounter = Context.getEncounterService().getEncounter(Integer.parseInt(referralEncounterId));
			Encounter enrollmentEncounter = new Encounter();
			enrollmentEncounter.setPatient(referralEncounter.getPatient());
			enrollmentEncounter.setProvider(Context.getProviderService().getProvider(HaitiMobileClinicConstants.UNKNOWN_PROVIDER_ID).getPerson());
			enrollmentEncounter.setEncounterDatetime(new Date()); //TODO somehow format incoming date
			enrollmentEncounter.setLocation(Context.getLocationService().getLocation(Context.getAuthenticatedUser().getUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCATION)));
			enrollmentEncounter.setEncounterType(Context.getEncounterService().getEncounterType(HaitiMobileClinicConstants.ENCOUNTER_TYPE_ID_STATIC_CLINIC_ENROLLMENT));
			enrollmentEncounter.setForm(Context.getFormService().getForm(HaitiMobileClinicConstants.FORM_ID_STATIC_CLINIC_ENROLLMENT));
			
			Obs o = new Obs();
			o.setConcept(Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_REFERRAL_REASON));
			o.setValueCoded(Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_REFERRAL_REASON_HIV));
			o.setEncounter(enrollmentEncounter);
			enrollmentEncounter.addObs(o);
			enrollmentEncounter = Context.getEncounterService().saveEncounter(enrollmentEncounter);
			
			DateFormat df = new SimpleDateFormat(HaitiMobileClinicConstants.DATE_FORMAT_DISPLAY, Context.getLocale());
			return ("<a href='/openmrs/module/htmlformentry/htmlFormEntry.form?encounterId=" + enrollmentEncounter.getEncounterId() + "'>" + df.format(enrollmentEncounter.getEncounterDatetime()) + "</a>");
		} catch (Exception e) {
			log.error(e);
		}
		return "(internal error)";
	}

}
