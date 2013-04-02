/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.haitimobileclinic;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.addresshierarchy.util.AddressHierarchyImportUtil;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.util.OpenmrsConstants;

/**
 * This class contains the logic that is run every time this module
 * is either started or shutdown
 */
public class HaitiMobileClinicActivator implements ModuleActivator {

	private Log log = LogFactory.getLog(this.getClass());

	private String ADDRESS_HIERARCHY_CSV_FILE = "haiti_address_hierarchy_entries_5.csv";

	//private ConfigureIdGenerators configureIdGenerators;

	private void setupCoreGlobalProperties() {
		setExistingGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, "ht, fr, en");
	}

	private void setupAddressHierarchy() {
		AddressHierarchyService ahService = Context.getService(AddressHierarchyService.class);
		
		// first check to see if we need to configure the address hierarchy levels
		int numberOfLevels = ahService.getAddressHierarchyLevelsCount();
		
		// if not 0 or 6 levels, we are in a weird state we can't recover from
		if (numberOfLevels != 0 && numberOfLevels != 6) {
			throw new RuntimeException("Unable to configure address hierarchy as it is currently misconfigured with "
			        + numberOfLevels + "levels");
		}
		
		// add the address hierarchy levels & entries if they don't exist, otherwise verify that they are correct
		if (numberOfLevels == 0) {
			AddressHierarchyLevel country = new AddressHierarchyLevel();
			country.setAddressField(AddressField.COUNTRY);
			ahService.saveAddressHierarchyLevel(country);
			
			AddressHierarchyLevel stateProvince = new AddressHierarchyLevel();
			stateProvince.setAddressField(AddressField.STATE_PROVINCE);
			stateProvince.setParent(country);
			ahService.saveAddressHierarchyLevel(stateProvince);
			
			AddressHierarchyLevel cityVillage = new AddressHierarchyLevel();
			cityVillage.setAddressField(AddressField.CITY_VILLAGE);
			cityVillage.setParent(stateProvince);
			ahService.saveAddressHierarchyLevel(cityVillage);
			
			AddressHierarchyLevel address3 = new AddressHierarchyLevel();
			address3.setAddressField(AddressField.ADDRESS_3);
			address3.setParent(cityVillage);
			ahService.saveAddressHierarchyLevel(address3);
			
			AddressHierarchyLevel address1 = new AddressHierarchyLevel();
			address1.setAddressField(AddressField.ADDRESS_1);
			address1.setParent(address3);
			ahService.saveAddressHierarchyLevel(address1);
			
			AddressHierarchyLevel address2 = new AddressHierarchyLevel();
			address2.setAddressField(AddressField.ADDRESS_2);
			address2.setParent(address1);
			ahService.saveAddressHierarchyLevel(address2);
			
			// load in the csv file
			InputStream file = getClass().getClassLoader().getResourceAsStream(ADDRESS_HIERARCHY_CSV_FILE);
			AddressHierarchyImportUtil.importAddressHierarchyFile(file, "\\|");
		}
		// at least verify that the right levels exist
		// TODO: perhaps do more validation here?
		else {
			AddressField[] fields = { AddressField.COUNTRY, AddressField.STATE_PROVINCE, AddressField.CITY_VILLAGE,
			        AddressField.ADDRESS_3, AddressField.ADDRESS_1, AddressField.ADDRESS_2 };
			int i = 0;
			
			for (AddressHierarchyLevel level : ahService.getOrderedAddressHierarchyLevels(true)) {
				if (level.getAddressField() != fields[i]) {
					throw new RuntimeException("Address field " + i + " improperly configured: is "
					        + level.getAddressField() + " but should be " + fields[i]);
				}
				i++;
				
			}
		}
	}
/*
	private void setupIdentifierGeneratorsIfNecessary(MirebalaisHospitalService service,
	        IdentifierSourceService identifierSourceService) {

		configureIdGenerators = new ConfigureIdGenerators(customProperties, identifierSourceService, service);

		createPatientIdGenerator(service);

		createDossierNumberGenerator(service);
	}

	private void createDossierNumberGenerator(MirebalaisHospitalService service) {
		PatientIdentifierType dossierIdentifierType = service.getDossierIdentifierType();

		SequentialIdentifierGenerator sequentialIdentifierGenerator = configureIdGenerators
		        .sequentialIdentifierGeneratorToDossier(dossierIdentifierType);
		configureIdGenerators.autoGenerationOptions(sequentialIdentifierGenerator);
	}

	private void createPatientIdGenerator(MirebalaisHospitalService service) {
		PatientIdentifierType zlIdentifierType = service.getZlIdentifierType();

		RemoteIdentifierSource remoteZlIdentifierSource = configureIdGenerators.remoteZlIdentifierSource(zlIdentifierType);
		IdentifierPool localZlIdentifierPool = configureIdGenerators.localZlIdentifierSource(remoteZlIdentifierSource);
		configureIdGenerators.autoGenerationOptions(localZlIdentifierPool);
	}

*/
	/*
	private void setupNamePhoneticsGlobalProperties() {
		setExistingGlobalProperty(NamePhoneticsConstants.GIVEN_NAME_GLOBAL_PROPERTY, "Double Metaphone Alternate");
		setExistingGlobalProperty(NamePhoneticsConstants.MIDDLE_NAME_GLOBAL_PROPERTY, "Double Metaphone Alternate");
		setExistingGlobalProperty(NamePhoneticsConstants.FAMILY_NAME_GLOBAL_PROPERTY, "Double Metaphone Alternate");
		setExistingGlobalProperty(NamePhoneticsConstants.FAMILY_NAME2_GLOBAL_PROPERTY, "Double Metaphone Alternate");
	}

	private void setupPatientRegistrationGlobalProperties() {
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.SUPPORTED_TASKS,
		    "patientRegistration|primaryCareReception|edCheckIn|patientLookup");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.SEARCH_CLASS,
		    "org.openmrs.module.patientregistration.search.DefaultPatientRegistrationSearch");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.LABEL_PRINT_COUNT, "1");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PROVIDER_ROLES, "LacollineProvider");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PROVIDER_IDENTIFIER_PERSON_ATTRIBUTE_TYPE,
		    "Provider Identifier");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PRIMARY_IDENTIFIER_TYPE, "ZL EMR ID");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.URGENT_DIAGNOSIS_CONCEPT,
		    "PIH: Haiti nationally urgent diseases");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.NOTIFY_DIAGNOSIS_CONCEPT,
		    "PIH: Haiti nationally notifiable diseases");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.NON_CODED_DIAGNOSIS_CONCEPT,
		    "PIH: ZL Primary care diagnosis non-coded");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.NEONATAL_DISEASES_CONCEPT,
		    "PIH: Haiti neonatal diseases");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PRIMARY_CARE_VISIT_ENCOUNTER_TYPE,
		    "Primary Care Visit");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.CODED_DIAGNOSIS_CONCEPT,
		    "PIH: ZL Primary care diagnosis");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.AGE_RESTRICTED_CONCEPT,
		    "PIH: Haiti age restricted diseases");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.RECEIPT_NUMBER_CONCEPT,
		    "PIH: Receipt number|en:Receipt Number|ht:Nimewo Resi a");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PAYMENT_AMOUNT_CONCEPT,
		    "PIH: Payment amount|en:Payment amount|ht:Kantite lajan");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.VISIT_REASON_CONCEPT,
		    "PIH: Reason for HUM visit|en:Visit reason|ht:Rezon pou vizit");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PRIMARY_CARE_RECEPTION_ENCOUNTER_TYPE, "Check-in");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PATIENT_REGISTRATION_ENCOUNTER_TYPE,
		    "Patient Registration");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.NUMERO_DOSSIER, "Nimewo Dosye");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.ID_CARD_PERSON_ATTRIBUTE_TYPE, "Telephone Number");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.PATIENT_VIEWING_ATTRIBUTE_TYPES, "Telephone Number");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.ID_CARD_LABEL_TEXT, "Zanmi Lasante Patient ID Card");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.ICD10_CONCEPT_SOURCE, "ICD-10");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.BIRTH_YEAR_INTERVAL, "1");
		setExistingGlobalProperty(PatientRegistrationGlobalProperties.MEDICAL_RECORD_LOCATION_TAG,
		    "71c99f93-bc0c-4a44-b573-a7ac096ff636");

	}

	private void setupEmrGlobalProperties() {
		// TODO create an encounter type to represent placing orders in our MDS package
		setExistingGlobalProperty(EmrConstants.GP_PLACE_ORDERS_ENCOUNTER_TYPE, "1373cf95-06e8-468b-a3da-360ac1cf026d");

		// TODO make sure we have the order type correct, and created via a MDS package
		setExistingGlobalProperty(EmrConstants.GP_TEST_ORDER_TYPE, "13116a48-15f5-102d-96e4-000c29c2a5d7");

		// TODO add a Clinician encounter role to our MDS packages
		setExistingGlobalProperty(EmrConstants.GP_CLINICIAN_ENCOUNTER_ROLE, "a0b03050-c99b-11e0-9572-0800200c9a66");

		// check_in clerk encounter role is set to Oupatient Application User Role
		setExistingGlobalProperty(EmrConstants.GP_CHECK_IN_CLERK_ENCOUNTER_ROLE, "cbfe0b9d-9923-404c-941b-f048adc8cdc0");

		// paper record location = Mirebalais
		setExistingGlobalProperty(EmrConstants.GP_PAPER_RECORD_IDENTIFIER_TYPE, "e66645eb-03a8-4991-b4ce-e87318e37566");

		setExistingGlobalProperty(EmrConstants.GP_XRAY_ORDERABLES_CONCEPT, "35c24af8-6d60-4189-95c6-7e91e421d11f");
		setExistingGlobalProperty(EmrConstants.GP_CT_SCAN_ORDERABLES_CONCEPT, "381d653b-a6b7-438a-b9f0-5034b5272def");
		setExistingGlobalProperty(EmrConstants.GP_ULTRASOUND_ORDERABLES_CONCEPT, "a400b7e5-6b2f-404f-84d0-6eb2ca611a7d");
		setExistingGlobalProperty(EmrConstants.GP_AT_FACILITY_VISIT_TYPE, "f01c54cb-2225-471a-9cd5-d348552c337c");
		setExistingGlobalProperty(EmrConstants.GP_CHECK_IN_ENCOUNTER_TYPE, "55a0d3ea-a4d7-4e88-8f01-5aceb2d3c61b");
		setExistingGlobalProperty(EmrConstants.PRIMARY_IDENTIFIER_TYPE, "ZL EMR ID");

		setExistingGlobalProperty(EmrConstants.PAYMENT_AMOUNT_CONCEPT, "5d1bc5de-6a35-4195-8631-7322941fe528");
		setExistingGlobalProperty(EmrConstants.PAYMENT_REASON_CONCEPT, "36ba7721-fae0-4da4-aef2-7e476cc04bdf");
		setExistingGlobalProperty(EmrConstants.PAYMENT_RECEIPT_NUMBER_CONCEPT, "20438dc7-c5b4-4d9c-8480-e888f4795123");
		setExistingGlobalProperty(EmrConstants.PAYMENT_CONSTRUCT_CONCEPT, "7a6330f1-9503-465c-8d63-82e1ad914b47");

	}
	 */

	@Override
	public void contextRefreshed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void started() {
		log.info("Starting HaitiMobileClinic Module");
		setupCoreGlobalProperties();
		setupAddressHierarchy();
	}

	@Override
	public void stopped() {
		log.info("Shutting down HaitiMobileClinic Module");
	}

	@Override
	public void willRefreshContext() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void willStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void willStop() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Sets global property value or throws an exception if that global property does not already exist
	 * (Set as protected so we can override it for testing purposes)
	 *
	 * @param propertyName
	 * @param propertyValue
	 */
	protected void setExistingGlobalProperty(String propertyName, String propertyValue) {
		AdministrationService administrationService = Context.getAdministrationService();
		GlobalProperty gp = administrationService.getGlobalPropertyObject(propertyName);
		if (gp == null) {
			throw new RuntimeException("global property " + propertyName + " does not exist");
		}
		gp.setPropertyValue(propertyValue);
		administrationService.saveGlobalProperty(gp);
	}

	/**
	 * Sets global property value or creates global property if needed
	 * @param propertyName
	 * @param propertyValue
	 */
	private void setOrCreateGlobalProperty(String propertyName, String propertyValue) {
		AdministrationService administrationService = Context.getAdministrationService();
		GlobalProperty gp = administrationService.getGlobalPropertyObject(propertyName);
		if (gp == null) {
			gp = new GlobalProperty();
			gp.setProperty(propertyName);
		}
		gp.setPropertyValue(propertyValue);
		administrationService.saveGlobalProperty(gp);
	}

}