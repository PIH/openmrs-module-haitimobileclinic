<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/haitimobileclinic/haitimobileclinicHeader.jsp"%>
<%@ include file="/WEB-INF/view/module/haitimobileclinic/details.jsp"%>


<openmrs:htmlInclude file="/moduleResources/haitimobileclinic/patientDashboard.js"/>

<script type="text/javascript"><!--
	var taskName="${task}";
	var nextTask="${nextTask}";
	var patient = "${patient}";
	var patientId ="${patient.id}";
	var firstNameVal = "${patient.givenName}";
	var lastNameVal = "${patient.familyName}";
	var genderVal = "${patient.gender}";
	var patientBirthdate = '<openmrs:formatDate date="${patient.birthdate}" format="${_dateFormatDisplayDash}"/>';
	
	var patientPreferredIdentifier = "${preferredIdentifier}";
	var cardCounterMessage = '<spring:message code="haitimobileclinic.idCardCounter"/>'; 
	var cardPrintedStatus = "${cardInfo.printingObs.valueText}";
	var cardPrintedLastDate="${cardInfo.lastPrintingDate}";
	var cardPrintedCounter = "${cardInfo.printingCounter}";
	var scanIdCard = "${scanIdCard}";
	var cardPrintedStatusDate = "${cardInfo.printingObs.obsDatetime}";	
	var statusLabel = '<spring:message code="haitimobileclinic.idCardPrinted"/>' + ":";
	var statusDateValue = '<haitimobileclinic:pocFormatDate date="${cardInfo.printingObs.obsDatetime}" format="${_dateFormatDisplayDash}"/>';
	var duplicatePatientsAlert='<spring:message code="haitimobileclinic.duplicatePatientsFound"/>'; 
	
	var patientIdLabel = 'patientId';
	var bornLabel = '<spring:message code="haitimobileclinic.person.birthdate"/>'; 
	var personDateCreatedLabel = '<spring:message code="haitimobileclinic.person.dateCreated"/>'; 
	var address1Label = '<spring:message code="haitimobileclinic.person.address.address1"/>'; 
	var cityVillageLabel = '<spring:message code="haitimobileclinic.person.address.cityVillage"/>'; 
	var zlEmrIdLabel = '<spring:message code="haitimobileclinic.patient.zlEmrId"/>'; 
	var dossierNumberLabel= '<spring:message code="haitimobileclinic.menu.numero"/>'; 	
	var firstEncounterDateLabel = '<spring:message code="haitimobileclinic.patient.firstEncounterDate"/>'; 	
	var dentalDossierTypeId="${dentalDossier.identifierType.id}"
	var mobileClinicDossierTypeId="${mobileClinicDossier.identifierType.id}"
		
	var duplicatePatientsData = [
		<c:forEach var="duplicatePatient" items="${duplicatePatients}" varStatus="i">
			{
				patientId : "${duplicatePatient.patientId}", 
				firstName : "${duplicatePatient.firstName}" ,
				lastName : "${duplicatePatient.lastName}" ,
				gender : "${duplicatePatient.gender}" ,
				birthdate : '<haitimobileclinic:pocFormatDate date="${duplicatePatient.birthdate}" format="${_dateFormatDisplayDash}"/>' ,
				personDateCreated : '<haitimobileclinic:pocFormatDate date="${duplicatePatient.personDateCreated}" format="${_dateFormatDisplayDash}"/>' ,
				address1 : "${duplicatePatient.address1}" ,
				cityVillage : "${duplicatePatient.cityVillage}" ,
				zlEmrId : "${duplicatePatient.zlEmrId}" ,
				dossierNumber : "${duplicatePatient.dossierNumber}" ,
				firstEncounterDate : '<haitimobileclinic:pocFormatDate date="${duplicatePatient.firstEncounterDate}" format="${_dateFormatDisplayDash}"/>' 
			}
			<c:if test="${!i.last}">,</c:if>  
		</c:forEach>
		];
	
	
	
</script>

<!-- SPECIALIZED STYLES FOR THIS PAGE -->
<style type="text/css">


</style>

<%@ include file="/WEB-INF/view/module/haitimobileclinic/workflow/_menuTopBar.jsp"%>
<%@ include file="/WEB-INF/view/module/haitimobileclinic/workflow/_topBar.jsp"%>
	
<div class="middleArea">
	<div class="menu" id="menuArea">
		<table class="menu">
			<tr>
				<th class="menu"><spring:message code="haitimobileclinic.patientDashboard.title" /></th>
			</tr>
			<tr>
				<td id="overviewMenu" class="menu highlighted"><spring:message code="haitimobileclinic.patientDashboard.overview" /></td>
			</tr>
			<tr>
				<td id="encountersMenu" class="menu"><spring:message code="haitimobileclinic.patientDashboard.encounters" /></td>
			</tr>
		</table>
	</div>
   
	<div class="partBar mainArea largeFont">
		<div id="overviewDashboardDiv" class="padded">
			<table id="overviewTable" width="100%">				
				<tr>
					<td width="50%" valign="top">
						<table width="100%">
							<tr>
								<td class="labelSmall">
									<spring:message code="haitimobileclinic.person.name"/>
								</td>
							</tr>
							<tr>
								<td class="questionBox" width="75%">
									${patient.personName}
								</td>
								<td width="5%">&nbsp;
								</td>
								<td class="leftalign" width="20%">									
									<button type="button" class="editPatientDemo editDemoDiv" id="lastNameDiv" />
								</td>
							</tr>
							<tr>
								<td class="labelSmall">
									<spring:message code="haitimobileclinic.gender"/>
								</td>
							</tr>
							<tr>
								<td class="questionBox" width="75%">
									${patient.gender}
								</td>
								<td width="5%">&nbsp;
								</td>
								<td class="leftalign" width="20%">								
									<button type="button" class="editPatientDemo editDemoDiv" id="genderDiv" />
								</td>
							</tr>
							<tr>
								<td class="labelSmall">
									<spring:message code="haitimobileclinic.person.birthdate"/>
								</td>
							</tr>
							<tr>
								<td class="questionBox" width="75%">
									<openmrs:formatDate date="${patient.birthdate}" format="${_dateFormatDisplayDash}"/>
									<c:if test="${patient.birthdateEstimated == true}">
										(<spring:message code="haitimobileclinic.person.birthdate.estimated"/>)
									</c:if>	
								</td>
								<td width="5%">&nbsp;
								</td>
								<td class="leftalign" width="20%">								
									<button type="button" class="editPatientDemo editDemoDiv" id="birthdateDiv" />
								</td>
							</tr>
							<tr>
								<td class="labelSmall">
									<spring:message code="haitimobileclinic.person.address"/>
								</td>
							</tr>
							<tr>
								<td class="questionBox" width="75%">
									<table>									
									<tr>										
										<td>
											<span class="labelVerySmall"><spring:message code="haitimobileclinic.person.address.address2"/>:</span>
										</td>	
										<td>
											<span>${patient.personAddress['address2']}</span><br>
										</td>											
									</tr>										
									</table>
								</td>
								<td width="5%">&nbsp;
								</td>
								<td class="leftalign" width="20%">									
									<button type="button" class="editPatientDemo editDemoDiv" id="addressLandmarkDiv" />
								</td>
							</tr>
							<tr>
								<td class="questionBox" width="75%">
									<table>
									<c:forEach var="addressLevel" items="${addressHierarchyLevels}">
										<c:if test="${addressLevel != 'address2'}">
										<tr>										
											<td>
												<span class="labelVerySmall"><spring:message code="haitimobileclinic.person.address.${addressLevel}"/>:</span>
											</td>	
											<td>
												<span>${patient.personAddress[addressLevel]}</span><br>
											</td>											
										</tr>	
										</c:if>
									</c:forEach>
									</table>
								</td>
								<td width="5%">&nbsp;
								</td>
								<td class="leftalign" width="20%">									
									<button type="button" class="editPatientDemo editDemoDiv" id="possibleLocalityDiv" />
								</td>
							</tr>
						</table>
					</td>					
					<td width="50%" valign="top">
						<table width="100%">
<%-- 							<form id="printIDCardForm" method="post">
 --%>							<tr>
								<td class="labelSmall">
									${preferredIdentifier.identifierType.name}
								</td>
							</tr>
							<tr>
								<td class="questionBox" width="60%">${preferredIdentifier}&nbsp;
								</td>
								<td width="3%">&nbsp;
								</td>
								<td width="17%" class="leftalign">									
									<button type="button" class="editPatientDemo" id="editMobileClinicDossier" />
								</td>
								<td width="3%">&nbsp;
								</td>
								<td width="17%" class="leftalign">
<%--  								<c:if test="${!empty preferredIdentifier.identifier}">
 									<button name="printIDCard" type="submit" class="printButton" />
 								</c:if>										
 --%> 								</td>
							</tr>
<%-- 							</form>
 --%><%--  							<form id="printLabelForm" method="post">
--%>							<c:if test="${!empty numeroDossier}">							
							<tr>
								<td class="labelSmall"> 
									${numeroDossier.identifierType.name}
								</td>
							</tr>
							<tr>
								<td class="questionBox" width="60%">
									<c:if test="${!empty numeroDossier.identifier}"> 
										${numeroDossier.identifier}&nbsp;<!-- (<spring:message code="haitimobileclinic.outpatient"/>) -->
									</c:if>		
								</td>
								<td width="3%">&nbsp;
								</td>
								<td width="17%" class="leftalign">									
									<button type="button" class="editPatientDemo" id="editDossier" />
								</td>
								<td width="3%">&nbsp;
								</td>
								<td width="17%" class="leftalign">																		
<!-- 									<button name="printDossierLabel" type="submit" class="printButton printDossier" />									
 -->								</td>
							</tr>
							</c:if>	
<!-- 							</form>
 --><%--  							<form id="printDentalLabelForm" method="post">
							<c:if test="${!empty dentalDossier}">							
							<tr>
								<td class="labelSmall">									
									<spring:message code="haitimobileclinic.menu.dentalDossier"/>
								</td>
							</tr>
							<tr>
								<td class="questionBox" width="60%">
									<c:if test="${!empty dentalDossier.identifier}"> 
										${dentalDossier.identifier}&nbsp;(<spring:message code="haitimobileclinic.dental"/>)
									</c:if>		
								</td>
								<td width="3%">&nbsp;
								</td>
								<td width="17%" class="leftalign">									
									<button type="button" class="editPatientDemo" id="editDentalDossier" />
								</td>
								<td width="3%">&nbsp;
								</td>
								<td width="17%" class="leftalign">																		
								</td>
							</tr>
							</c:if>
							</form>
 	 --%>						<openmrs:forEachDisplayAttributeType personType="patient" displayType="viewing" var="attrType">		
							<tr>
								<td class="labelSmall">
									<spring:message code="haitimobileclinic.${fn:replace(attrType.name, ' ', '')}" text="${attrType.name}"/>
								</td>
							</tr>
							<tr>
								<td class="questionBox" width="60%">
									${patient.attributeMap[attrType.name].hydratedObject}
								</td>
								<td width="3%">&nbsp;
								</td>
								<td class="leftalign" width="17%">									
									<button type="button" class="editPatientDemo editDemoDiv" id="phoneNumberDiv" />
								</td>
								<td width="3%">&nbsp;
								</td>
								<td width="17%">&nbsp;
								</td>
							</tr>
							</openmrs:forEachDisplayAttributeType>	
<%-- 							<tr>
								<td class="labelSmall">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td class="labelSmall">									
									<span id="lastStatusLabel" name="lastStatusLabel">
									</span>
									<br/>
									<span id="lastStatusDate" name="lastStatusDate">										
									</span>
								</td>
							</tr>
							<tr>
								<td>
									<img id="printedIdCard" align="left" src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/no-card-icon.png">									
								</td>								
							</tr>
							<tr>
								<td class="labelSmall">								
									<span id="printingCounterLabel" name="printingCounterLabel">
									</span>
								</td>
							</tr>
 --%>						</table>
					</td>
				</tr>
			</table>			
		</div>
		<div id="encounterDashboardDiv" class="padded hiddenDiv">
			<table class="patientDashboardTable">
				<tr>		
					<th><spring:message code="haitimobileclinic.patientDashboard.recentEncounters" />:</th>
				</tr>
				<tr>		
					<td style="vertical-align: top;"> 
						<table style="width:100%; cell-padding:10px;">
							<tr style="background-color: gray; color: white;">
								<th class="encounter"><spring:message code="haitimobileclinic.date" /></th>
								<th class="encounter"><spring:message code="haitimobileclinic.encounterType" /></th>
								<th class="encounter"><spring:message code="haitimobileclinic.location" /></th>
								<th class="encounter"><spring:message code="haitimobileclinic.createdBy" /></th>
							</tr>
							<openmrs:forEachEncounter encounters="${encounters}" sortBy="encounterDatetime" descending="true" var="enc">
								<tr class="<c:choose><c:when test="${count % 2 == 0}">alt0</c:when><c:otherwise>alt1</c:otherwise></c:choose>">									
									<td class="encounter">										
										<haitimobileclinic:pocFormatDate date="${enc.encounterDatetime}" format="${_dateFormatDisplayDash}"/>
									</td>						
									<td class="encounter">
										
										<c:if test="${!empty encounterLocale[enc.encounterType.id]}">
											<c:set var="locEncounterName" value="${encounterLocale[enc.encounterType.id]}"/>													
										</c:if>
										<c:choose>
											<c:when test="${!empty encounterLocale[enc.encounterType.id]}">
												<c:set var="locEncounterName" value="${encounterLocale[enc.encounterType.id]}"/>
											</c:when>	
											<c:otherwise>
												<c:set var="locEncounterName" value="${enc.encounterType.name}"/>												
											</c:otherwise>											
										</c:choose>
										<c:choose>
											<c:when test="${!empty editURLs[enc.encounterType.id]}">
											<a href='${pageContext.request.contextPath}/${editURLs[enc.encounterType.id]}?patientId=${patient.id}&encounterId=${enc.id}'>
												${locEncounterName}
											</a>
											</c:when>	
											<c:otherwise>
												${locEncounterName}
											</c:otherwise>											
										</c:choose>
										
									</td>
									<td class="encounter">${enc.location.name}</td>
									<td class="encounter">${enc.creator.personName}</td>						
								</tr>
							</openmrs:forEachEncounter>				
						</table>
					</td>
				</tr>
			</table>
		</div>
		<div id="scanIdCardDiv" name="scanIdCardDiv" class="padded hiddenDiv ajaxResultsDiv">						
			<table class="maxSize">
				<tr>
					<td>
						<b class="leftalign"><spring:message code="haitimobileclinic.scanIdCard"/></b>						
					</td>																	
				</tr>		
				<tr>
					<td>
						<img src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/scanCard.png"></img>						
					</td>																	
				</tr>		
				<tr>
					<td>
						<input id="scanPatientIdentifier" class="largeFont" style="height:30px; width:350px; font-size:25px" AUTOCOMPLETE='OFF' 
						name="scanPatientIdentifier" value=""/>
					</td>
				</tr>
			</table>	
			<div id="scanBtnDiv" name="scanBtnDiv" class="partBar">
				<table class="maxSize">
					<tr class="centered">
						<td width="50%">
							&nbsp;
						</td>
						<td width="30%">
							<button id="brokenPrinterBtn" name="brokenPrinterBtn" type="button" class="unknownBirthdate">
								<spring:message code="haitimobileclinic.brokenPrinter"/>
							</button>
						</td>
						<td width="20%">
							<form id="reprintIDCardForm" method="post">
								<button id="reprintIDCardBtn" name="printIDCard" type="submit" class="unknownBirthdate">
									<spring:message code="haitimobileclinic.reprintIDCard"/>
								</button>
							</form>	
						</td>
					</tr>
				</table>
			</div>
		</div>
		<div id="dialog-confirm" name="dialog-confirm" title='<spring:message code="haitimobileclinic.changePrintingStatus"/>?' class="padded hiddenDiv">	
			<table class="maxSize">							
				<tr>
					<td>
						<b><span style="text-align:center;"><spring:message code="haitimobileclinic.doesHaveCard"/>?</span></b>
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
				<div id="confirmExistingPatientModalDiv" title='<spring:message code="haitimobileclinic.duplicatePatients"/>'>
					<table>							
						<tr>
							<td>
								<spring:message code="haitimobileclinic.youHaveEntered"/>
								<br>
								<b><span id="modalPatientName" name="modalPatientName"></span></b>,
								&nbsp;&nbsp;<spring:message code="haitimobileclinic.person.birthdate"/>:
								<b><span id="modalPatientGenderDOB" name="modalPatientGenderDOB"></span></b>
								<br>
								<spring:message code="haitimobileclinic.duplicatePatients"/>
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
					<div id="confirmPatientModalDiv" title="Similar patients">
					</div>	
				</div>
			</div>
		</div>
		<div id="dialog-removeDuplicate" name="dialog-removeDuplicate" title='<spring:message code="haitimobileclinic.notDuplicate"/>?' class="padded hiddenDiv">	
			<table class="maxSize">							
				<tr>
					<td>
						<b><span style="color:black; text-align:center;"><spring:message code="haitimobileclinic.removeDuplicate"/>?</span></b>
					</td>
				</tr>
			</table>	
		</div>
		<div id="dialog-confirmDuplicate" name="dialog-confirmDuplicate" title='<spring:message code="haitimobileclinic.confirmDuplicate"/>?' class="padded hiddenDiv">	
			<table class="maxSize">							
				<tr>
					<td>
						<b><span style="color:black; text-align:center;"><spring:message code="haitimobileclinic.addDuplicate"/>?</span></b>
					</td>
				</tr>
			</table>	
		</div>
	</div>
</div>


<%@ include file="/WEB-INF/view/module/haitimobileclinic/workflow/_menuBottomBar.jsp"%>
<%@ include file="/WEB-INF/view/module/haitimobileclinic/workflow/_bottomBar.jsp"%>
