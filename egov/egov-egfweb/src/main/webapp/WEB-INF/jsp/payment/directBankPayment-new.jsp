<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>


<html>
<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java"%>
<head>
<title><s:text name="lbl.direct.bank.payment"/> </title>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/javascript/voucherHelper.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/javascript/directBankPaymentHelper.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/calendar.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/dateValidation.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/ajaxCommonFunctions.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/autocomplete-debug.js"></script>
<link rel="stylesheet" href="/services/EGF/struts/xhtml/styles.css"
	type="text/css" />
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1252">
<style type="text/css">
#codescontainer {
	position: absolute;
	left: 11em;
	width: 9%;
	text-align: left;
}

#codescontainer .yui-ac-content {
	position: absolute;
	width: 600px;
	border: 1px solid #404040;
	background: #fff;
	overflow: hidden;
	z-index: 9050;
}

#codescontainer .yui-ac-shadow {
	position: absolute;
	margin: .3em;
	width: 300px;
	background: #a0a0a0;
	z-index: 9049;
}

#codescontainer ul {
	padding: 5px 0;
	width: 100%;
}

#codescontainer li {
	padding: 0 5px;
	cursor: default;
	white-space: nowrap;
}

#codescontainer li.yui-ac-highlight {
	background: #ff0;
}

#codescontainer li.yui-ac-prehighlight {
	background: #FFFFCC;
}
</style>
<script>
	path="${pageContext.request.contextPath}";
	var showMode='<s:property value="showMode"/>';	
	
		var totaldbamt=0,totalcramt=0;
		var OneFunctionCenter= <s:property value="isRestrictedtoOneFunctionCenter"/>; 
		//bootbox.alert(">>.."+OneFunctionCenter);                 
		var makeVoucherDetailTable = function() {
			<s:if test='%{isRestrictedtoOneFunctionCenter == true}'>                                   
			var voucherDetailColumns = [                   
				{key:"functionid",hidden:true,  formatter:createTextFieldFormatterJV(VOUCHERDETAILLIST,".functionIdDetail","hidden")},
				{key:"function",hidden:true,label:'<s:text name="lbl.function.name"/>', formatter:createTextFieldFormatterForFunctionJV(VOUCHERDETAILLIST,".functionDetail","hidden")},    
				{key:"glcodeid",hidden:true, formatter:createTextFieldFormatterJV(VOUCHERDETAILLIST,".glcodeIdDetail","hidden")},
				{key:"glcode",label:'<s:text name="lbl.account.code"/> <span class="mandatory1">*</span>',   formatter:createTextFieldFormatterJV(VOUCHERDETAILLIST,".glcodeDetail","text")},
				{key:"accounthead", label:'<s:text name="lbl.account.head"/>',formatter:createLongTextFieldFormatterJV(VOUCHERDETAILLIST,".accounthead")},
				{key:"rate", label:'Rate',formatter:createRateFieldFormatterJV(VOUCHERDETAILLIST,".rateDetail","updateRateDetailJV()")},				
				{key:"debitamount",label:'<s:text name="lbl.debit.amount"/>', formatter:createAmountFieldFormatterJV(VOUCHERDETAILLIST,".debitAmountDetail","updateDebitAmountJV()")}, 
				{key:"creditamount",label:'<s:text name="lbl.credit.amount"/>',formatter:createAmountFieldFormatterJV(VOUCHERDETAILLIST,".creditAmountDetail","updateCreditAmountJV()")},
				{key:'Add',label:'<s:text name="lbl.add"/>',formatter:createAddImageFormatter("${pageContext.request.contextPath}")},
				{key:'Delete',label:'<s:text name="lbl.delete"/>',formatter:createDeleteImageFormatter("${pageContext.request.contextPath}")}
			];
			</s:if>
			<s:else>
			var voucherDetailColumns = [ 
       			{key:"functionid",hidden:true,  formatter:createTextFieldFormatterJV(VOUCHERDETAILLIST,".functionIdDetail","hidden")},
       			{key:"function",label:'<s:text name="lbl.function.name"/>', formatter:createTextFieldFormatterForFunctionJV(VOUCHERDETAILLIST,".functionDetail","text")},         
       			{key:"glcodeid",hidden:true, formatter:createTextFieldFormatterJV(VOUCHERDETAILLIST,".glcodeIdDetail","hidden")},
       			{key:"glcode",label:'<s:text name="lbl.account.code"/> <span class="mandatory1">*</span>',formatter:createTextFieldFormatterJV(VOUCHERDETAILLIST,".glcodeDetail","text")},
       			{key:"accounthead", label:'<s:text name="lbl.account.head"/>',formatter:createLongTextFieldFormatterJV(VOUCHERDETAILLIST,".accounthead")},
       			{key:"rate", label:'Rate',formatter:createRateFieldFormatterJV(VOUCHERDETAILLIST,".rateDetail","updateRateDetailJV()")},
       			{key:"debitamount",label:'<s:text name="lbl.debit.amount"/>', formatter:createAmountFieldFormatterJV(VOUCHERDETAILLIST,".debitAmountDetail","updateDebitAmountJV()")}, 
       			{key:"creditamount",label:'<s:text name="lbl.credit.amount"/>',formatter:createAmountFieldFormatterJV(VOUCHERDETAILLIST,".creditAmountDetail","updateCreditAmountJV()")},
       			{key:'Add',label:'<s:text name="lbl.add"/>',formatter:createAddImageFormatter("${pageContext.request.contextPath}")},
       			{key:'Delete',label:'<s:text name="lbl.delete"/>',formatter:createDeleteImageFormatter("${pageContext.request.contextPath}")}
       		];
		</s:else>         
	    var voucherDetailDS = new YAHOO.util.DataSource(); 
		billDetailsTable = new YAHOO.widget.DataTable("billDetailTable",voucherDetailColumns, voucherDetailDS);
		billDetailsTable.on('cellClickEvent',function (oArgs) {
			var target = oArgs.target;
			var record = this.getRecord(target);
			var column = this.getColumn(target);
			if (column.key == 'Add') { 
			 	if(showMode=='nonbillPayment')
			 	return;
					billDetailsTable.addRow({SlNo:billDetailsTable.getRecordSet().getLength()+1});
				updateAccountTableIndex();
			}
			if (column.key == 'Delete') { 	
				if(showMode=='nonbillPayment')
			 		return;
				if(this.getRecordSet().getLength()>1){			
					this.deleteRow(record);
					allRecords=this.getRecordSet();
					for(var i=0;i<allRecords.getLength();i++){
						this.updateCell(this.getRecord(i),this.getColumn('SlNo'),""+(i+1));
					}
					updateDebitAmountJV();updateCreditAmountJV();
					check();
				}
				else{
					bootbox.alert("<s:text name='msg.this.row.can.not.be.deleted'/>");
				}
			}
			
			        
		}
		);
		<s:iterator value="billDetailslist" status="stat">
				billDetailsTable.addRow({SlNo:billDetailsTable.getRecordSet().getLength()+1,
					"functionid":'<s:property value="functionIdDetail"/>',
					"function":'<s:property value="functionDetail"/>',
					"glcodeid":'<s:property value="glcodeIdDetail"/>',
					"glcode":'<s:property value="glcodeDetail"/>',
					"accounthead":'<s:property value="accounthead"/>',
					"debitamount":'<s:text name="format.number" ><s:param value="%{debitAmountDetail}"/></s:text>',
					"creditamount":'<s:text name="format.number" ><s:param value="%{creditAmountDetail}"/></s:text>'
				});
				var index = '<s:property value="#stat.index"/>';
				updateGridPJV('functionIdDetail',index,'<s:property value="functionIdDetail"/>');
				updateGridPJV('functionDetail',index,'<s:property value="functionDetail"/>');
				updateGridPJV('glcodeIdDetail',index,'<s:property value="glcodeIdDetail"/>');
				updateGridPJV('glcodeDetail',index,'<s:property value="glcodeDetail"/>');
				updateGridPJV('accounthead',index,'<s:property value="accounthead"/>');
				updateGridPJV('debitAmountDetail',index,'<s:text name="format.number" ><s:param value="%{debitAmountDetail}"/></s:text>');
				updateGridPJV('creditAmountDetail',index,'<s:text name="format.number" ><s:param value="%{creditAmountDetail}"/></s:text>');
				totaldbamt = totaldbamt+parseFloat('<s:property value="debitAmountDetail"/>');
				totalcramt = totalcramt+parseFloat('<s:property value="creditAmountDetail"/>');
				updateAccountTableIndex();	
			</s:iterator>
				

		var tfoot = billDetailsTable.getTbodyEl().parentNode.createTFoot();
		var tr = tfoot.insertRow(-1);
		var th = tr.appendChild(document.createElement('th'));
		th.colSpan = 6;
		th.innerHTML = 'Total&nbsp;&nbsp;&nbsp;';
		th.align='right';
		var td = tr.insertCell(-1);
		td.width="90"
		td.innerHTML="<input type='text' style='text-align:right;width:100px;'  id='totaldbamount' name='totaldbamount' readonly='true' tabindex='-1'/>";
		var td = tr.insertCell(-1);
		td.width="90"
		td.align="right"
		td.innerHTML="<input type='text' style='text-align:right;width:100px;'  id='totalcramount' name='totalcramount' readonly='true' tabindex='-1'/>";
		document.getElementById('totaldbamount').value=totaldbamt.toFixed(2);
		document.getElementById('totalcramount').value=totalcramt.toFixed(2); 
		}
		var glcodeOptions=[{label:"<s:text name='lbl.select'/>", value:"0"}];
		<s:iterator value="dropdownData.glcodeList">
	    glcodeOptions.push({label:'<s:property value="glcode"/>', value:'<s:property value="id"/>'})
	</s:iterator>
	var detailtypeOptions=[{label:"<s:text name='lbl.select'/>", value:"0"}];
	<s:iterator value="dropdownData.detailTypeList">
	    detailtypeOptions.push({label:'<s:property value="name"/>', value:'<s:property value="id"/>'})
	</s:iterator>
	
	
	
		
	var makeSubLedgerTable = function() {
		var subledgerColumns = [ 
			{key:"subledgerCode",hidden:true, formatter:createSLTextFieldFormatterJV(SUBLEDGERLIST,".subledgerCode","hidden")},
			{key:"glcode.id",label:'<s:text name="lbl.account.code"/> <span class="mandatory1">*</span>', formatter:createDropdownFormatterJV(SUBLEDGERLIST,"loaddropdown(this)"),  dropdownOptions:glcodeOptions},
			{key:"detailTypeName",hidden:true, formatter:createSLTextFieldFormatterJV(SUBLEDGERLIST,".detailTypeName","hidden")},
			{key:"detailType.id",label:'<s:text name="lbl.type"/> <span class="mandatory1">*</span>', formatter:createDropdownFormatterJV1(SUBLEDGERLIST),dropdownOptions:detailtypeOptions},
			{key:"detailCode",label:'<s:text name="lbl.code"/> <span class="mandatory1">*</span>', formatter:createSLDetailCodeTextFieldFormatterJV(SUBLEDGERLIST,".detailCode","splitEntitiesDetailCode(this)", ".search", "openSearchWindowFromJV(this)")},
			{key:"detailKeyId",hidden:true, formatter:createSLHiddenFieldFormatterJV(SUBLEDGERLIST,".detailKeyId")},
			{key:"detailKey",label:'<s:text name="lbl.name"/>', formatter:createSLLongTextFieldFormatterJV(SUBLEDGERLIST,".detailKey","")},
			{key:"amount",label:'<s:text name="lbl.amount"/>', formatter:createSLAmountFieldFormatterJV(SUBLEDGERLIST,".amount")},
			{key:'Add',label:'<s:text name="lbl.add"/>',formatter:createAddImageFormatter("${pageContext.request.contextPath}")},
			{key:'Delete',label:'<s:text name="lbl.delete"/>',formatter:createDeleteImageFormatter("${pageContext.request.contextPath}")}
		];
	    var subledgerDS = new YAHOO.util.DataSource(); 
		subLedgersTable = new YAHOO.widget.DataTable("subLedgerTable",subledgerColumns, subledgerDS);
		subLedgersTable.on('cellClickEvent',function (oArgs) {
			var target = oArgs.target;
			var record = this.getRecord(target);
			var column = this.getColumn(target);
			if (column.key == 'Add') { 
			if(showMode=='nonbillPayment')
			 		return;
				subLedgersTable.addRow({SlNo:subLedgersTable.getRecordSet().getLength()+1});
				updateSLTableIndex();
				check();
			}
			if (column.key == 'Delete') { 	
			if(showMode=='nonbillPayment')
			 		return;		
				if(this.getRecordSet().getLength()>1){			
					this.deleteRow(record);
					allRecords=this.getRecordSet();
					for(var i=0;i<allRecords.getLength();i++){
						this.updateCell(this.getRecord(i),this.getColumn('SlNo'),""+(i+1));
					}
				}
				else{
					bootbox.alert("<s:text name='msg.this.row.can.not.be.deleted'/>");
				}
			}        
		});
	
		<s:iterator value="subLedgerlist" status="stat">
				subLedgersTable.addRow({SlNo:subLedgersTable.getRecordSet().getLength()+1,
					"subledgerCode":'<s:property value="subledgerCode"/>',
					"glcode.id":'<s:property value="glcode.id"/>',
					"detailType.id":'<s:property value="detailType.id"/>',
					"detailTypeName":'<s:property value="detailTypeName"/>',
					"detailCode":'<s:property value="detailCode"/>',
					"detailKeyId":'<s:property value="detailKeyId"/>',
					"detailKey":'<s:property value="detailKey"/>',
					"debitAmount":'<s:text name="format.number" ><s:param value="%{debitAmount}"/></s:text>',
					"creditAmount":'<s:text name="format.number" ><s:param value="%{creditAmount}"/></s:text>'
				});'<s:property value="glcode.id"/>'
				var index = '<s:property value="#stat.index"/>';
				updateGridSLDropdownJV('glcode.id',index,'<s:property value="glcode.id"/>','<s:property value="subledgerCode"/>');
				updateGridSLDropdownJV('detailType.id',index,'<s:property value="detailType.id"/>','<s:property value="detailTypeName"/>');
				updateSLGridPJV('detailCode',index,'<s:property value="detailCode"/>');
				updateSLGridPJV('subledgerCode',index,'<s:property value="subledgerCode"/>');
				updateSLGridPJV('detailKeyId',index,'<s:property value="detailKeyId"/>');
				updateSLGridPJV('detailKey',index,'<s:property value="detailKey"/>');
				updateSLGridPJV('amount',index,'<s:text name="format.number" ><s:param value="%{amount}"/></s:text>');
				updateSLTableIndex();
			</s:iterator>
	
	}
	var amountshouldbenumeric='<s:text  name="amount.should.be.numeric"/>';
	var succesMessage='<s:text name="directbank.transaction.succcess"/>';
	var totalsnotmatchingamount='<s:text name="totals.not.matching.amount"/>';
	var 	button='<s:property value="button"/>';
	</script>

</head>
<body
	onload="onLoadTask_new();loadDropDownCodesExcludingCashAndBank();loadDropDownCodesFunction();documentdep();onloadtriple();">
	<s:form action="directBankPayment" theme="css_xhtml" name="dbpform"
		validate="true">
		<s:push value="model">
			<jsp:include page="../budget/budgetHeader.jsp">
				<jsp:param value="Direct Bank Payment" name="heading" />
			</jsp:include>
			<div class="formmainbox">
				<div class="subheadnew"><s:text name="lbl.create.direct.bank.payment"/> </div>

				<div align="center">
					<font style='color: red;'>
						<p class="error-block" id="lblError"></p>
					</font>
				</div>
				<span class="mandatory1">
					<div id="Errors">
						<s:actionerror />
						<s:fielderror />
					</div> <s:actionmessage />
				</span>
				<table border="0" width="100%" cellspacing="0" cellpadding="0">
					<tr>
						<td width="10%" class="bluebox"></td>
						<s:if test="%{shouldshowVoucherNumber()}">
							<td class="bluebox" width="22%"><s:text
									name="voucher.number" /><span class="mandatory1">*</span></td>
							<td class="bluebox" width="22%"><s:textfield
									name="voucherNumber" id="voucherNumber" /></td>
						</s:if>
						<s:hidden name="id" />

						<td class="bluebox" width="18%"><s:text name="voucher.date" /><span
							class="mandatory1">*</span></td>
						<s:date name='voucherDate' var="voucherDateId" format='dd/MM/yyyy' />
						<td class="bluebox" width="34%">
							<div name="daterow">
								<s:textfield id="voucherDate" name="voucherDate"
									value="%{voucherDateId}" data-date-end-date="0d"
									onkeyup="DateFormat(this,this.value,event,false,'3')"
									placeholder="DD/MM/YYYY" class="form-control datepicker"
									data-inputmask="'mask': 'd/m/y'" />

							</div>
						</td>
					</tr>
					<%@include file="directBankPayment-form.jsp"%>


					<div class="subheadsmallnew"></div>
					<div align="left" class="mandatory1">* <s:text name="lbl.mendatory.fields"/> </div>
					<s:hidden name="typeOfAccount" id="typeOfAccount"
						value="%{typeOfAccount}" />

					</br>
				</table>
				<input type="hidden" id="commonBean.fundnew" name="commonBean.fundnew" value="${commonBean.fundnew}" />
			<input type="hidden" id="commonBean.departmentnew" name="commonBean.departmentnew" value="${commonBean.departmentnew}" />
			<input type="hidden" id="commonBean.functionnew" name="commonBean.functionnew" value="${commonBean.functionnew}" />
				<s:hidden name="cutOffDate" id="cutOffDate" />
				<s:hidden name="bankBalanceCheck" id="bankBalanceCheck" value="%{bankBalanceCheck}" />
            	<jsp:include page="../payment/commonWorkflowMatrix.jsp"/>
			</div>
			<div align="center">
            	<jsp:include page="../payment/commonWorkflowMatrix-button.jsp"/>
			</div>
		</s:push>
		<s:hidden name="showMode" />
		<s:token />
	</s:form>
	<script type="text/javascript">
	
	
	var val=false;
	 var paymentChequeNo=null;
	 /* 
	jQuery("#modeOfPaymentrtgs").change(function(){
   if( $(this).is(":checked") ){ 
      val = $(this).val(); 
      
   }
   if(val==true){
   	jQuery("#payemnttr").show();
   }
   else{
   	jQuery("#payemnttr").hide();
   	val=false;
   }
	}); */
	
	
	jQuery("#paymentChequeNo").change(function(){
		
		paymentChequeNo = jQuery("#paymentChequeNo").val();
		
		if(null==paymentChequeNo||paymentChequeNo=="")
		{
			bootbox.alert('Please Enter Payment Cheque Number');
			jQuery("#paymentChequeNo").val("");
			undoLoadingMask();
			
			return false;
		}
		else if(!/^[0-9]+$/.test(paymentChequeNo)){
			bootbox.alert('Please Enter valid Payment Cheque Number (Allowed input:0-9)');
			jQuery("#paymentChequeNo").val("");
			undoLoadingMask();
		return false;
		}
		else if(paymentChequeNo.length!=6){
			bootbox.alert('Please insert 6 Digit Cheque Number');
			jQuery("#paymentChequeNo").val("");
			undoLoadingMask();
		return false;			
	}
		
   });
	
	
function onLoadTask_new()
{
	//bootbox.alert(showMode);                                                      
	if(button!=null && button!="")
	{
		if(document.getElementById("Errors").innerHTML=='')  
		{
			bootbox.alert(succesMessage);
			if(button=="Save_Close")
				{
				window.close();
				}
			else if(button=="Save_View")
				{
						var vhId='<s:property value="voucherHeader.id"/>';
						document.forms[0].action = "${pageContext.request.contextPath}/voucher/preApprovedVoucher-loadvoucherview.action?vhid="+vhId;
						return true;
				}
			else if(button=="Save_New")
				{      	
					document.forms[0].button.value='';
				    document.forms[0].action = "directBankPayment-newform.action";
				 	return true;
				}
		}
		
		
 	}else
 	{
 		
 		<s:if test="%{showMode=='nonbillPayment'}">
			//bootbox.alert('<s:property value="showMode"/>');
			if(document.getElementById("Errors").innerHTML!='')
			{
			document.getElementById('buttondiv').style.display="none";
			document.getElementById('buttondivdefault').style.display="block";
			}
		</s:if>
 	}
 	
		
		if(showMode=='nonbillPayment')
		{
		disableForNonBillPayment();	
		disableYUIAddDeleteButtons(true);
		}
		if(document.getElementById('approverDepartment'))
			document.getElementById('approverDepartment').value = "-1";
		if (jQuery("#bankBalanceCheck") == null || jQuery("#bankBalanceCheck").val() == "") {
			disableForm();
		}
}

function populateAccNum(branch){
	//alert("populate Acc Num");
	var fundObj = "1";//document.getElementById('fundId');
	var bankbranchId = branch.options[branch.selectedIndex].value;
	var index=bankbranchId.indexOf("-");
	var bankId = bankbranchId.substring(0,index);
	var brId=bankbranchId.substring(index+1,bankbranchId.length);
	
	var vTypeOfAccount = '<s:property value="%{typeOfAccount}"/>';
	
	populateaccountNumber({fundId:fundObj,bankId:bankId,branchId:brId,typeOfAccount:vTypeOfAccount})
}
function onSubmit()
{
	if(checkdate())
	{
	enableAll();
	var balanceCheckMandatory='<s:text name="payment.mandatory"/>';
	var balanceCheckWarning='<s:text name="payment.warning"/>';
	var noBalanceCheck='<s:text name="payment.none"/>';
	var firstsignatory='';
	var backlogEntry='';
	if(document.getElementById('firstsignatory') == null || document.getElementById('firstsignatory').value == '-1')
	{
		bootbox.alert("Please select First Signatory");
		undoLoadingMask();
		return false;
	}
	else
		{
			firstsignatory=document.getElementById('firstsignatory').value;
		}
	/* comment by abhishek on 12042021 
	var secondsignatory=''
	if(document.getElementById('secondsignatory') == null || document.getElementById('secondsignatory').value == '-1')
	{
		bootbox.alert("Please select Second Signatory");
		undoLoadingMask();
		return false;
	}
	else
	{ */
		secondsignatory=document.getElementById('secondsignatory').value;
	//}
 
		if(document.getElementById("modeOfPaymentcheque").checked == true){
			paymentChequeNo = jQuery("#paymentChequeNo").val();
			
			if(null==paymentChequeNo||paymentChequeNo=="")
			{
				bootbox.alert('Payment Cheque Number is mandatory ');
				jQuery("#paymentChequeNo").val("");
				undoLoadingMask();
				return false;
			}
		}
		
	
	if(document.getElementById('backlogEntry') == null || document.getElementById('backlogEntry').value == '-1')
	{
		bootbox.alert("Please select whether it is backdated entry");
		undoLoadingMask();
		return false;
	}
	else
		{
		backlogEntry=document.getElementById('backlogEntry').value;
		}
	if(document.getElementById("description") == null || document.getElementById("description").value =='')
		{
		bootbox.alert("<s:text name='msg.payment.narration.mandatory'/>");
		 return false;
		}
	var submiturl='/services/EGF/payment/directBankPayment-create.action?secondsignatory='+secondsignatory+'&firstsignatory='+firstsignatory+'&backlogEntry='+backlogEntry;																																									
 
 
	if (!validateForm_directBankPayment()) {
					
		undoLoadingMask();
		return false;
	}
	else if (!updateAndCheckAmount()) {
				  
		undoLoadingMask();
		return false;
	}
	else if(jQuery("#bankBalanceCheck").val()==noBalanceCheck)
		{
					
		if(document.getElementById("modeOfPaymentcheque").checked == true)
			{
				
			}
									  
		document.dbpform.action = submiturl;					  
		return true;
		}
	else if(!balanceCheck() && jQuery("#bankBalanceCheck").val()==balanceCheckMandatory){
			 bootbox.alert("<s:text name='msg.insufficient.bank.balance'/>");
			 return false;
			}
	else if(!balanceCheck() && jQuery("#bankBalanceCheck").val()==balanceCheckWarning){
		if(document.getElementById("modeOfPaymentcheque").checked == true)
		{
			
		}
		 var msg = confirm("<s:text name='msg.insuff.bank.bal.do.you.want.to.process'/>");
		 if (msg == true) {
			 document.dbpform.action = '/services/EGF/payment/directBankPayment-create.action?secondsignatory='+secondsignatory+'&firstsignatory='+firstsignatory+'&backlogEntry='+backlogEntry;
					 
										
			 document.dbpform.submit();
			return true;
		 } else {
						
			 undoLoadingMask();
		   	return false;
			}
		}
	else{

				 
		if(document.getElementById("modeOfPaymentcheque").checked == true)
		{
			
		}
		document.dbpform.action = '/services/EGF/payment/directBankPayment-create.action?secondsignatory='+secondsignatory+'&firstsignatory='+firstsignatory+'&backlogEntry='+backlogEntry;
		document.dbpform.submit();
	}
	}
	else{
		bootbox.alert("Please select back dated entry option correctly");
		return false;
	}
}

function validateCutOff()
{
var cutOffDatePart=document.getElementById("cutOffDate").value.split("/");
var voucherDatePart=document.getElementById("voucherDate").value.split("/");
var cutOffDate = new Date(cutOffDatePart[1] + "/" + cutOffDatePart[0] + "/"
		+ cutOffDatePart[2]);
var voucherDate = new Date(voucherDatePart[1] + "/" + voucherDatePart[0] + "/"
		+ voucherDatePart[2]);
if(voucherDate<=cutOffDate)
{
	return true;
}
else{
	var msg1='<s:text name="wf.vouchercutoffdate.message"/>';
	var msg2='<s:text name="wf.cutoffdate.msg"/>';
	bootbox.alert(msg1+" "+document.getElementById("cutOffDate").value+" "+msg2);
		return false;
	}
}

function checkdate()
{
	//backlogEntry
	var backlog=document.getElementById('backlogEntry').value;
	var date2=document.getElementById('voucherDate').value;
	var parts = date2.split("/");
	var date = new Date(parts[1] + "/" + parts[0] + "/" + parts[2]);
	var curdate = new Date();
	if(backlog!='Y'){
	if(date.setHours(0,0,0,0) == curdate.setHours(0,0,0,0)) {
		if(backlog == 'N'){
	    	return true;
	    }
	    return false;
	}
	else{
		return false;
	}
	}else{
		if(date.setHours(0,0,0,0) < curdate.setHours(0,0,0,0)){
			console.log(":::: backdated");
			return true;
		}else{
			console.log(":::: not backdated");
			return false;
		}
	}
		
}

</SCRIPT>
</body>
</html>
