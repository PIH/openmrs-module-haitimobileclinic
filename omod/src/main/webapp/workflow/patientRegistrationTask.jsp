<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/haitimobileclinic/haitimobileclinicHeader.jsp"%>
<%@ include file="/WEB-INF/view/module/haitimobileclinic/details.jsp"%>

<openmrs:htmlInclude file="/moduleResources/haitimobileclinic/patientRegistrationTask.js"/>
<script type="text/javascript">
	var nextTask = "${nextTask}";
</script>

<%@ include file="/WEB-INF/view/module/haitimobileclinic/workflow/_menuTopBar.jsp"%>
<%@ include file="/WEB-INF/view/module/haitimobileclinic/workflow/_topBar.jsp"%>		

<div class="middleArea">
	<div class="menu" id="menuArea">
		<table class="menu">
			<tr>
				<th class="menu"><spring:message code="haitimobileclinic.tasks.${registration_task}"/></th>
			</tr>
		</table>
	</div>
	</div>
	<div class="partBar mainArea largeFont">
		<table height="100%" width="100%">
			<tr>
				<td align="center" valign="center">
					
					<div>
						<table align="center">	
							<tr>
								<td align="left">
									<b>
										<spring:message code="haitimobileclinic.findPatientByIdentifier"/>
									</b>
								</td>	
							</tr>
							<tr>
								<td align="left">
									<input id="patientIdentifier" class="largeFont" style="height:45px; width:450px; font-size:30px" AUTOCOMPLETE='OFF'
									name="patientIdentifier" value="${patientIdentifier}"/>
								</td>
							</tr>
							<tr style="height:100px">
								<td align="center">
									<spring:message code="haitimobileclinic.or"/>
								</td>
							</tr>
							<tr>
								<td align="left">
									<button id="searchByNameBtn" type="button" style="height: 45px; width: 450px; font-size:25px; font-weight:bold;" >
										<span class="largeFont"><spring:message code="haitimobileclinic.lookUpByName"/></span>
									</button>
								</td>
							</tr>
						</table>
					</div>
					
				</td>
			</tr>
		</table>		
	</div>
	<div id="messageArea" class="hiddenDiv">
		<!-- displays alert messages -->
		<div id="matchedPatientDiv" name="matchedPatientDiv" class="matchedPatientClass" style="visibility:hidden">
			<div id="confirmExistingPatientDiv">
				<table class="confirmExistingPatientList searchTableList">
				</table>
			</div>
			<div id="confirmExistingPatientModalDiv" title='<spring:message code="haitimobileclinic.similarPatients"/>'>
				<table width="100%">							
					<tr>
						<td>
							<spring:message code="haitimobileclinic.youHaveEntered"/>
							<br>
							<b><span id="fieldInput" name="fieldInput"></span></b>	
							<br>
							<br>
							<spring:message code="haitimobileclinic.similarPatientsFound"/>
						</td>
					</tr>
					<tr>												
						<td style="text-align:left;border:solid 1px;">
							<div id="overflowDiv" style="overflow: auto;">
								<table class="confirmExistingPatientModalList searchTableList">
								</table>
							</div>
						</td>
					</tr>					
				</table>	
				<div id="confirmPatientModalDiv" title="<spring:message code="haitimobileclinic.confirmPatient"/>">
				</div>	
			</div>
		</div>
	</div>
</div>

<%@ include file="/WEB-INF/view/module/haitimobileclinic/workflow/_menuBottomBar.jsp"%>
<%@ include file="/WEB-INF/view/module/haitimobileclinic/workflow/_bottomBar.jsp"%>	

<%@ include file="/WEB-INF/view/module/haitimobileclinic/haitimobileclinicFooter.jsp"%>
