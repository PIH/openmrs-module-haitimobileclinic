package org.openmrs.module.haitimobileclinic.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.util.HaitiMobileClinicWebUtil;
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

	@RequestMapping(value = "/module/haitimobileclinic/referrals.form", method = RequestMethod.GET)
	public ModelAndView referrals(@RequestParam String enrollmentReason, @RequestParam(required = false) String locationId, @RequestParam(required = false) Date fromDate, @RequestParam(required = false) Date toDate,
			ModelAndView mav) {
		Location loc = null;
		if (locationId == null || "".equals(locationId)) {
			loc = Context.getLocationService().getLocation(Integer.parseInt(Context.getAuthenticatedUser().getUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCATION)));
		} else {
			loc = Context.getLocationService().getLocation(locationId);
		}

		// find obs with matching referral reason
		Concept question = Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_REFERRAL_REASON);
		
		Concept answer = HaitiMobileClinicWebUtil.referralReasonAnswer(enrollmentReason);
		if (answer == null) {
			return null;
		}
		Set<Integer> patientIds = new TreeSet<Integer>(); 
		List<Obs> allReferralObses = Context.getObsService().getObservations(
				null,
				null, Arrays.asList(question),
				Arrays.asList(answer), null, null, null, null, null, null,
				null, false);
		for (Obs o : allReferralObses) {
			// slightly inefficient as this goes through many patients to find out if the most recent consultation encounter
			// contains a relevant referral
			Encounter referral = HaitiMobileClinicWebUtil.mostRecentReferralEncounter(o.getEncounter().getEncounterDatetime(), toDate, o.getEncounter().getPatient(), answer);
			if (referral != null) {
				// patient was referred with a particular encounter
				// now check if there is a later enrollment encounter
				Encounter enrollment = HaitiMobileClinicWebUtil.matchingEnrollmentEncounter(referral, toDate, answer);
				if (enrollment == null) {
					// no later enrollment found, referral for this patient is still pending
					patientIds.add(o.getEncounter().getPatientId());
				}
			}
		}
//		Cohort cohort = Context.getPatientSetService().getAllPatients();
		Cohort cohort = new Cohort("referrals", "referrals", patientIds);
		mav.getModelMap().addAttribute("enrollmentReason", enrollmentReason);
		mav.getModelMap().addAttribute("cohort", cohort);
		mav.getModelMap().addAttribute("memberIds", cohort.getMemberIds());
		return mav;
	}

	private Encounter findMatchingStaticClinicEnrollmentVisit(Encounter encounter, Concept referralReasonAnswer) {
		Concept question = Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_REFERRAL_REASON);

		List<Encounter> encounters = Context.getEncounterService().getEncounters(encounter.getPatient(), encounter.getLocation(), encounter.getEncounterDatetime(), null, null, Arrays.asList(Context.getEncounterService().getEncounterType(HaitiMobileClinicConstants.ENCOUNTER_TYPE_ID_STATIC_CLINIC_ENROLLMENT)), null, false);
		if (encounters.isEmpty()) {
			// no encounter at all found, stop here
			return null;
		}
		List<Obs> obses = Context.getObsService().getObservations(
				null,
				encounters, Arrays.asList(question),
				Arrays.asList(referralReasonAnswer), null, null, null, 1, null, null,
				null, false);
		if (obses != null && obses.size() > 0) {
			return obses.get(0).getEncounter();
		}
		return null;
	}


	@RequestMapping(value = "/module/haitimobileclinic/enroll.form", method = RequestMethod.POST)
	public @ResponseBody String enroll(@RequestParam String referralEncounterId, @RequestParam String enrollmentDate, @RequestParam String enrollmentReason) {
		try {
			Encounter referralEncounter = Context.getEncounterService().getEncounter(Integer.parseInt(referralEncounterId));
			Encounter enrollmentEncounter = new Encounter();
			enrollmentEncounter.setPatient(referralEncounter.getPatient());
			enrollmentEncounter.setProvider(Context.getProviderService().getProvider(HaitiMobileClinicConstants.UNKNOWN_PROVIDER_ID).getPerson());
			enrollmentEncounter.setEncounterDatetime(new Date()); //TODO somehow format incoming date
			enrollmentEncounter.setLocation(Context.getLocationService().getLocation(Integer.parseInt(Context.getAuthenticatedUser().getUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCATION))));
			enrollmentEncounter.setEncounterType(Context.getEncounterService().getEncounterType(HaitiMobileClinicConstants.ENCOUNTER_TYPE_ID_STATIC_CLINIC_ENROLLMENT));
			enrollmentEncounter.setForm(Context.getFormService().getForm(HaitiMobileClinicConstants.FORM_ID_STATIC_CLINIC_ENROLLMENT));
			
			Obs o = new Obs();
			o.setConcept(Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_REFERRAL_REASON));
			o.setValueCoded(HaitiMobileClinicWebUtil.referralReasonAnswer(enrollmentReason));
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
