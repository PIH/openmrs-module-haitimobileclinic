<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/haitimobileclinic/haitimobileclinicHeader.jsp"%>
<openmrs:htmlInclude file="/moduleResources/haitimobileclinic/jquery.flot.min.js"/>
<script type="text/javascript">
<!--
		
	$j(document).ready(function(){
		// TODO: abstract this out into a tag or elsewhere
		// use the jquery datepicker
		$j('#fromDate').datepicker({ changeMonth:true, changeYear: true, dateFormat: '${_dateFormatInputJavascript}', yearRange: '-100:0',
										monthNamesShort: ['<spring:message code="haitimobileclinic.month.1"/>', '<spring:message code="haitimobileclinic.month.2"/>',
										                  '<spring:message code="haitimobileclinic.month.3"/>', '<spring:message code="haitimobileclinic.month.4"/>',
										                  '<spring:message code="haitimobileclinic.month.5"/>', '<spring:message code="haitimobileclinic.month.6"/>',
										                  '<spring:message code="haitimobileclinic.month.7"/>', '<spring:message code="haitimobileclinic.month.8"/>',
										                  '<spring:message code="haitimobileclinic.month.9"/>', '<spring:message code="haitimobileclinic.month.10"/>',
										                  '<spring:message code="haitimobileclinic.month.11"/>', '<spring:message code="haitimobileclinic.month.12"/>']});
														  
		$j('#untilDate').datepicker({ changeMonth:true, changeYear: true, dateFormat: '${_dateFormatInputJavascript}', yearRange: '-100:0',
										monthNamesShort: ['<spring:message code="haitimobileclinic.month.1"/>', '<spring:message code="haitimobileclinic.month.2"/>',
										                  '<spring:message code="haitimobileclinic.month.3"/>', '<spring:message code="haitimobileclinic.month.4"/>',
										                  '<spring:message code="haitimobileclinic.month.5"/>', '<spring:message code="haitimobileclinic.month.6"/>',
										                  '<spring:message code="haitimobileclinic.month.7"/>', '<spring:message code="haitimobileclinic.month.8"/>',
										                  '<spring:message code="haitimobileclinic.month.9"/>', '<spring:message code="haitimobileclinic.month.10"/>',
										                  '<spring:message code="haitimobileclinic.month.11"/>', '<spring:message code="haitimobileclinic.month.12"/>']});
	});
		
-->
</script>

<script type="text/javascript">
	function removeAndSubmit(elementId) {
		$j("#"+elementId).val("");
		submitForm();
	}
	function addAndSubmit(element) {
		$j("#nextFilter").val($j(element).text());
		submitForm();
	}
	function submitForm() {
		$j("#usageForm").submit();
	}
</script>

<c:set var="locationId" value="${empty location ? '' : location.id}"/>
<c:set var="encounterTypeId" value="${empty encounterType ? '' : encounterType.id}"/>

<form method="post" id="usageForm">
	<div id="content" style="padding-top:20px;"/>
		<table width="100%">
			<tr>
				<td valign="top" align="left" style="white-space:nowrap;">
					
					<b><spring:message code="haitimobileclinic.location"/></b><br/>
					<select name="location" onchange="submitForm();">
						<option value=""${empty location ? ' selected' : ''}><spring:message code="haitimobileclinic.allLocations"/></option>
						<c:forEach items="${locations}" var="l">
							<option value="${l.id}"${location == l ? ' selected' : ''}>
								${l.name}
							</option>
						</c:forEach>
					</select>
					<br/><br/>
					
					<b>Period</b><br/>		
					<spring:message code="general.fromDate"/>
					<input id="fromDate" name="fromDate" value="<openmrs:formatDate date="${from}" format="${_dateFormatInput}"/>"/>&nbsp;&nbsp;					
					<spring:message code="haitimobileclinic.general.untilDate"/>		
					<input id="untilDate" name="untilDate" value="<openmrs:formatDate date="${until}" format="${_dateFormatInput}"/>"/>		
					<br/><br/>
										
					
					<input type="hidden" id="nextFilter" name="${nextField}" value=""/>
				</td>
			</tr>
		</table>	

	</div/
	<br/><br/>
	
	<button type="submit"><spring:message code="haitimobileclinic.continue"/></button>
	
	</form>

<%@ include file="/WEB-INF/view/module/haitimobileclinic/haitimobileclinicFooter.jsp"%>