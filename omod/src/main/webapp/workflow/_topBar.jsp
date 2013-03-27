<c:choose>
	<c:when test="${currentTask == 'retrospectiveEntry'}">
		<div class="partBar topBar">			
			<table style="width:95%; height:100%; margin-bottom: auto; margin-right: auto;">
				<tr class="whiteColor smallerFont">
					<td style="width:30%; padding: 0px 20px 0px 20px;">
						<spring:message code="haitimobileclinic.tasks.patientRegistration"/>
						<c:if test="${taskProgress.completedTasks['registrationTask'] ==1}">
							<img id="registrationCompleteImg" src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/checkmark-green.png">
						</c:if>
					</td>					
					<td style="width:30%; text-align: center;">
						<spring:message code="haitimobileclinic.tasks.primaryCareReception"/>
						<c:if test="${taskProgress.completedTasks['receptionTask'] ==1}">
							<img id="receptionCompleteImg" src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/checkmark-green.png">
						</c:if>
					</td>
					<td style="width:30%; text-align: right; padding: 0px 40px 0px 0px;">
						<spring:message code="haitimobileclinic.tasks.primaryCareVisit"/>
						<c:if test="${taskProgress.completedTasks['primaryCareVisitTask'] ==1}">
							<img id="primaryCareVisitCompleteImg" src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/checkmark-green.png">
						</c:if>
					</td>
				</tr>
				<tr>
					<td colspan="3" style="height:36px; width:100%; float:center; background : url('${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/${taskProgress.progressBarImage}') center center no-repeat;">					
					</td>
				</tr>
			</table>			
		</div>	
	</c:when>	
	<c:when test="${currentTask == 'mobileClinic'}">
		<div class="partBar topBar">			
			<table style="width:95%; height:100%; margin-bottom: auto; margin-right: auto;">
				<tr class="whiteColor smallerFont">
					<td style="width:50%; padding: 0px 20px 0px 20px;">
						<spring:message code="haitimobileclinic.tasks.patientRegistration"/>
						<c:if test="${taskProgress.completedTasks['registrationTask'] ==1}">
							<img id="registrationCompleteImg" src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/checkmark-green.png">
						</c:if>
					</td>					
					<td style="width:50%; text-align: right; padding: 0px 40px 0px 0px;">
						<spring:message code="haitimobileclinic.tasks.mobileClinicReception"/>
						<c:if test="${taskProgress.completedTasks['mobileClinicReception'] ==1}">
							<img id="primaryCareVisitCompleteImg" src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/checkmark-green.png">
						</c:if>
					</td>
				</tr>
				<tr>
					<td colspan="3" style="height:36px; width:100%; float:center; background : url('${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/${taskProgress.progressBarImage}') center center no-repeat;">					
					</td>
				</tr>
			</table>			
		</div>	
	</c:when>	
	<c:otherwise>
		<div class="partBar topBar">
			<c:if test="${!empty patient}">
				<table style="width:95%; height:60%; margin-left: auto; margin-right: auto;">
				<tr><td style="text-align:left;" >
					<b>
						<c:if test="${!empty patient.personName}">
							&nbsp;${patient.personName}
							<c:if test="${!empty patient.gender}">
								 (<spring:message code="haitimobileclinic.gender.${patient.gender}"/>)
							</c:if>					
						</c:if>
					</b>
					<openmrs:formatDate date="${patient.birthdate}" format="${_dateFormatDisplayDash}"/>
					<c:if test="${patient.birthdateEstimated == true}">
						(<spring:message code="haitimobileclinic.person.birthdate.estimated"/>)
					</c:if>
				</td>
			
				<td style="text-align:right;" >
					<b>
						<c:if test="${!scanIdCard == 'true'}">									
							<c:if test="${!empty preferredIdentifier}">
									${preferredIdentifier}&nbsp;
							</c:if>
						</c:if>
					</b>
				</td>
				</tr>
				</table>
			</c:if>
		</div>	
	</c:otherwise>											
</c:choose>
