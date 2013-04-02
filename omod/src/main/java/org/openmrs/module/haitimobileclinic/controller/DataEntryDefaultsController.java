package org.openmrs.module.haitimobileclinic.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.PersonAddress;
import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.web.controller.PseudoStaticContentController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@SessionAttributes({ "sessionDate", "sessionLocation", "sessionLocationAddressHierarchyId", "sessionCoordinates", "sessionStaticLocation",
	"sessionStaticLocationName", "sessionChwName1", "sessionChwName2", "sessionChwName3", "sessionNecName1", "sessionNecName2" })
@Controller
public class DataEntryDefaultsController {

	protected final Log log = LogFactory.getLog(getClass());

	@RequestMapping(value = "/module/haitimobileclinic/dataEntryDefaults.form", method = RequestMethod.POST)
	public String setDataEntryDefaults(@RequestParam String sessionDate,
			@RequestParam(required = false) String country,
			@RequestParam(required = false) String stateProvince,
			@RequestParam(required = false) String cityVillage, 
			@RequestParam(required = false) String address3,
			@RequestParam(required = false) String address1,
			@RequestParam String sessionCoordinates,
			@RequestParam String sessionStaticLocation,
			@RequestParam String sessionStaticLocationName,
			@RequestParam String sessionChwName1,
			@RequestParam String sessionChwName2,
			@RequestParam String sessionChwName3,
			@RequestParam String sessionNecName1, 
			@RequestParam String sessionNecName2, 
			ModelMap model) {
		if (!Context.hasPrivilege(PrivilegeConstants.VIEW_PATIENTS))
			throw new APIAuthenticationException("Privilege required: " + PrivilegeConstants.VIEW_PATIENTS);
		// take form values and store into session
		model.addAttribute("sessionDate", sessionDate);
		String location = stateProvince + "|" + cityVillage + "|" + address3 + "|" + address1;
		location = location.replace("null",  "");
		model.addAttribute("sessionLocation", location);
		AddressHierarchyEntry entry = findAddressHierarchyEntry(country, stateProvince, cityVillage, address3, address1);
		model.addAttribute("sessionLocationAddressHierarchyId", entry == null ? "<not found>" : entry.getId());
		
		model.addAttribute("sessionCoordinates", sessionCoordinates);
		model.addAttribute("sessionStaticLocationName", sessionStaticLocationName);
		model.addAttribute("sessionStaticLocation", sessionStaticLocation);
		model.addAttribute("sessionChwName1", sessionChwName1);
		model.addAttribute("sessionChwName2", sessionChwName2);
		model.addAttribute("sessionChwName3", sessionChwName3);
		model.addAttribute("sessionNecName1", sessionNecName1);
		model.addAttribute("sessionNecName2", sessionNecName2);
		return "redirect:/";
	}

	private AddressHierarchyEntry findAddressHierarchyEntry(
			String country, String stateProvince, String cityVillage,
			String address3, String address1) {
		PersonAddress pa = new PersonAddress();
		pa.setCountry(country);
		pa.setStateProvince(stateProvince);
		pa.setCityVillage(cityVillage);
		pa.setAddress3(address3);
		pa.setAddress1(address1);
		AddressHierarchyService ahs = Context.getService(AddressHierarchyService.class);
		AddressHierarchyLevel level = null;
		if (!isEmpty(country) && isEmpty(stateProvince)) {
			level = ahs.getAddressHierarchyLevelByAddressField(AddressField.COUNTRY);
		} else if (!isEmpty(stateProvince) && isEmpty(cityVillage)) {
			level = ahs.getAddressHierarchyLevelByAddressField(AddressField.STATE_PROVINCE);
		} else if (!isEmpty(cityVillage) && isEmpty(address3)) {
			level = ahs.getAddressHierarchyLevelByAddressField(AddressField.CITY_VILLAGE);
		} else if (!isEmpty(address3) && isEmpty(address1)) {
			level = ahs.getAddressHierarchyLevelByAddressField(AddressField.ADDRESS_3);
		} else if (!isEmpty(address1)) {
			level = ahs.getAddressHierarchyLevelByAddressField(AddressField.ADDRESS_1);
		}
		List<AddressHierarchyEntry> entries = ahs.getPossibleAddressHierarchyEntries(pa, level);
		if (entries.isEmpty()) {
			return null;
		} else {
			return entries.get(0);
		}
	}

	private boolean isEmpty(String string) {
		return (string == null || "".equals(string));
	}

	@RequestMapping(value = "/module/haitimobileclinic/dataEntryDefaults.form", method = RequestMethod.GET)
	public void initDataEntryDefaults(ModelMap model) {
		if (!Context.hasPrivilege(PrivilegeConstants.VIEW_PATIENTS))
			throw new APIAuthenticationException("Privilege required: " + PrivilegeConstants.VIEW_PATIENTS);
		// update user profile with static location from globalproperty default_location
		Location loc = Context.getLocationService().getLocation(Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCATION_NAME));
		
		User loginUser = Context.getAuthenticatedUser();
		UserService us = Context.getUserService();
		User user = us.getUser(loginUser.getUserId());
		Map<String, String> properties = user.getUserProperties();
		properties.put(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCATION, "" + loc.getId());
		user.setUserProperties(properties);
		us.saveUser(user, null);
		PseudoStaticContentController.invalidateCachedResources(properties);
		Context.refreshAuthenticatedUser();

		model.addAttribute("sessionStaticLocation", loc.getLocationId());
		model.addAttribute("sessionStaticLocationName", loc.getName());
	}
}
