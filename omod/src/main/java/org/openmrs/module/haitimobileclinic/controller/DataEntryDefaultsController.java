package org.openmrs.module.haitimobileclinic.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@SessionAttributes({ "sessionDate", "sessionLocation", "sessionCoordinates",
		"sessionChwNames", "sessionNecName" })
@Controller
public class DataEntryDefaultsController {

	protected final Log log = LogFactory.getLog(getClass());

	@RequestMapping(value = "/module/haitimobileclinic/dataEntryDefaults.form", method = RequestMethod.GET)
	public ModelAndView viewDataEntryDefaults(ModelAndView mav) {
		return mav;
	}

	@RequestMapping(value = "/module/haitimobileclinic/dataEntryDefaults.form", method = RequestMethod.POST)
	public String setDataEntryDefaults(@RequestParam String sessionDate,
			@RequestParam(required = false) String country,
			@RequestParam(required = false) String stateProvince,
			@RequestParam(required = false) String cityVillage, 
			@RequestParam(required = false) String address3,
			@RequestParam(required = false) String address1,
			@RequestParam String sessionCoordinates,
			@RequestParam String sessionChwName1,
			@RequestParam String sessionChwName2,
			@RequestParam String sessionChwName3,
			@RequestParam String sessionNecName, 
			ModelMap model) {
		// take form values and store into session
		model.addAttribute("sessionDate", sessionDate);
		model.addAttribute("sessionLocation", country + "|" + stateProvince
				+ "|" + cityVillage + "|" + address3 + "|" + address1);
		model.addAttribute("sessionCoordinates", sessionCoordinates);
		model.addAttribute("sessionChwName1", sessionChwName1);
		model.addAttribute("sessionChwName2", sessionChwName2);
		model.addAttribute("sessionChwName3", sessionChwName3);
		model.addAttribute("sessionNecName", sessionNecName);
		model.addAttribute("message",
				"Default Data Entry Values Successfully Set.");
		return "redirect:/";
	}
}
