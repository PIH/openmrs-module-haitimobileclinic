<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ taglib prefix="referrals" uri="/WEB-INF/view/module/haitimobileclinic/resources/referrals.tld"%>
<%@ taglib prefix="tbSuspect" uri="/WEB-INF/view/module/haitimobileclinic/resources/tbSuspect.tld"%>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<script src="/openmrs/moduleResources/htmlformentry/htmlFormEntry.js" type="text/javascript"></script>

<script>
function save(fieldToUpdate, tbSuspectEncounterId, sputumResult1, sputumResult1Date, sputumResult2, sputumResult2Date, sputumResult3, sputumResult3Date, ppdResult, ppdResultDate, status, statusDate, existingResultEncounterId) {	
	$j.post(
			 '/openmrs/module/haitimobileclinic/saveTbResult.form',
			 { 'tbSuspectEncounterId': tbSuspectEncounterId, 'existingResultEncounterId': existingResultEncounterId,
				 'sputumResult1': sputumResult1, 'sputumResult1Date': sputumResult1Date, 
				 'sputumResult2': sputumResult2, 'sputumResult2Date': sputumResult2Date, 
				 'sputumResult3': sputumResult3, 'sputumResult3Date': sputumResult3Date, 
				 'ppdResult': ppdResult, 'ppdResultDate': ppdResultDate,
				 'status': status, 'statusDate': statusDate },
			 function (data) {
				 fieldToUpdate.fadeOut(1000, function() {
					  fieldToUpdate.fadeIn(1000);
					});
		  		}
		);
	}
</script>

<div id="findPatient" class="portlet">
	<div>
		<b class="boxHeader">TB Status and Results</b>
		<div class="box">
			<div class="searchWidgetContainer" id="referrals">
			<span class="openmrsSearchDiv" style="display: inline;">
					<div id="openmrsSearchTable_wrapper" class="dataTables_wrapper">
						<table id="openmrsSearchTable" cellspacing="0" cellpadding="2"
							style="width: 100%">
							<thead id="searchTableHeader">
								<tr>
									<th>Name</th>
									<th>Site</th>
									<th>CHW names</th>
									<th>NEC name</th>
									<th>Screening visit</th>
									<th>Age</th>
									<th>TB Results and status</th>
									<th></th>
								</tr>
							</thead>
							<c:forEach var="patientId" items="${memberIds}" varStatus="loopStatus">
								<script type="text/javascript">
									$j(function() {
										$j('#enroll-${patientId}').hide();
									    $j('#staticVisitDate-${patientId}').change(function() {
									    	  if ($j('#staticVisitDate-${patientId}').val()) {
										            $j('#enroll-${patientId}').show();
									    	  } else {
										            $j('#enroll-${patientId}').hide();
									    	  }
									    });
									});
								</script>
								<c:set var="tbSuspectEncounterId">
									<tbSuspect:tbSuspectEncounterId patientId="${patientId}"/>
								</c:set>
								<tbody>
									<tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
										<td><referrals:patientName patientId="${patientId}" /></td>
										<td><referrals:site referralEncounterId="${tbSuspectEncounterId}" /></td>
										<td><referrals:chwNames referralEncounterId="${tbSuspectEncounterId}"/></td>
										<td><referrals:necName referralEncounterId="${tbSuspectEncounterId}" /></td>
										<td><referrals:mobileVisitDate referralEncounterId="${tbSuspectEncounterId}" /></td>
										<td><referrals:age patientId="${patientId}" /></td>
										<td><span id='span-${patientId}'><tbSuspect:tbResultAndStatus tbSuspectEncounterId="${tbSuspectEncounterId}" /></span></td>
										<td>
											<a id='save-${patientId}' href="javascript:save($j('#save-${patientId}'), ${tbSuspectEncounterId},
												$j('#sputum1-${patientId}').val(), $j('#sputumdate1-${patientId}').val(), 
												$j('#sputum2-${patientId}').val(), $j('#sputumdate2-${patientId}').val(), 
												$j('#sputum3-${patientId}').val(), $j('#sputumdate3-${patientId}').val(), 
												$j('#ppd-${patientId}').val(), $j('#ppddate-${patientId}').val(), 
												$j('#status-${patientId}').val(), $j('#statusdate-${patientId}').val(),
												$j('#resultEncounterId-${patientId}').val());">Save</a>
										</td>
									</tr>
								</tbody>
							</c:forEach>
						</table>
					</div>
				</span>
			</div>
		</div>
	</div>
	<br>
	<input type="button" onClick="window.print()" value="Print"/>
	<br>
	<br>
	&nbsp;
	<br>
	Only patients with pending TB referrals are listed. If a patient is missing, <a href="/openmrs/findPatient.htm">Find the patient</a> and check the referral section of the last Mobile Clinic Consultation Sheet(s) and make sure the patient is not yet enrolled with a matching Static Clinic Enrollment.
</div>

<br>
&nbsp;
<br>

<%@ include file="/WEB-INF/template/footer.jsp"%>