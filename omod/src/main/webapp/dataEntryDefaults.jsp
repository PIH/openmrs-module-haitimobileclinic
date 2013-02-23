<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="View Patients" otherwise="/login.htm"
	redirect="/module/haitimobileclinic/dataEntryDefaults.htm" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<openmrs:htmlInclude
	file="/moduleResources/addresshierarchy/addressHierarchy.js" />

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

	});
</script>

${message}

<form method="post" action="dataEntryDefaults.form">
	<table>
		<tr>
			<td>Date:</td>
			<td><input type="text" name="sessionDate" id="sessionDate"
				size="11" value="${sessionDate}" onfocus="showCalendar(this,60)"
				onChange="clearError('sessionDate')" /></td>
		</tr>
		<tr>
			<td>Clinic Location:</td>
			<td>
				<table class="address">
					<tr>
						<td>Country</td>
						<td><select style="display: none" name="country"
							class="country"
							onChange="handleAddressFieldChange($j(this), $j(this).closest('.address').find('.stateProvince'));" />
						</td>
						<td><input type="text" style="display: none" value=""
							class="other" /></td>
					</tr>
					<tr>
						<td>Départment</td>
						<td><select style="display: none" name="stateProvince"
							class="stateProvince"
							onChange="handleAddressFieldChange($j(this), $j(this).closest('.address').find('.cityVillage'));" />
						</td>
						<td><input type="text" style="display: none" value=""
							class="other" /></td>
					</tr>
					<tr>
						<td>Commune</td>
						<td><select style="display: none" name="cityVillage"
							class="cityVillage"
							onChange="handleAddressFieldChange($j(this), $j(this).closest('.address').find('.address3'));" />
						</td>
						<td><input type="text" style="display: none" value=""
							class="other" /></td>
					</tr>
					<tr>
						<td>Section Communale</td>
						<td><select style="display: none" name="address3"
							class="address3"
							onChange="handleAddressFieldChange($j(this), $j(this).closest('.address').find('.address1'));" />
						</td>
						<td><input type="text" style="display: none" value=""
							class="other" /></td>
					</tr>
					<tr>
						<td>Locality Habitation</td>
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
			<td>GPS coordinates:</td>c
			<td><input type="text" name="sessionCoordinates"
				id="sessionCoordinates" size="25" value="${sessionCoordinates}" /></td>
		</tr>
		<tr>
			<td>CHW names:</td>
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
			<td>NEC name:</td>
			<td><input type="text" name="sessionNecName" id="sessionNecName"
				size="25" value="${sessionNecName}" /></td>
		</tr>
		<tr />
		<tr />
		<tr>
			<td><input type="submit" value="Set defaults" /></td>
		</tr>

	</table>
</form>

<br>
&nbsp;
<br>

<%@ include file="/WEB-INF/template/footer.jsp"%>