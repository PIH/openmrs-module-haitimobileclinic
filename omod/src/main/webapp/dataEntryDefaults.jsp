<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="View Patients" otherwise="/login.htm" redirect="/module/haitimobileclinic/dataEntryDefaults.form" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<openmrs:htmlInclude file="/moduleResources/addresshierarchy/addressHierarchy.js" />
<%@ taglib prefix="referrals" uri="/WEB-INF/view/module/haitimobileclinic/resources/referrals.tld"%>

<script src="/openmrs/moduleResources/htmlformentry/htmlFormEntry.js" type="text/javascript"></script>

<script type="text/javascript">
	var $j = jQuery;

	var addressHierarchyLevels = [ 'country', 'stateProvince', 'cityVillage',
			'address3', 'address1', 'address2', ];
	var pageContext = '/openmrs';
	var other = "Other";
	var allowFreetext = true;

	$j(document).ready(function() {

		// initialize all the address field (if necessary) by updating the options in the relevant select list
		// note that we need to reference the select lists by name (as opposed to class) because there may be multiple
		// instances of the address portlet (and therefore multiple addresses) on a single page
		// note that we build the search string for each hierarchy level by concatenating the values of the previous levels

		// value for the selection list is the current value for the field (if one exist), otherwise the default value (if one exists)
		// only display selection this for list level if a) the previous level in the hierarchy has a value, b) the level itself has a value, or c) this is the top level in the hierarchy
		updateOptions($j('select[name=country]'), "", "Haiti"); // use double quotes here so as not conflict with ' in location names			

		// value for the selection list is the current value for the field (if one exist), otherwise the default value (if one exists)
		// only display selection this for list level if a) the previous level in the hierarchy has a value, b) the level itself has a value, or c) this is the top level in the hierarchy
		updateOptions($j('select[name=stateProvince]'), "Haiti|", ""); // use double quotes here so as not conflict with ' in location names
		
		// todo, here someone should prepopulate the comboboxes so that already select locations are shown again
	});
</script>

<div align="center">
	<br/><br/><br/>
	<spring:message code="haitimobileclinic.settingDefaults"/>
	<br/><br/><br/>
	<form method="post" action="dataEntryDefaults.form">
		<table>
			<tr>
				<td><spring:message code="haitimobileclinic.staticClinicUnchangeable"/>:</td>c
				<td>
					<input type="text" name="sessionStaticLocationName" readonly
						id="sessionStaticLocationName" size="25" value="${sessionStaticLocationName}" />
					<input type="hidden" name="sessionStaticLocation"
						id="sessionStaticLocation" size="25" value="${sessionStaticLocation}" />
				</td>
			</tr>
			<tr>
				<td><spring:message code="haitimobileclinic.clinicDate"/>:</td>
				<td>
					<referrals:datePicker id='sessionDate' initialValue='${sessionDate}'/>
			</tr>
			<tr>
				<td><spring:message code="haitimobileclinic.clinicLocation"/>:</td>
				<td>
					<table class="address">
						<tr>
							<td><spring:message code="haitimobileclinic.country"/></td>
							<td><select style="display: none" name="country"
								class="country"
								onChange="handleAddressFieldChange($j(this), $j(this).closest('.address').find('.stateProvince'));" />
							</td>
							<td><input type="text" style="display: none" value=""
								class="other" /></td>
						</tr>
						<tr>
							<td><spring:message code="haitimobileclinic.department"/></td>
							<td><select style="display: none" name="stateProvince"
								class="stateProvince"
								onChange="handleAddressFieldChange($j(this), $j(this).closest('.address').find('.cityVillage'));" />
							</td>
							<td><input type="text" style="display: none" value=""
								class="other" /></td>
						</tr>
						<tr>
							<td><spring:message code="haitimobileclinic.commune"/></td>
							<td><select style="display: none" name="cityVillage"
								class="cityVillage"
								onChange="handleAddressFieldChange($j(this), $j(this).closest('.address').find('.address3'));" />
							</td>
							<td><input type="text" style="display: none" value=""
								class="other" /></td>
						</tr>
						<tr>
							<td><spring:message code="haitimobileclinic.sectionCommunale"/></td>
							<td><select style="display: none" name="address3"
								class="address3"
								onChange="handleAddressFieldChange($j(this), $j(this).closest('.address').find('.address1'));" />
							</td>
							<td><input type="text" style="display: none" value=""
								class="other" /></td>
						</tr>
						<tr>
							<td><spring:message code="haitimobileclinic.localityHabitation"/></td>
							<td><select style="display: none" name="address1"
								class="address1"
								onChange="handleAddressFieldChange($j(this), '');" /></td>
							<td><input type="text" style="display: none" value=""
								class="other" /></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td><spring:message code="haitimobileclinic.gpsCoordinates"/>:</td>c
				<td><input type="text" name="sessionCoordinates"
					id="sessionCoordinates" size="25" value="${sessionCoordinates}" /></td>
			</tr>
			<tr>
				<td><spring:message code="haitimobileclinic.chwNames3"/>:</td>
				<td>
				<input type="text" name="sessionChwName1"
					id="sessionChwName1" size="25" value="${sessionChwName1}" />
					 <input
					type="text" name="sessionChwName2" id="sessionChwName2" size="25"
					value="${sessionChwName2}" /> 
					<input type="text"
					name="sessionChwName3" id="sessionChwName3" size="25"
					value="${sessionChwName3}" /></td>
			</tr>
			<tr>
				<td><spring:message code="haitimobileclinic.necNames2"/>:</td>
				<td><input type="text" name="sessionNecName1" id="sessionNecName2"
					size="25" value="${sessionNecName1}" />
					<input type="text" name="sessionNecName2" id="sessionNecName2"
					size="25" value="${sessionNecName2}" /></td>
			</tr>
			<tr />
			<tr />
			<tr>
				<td><input type="submit" value="<spring:message code="haitimobileclinic.setDefaults"/>" /></td>
			</tr>
	
		</table>
	</form>
</div>

<br>
&nbsp;
<br>

<%@ include file="/WEB-INF/template/footer.jsp"%>