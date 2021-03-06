package org.openmrs.module.haitimobileclinic;


public class HaitiMobileClinicConstants {

	// the default class to use for a patient search if search class global property is blank
    public static final String DEFAULT_SEARCH_CLASS = "org.openmrs.module.haitimobileclinic.search.DefaultHaitiMobileClinicSearch";
   
    public static final String MODULE_NAME = "haitimobileclinic";
    // i18n messages to be used in the java script
    public static final String JSCRIPT_MESSAGES = "haitimobileclinic.jMessages.";
    // i18n tooltip messages to be used in the java script
    public static final String JSCRIPT_TOOLTIP = "haitimobileclinic.toolTip.";
    
	// date formats for display and input
    public static final String DATE_FORMAT_DISPLAY = "dd/MMM/yyyy";
	public static final String DATE_FORMAT_INPUT = "dd/MM/yyyy";
	
	public static final String REGISTRATION_PATIENT= "registration_patient";	
	public static final String REGISTRATION_PATIENT_GENDER= "registration_patientGender";
	public static final String WORKFLOW_FIRST_PAGE= "redirect:/module/haitimobileclinic/workflow/selectLocationAndService.form";
	
	public static final String SESSION_REGISTRATION_LOCATION = "registration_location";
	public static final String SESSION_REGISTRATION_TASK = "registration_task";
	public static final String SESSION_TASK_GROUP = "registration_task_group";
	public static final String SESSION_TASK_PROGRESS = "task_progress";
	public static final String NUMERO_DOSSIER = "numeroDossier";
	public static final String DENTAL_DOSSIER = "dentalDossier";
	public static final String MOBILE_CLINIC_DOSSIER = "mobileClinicDossier";
	public static final String IDENTIFIER_TYPE_ID = "identifierTypeId";
	public static final String IDENTIFIER_TYPE_NAME = "identifierTypeName";
	public static final String RETROSPECTIVE_TASK = "retrospectiveEntry";
	
	public static final String RETROSPECTIVE_PROGRESS_1_IMG = "progress-1.png";
	public static final String RETROSPECTIVE_PROGRESS_2_IMG = "progress-2.png";
	public static final String RETROSPECTIVE_PROGRESS_3_IMG = "progress-3.png";
	public static final String RETROSPECTIVE_PROGRESS_4_IMG = "progress-4.png";
	
	public static final String POC_CONFIGURATION_FILE = "poc_config.xml";
	public static final String FALSE_DUPLICATES_MAP = "falseDuplicatesMap";
	public static final String PATIENT_DUPLICATES_MAP = "patientDuplicatesMap";
	
	// user activities that are logged
	
	public static final String ACTIVITY_REGISTRATION_LOCATION_CHANGED = "Registration Location Changed";
	public static final String ACTIVITY_REGISTRATION_TASK_CHANGED = "Registration Task Changed";
	public static final String ACTIVITY_REGISTRATION_INITIATED = "Registration Task Initiated";
	public static final String ACTIVITY_REGISTRATION_CREATE_STARTED = "Registration Task Create Patient Initiated";
	public static final String ACTIVITY_REGISTRATION_EDIT_STARTED = "Registration Task Edit Patient Initiated";
	public static final String ACTIVITY_REGISTRATION_SUBMITTED = "Registration Task Submitted";
	public static final String ACTIVITY_REGISTRATION_NEW_ZL_ID = "Registration Task New ZL EMR ID Created";
	public static final String ACTIVITY_REGISTRATION_COMPLETED = "Registration Task Completed";
	public static final String ACTIVITY_PATIENT_LOOKUP_INITIATED = "Patient Lookup Initiated";
	public static final String ACTIVITY_PATIENT_LOOKUP_STARTED = "Patient Lookup Started";
	public static final String ACTIVITY_PATIENT_LOOKUP_COMPLETED = "Patient Lookup Completed";
	public static final String ACTIVITY_PRIMARY_CARE_RECEPTION_ENCOUNTER_STARTED = "Primary Care Reception Encounter Started";
	public static final String ACTIVITY_PRIMARY_CARE_RECEPTION_ENCOUNTER_COMPLETED = "Primary Care Reception Encounter Completed";
	public static final String ACTIVITY_PRIMARY_CARE_RECEPTION_DOSSIER_STARTED = "Primary Care Reception Dossier Started";
	public static final String ACTIVITY_PRIMARY_CARE_RECEPTION_DOSSIER_COMPLETED = "Primary Care Reception Dossier Completed";
	public static final String ACTIVITY_PRIMARY_CARE_VISIT_ENCOUNTER_STARTED = "Primary Care Visit Encounter Started";
	public static final String ACTIVITY_PRIMARY_CARE_VISIT_ENCOUNTER_COMPLETED = "Primary Care Visit Encounter Completed";
	public static final String ACTIVITY_ID_CARD_PRINTING_SUCCESSFUL = "ID Card Printing Successful";
	public static final String ACTIVITY_ID_CARD_PRINTING_FAILED = "ID Card Printing Failed";
	public static final String ACTIVITY_DOSSIER_LABEL_PRINTING_SUCCESSFUL = "Dossier Label Printing Successful";
	public static final String ACTIVITY_DOSSIER_LABEL_PRINTING_FAILED = "Dossier Label Printing Failed";
	public static final String ACTIVITY_ID_CARD_LABEL_PRINTING_SUCCESSFUL = "ID Card Label Printing Successful";
	public static final String ACTIVITY_ID_CARD_LABEL_PRINTING_FAILED = "ID Card Label Printing Failed";
	public static final String ACTIVITY_ENCOUNTER_SAVED = "Encounter Saved";

	public static final String ACTIVITY_MOBILE_CLINIC_RECEPTION_ENCOUNTER_STARTED = "Mobile Clinic Reception Encounter Started";
	public static final String ACTIVITY_MOBILE_CLINIC_RECEPTION_ENCOUNTER_COMPLETED = "Mobile Clinic Reception Encounter Completed";
	
	public static final Integer UNKNOWN_PROVIDER_ID = 1; 
	public static final Integer ENCOUNTER_TYPE_ID_MOBILE_CLINIC_CONSULTATION = 19; 
	public static final Integer ENCOUNTER_TYPE_ID_STATIC_CLINIC_ENROLLMENT = 20; 
	public static final Integer ENCOUNTER_TYPE_ID_TB_RESULTS = 21; 
	public static final Integer FORM_ID_STATIC_CLINIC_ENROLLMENT = 3;
	public static final Integer FORM_ID_TB_RESULTS = 4; 
	public static final Integer CONCEPT_ID_REFERRAL_REASON = 6760;
	public static final Integer CONCEPT_ID_REFERRAL_REASON_HIV = 6751; 
	public static final Integer CONCEPT_ID_REFERRAL_REASON_TB = 6752; 
	public static final Integer CONCEPT_ID_REFERRAL_REASON_MALNUTRITION = 6753; 
	public static final Integer CONCEPT_ID_SPUTUM_RESULT_1 = 6777; 
	public static final Integer CONCEPT_ID_SPUTUM_RESULT_2 = 6778; 
	public static final Integer CONCEPT_ID_SPUTUM_RESULT_3 = 6779; 
	public static final Integer CONCEPT_ID_CONFIRMATIVE_TB_STATUS = 6782;
	public static final Integer CONCEPT_ID_SPUTUM_RESULT_DATE_1 = 6783; 
	public static final Integer CONCEPT_ID_SPUTUM_RESULT_DATE_2 = 6784; 
	public static final Integer CONCEPT_ID_SPUTUM_RESULT_DATE_3 = 6785;
	public static final Integer CONCEPT_ID_CONFIRMATIVE_TB_STATUS_DATE = 6786;
	public static final Integer CONCEPT_ID_PPD_RESULT = 6787; 
	public static final Integer CONCEPT_ID_PPD_RESULT_DATE = 6788;
	public static final Integer CONCEPT_ID_CONFIRMATIVE_TB_STATUS_POSITIVE = 703; 
}
