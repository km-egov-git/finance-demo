
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

<%@ taglib prefix="egov" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%@ taglib prefix="s" uri="/WEB-INF/taglib/struts-tags.tld" %>  
<%@ include file="/includes/taglibs.jsp" %>
<%@ taglib prefix="egov-authz" uri="/WEB-INF/taglib/egov-authz.tld" %> 
<link rel="stylesheet" type="text/css" href="<egov:url path='/yui/assets/skins/sam/autocomplete.css'/>" />
<head>
	<title>Receipt</title>
	
<script>
	
function openSource(){
	
	//alert("openSource()");
	var vouchermissourcepath=document.getElementById('vouchermissourcepath').value;
	
	if(vouchermissourcepath=="" || vouchermissourcepath=='null')
		bootbox.alert('Source is not available');
	else{
		var url = '/services/collection/receipts/receipt-viewReceipts.action?selectedReceipts='+vouchermissourcepath+'&showMode=view';
		window.open(url,'Source','resizable=yes,scrollbars=yes,left=300,top=40, width=900, height=700')
	}   
}
</script>
	
	
</head>
<body>
<s:form theme="simple" name="searchReceiptForm" action="searchReceipt-search.action">
<div class="subheadnew">
</div>
<span class="mandatory1"> 
	<font style='color: green;font-weight: bold;text-align: center;' > 
		<s:actionerror /> 
		<s:fielderror />
		<s:actionmessage />
		<h1><s:property value="message"/></h1>
	</font>
</span>
 <input name="selectedReceipts" type="hidden" id="selectedReceipts"
				value="${reciptNumber}"/> 
<%-- <input name="selectedReceipts" type="hidden" id="selectedReceipts"
				value="${paymentId}"/>	 --%>			
<input type="hidden" name="vouchermissourcepath" value="${vouchermissourcepath}" id="vouchermissourcepath">	
				
<div class="buttonbottom">
  <input name="button32" type="button" class="buttonsubmit" id="button32" value="View" onclick="openSource()"/>
  <input name="button32" type="button" class="buttonsubmit" id="button32" value="Print" onclick="openSource()"/> 
</div>
</s:form>
</body>
	
