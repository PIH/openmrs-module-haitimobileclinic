$j(document).ready(function(){	

	var divItems = new Array("numeroDossierDiv"														 
	);
	
	var leftMenuItems = new Array("numeroDossierMenu"
	);
	
	var navigationArrows = new Array("cross-red", 
									"checkmark-yellow"
	);
	
	var prevDiv ='';
	var nextDiv='';
	
	
	jQuery.hideAllDiv = function() {			
		for(i=0; i<divItems.length; i++){				
			var divItem = "#"+divItems[i];			
			$j(divItem).hide();
		}					
	};
	
	$j.removeHighlightedMenu = function() {
		for(i=0; i<leftMenuItems.length; i++){				
			var menuItem = "#"+leftMenuItems[i];			
			$j(menuItem).removeClass('highlighted');					
		}
	};
	
	$j.hideNavigationArrows = function() {
		for(i=0; i<navigationArrows.length; i++){				
			var arrowItem = "#"+navigationArrows[i];			
			$j(arrowItem).hide();
		}
	};
	
	
	$j.setNumeroDossierDiv = function() {				
		prevDiv=null;
		nextDiv=null;		
		$j("#numeroDossierMenu").addClass('highlighted');				
		$j('#checkmark-yellow').show();		
		$j("[input[id^='patientInput']").focus();
	};
	
	$j('#cross-red').click(function(event){
		window.location.href=pageContextAddress + '/module/haitimobileclinic/workflow/primaryCareReceptionTask.form';
	});
	
	$j.setupDiv = function(devId) {
		$j.hideAllDiv();	
		$j.removeHighlightedMenu();	
		$j.hideNavigationArrows();
		$j('#cross-red').show();
		$j("#"+devId).css("visibility", "visible");
		$j("#"+devId).show();
		if(devId=='numeroDossierDiv'){
			$j.setNumeroDossierDiv();
		}else if(devId=='confirmPrintDiv'){
			$j.setConfirmPrintDiv();
		}else if(devId=='printDossierLabelDiv'){
			$j.setPrintDossierLabelDiv();
		}
	};
	
	$j.validateNumeroDossierDivData = function() {
		numeroDossierVal = $j('#patientInputNumeroDossier').val();
		console.log("numeroDossierVal="+numeroDossierVal);
		$j('#hiddenNumeroDossier').val(numeroDossierVal);
		return true;
	};
	
	$j.validateDivData = function() {
		//place holder for validating entry data
		if ($j('#numeroDossierDiv').is(':visible') ){						
			return $j.validateNumeroDossierDivData();			
		}
		return true;
	};

	var alreadySubmitted = false;
	
	$j('#checkmark-yellow').click(function(event){
		if (!alreadySubmitted) {
			alreadySubmitted = true;
			if($j.validateDivData()){										
				if(nextDiv !== null){								
					$j.setupDiv(nextDiv);				
				}
				 $j('#haitiMobileClinicEncounter').submit();
			}
		}		
	});
	
	$j.setupDiv('numeroDossierDiv');

	$j.populateConfirmForm = function() {															
		$j('#hiddenNumeroDossier').val(numeroDossierVal);
	};
	
	$j('#patientInputNumeroDossier').keyup(function(event) {
		if ($j(this).val() == '') {
			$j('#checkmark-yellow').hide();
		}
		else {
			$j('#checkmark-yellow').show();
		}
	});
	
	$j('#patientInputNumeroDossier').keypress(function(event) {		
		if(event.keyCode == 13){			
			$j('#checkmark-yellow').click();			
			event.preventDefault();
		}
	});
});