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


<jsp:include page="../voucher/fromvouchertrans-filter-new.jsp" />
<script type="text/javascript"
	src="/services/egi/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}"></script>
<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
<tr>
	<td class="greybox"></td>
	<td class="greybox"><s:text name="bank" /> <span class="greybox"><span
			class="mandatory1">*</span></span></td>
		
	<s:if test="%{bankBranchList.size()>0}">
	 <td class="greybox">
      <s:select  name="commonBean.bankId" id="bankId"
       list="dropdownData.bankList" listKey="bankBranchId" listValue="bankBranchName" 
       headerKey="-1" headerValue="%{getText('lbl.choose.options')}"
		 onChange="populateAccNum(this);"/>
      </td>         
	</s:if>	
	<s:else>	
		<egov:ajaxdropdown id="bankId" fields="['Text','Value']"
		dropdownId="bankId"
		url="/voucher/common-ajaxLoadBanksByFundAndType.action" /> 
	    <td class="greybox"><s:select name="commonBean.bankId" id="bankId"
			list="dropdownData.bankList" listKey="bankBranchId"
			listValue="bankBranchName" headerKey="" headerValue="%{getText('lbl.choose.options')}"
			onChange="populateAccNum(this);"/></td>
		</s:else>	
	
	<td class="greybox"><s:text name="amount" /><span
		class="mandatory1">*</span></td>
	<td class="greybox"><s:textfield name="commonBean.amount" readOnly="true"
			id="amount" maxlength="18" onblur="validateDigitsAndDecimal(this);"
			cssStyle="text-align:right" /></td>
</tr>

<tr>
	<td class="bluebox" width="10%"></td>
	<egov:ajaxdropdown id="accountNumber" fields="['Text','Value']"
		dropdownId="accountNumber"
		url="voucher/common-ajaxLoadBankAccounts.action" />
	<td class="bluebox" width="22%"><s:text name="account.number" /><span
		class="bluebox"><span class="mandatory1">*</span></span></td>
	<td class="bluebox" width="22%"><s:select
			name="commonBean.accountNumberId" id="accountNumber"
			list="dropdownData.accNumList" listKey="id"
			listValue="accountnumber+'-'+accounttype" headerKey=""
			headerValue="%{getText('lbl.choose.options')}"
			onChange="populateNarration(this);populateAvailableBalance(this);" />
		<!--<s:textfield name="accnumnar" id="accnumnar"
			value="%{commonBean.accnumnar}" readonly="true" tabindex="-1" /></td>-->
	<egov:updatevalues id="availableBalance" fields="['Text']"
		url="/payment/payment-ajaxGetAccountBalance.action" />
	<td class="bluebox" id="balanceText"><s:text
			name="balance.available" /></td>
	<td class="bluebox" id="balanceAvl"><s:textfield
			name="commonBean.availableBalance" id="availableBalance"
			readonly="true" style="text-align:right"
			value="%{commonBean.availableBalance}" /></td>


</tr>
<td class="greybox"></td>
<td class="greybox"><s:text name="modeofpayment" /> <span
	class="greybox"><span class="mandatory1">*</span></span></td>
<td class="greybox"><s:radio name="commonBean.modeOfPayment"
		id="modeOfPayment" list="%{modeOfPaymentMap}" /></td>
<td class="greybox"><s:text name="paidto" /><span
	class="mandatory1">*</span></td>
<td class="greybox"><s:textfield name="commonBean.paidTo" class = "patternvalidation" readOnly="true"
		id="paidTo" maxlength="250" data-pattern="alphanumericwithspaceanddot"
		value="%{commonBean.paidTo}"	cssStyle="text-align:right"/></td>
</tr>
<%-- <tr>
	<td class="bluebox"></td>
	<td class="bluebox"><s:text name="link.ref.number" /><span
		class="bluebox"></td>
	<td class="bluebox"><s:textfield
			name="commonBean.linkReferenceNumber"
			id="commonBean.linkReferenceNumber" size="25" /> <img
		src="/services/egi/resources/erp2/images/searchicon.gif"
		onclick="openViewVouchers()" /> <s:hidden
			name="commonBean.documentId" id="commonBean.documentId" /></td>
	<TD></TD>
	<TD></TD>
</tr> --%>
<tr>
	<td class="greybox"></td>
	<td class="greybox"><s:text name="document.number" /><span
		class="greybox"><span class="mandatory1">*</span></span></td>
	<td class="greybox"><s:textfield name="commonBean.documentNumber" readOnly="true"
			id="commonBean.documentNumber" value="%{commonBean.documentNumber}" size="25" /></td>
	<td class="greybox"><s:text name="document.date" /><span
		class="greybox"><span class="mandatory1">*</span></span></td>
	<s:date name='commonBean.documentDate' var="commonBean.documentDateId"
		format='dd/MM/yyyy' />
	<td class="greybox"><s:textfield id="documentDate" readonly="true"
			name="commonBean.documentDate" value="%{commonBean.documentdate}"
			data-date-end-date="0d"
			onkeyup="DateFormat(this,this.value,event,false,'3')"
			placeholder="DD/MM/YYYY" class="form-control datepicker"
			data-inputmask="'mask': 'd/m/y'" /></td>
</tr>


<s:if test="%{instrumentHeaderList.size()>0}">
	<s:iterator var="p" value="instrumentHeaderList" status="s">
		<tr>
			<td class="bluebox"></td>
			<td class="bluebox"><s:text name="lbl.cheque.number"/> </td>
			<td class="bluebox"><s:property value="%{instrumentNumber}" />
			</td>
			<td class="bluebox"><s:text name="lbl.cheque.date"/></td>
			<td class="bluebox"><s:date name="%{instrumentDate}"
					format="dd/MM/yyyy" /></td>
		</tr>
		<tr>
			<td class="greybox"></td>
			<td class="greybox"><s:text name="lbl.party.name"/></td>
			<td class="greybox"><s:property value="%{payTo}" /></td>
		</tr>
	</s:iterator>
</s:if>


<s:if test="%{commonBean.refundable == 'Y'}">
    <tr>
	<td class="bluebox"></td>
	<td class="bluebox">Refund Narration<span class="mandatory1">*</span></td>
	<td class="bluebox" colspan="3"><s:textarea name="description" value="%{commonBean.narrtion}"
			id="description" style="width:95%" /></td>
    </tr>
</s:if>
<<s:else>
 <tr>
	<td class="bluebox"></td>
	<td class="bluebox"><s:text name="voucher.narration" /><span class="mandatory1">*</span></td>
	<td class="bluebox" colspan="3"><s:textarea name="description" value="%{commonBean.narrtion}" 
			id="description" style="width:95%" /></td>
</tr>
</s:else>

<tr>
	<td class="greybox"></td>
	<td class="greybox"><s:text name="payment.firstsignatory" /><span
		class="greybox"><span class="mandatory1">*</span></span></td>
	<td class="greybox"><s:select name="firstsignatory" headerKey="-1"
			headerValue="Select First Signatory" 
			list="#{'Commissioner':'Commissioner' ,'Additional Commissioner':'Additional Commissioner' ,'Deputy Commissioner':'Deputy Commissioner','Executive Officer':'Executive Officer'}"
			 id="firstsignatory" /></td>
	<td class="greybox"><s:text name="payment.secondsignatory" /><span
		class="greybox"><span class="mandatory1">*</span></span></td>
	<td class="greybox"><s:select name="secondsignatory" headerKey="-1"
			headerValue="Select Second Signatory"
			list="#{'Commissioner':'Commissioner' ,'Additional Commissioner':'Additional Commissioner' ,'Deputy Commissioner':'Deputy Commissioner','Executive Officer':'Executive Officer','Accounts Officer':'Accounts Officer'}"
			 id="secondsignatory"/></td>
</tr>
<tr>
	<td class="greybox"></td>
	<td class="greybox"><s:text name="backlog.entry" /><span
		class="greybox"><span class="mandatory1">*</span></span></td>
	<td class="greybox"><s:select name="backlogEntry" headerKey="-1"
			headerValue="Select" 
			list="#{'Y':'Yes' ,'N':'No'}" id="backlogEntry" /></td>
	<td class="greybox">&nbsp;</td>
	<td class="greybox">&nbsp;</td>
</tr>
</table>
<div id="budgetSearchGrid">
	<div align="center">
		<br>
		<table cellspacing="0" cellpadding="0" border="0" width="95%"
			style="border-right: 0px solid rgb(197, 197, 197);"
			class="tablebottom">
			<tbody>
				<tr>
					<td colspan="6">
						<div id="labelAD" align="center">
							<div class="subheadsmallnew">
								<strong><s:text name="lbl.account.details"/></strong>
							</div>
						</div>
						<div class="yui-skin-sam" align="center">
							<div id=billDetailTable></div>
						</div> <script>
							makeVoucherDetailTable();
							document.getElementById('billDetailTable')
									.getElementsByTagName('table')[0].width = "95%";
						</script>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</div> 

<div id="codescontainer"></div>

<div id="labelSL" align="center">
	<div id="budgetSearchGrid">
		<div align="center">
			<br>
			<table cellspacing="0" cellpadding="0" border="0" width="95%"
				style="border-right: 0px solid rgb(197, 197, 197);"
				class="tablebottom">
				<tbody>
					<tr>
						<td colspan="6">
							<div class="subheadsmallnew">
								<strong><s:text name="lbl.subledger.details"/></strong>
							</div>
							</div>

							<div class="yui-skin-sam" align="center">
								<div id="subLedgerTable"></div>


								<script>
									makeSubLedgerTable();
									document.getElementById('subLedgerTable')
											.getElementsByTagName('table')[0].width = "95%"
								</script>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div> 

