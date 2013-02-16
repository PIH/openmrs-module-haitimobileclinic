<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/haitimobileclinic/haitimobileclinicHeader.jsp"%>
<%@ include file="/WEB-INF/view/module/haitimobileclinic/details.jsp"%>

<openmrs:htmlInclude file="/moduleResources/haitimobileclinic/patientDemo.js"/>

<!-- JQUERY FOR THIS PAGE -->
<script type="text/javascript">
	var nextTask = "${nextTask}";
	var currentTask = "${currentTask}";
	var maleGender = '<spring:message code="haitimobileclinic.gender.M"/>';
	var femaleGender = '<spring:message code="haitimobileclinic.gender.F"/>';
	var birthdateLabel = '<spring:message code="haitimobileclinic.person.birthdate"/>' + ":" ;
	var ageEstimateLabel = '<spring:message code="haitimobileclinic.ageEstimate"/>' + ":" ;
	var estimateYearsLabel = '<spring:message code="haitimobileclinic.years"/>';
	var estimateMonthsLabel = '<spring:message code="haitimobileclinic.months"/>';
	var similarAlert='<spring:message code="haitimobileclinic.similarAlert"/>';
	var similarExactAlert='<spring:message code="haitimobileclinic.similarExactAlert"/>';
	var similarSoundexAlert='<spring:message code="haitimobileclinic.similarSoundexAlert"/>';
	var similarFilterAlert='<spring:message code="haitimobileclinic.similarFilterAlert"/>';
	var addressNotKnownLabel= '<spring:message code="haitimobileclinic.person.addressNotFound"/>';
	var leavePageAlert = '<spring:message code="haitimobileclinic.alert.leavePageConfirmation"/>';
	var monthData = [<c:forEach begin="1" end="12" varStatus="i">'<spring:message code="haitimobileclinic.month.${i.count}"/>',</c:forEach>];
	var firstNameVal='';
	var lastNameVal='';	
	var phoneNumber='';
	var patientId = '';
	var editDivId="${editDivId}";
	patientId="${patient.id}";
	var editPatientIdentifier = "${patientPreferredIdentifier}";
	if(patientId.length>0){
		firstNameVal= "${patient.givenName}"; 
		lastNameVal= "${patient.familyName}"; 
		<openmrs:forEachDisplayAttributeType personType="patient" displayType="viewing" var="attrType">
			phoneNumber = '${patient.attributeMap[attrType.name].hydratedObject}';
		</openmrs:forEachDisplayAttributeType>		
	}
	var genderVal = "${patient.gender}"; 
	var patientBirthdate= '<openmrs:formatDate date="${patient.birthdate}" format="${_dateFormatInput}"/>';	
	var birthdateDay=0;
	var birthdateMonth='';
	var birthdateMonthId=0;
	var birthdateYear=0;
	if(patientBirthdate.length>1){
		var elements = patientBirthdate.split("/");
		if(elements.length ==3){
			birthdateDay = elements[0];
			birthdateMonthId = elements[1];
			birthdateYear = elements[2];
			birthdateMonthId = parseInt(birthdateMonthId, 10) -1;
			birthdateMonth = monthData[birthdateMonthId];
		}
	}
	var personAddress='';
	var iterator=0;
	var patientAddress = "${patient.personAddress}";
	if(patientAddress.length>0){
		<c:forEach var="addressLevel" items="${addressHierarchyLevels}">									
			if(iterator==0){
				personAddress = personAddress + "${patient.personAddress[addressLevel]}";		
			}else{			
				personAddress = personAddress + "," + "${patient.personAddress[addressLevel]}";	
			}				
			iterator = iterator + 1;
		</c:forEach>
		console.log("enterPatientDemo.jsp:personAddress=" + personAddress);		
		
	}
	var departmentsData= new Array();
	var communeData= new Array();
	var sectionCommuneData= new Array();
	var localitieData= new Array();
	
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
			<th class="menu"><spring:message code="haitimobileclinic.lookUpByName"/></th>
		</tr>
		<tr>
			<td class="menu highlighted" id="nameMenu"><spring:message code="haitimobileclinic.person.name"/></td>
		</tr>
		<tr>
			<td class="menu" id="genderMenu"><spring:message code="haitimobileclinic.gender"/></td>
		</tr>
		<tr>
			<td class="menu" id="ageMenu"><spring:message code="haitimobileclinic.age"/></td>
		</tr>
		<tr>
			<td class="menu" id="addressMenu"><spring:message code="haitimobileclinic.person.address"/></td>
		</tr>
		<tr>
			<td class="menu" id="cellPhoneMenu"><spring:message code="haitimobileclinic.person.cellPhone"/></td>
		</tr>
		<tr>
			<td class="menu" id="confirmMenu"><spring:message code="haitimobileclinic.person.confirm"/></td>
		</tr>
<!-- 		<tr>
			<td class="menu" id="printIdCardMenu"><spring:message code="haitimobileclinic.menu.printIdCard"/></td>
		</tr>
 -->	</table>
	</div>

	<div class="partBar mainArea largeFont">
	
		<form id="patientNameSearch" method="post">	
			<input type="hidden" id="searchFieldName" name="searchFieldName" value="" />
			<input type="hidden" id="resultsCounter" name="resultsCounter" value="8" />
		</form>	
		
		<div id="firstNameDiv" name="firstNameDiv" class="firstNameClass padded hiddenDiv">					
			<table height="100%" width="100%">												
				<tr>
					<td>
						<b class="leftalign"><spring:message code="haitimobileclinic.person.enterFirstName"/></b>
					</td>											
				</tr>	
				<tr>
					<td>
						<input class="inputField highlighted" type="text" id="patientInputFirstName" name="patientInputFirstName" value="" style="width:95%;" AUTOCOMPLETE='OFF'/>
						<img class="cross-black" src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/cross-black.png" title='<spring:message code="haitimobileclinic.clearEntry"/>'></img>
					</td>
				</tr>
				<tr>
					<td class="ajaxResultsCell">	
						<div id="firstNameTableDiv" class="ajaxResultsDiv">
							<table class="patientFirstNameList tableList" width="100%">								
							</table>
						</div>
					</td>						
				</tr>
			</table>
		</div>
		
		<div id="lastNameDiv" name="lastNameDiv" class="lastNameClass padded">
			<table width="100%">												
				<tr>
					<td>
						<b class="leftalign"><spring:message code="haitimobileclinic.person.enterLastName"/></b>
					</td>											
				</tr>	
				<tr>
					<td>
						<input class="inputField" type="text" id="patientInputLastName" name="patientInputLastName" value=""  style="width:95%;" AUTOCOMPLETE='OFF'/>
						<img class="cross-black" src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/cross-black.png"></img>
					</td>
				</tr>
				<tr>
					<td class="ajaxResultsCell">	
						<div id="lastNameTableDiv" class="ajaxResultsDiv">
							<table class="patientLastNameList tableList" width="100%">								
							</table>
						</div>
					</td>						
				</tr>
			</table>
		</div>	
		
		<div id="genderDiv" name="genderDiv" class="padded hiddenDiv">
			<table width="100%">
				<tr>
					<td align="left" style="width:100%;">
						<b><spring:message code="haitimobileclinic.person.enterGender"/></b>
					</td>											
				</tr>	
				
				<tr align="left" valign="top" style="height:330px;">
					<td>
						<table align="left" class="none" cellspacing="0">									
							<tr id="rdioTrM" name="rdioTrM" class="RadioClass">
								<td style="height : 50px;">
									<span><spring:message code="haitimobileclinic.gender.M"/></span>
								</td>	
								<td style="height : 50px;">
									 <input type="radio" name="rdio" id="rdioM" value="M" class="radioClass"/><br>
								</td>
							</tr>
							<tr id="rdioTrF" name="rdioTrF">
								<td style="height : 50px;">
									<span><spring:message code="haitimobileclinic.gender.F"/></span>
								</td>	
								<td style="height : 50px;">
									<input type="radio" name="rdio" id="rdioF" value="F" class="radioClass"/><br>
								</td>
							</tr>
						</table>
					</td>							
				</tr>
			</table>
		</div>
		
		<div id="birthdateDiv" name="birthdateDiv" class="birthdateClass padded hiddenDiv">
			<table style="width: 90%; cell-padding: 20px;">
				<tr>
					<td align="left"><b><spring:message
						code="haitimobileclinic.person.enterBirthdate" /></b></td>
			
					<td align="right">
						<button id="unknownBirthdate" type="button" class="unknownBirthdate">
							<span class="largeFont"><spring:message code="haitimobileclinic.person.birthdateUnknown"/></span>
						</button> 
					</td>
				</tr>
				<tr>
					<td align="left" colspan="2" height="20px"></td>
				</tr>
			</table>

			<table style="width: 90%;">				
				<tr align="left" valign="bottom" style="text-align:left;">	
					<td colspan="4">
						<c:if test="${fn:length(birthdateErrors.allErrors) > 0}">
							<c:forEach var="error" items="${birthdateErrors.allErrors}">
								<span class="error"><spring:message code="${error.code}"/></span>										
							</c:forEach>
							<br/>
						</c:if>
					</td>					
				</tr>					
				<tr>
					<td style="width:20%;">
						<input id="day" name="day" class="birthdateField inputField" AUTOCOMPLETE='OFF' value="${birthdate.day}" />
					</td>
					<td style="width:45%;">
						<input id="monthAutocomplete" class="birthdateField inputField" />																		
					</td>
					<td style="width:25%;" align="left">							
						<input id="year" name="year" class="birthdateField inputField" AUTOCOMPLETE='OFF' value="${birthdate.year}" />
					</td>
					<td>
						<button type="button" class="NavigationButton"  style="visibility: hidden;">
							<spring:message code="haitimobileclinic.cancel"/>
						</button>
					</td>
				</tr>
				<tr font-size="small">
					<td>
						<spring:message code="haitimobileclinic.day.long"/>
					</td>
					<td>
						&nbsp;
					</td>
					<td>
						<spring:message code="haitimobileclinic.year.long"/>
					</td>
					<td>
						<button type="button" class="NavigationButton"  style="visibility: hidden;">
							<spring:message code="haitimobileclinic.cancel"/>
						</button>
					</td>
				</tr>	
			</table>			
		</div>	
		
		<div id="ageEstimateDiv" name="ageEstimateDiv" class="birthdateClass padded hiddenDiv">					
			<table>
				<tr>
					<td style="text-align:left;">
						<b><spring:message code="haitimobileclinic.ageEstimate"/></b>
					</td>
					<td style="text-align:right;">
						<button id="birthdateEnter" type="button" class="unknownBirthdate">
							<span class="largeFont"><spring:message code="haitimobileclinic.birthdate.enterBirthdate"/></span>
						</button> 						
					</td>											
				</tr>
				<tr>
					<td align="left" colspan="2" height="20px"></td>
				</tr>
				<tr style="text-align:left; vertical-align:bottom">	
					<td>
						<c:if test="${fn:length(birthdateErrors.allErrors) > 0}">
							<c:forEach var="error" items="${birthdateErrors.allErrors}">
								<span class="error"><spring:message code="${error.code}"/></span>										
							</c:forEach>
							<br/>
						</c:if>
					</td>					
				</tr>					
				<tr style="text-align:left; vertical-align:top">
					<td>
						<input id="estimateYears" name="years" class="birthdateField inputField" AUTOCOMPLETE='OFF' value="${age.years}" />
					</td>							
					<td>							
						<input id="estimateMonths" name="months" class="birthdateField inputField" AUTOCOMPLETE='OFF' value="${age.months}"/>
					</td>
				</tr>	
				<tr font-size="small" style="text-align:center; vertical-align:bottom">
					<td>
						<spring:message code="haitimobileclinic.years"/>
					</td>
					<td>
						<spring:message code="haitimobileclinic.months"/>
					</td>							
				</tr>		
			</table>					
		</div>
		<div id="addressLandmarkDiv" name="addressLandmarkDiv" class="padded hiddenDiv">					
			<table width="100%">
				<tr>
					<td>
						<table width="95%">
							<tr>
								<td align="left">
									<b>
										<spring:message code="haitimobileclinic.person.address.enterLandmark"/>
									</b>
								</td>								
							</tr>
						</table>
					</td>
				</tr>								
				<tr>
					<td align="left" style="width:95%; height:30px">
						<div id="addressLandmarkTableDiv" style="overflow: auto; height: 300px; width:95%;">
							<input id="addressLandmarkField" class="inputField" style="width:95%;"/>
							<img class="cross-black" src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/cross-black.png"></img>	
						</div>
					</td>
				</tr>							
			</table>					
		</div>
		<div id="possibleLocalityDiv" name="possibleLocalityDiv" class="padded hiddenDiv">					
			<table width="100%">
				<tr>
					<td>
						<table width="95%">
							<tr>
								<td align="left">
									<b>
										<spring:message code="haitimobileclinic.person.address.enterLocalitie"/>
									</b>
								</td>								
							</tr>
						</table>
					</td>
				</tr>								
				<tr>
					<td align="left" style="width:95%; height:30px">					
						<input id="possibleLocalityField" class="inputField" style="width:95%;"/>
						<img class="cross-black" src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/cross-black.png"></img>						
					</td>
				</tr>	
				<tr>
					<td class="ajaxResultsCell">	
						<div id="possibleLocalityTableDiv" class="ajaxResultsDiv">
							<table class="tableList" id="possibleLocalityList" width="100%">								
							</table>
						</div>
					</td>						
				</tr>
				<tr>
					<td>
						<div id="confirmPossibleLocalityModalDiv" title="" class="hiddenDiv">
							<table width="100%">							
								<tr>
									<td>
										<spring:message code="haitimobileclinic.person.address.locality"/>:&nbsp										
										<b><span id="possibleLocalityEntered" name="possibleLocalityEntered"></span></b>	
										<br>
										<br>
										<spring:message code="haitimobileclinic.person.address.selectAddress"/>:
									</td>
								</tr>
								<tr>												
									<td style="text-align:left;border:solid 1px;">
										<div id="overflowPossibleLocalityDiv" style="overflow: auto; width:100%" >
											<table id="confirmPossibleLocalityModalList" class="searchTableList">
											</table>
										</div>
									</td>
								</tr>					
							</table>		
						</div>
					</td>
				</tr>
			</table>					
		</div>
		
		<div id="addressDepartmentDiv" name="addressDepartmentDiv" class="padded hiddenDiv">					
			<table width="100%">
				<tr>
					<td>
						<table width="95%">
							<tr>
								<td align="left">
									<b>
										<spring:message code="haitimobileclinic.person.address.enterDepartment"/>
									</b>
								</td>								
							</tr>
						</table>
					</td>
				</tr>								
				<tr>
					<td align="left" style="width:95%; height:30px">
						<div id="addressDepartmentTableDiv" style="overflow: auto; height: 300px; width:95%;">
							<input id="addressDepartmentAutocomplete" class="inputField" style="width:95%;"/>
							<img class="cross-black" src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/cross-black.png"></img>	
						</div>
					</td>
				</tr>							
			</table>					
		</div>
		
		<div id="addressCommuneDiv" name="addressCommuneDiv" class="padded hiddenDiv">					
			<table width="100%">
				<tr>
					<td>
						<table width="95%">
							<tr>
								<td align="left">
									<b>
										<spring:message code="haitimobileclinic.person.address.enterCommune"/>
									</b>
								</td>								
							</tr>
						</table>
					</td>
				</tr>								
				<tr>
					<td align="left" style="width:95%; height:30px">
						<div id="addressCommuneTableDiv" style="overflow: auto; height: 300px; width:95%;">
							<input id="addressCommuneAutocomplete" class="inputField" style="width:95%;"/>
							<img class="cross-black" src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/cross-black.png"></img>	
						</div>
					</td>
				</tr>							
			</table>					
		</div>
		
		<div id="addressSectionCommuneDiv" name="addressSectionCommuneDiv" class="padded hiddenDiv">					
			<table width="100%">
				<tr>
					<td>
						<table width="95%">
							<tr>
								<td align="left">
									<b>
										<spring:message code="haitimobileclinic.person.address.enterSectionCommune"/>
									</b>
								</td>
							</tr>
						</table>
					</td>
				</tr>								
				<tr>
					<td align="left" style="width:95%; height:30px">
						<div id="addressSectionCommuneTableDiv" style="overflow: auto; height: 300px; width:95%;">
							<input id="addressSectionCommuneAutocomplete" class="inputField" style="width:95%;"/>
							<img class="cross-black" src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/cross-black.png"></img>	
						</div>
					</td>
				</tr>							
			</table>					
		</div>
		<div id="addressLocalitieDiv" name="addressLocalitieDiv" class="padded hiddenDiv">					
			<table width="100%">
				<tr>
					<td>
						<table width="95%">
							<tr>
								<td align="left">
									<b>
										<spring:message code="haitimobileclinic.person.address.enterLocalitie"/>
									</b>
								</td>								
							</tr>
						</table>
					</td>
				</tr>								
				<tr>
					<td align="left" style="width:95%; height:30px">
						<div id="addressLocalitieTableDiv" style="overflow: auto; height: 300px; width:95%;">
							<input id="addressLocalitieAutocomplete" class="inputField" style="width:95%;"/>
							<img class="cross-black" src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/cross-black.png"></img>	
						</div>
					</td>
				</tr>							
			</table>					
		</div>
		<div id="phoneNumberDiv" name="phoneNumberDiv" class="padded hiddenDiv">					
			<table width="100%">
				<tr>
					<td>
						<b class="leftalign"><spring:message code="haitimobileclinic.person.enterCellPhone"/></b>
					</td>																	
				</tr>
				<tr align="left" valign="bottom" style="text-align:left;">								
					<td>
						<c:if test="${fn:length(phoneNumberErrors.allErrors) > 0}">
							<c:forEach var="error" items="${phoneNumberErrors.allErrors}">
								<span class="error"><spring:message code="${error.code}"/></span>										
							</c:forEach>
							<br/>
						</c:if>
					</td>													
				</tr>					
				
				<tr>
					<td>
						<input class="inputField" type="text" id="patientInputPhoneNumber" name="patientInputPhoneNumber" value="" AUTOCOMPLETE='OFF' style="width:95%"/>
						<img class="cross-black" src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/cross-black.png"></img>
					</td>									
				</tr>							
			</table>					
		</div>
		
		<div id="confirmDiv" name="confirmDiv" class="confirmClass padded hiddenDiv">
			<form id="patientSearch" name="patientSearch" method="post">
				<input type="hidden" id="givenName" name="givenName" value="" />
				<input type="hidden" id="familyName" name="familyName" value="" />
			</form>
			<form id="confirmPatientInfoForm" name="confirmPatientInfoForm" method="post">
			    <input type="hidden" id="hiddenNextTask" name="hiddenNextTask" value="" />
				<table style="margin-left:5px;" width="95%">
					<tr>
						<td align="left" style="width:100%;">
							<b><spring:message code="haitimobileclinic.confirmPatientDetails"/></b>
						</td>											
					</tr>	
					<tr align="left" valign="bottom" style="text-align:left;">	
						<td>
							<c:if test="${fn:length(birthdateErrors.allErrors) > 0}">
								<c:forEach var="error" items="${birthdateErrors.allErrors}">
									<span class="error"><spring:message code="${error.code}"/></span>										
								</c:forEach>
								<br/>
							</c:if>
						</td>					
					</tr>				
					<tr align="left" valign="top" style="text-align:left;height:250px">
						<td>
							<table>
								<tr>
									<td style="width:200px;">										
										<spring:message code="haitimobileclinic.person.firstName"/>:
									</td>										
									<td>
										<b>
										<span id="confirmFirstName" name="confirmFirstName"></span>
										</b>
										<input type="hidden" id="hiddenConfirmFirstName" name="hiddenConfirmFirstName" value="" />
									</td>
								</tr>
								<tr>
									<td style="width:200px;">										
										<spring:message code="haitimobileclinic.person.lastName"/>:
									</td>
									<td>
										<b>
										<span id="confirmLastName" name="confirmLastName"></span>
										</b>
										<input type="hidden" id="hiddenConfirmLastName" name="hiddenConfirmLastName" value="" />
									</td>
								</tr>
								<tr>
									<td style="width:200px;">										
										<spring:message code="haitimobileclinic.gender"/>:
									</td>
									<td>
										<b>
										<span id="confirmGender" name="confirmGender"></span>
										</b>
										<input type="hidden" id="hiddenConfirmGender" name="hiddenConfirmGender" value="" />																								
									</td>
								</tr>
								<tr>
									<td style="width:200px;">	
										<span id="confirmBirthdateLabel" name="confirmBirthdateLabel"></span>												
									</td>
									<td>
										<b>
										<span id="confirmBirthdate" name="confirmBirthdate"></span>
										</b>
										<input type="hidden" id="hiddenConfirmDay" name="day" class="birthdateField" type="number" value="${birthdate.day}"/>
										<input type="hidden" id="hiddenConfirmMonth" name="month" class="birthdateField"  value="${birthdate.month}"/>
										<input type="hidden" id="hiddenConfirmYear" name="year" class="birthdateField" type="number" value="${birthdate.year}"/>
										<input type="hidden" id="hiddenConfirmEstimateYears" name="years" class="birthdateField" type="number" value="${age.years}"/>
										<input type="hidden" id="hiddenConfirmEstimateMonths" name="months" class="birthdateField" type="number" value="${age.months}"/>											
										<input type="hidden" id="hiddenPrintIdCard" name="hiddenPrintIdCard" value="no"/>											
									</td>
								</tr>
								<tr>
									<td class="confirmColumn"  valign="top">										
										<spring:message code="haitimobileclinic.person.address"/>
									</td>
									<td>												
										<input type="hidden" id="hiddenPatientAddress" name="hiddenPatientAddress" class="address" value="${patient.personAddress}"/>									
									</td>
								</tr>
								<tr>
									<td valign="top">										
										<spring:message code="haitimobileclinic.person.address.address2"/>:
									</td>
									<td>																						
										<b>
										<span id="confirmAddress0" name="confirmAddress0"></span><br>										
										</b>
									</td>
								</tr>
								<tr>
									<td valign="top">										
										<spring:message code="haitimobileclinic.person.address.address1"/>:
									</td>
									<td>																						
										<b>
										<span id="confirmAddress1" name="confirmAddress1"></span><br>										
										</b>
									</td>
								</tr>
								<tr>
									<td valign="top">										
										<spring:message code="haitimobileclinic.person.address.neighborhoodCell"/>:
									</td>
									<td>																						
										<b>
										<span id="confirmAddress2" name="confirmAddress2"></span><br>										
										</b>
									</td>
								</tr>
								<tr>
									<td valign="top">									
										<spring:message code="haitimobileclinic.person.address.cityVillage"/>:
									</td>
									<td>																						
										<b>
										<span id="confirmAddress3" name="confirmAddress3"></span><br>										
										</b>
									</td>
								</tr>
								<tr>
									<td valign="top">									
										<spring:message code="haitimobileclinic.person.address.stateProvince"/>:
									</td>
									<td>																						
										<b>
										<span id="confirmAddress4" name="confirmAddress4"></span><br>										
										</b>
									</td>
								</tr>
								<tr>
									<td valign="top">									
										<spring:message code="haitimobileclinic.person.address.country"/>:
									</td>
									<td>																						
										<b>
										<span id="confirmAddress5" name="confirmAddress5"></span><br>										
										</b>
									</td>
								</tr>								
								<tr>
									<td style="width:200px;">										
										<spring:message code="haitimobileclinic.person.cellPhone"/>:
									</td>
									<td>
										<b>
										<span id="confirmPhoneNumber" name="confirmPhoneNumber"></span>
										<input type="hidden" id="hiddenConfirmPhoneNumber" name="hiddenConfirmPhoneNumber" value="" />
										</b>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</form>
		</div>
		
<%-- 		<div id="confirmPrintDiv" name="confirmPrintDiv" class="padded hiddenDiv">					
			<table class="maxSize">
				<tr>
					<td>						
						<div id="locationDiv" class="selectLocationClass">
						<table align="center" width="100%">
							<tr>
								<td align="left" style="padding: 5px">
									<b class="leftalign"><spring:message code="haitimobileclinic.confirmPrintCard"/></b>
								</td>
							</tr>
							<br />
							<tr>
								<td align="left">
								<table width="100%" style="border: solid 1px;">
									<tr>
										<td align="left" style="padding: 0px">
										<table class="yesNoList" width="100%">								
												<tr id="yesRow" name="yesRow" class="yesNoListRow">													
													<td id="printYes" name="printYes" style="padding: 5px">
													<spring:message code="haitimobileclinic.yes"/>
													</td>													
												</tr>
												<tr  id="noRow" name="noRow" class="yesNoListRow">													
													<td  id="printNo" name="printNo" style="padding: 5px">
													<spring:message code="haitimobileclinic.no"/>
													</td>													
												</tr>												
										</table>
										</td>
									</tr>
								</table>
								</td>
							</tr>
						</table>
					</div>
					</td>																	
				</tr>											
			</table>					
		</div>
		<div id="printIdCardDiv" name="printIdCardDiv" class="padded hiddenDiv ajaxResultsDiv">					
			<table width="100%" height="100%">
				<tr>
					<td>
						<b class="leftalign"><spring:message code="haitimobileclinic.waitIdCard"/></b>
					</td>																	
				</tr>		
				<tr>
					<td>
						<img src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/biggerloader.gif"></img>
					</td>																	
				</tr>										
			</table>					
		</div>
 --%><%-- 		<div id="scanIdCardDiv" name="scanIdCardDiv" class="padded hiddenDiv ajaxResultsDiv">						
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
							<button id="reprintIDCardBtn" name="reprintIDCardBtn" type="button" class="unknownBirthdate">
								<spring:message code="haitimobileclinic.reprintIDCard"/>
							</button>
						</td>
					</tr>
				</table>
			</div>
		</div>		
 --%>	</div>
	
	<div id="messageArea" class="hiddenDiv">
		<!-- displays alert messages -->
		<div id="matchedPatientDiv" name="matchedPatientDiv" class="matchedPatientClass" style="visibility:hidden">
			<div id="loadingSimilarPatients" style="background-color: #FF732F;">
				<table>									
					<tr>
						<td align="left">
							<img src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/smallwhiteloader.gif"></img>
							<span><spring:message code="haitimobileclinic.searchingMatchingPatients"/></span>
						</td>																	
					</tr>										
				</table>												
			</div>
			<div id="confirmExistingPatientDiv">
				<table class="confirmExistingPatientList searchTableList">
				</table>
			</div>
			<div id="confirmExistingPatientModalDiv" title='<spring:message code="haitimobileclinic.similarPatients"/>'>
				<table>							
					<tr>
						<td>
							<spring:message code="haitimobileclinic.youHaveEntered"/>
							<br>
							<b><span id="modalPatientName" name="modalPatientName"></span></b>
							<br>
							<b><span id="modalPatientGenderDOB" name="modalPatientGenderDOB"></span></b>
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
				<div id="confirmPatientModalDiv" title="Similar patients">
				</div>	
			</div>
		</div>
	</div>
	<div id="loadingGraph" class="ajaxResultsDiv hiddenDiv">
		<table width="100%" height="100%">									
			<tr>
				<td align="center">
					<img src="${pageContext.request.contextPath}/moduleResources/haitimobileclinic/images/biggerloader.gif"></img>
				</td>																	
			</tr>										
		</table>												
	</div>

	<div id="contextualInfo" class="hiddenDiv">
	</div>
	
</div>


<%@ include file="/WEB-INF/view/module/haitimobileclinic/workflow/_menuBottomBar.jsp"%>
<%@ include file="/WEB-INF/view/module/haitimobileclinic/workflow/_bottomBar.jsp"%>
