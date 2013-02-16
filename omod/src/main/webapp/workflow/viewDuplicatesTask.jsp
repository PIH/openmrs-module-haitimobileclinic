<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/haitimobileclinic/haitimobileclinicHeader.jsp"%>
<%@ include file="/WEB-INF/view/module/haitimobileclinic/details.jsp"%>

<openmrs:htmlInclude file="/moduleResources/haitimobileclinic/viewDuplicatesTask.js"/>
<script type="text/javascript">
"${pocDuplicates}";
	var pocDuplicates = [
		<c:forEach var="pocDuplicate" items="${pocDuplicates}" varStatus="j">
			{
				id: "${pocDuplicate.id}",
				identifiers: "${pocDuplicate.identifiers}",
				personName: "${pocDuplicate.personName}",
				firstName: "${pocDuplicate.givenName}", 
				lastName: "${pocDuplicate.familyName}", 
				age: "${pocDuplicate.age}",
				gender: "${pocDuplicate.gender}",
				birthdate: '<haitimobileclinic:pocFormatDate date="${pocDuplicate.birthdate}" format="${_dateFormatDisplayDash}"/>'
			}
			<c:if test="${!j.last}">,</c:if> 
		</c:forEach>
	
	];

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
	<div class="partBar mainArea largeFont">
		<table height="100%" width="100%">
			<tr>
				<td class="mergeResultsCell">		
					<div id="duplicatesDiv"  name="duplicatesDiv" class="padded" style="overflow: auto; height: 300px">
						<table id="duplicateTableListId" class="duplicateTableList patientDashboardTable" style="cell-padding:10px;">
							<tr style="background-color: gray; color: white;">
								<th class="encounter"></th>
								<th class="encounter"><spring:message code="haitimobileclinic.patientId" /></th>
								<th class="encounter"><spring:message code="haitimobileclinic.person.firstName" /></th>
								<th class="encounter"><spring:message code="haitimobileclinic.person.lastName" /></th>	
								<th class="encounter"><spring:message code="haitimobileclinic.gender" /></th>
								<th class="encounter"><spring:message code="haitimobileclinic.person.birthdate" /></th>								
							</tr>
						</table>
					</div>
				</td>
			</tr>
			<tr>
				<td align="left" valign="top">
					<button id="mergeBtn" name="mergeBtn" type="button" class="unknownBirthdate">
							<span class="largeFont"><spring:message code="haitimobileclinic.mergePatients"/></span>
					</button>
				</td>
			</tr>
		</table>	
				
	</div>	
</div>

<%@ include file="/WEB-INF/view/module/haitimobileclinic/workflow/_menuBottomBar.jsp"%>
<%@ include file="/WEB-INF/view/module/haitimobileclinic/workflow/_bottomBar.jsp"%>	

<%@ include file="/WEB-INF/view/module/haitimobileclinic/haitimobileclinicFooter.jsp"%>
