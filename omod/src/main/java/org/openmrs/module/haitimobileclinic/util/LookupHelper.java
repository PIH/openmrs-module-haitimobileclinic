package org.openmrs.module.haitimobileclinic.util;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.util.OpenmrsConstants;

public class LookupHelper {

	public static Set<Integer> patientIdsWithConfirmativeTbAndPendingReferrals(Date toDate) {
		Concept question = Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_REFERRAL_REASON);
		Set<Integer> patientIds = new TreeSet<Integer>(); 
		Concept answer = LookupHelper.referralReasonAnswer("tb");
		if (answer == null) {
			return patientIds;
		}
		List<Obs> allReferralObses = Context.getObsService().getObservations(
				null,
				null, Arrays.asList(question),
				Arrays.asList(answer), null, null, null, null, null, null,
				null, false);
		Concept confirmativeTbAnswer = Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_CONFIRMATIVE_TB_STATUS_POSITIVE);
		Concept confirmativeTbQuestion = Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_CONFIRMATIVE_TB_STATUS);
		
		for (Obs o : allReferralObses) {
			// slightly inefficient as this goes through many patients to find out if the most recent consultation encounter
			// contains a relevant referral
			Encounter referral = LookupHelper.mostRecentReferralEncounter(o.getEncounter().getEncounterDatetime(), toDate, o.getEncounter().getPatient(), answer);
			if (referral != null) {
				// patient was referred with a particular encounter
				// now check if there is a later enrollment encounter
				Encounter enrollment = LookupHelper.matchingEnrollmentEncounter(referral, toDate, answer);
				if (enrollment == null) {
					// no later enrollment found, referral for this patient is still pending
					// now check if there is a confirmative TB result
					Encounter tbResult = LookupHelper.getMatchingTbResultsEncounter(o.getEncounter(), null);
					if (tbResult != null) {
						List<Obs> confirmativeTb = Context.getObsService().getObservations(
								Arrays.asList((Person) tbResult.getPatient()),
								Arrays.asList(tbResult), Arrays.asList(confirmativeTbQuestion),
								Arrays.asList(confirmativeTbAnswer), null, null, null, null, null, null,
								null, false);
						if (!confirmativeTb.isEmpty()) {
							patientIds.add(o.getEncounter().getPatientId());
						}
					}
				}
			}
		}
		return patientIds;
	}

	public static Set<Integer> patientIdsWithPendingReferrals(
			String enrollmentReason, Date toDate) {
		Concept question = Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_REFERRAL_REASON);
		Set<Integer> patientIds = new TreeSet<Integer>(); 
		Concept answer = LookupHelper.referralReasonAnswer(enrollmentReason);
		if (answer == null) {
			return patientIds;
		}
		List<Obs> allReferralObses = Context.getObsService().getObservations(
				null,
				null, Arrays.asList(question),
				Arrays.asList(answer), null, null, null, null, null, null,
				null, false);
		for (Obs o : allReferralObses) {
			// slightly inefficient as this goes through many patients to find out if the most recent consultation encounter
			// contains a relevant referral
			Encounter referral = LookupHelper.mostRecentReferralEncounter(o.getEncounter().getEncounterDatetime(), toDate, o.getEncounter().getPatient(), answer);
			if (referral != null) {
				// patient was referred with a particular encounter
				// now check if there is a later enrollment encounter
				Encounter enrollment = LookupHelper.matchingEnrollmentEncounter(referral, toDate, answer);
				if (enrollment == null) {
					// no later enrollment found, referral for this patient is still pending
					patientIds.add(o.getEncounter().getPatientId());
				}
			}
		}
		return patientIds;
	}

	public static Encounter matchingEnrollmentEncounter(Encounter referral, Date toDate, Concept referralReason) {
		// get all later enrollments
		List<Encounter> laterEnrollments = Context.getEncounterService().getEncounters(referral.getPatient(), referral.getLocation(), 
				referral.getEncounterDatetime(), toDate, null, Arrays.asList(Context.getEncounterService().getEncounterType(HaitiMobileClinicConstants.ENCOUNTER_TYPE_ID_STATIC_CLINIC_ENROLLMENT)), null, false);
		// only look into those with matching referral
		List<Obs> enrollments = Context.getObsService().getObservations(
				Arrays.asList((Person) referral.getPatient()),
				laterEnrollments, Arrays.asList(Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_REFERRAL_REASON)),
				Arrays.asList(referralReason), null, null, null, null, null, null,
				null, false);
		if (laterEnrollments.isEmpty() || enrollments.isEmpty()) {
			// no later enrollment found, still pending
			return null;
		}
		return enrollments.get(enrollments.size() - 1).getEncounter();
	}

	public static Encounter getMatchingTbResultsEncounter(Encounter e, Date toDate) {
		List<Encounter> encounters = Context.getEncounterService()
				.getEncounters(e.getPatient(), null, e.getEncounterDatetime(), toDate, null,
						Arrays.asList(Context.getEncounterService().getEncounterType(HaitiMobileClinicConstants.ENCOUNTER_TYPE_ID_TB_RESULTS)), null, null, null,
						false);
		if (encounters.isEmpty()) {
			return null;
		} else {
			return encounters.get(encounters.size() - 1);
		}
	}

	public static Encounter mostRecentReferralEncounter(Date fromDate,
			Date toDate, Patient patient, Concept answer)  {
		EncounterType consultation = Context.getEncounterService()
				.getEncounterType(HaitiMobileClinicConstants.ENCOUNTER_TYPE_ID_MOBILE_CLINIC_CONSULTATION);
		Location location = Context.getLocationService().getLocation(Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCATION_NAME));
		Concept question = Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_REFERRAL_REASON);
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
				return null;
			} else {
				return obses.get(0).getEncounter();
			}
		} else {
			return null;
		}
	}

	public static Concept referralReasonAnswer(String enrollmentReason) {
		if ("hiv".equalsIgnoreCase(enrollmentReason)) {
			return Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_REFERRAL_REASON_HIV);
		} else if ("tb".equalsIgnoreCase(enrollmentReason)) {
			return Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_REFERRAL_REASON_TB);
		} else if ("malnutrition".equalsIgnoreCase(enrollmentReason)) {
			return Context.getConceptService().getConcept(HaitiMobileClinicConstants.CONCEPT_ID_REFERRAL_REASON_MALNUTRITION);
		} else {
			HaitiMobileClinicWebUtil.log.error("from enrollment reason specified");
		}
		return null;
	}

}
