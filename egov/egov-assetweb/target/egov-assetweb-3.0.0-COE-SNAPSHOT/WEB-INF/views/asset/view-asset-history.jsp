<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>


<div class="container">
		<div class="panel panel-primary" data-collapsed="0" id="search-asset">
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="view-asset" text="View Asset History" />
				</div>
			</div>
			
			<div class="panel-body">
		<table class="table table-bordered" id="resultHeader">
			<thead>
				<tr>
					<th><spring:message code="lbl-sl-no" text="Sr. No." /></th>
					<th><spring:message code="lbl-asset-assetcategoryname" text="Transaction Date" /></th>
					<th><spring:message code="lbl-asset-department" text="Gross Value before Transaction(Rs.)" /></th>
					<th><spring:message code="lbl-asset-code"
							text="Transaction Type" /></th>
					<th><spring:message code="lbl-asset-code" text="Transaction Amount" /></th>
					<th><spring:message code="lbl-asset-code" text="Gross Value After Transaction(Rs.)" /></th>
					<%-- <c:if test="${isReference}">
						<th><spring:message code="lbl-action" text="Action" /></th>
					</c:if> --%>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="${assetHistoryList!=null && assetHistoryList.size() > 0}">
						<c:forEach items="${assetHistoryList}" var="assetHistory" varStatus="item">

							<tr id="assetView">
										<td>
										${item.index + 1}
										</td>
										<td>
										${assetHistory.transactionDate }
										</td>
										<td>
										${assetHistory.valueBeforeTrxn }
										</td>
										<td>
										${assetHistory.transactionType }
										</td>
										<td>
										${assetHistory.trxnValue}
										</td>
										<td>
										${assetHistory.valueAfterTrxn}
										</td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<td colspan="6">No Records Found..</td>

					</c:otherwise>
				</c:choose>
			</tbody>
		</table>
	</div>
	</div>
	<!-- Result Table Ends -->
	<div align="center"><input type="button" name="button2" id="button2" value="Close" class="btn btn-primary" onclick="window.parent.postMessage('close','*');window.close();"/></div>
</div>


<script src="<cdn:url value='/resources/app/js/i18n/jquery.i18n.properties.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/app/js/common/helper.js?rnd=${app_release_no}' context='/services/EGF'/>"></script>
<script src="<cdn:url value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"></script>


<link type="text/css" rel="stylesheet" href="https://cdn.datatables.net/1.11.3/css/jquery.dataTables.min.css">
<link type="text/css" rel="stylesheet" href="https://cdn.datatables.net/buttons/2.1.0/css/buttons.dataTables.min.css">
<script type="text/javascript" src="https://code.jquery.com/jquery-3.5.1.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/1.11.3/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/2.1.0/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.53/pdfmake.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.53/vfs_fonts.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/2.1.0/js/buttons.html5.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/2.1.0/js/buttons.print.min.js"></script>

<style>
@media (max-width: 768px) {
  .table-bordered tbody > tr {
    border-bottom: 1px solid #ebebeb;
  }
}
@media (max-width: 768px) {
  .table-bordered tbody > tr td {
    border: none;
  }
}
</style>

<script>
    $(document).ready(function() {
   $('#resultHeader').DataTable( {
       dom: 'Bfrtip',
       aaSorting : [],
       buttons: [
           'copy', 'csv', 'excel', 'pdf', 'print'
       ]
   } );
} );

function  selectAssetRef(code, name){
console.log("My value..."+code);
console.log("My value..."+name);
var retVal = name + '~'+ code; 
window.opener.onPopupClose(retVal);//myValue is the value you want to return to main javascript
window.close();
}
</script>