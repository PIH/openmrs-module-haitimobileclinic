package org.openmrs.module.haitimobileclinic.validator;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicGlobalProperties;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicUtil;
import org.openmrs.validator.PatientIdentifierValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Specific validator for Patient Registration use case
 */
public class PatientValidator implements Validator {
	
	/**
	 * Returns whether or not this validator supports validating a given class.
	 * 
	 * @param c The class to check for support.
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	public boolean supports(@SuppressWarnings("rawtypes") Class c) {
		return Patient.class.isAssignableFrom(c);
	}
	
	/**
	 * Validates the given Patient. 
	 * 
	 * @param obj The patient to validate.
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 */
	public void validate(Object obj, Errors errors) {
		Patient patient = (Patient) obj;
		
		// make sure a family name & given name have been specified (TODO: is it valid that all implementations will require this?)
		if(StringUtils.isBlank(patient.getFamilyName())) {
			errors.rejectValue("personName.familyName","haitimobileclinic.person.familyName.required");
		}
		
		if(StringUtils.isBlank(patient.getGivenName())) {
			errors.rejectValue("personName.givenName","haitimobileclinic.person.givenName.required");
		}
		
		// make sure they choose a gender
		if (StringUtils.isBlank(patient.getGender())) {
			errors.rejectValue("gender", "haitimobileclinic.gender.required");
		}
		
		// check patients birthdate against future dates and really old dates
		if (patient.getBirthdate() != null) {
			if (patient.getBirthdate().after(new Date())) {
				errors.rejectValue("birthdate", "haitimobileclinic.error.date.future");
			}
		}
		
		// check to make sure that the primary identifier has been specified, if it is required
		PatientIdentifierType primaryIdentifier = HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_PRIMARY_IDENTIFIER_TYPE();
		
		if (primaryIdentifier != null && (patient.getPatientIdentifier(primaryIdentifier) == null || patient.getPatientIdentifier(primaryIdentifier).getLocation() == null)){
			errors.reject("haitimobileclinic.person.primaryIdentifier.required");
		}
		
		// make sure that all identifiers that have been specified (and any that will be auto-assigned) have a location
		List<PatientIdentifierType> autoAssigned = HaitiMobileClinicUtil.getPatientIdentifierTypesAutoGenerated();
		for (PatientIdentifier identifier : patient.getIdentifiers()) {
		
			if (identifier.getLocation() == null && StringUtils.isNotBlank(identifier.getIdentifier())) {
				errors.reject("haitimobileclinic.person.identifier.mustHaveLocation");
				break;
			}
			// check the auto-assigned cases (but only if a fixed location has not been specified)
			if (identifier.getLocation() == null && HaitiMobileClinicGlobalProperties.GLOBAL_PROPERTY_FIXED_IDENTIFIER_LOCATION() == null 
					&& autoAssigned.contains(identifier.getIdentifierType())) {
				errors.reject("haitimobileclinic.person.identifier.mustHaveLocation");
				break;
			}
		}
		
		// run the specific identifier validators
		PatientIdentifierValidator piv = new PatientIdentifierValidator();
		if (patient.getIdentifiers() != null) {
			for (PatientIdentifier identifier : patient.getIdentifiers()) {
				// we only want to validate identifiers that aren't auto-assigned (since we haven't auto-assigned identifiers yet!)
				if(!autoAssigned.contains(identifier.getIdentifierType())) {
					piv.validate(identifier, errors);
				}
			}
		}
	}
}
