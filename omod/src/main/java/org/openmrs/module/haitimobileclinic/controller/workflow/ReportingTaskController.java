package org.openmrs.module.haitimobileclinic.controller.workflow;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicConstants;
import org.openmrs.module.haitimobileclinic.HaitiMobileClinicUtil;
import org.openmrs.module.haitimobileclinic.controller.AbstractPatientDetailsController;
import org.openmrs.module.haitimobileclinic.util.HaitiMobileClinicWebUtil;
import org.openmrs.module.haitimobileclinic.util.simpleconfig.POCConfiguration;
import org.openmrs.module.haitimobileclinic.util.simpleconfig.POCReport;
import org.openmrs.module.haitimobileclinic.util.simpleconfig.POCReportCategory;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Priority;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/module/haitimobileclinic/workflow/reportingTask.form")
public class ReportingTaskController extends AbstractPatientDetailsController {
	
	@SuppressWarnings("rawtypes")
	public boolean supports(Class c) {
		return c == CommandObject.class;
	}
	
	@ModelAttribute("reportsList")
    public List<POCReportCategory> getReportsList(HttpSession session) {		
				
		List<POCReportCategory> reportsList =null;
		POCConfiguration pocConfig = HaitiMobileClinicUtil.getConfigProperties();
		if(pocConfig!=null){						
			reportsList = pocConfig.getReportCategories();
		}
		return reportsList;
    }
	
	@ModelAttribute("report")
	public CommandObject getReport(HttpSession session, 
			@RequestParam(value= "reportId", required = false) String reportId,
			@RequestParam(value= "copyRequest", required = false) String copyRequest,
			@RequestParam(value= "messageId", required = false) String messageId){
		
		CommandObject command = new CommandObject();
		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
		ReportService reportService = Context.getService(ReportService.class);
		if (StringUtils.isNotBlank(copyRequest)) {
			ReportRequest req = reportService.getReportRequestByUuid(copyRequest);
			// avoid lazy init exceptions
			command.setReportDefinition(rds.getDefinitionByUuid(req.getReportDefinition().getParameterizable().getUuid()));
			for (Map.Entry<String, Object> param : req.getReportDefinition().getParameterMappings().entrySet()) {
				command.getUserEnteredParams().put(param.getKey(), param.getValue());
			}
			command.setSelectedRenderer(req.getRenderingMode().getDescriptor());
			//command.setConfiguredProcessorConfigurations(req.getReportProcessors());
		}else if(StringUtils.isNotBlank(reportId)){			
			ReportDefinition reportDefinition = rds.getDefinitionByUuid(reportId);			
			
			List<Parameter> parameters = reportDefinition.getParameters();
			if(parameters!=null && parameters.size()>0){
				String locationClassName = Location.class.getName();
				Location location = HaitiMobileClinicWebUtil.getRegistrationLocation(session);
				for (Parameter parameter : parameters){
					String paramClassName = parameter.getType().getName();					
					if(StringUtils.equalsIgnoreCase(paramClassName, locationClassName)){
						parameter.setDefaultValue(location);
						command.getUserEnteredParams().put(parameter.getName(), location);
					}
					
				}
				reportDefinition.setParameters(parameters);
			}				
			command.setReportDefinition(reportDefinition);
			if(StringUtils.isNotBlank(messageId)){
				command.setMessageId(messageId);
			}
		}
		
		command.setRenderingModes(reportService.getRenderingModes(command.getReportDefinition()));
		command.setAvailableProcessorConfigurations(reportService.getAllReportProcessorConfigurations(false));
		return command;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView reportingTask(	@RequestParam(value= "reportId", required = false) String reportId,
										@RequestParam(value= "copyRequest", required = false) String copyRequest,
										ModelMap model,
										HttpSession session) {
    	
		if (!HaitiMobileClinicWebUtil.confirmActiveHaitiMobileClinicSession(session)) {
			return new ModelAndView(HaitiMobileClinicConstants.WORKFLOW_FIRST_PAGE);
		}
	
		return new ModelAndView("/module/haitimobileclinic/workflow/reportingTask");	
	}
	
	@RequestMapping(params="reportHistoryOpen=true", method = RequestMethod.GET)
	public String openFromHistory(@RequestParam("uuid") String uuid, HttpServletResponse response, WebRequest request, ModelMap model) throws IOException {
		ReportService rs = Context.getService(ReportService.class);
		ReportRequest req = rs.getReportRequestByUuid(uuid);
		if (req == null) {
			log.warn("Cannot load report request " + uuid);
			request.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Cannot load report request", WebRequest.SCOPE_SESSION);
			return "redirect:/module/reporting/reports/reportHistory.form";
		}
		model.addAttribute("request", req);
		return "/module/haitimobileclinic/workflow/reportingTask";
	}
	
	@RequestMapping(params="reportDownload=true", method = RequestMethod.GET)
	public void downloadFromHistory(@RequestParam("uuid") String uuid, HttpServletResponse response, HttpServletRequest request) throws IOException {
		ReportRequest req = getReportService().getReportRequestByUuid(uuid);
		RenderingMode rm = req.getRenderingMode();

		String filename = rm.getRenderer().getFilename(req.getReportDefinition().getParameterizable(), rm.getArgument()).replace(" ", "_");
		response.setContentType(rm.getRenderer().getRenderedContentType(req.getReportDefinition().getParameterizable(), rm.getArgument()));
		byte[] data = getReportService().loadRenderedOutput(req);
		
		if (data != null) {
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			response.setHeader("Pragma", "no-cache");
			IOUtils.write(data, response.getOutputStream());
		}
		else {
			response.getWriter().write("There was an error retrieving the report");
		}
	}
	
	@RequestMapping(params="reportHistory=true", method = RequestMethod.GET)
	public void showReportHistory(
				@RequestParam(value= "uuid", required = true) String reportUUID,
				ModelMap model) {
		
		List<ReportRequest> complete = getReportService().getReportRequests(null, null, null, Status.COMPLETED, Status.FAILED, Status.SAVED);
		List<ReportRequest> queue = getReportService().getReportRequests(null, null, null, Status.REQUESTED);
		Collections.reverse(complete);
		Collections.reverse(queue);
		model.addAttribute("complete", complete);
		model.addAttribute("queue", queue);
		model.addAttribute("cached", getReportService().getCachedReports().keySet());
		
		Map<ReportRequest, String> shortNames = new HashMap<ReportRequest, String>();
		Map<ReportRequest, Boolean> isWebRenderer = new HashMap<ReportRequest, Boolean>();
		for (ReportRequest r : complete) {
			if (r.getRenderingMode().getRenderer() instanceof WebReportRenderer) {
				shortNames.put(r, "Web");
				isWebRenderer.put(r, true);
			} else {
				String filename = r.getRenderingMode().getRenderer().getFilename(r.getReportDefinition().getParameterizable(),
				    r.getRenderingMode().getArgument());
				try {
					filename = filename.substring(filename.lastIndexOf('.') + 1);
					filename = filename.toUpperCase();
				}
				catch (Exception ex) {}
				shortNames.put(r, filename);
				isWebRenderer.put(r, false);
			}
		}
		model.addAttribute("shortNames", shortNames);
		model.addAttribute("isWebRenderer", isWebRenderer);
		model.addAttribute("historyUUID", reportUUID);
	}
	
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView runReport(  @ModelAttribute("report") CommandObject report,
									HttpServletRequest request,									 
									HttpSession session, 
									Object commandObject, 
									ModelMap model) {
    	
		if (!HaitiMobileClinicWebUtil.confirmActiveHaitiMobileClinicSession(session)) {
			return new ModelAndView(HaitiMobileClinicConstants.WORKFLOW_FIRST_PAGE);
		}
		if(report==null){
			return new ModelAndView("/module/haitimobileclinic/workflow/reportingTask");	
		}
	
		ReportService reportService = Context.getService(ReportService.class);
		ReportDefinition reportDefinition = report.getReportDefinition();
		
		// Parse the input parameters into appropriate objects and fail validation if any are invalid
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		if (reportDefinition.getParameters() != null) {
			for (Parameter parameter : reportDefinition.getParameters()) {				
				
				Object defaultValue = parameter.getDefaultValue();
				if(defaultValue !=null){
					if(defaultValue instanceof Location){
						defaultValue = WidgetUtil.parseInput(((Location)defaultValue).getLocationId().toString()
								, parameter.getType());
					}else{
						defaultValue = WidgetUtil.parseInput(defaultValue.toString(), parameter.getType());
					}
					params.put(parameter.getName(), defaultValue);
				}
				if (report.getUserEnteredParams() != null) {
					Object value = report.getUserEnteredParams().get(parameter.getName());
					if (ObjectUtil.notNull(value)) {
						try {
							value = WidgetUtil.parseInput(value.toString(), parameter.getType());
							params.put(parameter.getName(), value);
						}
						catch (Exception ex) {							
							log.error("userEnteredParams[" + parameter.getName() + "]",  ex);
						}
					}
				}
			}
		}
		
		// Ensure that the chosen renderer is valid for this report
		RenderingMode renderingMode = report.getSelectedMode();
		if(renderingMode==null){
			log.error("selectedRenderer: reporting.Report.run.error.invalidRenderer");
		}
		
		if (!renderingMode.getRenderer().canRender(reportDefinition)) {
			log.error("selectedRenderer: reporting.Report.run.error.invalidRenderer");
		}
		
		Set<ReportProcessorConfiguration> processors = new HashSet<ReportProcessorConfiguration>();
		String[] processorParams = request.getParameterValues("configuredProcessors");
		if (processorParams != null) {
			for (String pp : processorParams) {
				processors.add(reportService.getReportProcessorConfiguration(Integer.parseInt(pp)));
			}
		}
		
		ReportRequest rr = null;
		if (report.getExistingRequestUuid() != null) {
			rr = reportService.getReportRequestByUuid(report.getExistingRequestUuid());
		}
		else {
			rr = new ReportRequest();
		}
		rr.setReportDefinition(new Mapped<ReportDefinition>(reportDefinition, params));
		rr.setBaseCohort(report.getBaseCohort());
	    rr.setRenderingMode(report.getSelectedMode());
	    rr.setPriority(Priority.NORMAL);
	    rr.setSchedule(report.getSchedule());
	    //rr.setReportProcessors(processors);
		
		rr = reportService.queueReport(rr);
		reportService.processNextQueuedReports();
		
		return new ModelAndView("redirect:/module/haitimobileclinic/workflow/reportingTask.form?reportHistoryOpen=true&uuid=" + rr.getUuid());	
	}
	
	private ReportService getReportService() {
		return Context.getService(ReportService.class);
	}
	
	public class CommandObject {
		
		private String existingRequestUuid;
		private ReportDefinition reportDefinition;
		private Mapped<CohortDefinition> baseCohort;
		private Map<String, Object> userEnteredParams;			
		private String selectedRenderer; // as RendererClass!Arg
		private Set<ReportProcessorConfiguration> configuredProcessorConfigurations;
		private String schedule;
		private String messageId;
		
		private List<RenderingMode> renderingModes;	
		private List<ReportProcessorConfiguration> availableProcessorConfigurations;
		
		public CommandObject() {
			userEnteredParams = new LinkedHashMap<String, Object>();
		}
		
		@SuppressWarnings("unchecked")
		public RenderingMode getSelectedMode() {
			if (selectedRenderer != null) {
				try {
					String[] temp = selectedRenderer.split("!");
					Class<? extends ReportRenderer> rc = (Class<? extends ReportRenderer>) Context.loadClass(temp[0]);
					String arg = (temp.length > 1 && StringUtils.isNotBlank(temp[1])) ? temp[1] : null;
					for (RenderingMode mode : renderingModes) {
						if (mode.getRenderer().getClass().equals(rc) && OpenmrsUtil.nullSafeEquals(mode.getArgument(), arg)) {
							return mode;
						}
					}
					log.warn("Could not find requested rendering mode: " + selectedRenderer);
				}
				catch (Exception e) {
					log.warn("Could not load requested renderer", e);
				}
			}
			return null;
		}

		public String getExistingRequestUuid() {
			return existingRequestUuid;
		}

		public void setExistingRequestUuid(String existingRequestUuid) {
			this.existingRequestUuid = existingRequestUuid;
		}

		public List<RenderingMode> getRenderingModes() {
			return renderingModes;
		}
		
		public void setRenderingModes(List<RenderingMode> rendereringModes) {
			this.renderingModes = rendereringModes;
		}
		
		public ReportDefinition getReportDefinition() {
			return reportDefinition;
		}
		
		public void setReportDefinition(ReportDefinition reportDefinition) {
			this.reportDefinition = reportDefinition;
		}

		public Mapped<CohortDefinition> getBaseCohort() {
			return baseCohort;
		}

		public void setBaseCohort(Mapped<CohortDefinition> baseCohort) {
			this.baseCohort = baseCohort;
		}

		public String getSelectedRenderer() {
			return selectedRenderer;
		}
		
		public void setSelectedRenderer(String selectedRenderer) {
			this.selectedRenderer = selectedRenderer;
		}
		
		public Map<String, Object> getUserEnteredParams() {
			return userEnteredParams;
		}
		
		public void setUserEnteredParams(Map<String, Object> userEnteredParams) {
			this.userEnteredParams = userEnteredParams;
		}

		public Set<ReportProcessorConfiguration> getConfiguredProcessorConfigurations() {
			return configuredProcessorConfigurations;
		}

		public void setConfiguredProcessorConfigurations(Set<ReportProcessorConfiguration> configuredProcessorConfigurations) {
			this.configuredProcessorConfigurations = configuredProcessorConfigurations;
		}

		public String getSchedule() {
			return schedule;
		}

		public void setSchedule(String schedule) {
			this.schedule = schedule;
		}
		
		public String getMessageId(){
			return messageId;
		}
		
		public void setMessageId(String messageId){
			this.messageId=messageId;
		}

		public List<ReportProcessorConfiguration> getAvailableProcessorConfigurations() {
			return availableProcessorConfigurations;
		}

		public void setAvailableProcessorConfigurations(List<ReportProcessorConfiguration> availableProcessorConfigurations) {
			this.availableProcessorConfigurations = availableProcessorConfigurations;
		}
	}
	
}
