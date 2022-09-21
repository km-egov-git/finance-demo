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
<head>
<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java"%>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/voucherHelper.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/contraBTBHelper.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/calendar.js?rnd=${app_release_no}"></script>
<script language="javascript"
	src="../resources/javascript/jsCommonMethods.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/dateValidation.js?rnd=${app_release_no}"></script>
<script type="text/javascript"
	src="/services/EGF/resources/javascript/ajaxCommonFunctions.js?rnd=${app_release_no}"></script>
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1252">
</head>
<body onload="onLoadTask_new();onloadtriple1();">
	<s:form action="contraBTB" theme="simple" name="cbtbform">
		<s:push value="model">
			<jsp:include page="../budget/budgetHeader.jsp">
				<jsp:param value="Bank to Bank Transfer" name="heading" />
			</jsp:include>
			<div class="formmainbox">
				<div class="formheading" />
				<div class="subheadnew"><s:text name="lbl.create.bank.to.bank.transfer"/> </div>
				<div id="listid" style="display: block">
					<br />
				</div>

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
				</br>
	
				<table border="0" width="100%" cellspacing="0" cellpadding="0">
					<tr>
						<td class="bluebox"></td>
						<s:if test="%{shouldShowHeaderField('vouchernumber')}">

							<td class="bluebox" width="22%"><s:text
									name="voucher.number" /><span class="mandatory1">*</span></td>
							<td class="bluebox" width="22%"><s:textfield
									name="voucherNumber" id="voucherNumber" /></td>
						</s:if>
						<s:hidden name="id" />

						<td class="bluebox" width="18%"><s:text name="voucher.date" /><span
							class="mandatory1">*</span></td>
						<td class="bluebox" width="34%"><s:textfield id="voucherDate"
								name="voucherDate" data-date-end-date="0d"
								onkeyup="DateFormat(this,this.value,event,false,'3')"
								placeholder="DD/MM/YYYY" class="form-control datepicker"
								data-inputmask="'mask': 'd/m/y'" /></td>
						<td class="bluebox"></td>
						<td class="bluebox"></td>
					</tr>
					<%@include file="contraBTB-form.jsp"%>
				</table>
			</div>
			<div class="mandatory1" align="left">* <s:text name="lbl.mendatory.field"/> </div>

			</br>
			</br>
			<%@include file="../voucher/SaveButtons.jsp"%>
			<input type="hidden" path="" id="contraBean.fundnew" value="${contraBean.fundnew}"/>
			<input type="hidden" path="" id="contraBean.departmentnew" value="${contraBean.departmentnew}"/>
			<input type="hidden" path="" id="contraBean.functionnew" value="${contraBean.functionnew}"/>
			<input type="hidden" id=name name="name" value="BankToBank" />
			<input type="hidden" id="type" name="type" value="Contra" />
			<s:hidden id="bankBalanceMandatory" name="bankBalanceMandatory"
				value="%{isBankBalanceMandatory()}" />
			<s:hidden id="startDateForBalanceCheckStr"
				name="startDateForBalanceCheckStr"
				value="%{startDateForBalanceCheckStr}" />
		</s:push>
	</s:form>
	<SCRIPT type="text/javascript">
function	onLoadTask_new()
{
	//loadFromDepartment();
	//loadToDepartment();
	var 	button='<s:property value="button"/>';
	var 	srcFund='<s:property value="contraBean.fromFundId"/>';
	var 	desFund='<s:property value="contraBean.toFundId"/>';
	
	
	if(srcFund!="" && desFund!="")
	{
		if(srcFund!=desFund)
		{
		document.getElementById('interFundRow1').style.visibility="visible";
		document.getElementById('interFundRow2').style.visibility="visible";
		document.getElementById('interFundRow3').style.visibility="visible";
		}
	}
	
	if(button!=null && button!="")
	{
	//alert(button);
	
	if(document.getElementById("Errors").value === undefined)  
	{
		
	
		
	if(button=="Save_Close")
	{
		//bootbox.alert('<s:text name="contra.transaction.succcess"/>');
		document.forms[0].button.value='';
        document.forms[0].action = "${pageContext.request.contextPath}/contra/contraBTB-create.action";
 		document.forms[0].submit();
	//window.close();
	}
	else if(button=="Save_View")
	{
			var vhId='<s:property value="vhId"/>';
			//document.forms[0].action = "${pageContext.request.contextPath}/voucher/preApprovedVoucher-loadvoucherview.action?vhid="+vhId;
			//document.forms[0].submit();
			//alert("::::::"+vhId);
	}
	else if(button=="Save_New")
	{   
		//bootbox.alert('<s:text name="contra.transaction.succcess"/>');
		document.forms[0].button.value='';
	        document.forms[0].action = "${pageContext.request.contextPath}/contra/contraBTB-create.action";
	 		document.forms[0].submit();
	}
	}
 }
 
 <s:if test="egovCommon.isShowChequeNumber()">
 document.getElementById("chequeGrid").style.visibility="visible";
 </s:if>
 <s:else>
 if('<s:property value="contraBean.modeOfCollection"/>'=='cheque')
 {
 document.getElementById("chequeGrid").style.visibility="hidden";
 }else
 {
 document.getElementById("chequeGrid").style.visibility="visible";
 }
 </s:else>
 

}

	function toggleChequeAndRefNumber(obj) {
			jQuery('#chequeNum').val('');
			document.getElementById('chequeNumberlblError').innerHTML='';
			/* if (obj.value == "RTGS/NEFT") {
			document.getElementById("chequeGrid").style.visibility="visible";
			document.getElementById("mdcNumber").innerHTML = '<s:text name="contra.refNumber" />';
			document.getElementById("mdcDate").innerHTML = '<s:text name="contra.refDate" />';
		} else if(obj.value == "pex") {
			 document.getElementById("chequeGrid").style.visibility="hidden";
		} else { */
		//<s:if test="egovCommon.isShowChequeNumber()">
		 //document.getElementById("chequeGrid").style.visibility="visible";
		 /* </s:if>
		 <s:else>
		 document.getElementById("chequeGrid").style.visibility="hidden";
		 </s:else> */
			document.getElementById("mdcNumber").innerHTML = '<s:text name="contra.chequeNumber" />';
			document.getElementById("mdcDate").innerHTML = '<s:text name="contra.chequeDate" />';
			
		//}
	}

	
	function decimalvalue(obj){
	var regexp_decimalvalue = /[^0-9.]/g ;
	if(jQuery('#modeOfCollectioncheque').is(':checked')){
		if(jQuery(obj).val().match(regexp_decimalvalue)){
			jQuery(obj).val( jQuery(obj).val().replace(regexp_decimalvalue,'') );
		}else if(jQuery(obj).val().length >= 7){
			var subString = jQuery(obj).val().substring(0,6);
			jQuery(obj).val(subString);
		}		
	}
	}

	function validateChequeNumber(obj){
		if(jQuery('#modeOfCollectioncheque').is(':checked')){
			document.getElementById('chequeNumberlblError').innerHTML='';
			if(obj.value.length != 6){
				document.getElementById('chequeNumberlblError').innerHTML =  '<s:text name="msg.cheque.number.must.be.six.digits" />';
			}
		}
	}

	if('<s:text name="%{isBankBalanceMandatory()}"/>'=='')
		document.getElementById('lblError').innerHTML = '<s:text name="msg.bank.bal.mendatory.param.not.defined" />';
		
		<s:if test="%{!validateUser('createpayment')}">
			//document.getElementById('errorSpan').innerHTML='<s:text name="payment.invalid.user"/>';
			if(document.getElementById('vouchermis.departmentid'))
			{
				var d = document.getElementById('vouchermis.departmentid');
				d.options[d.selectedIndex].text.value=d;
			}
			</s:if>
			
			<s:if test="%{getUserDepartment()!=null}">
				var d = document.getElementById('vouchermis.departmentid');
				var val='<s:text name="%{getUserDepartment()}"/>';
				d.value=val;
		</s:if>
		
		jQuery(document).ready(function() {
			jQuery("#voucherDate").datepicker().datepicker("setDate", new Date());
			});
	function viewdept(){
		var dept = document.getElementById('vouchermis.departmentid').value;
		//var dept = dom.get('voucher.department').value;
		//alert(":::::DepID::"+dept);
		var bankid = document.getElementById('fromAccountNumber').value;
		//alert(":::::bankID::"+bankid);
	}
		function validateReassignSurrenderChequeNumber(obj)
		{
			if(isNaN(obj.value))
			{
				bootbox.alert('Only Number is allowed."/>');
				obj.value='';
				return false;
			}
			if(obj.value.length!=6)
			{
				bootbox.alert("Cheque No. must be six Digit.");
				obj.value='';
				return false;
			}
			//Cheque number might contain . or - which is not handled by isNaN
			var pattPeriod=/\./i;
			var pattNegative=/-/i;
			if(obj.value.match(pattPeriod)!=null || obj.value.match(pattNegative)!=null )
			{
				bootbox.alert('<s:text name="msg.cheque.num.should.contaain.only.number"/>');
				obj.value='';
				return false;
			}
			var dept = document.getElementById('vouchermis.departmentid').value;
			
			var bankid = document.getElementById('fromAccountNumber').value;
			
				
			if(dom.get('voucher.department') && dom.get('voucher.department').value==-1)
			{
				
				bootbox.alert('<s:text name="msg.select.cheque.issued.dept"/>');
				obj.value='';
				return false;
			}
			
			
			var url = '${pageContext.request.contextPath}/voucher/common-ajaxValidateReassignSurrenderChequeNumber1.action?bankaccountId='+document.getElementById('fromAccountNumber').value+'&chequeNumber='+obj.value+'&departmentId='+dept;
			
			var transaction = YAHOO.util.Connect.asyncRequest('POST', url, callbackReassign, null);
		}
		var callback = {
			success: function(o) {  
				var res=o.responseText;
				res = res.split('~');
				if(res[1]=='false')
				{
					bootbox.alert('<s:text name="msg.enter.valid.cheque.number.or.cheque.already.used"/>', function() {
						return true;
					});
					document.getElementById('chequeNumber'+parseInt(res[0])).value='';
				}
		    },
		    failure: function(o) {
		    	bootbox.alert('failure');
		    }
		}
			var callbackReassign = {
			success: function(o) {
				var res=o.responseText;
				res = res.split('~');
				if(res[1]=='false')
				{
					bootbox.alert("Cheque No. is Already Used or not in Range.");     
					document.getElementById('chequeNumber'+parseInt(res[0])).value='';
				}
		    },
		    failure: function(o) {
		    	bootbox.alert('failure');
		    }
		}
		
</script>
</body>
</html>
