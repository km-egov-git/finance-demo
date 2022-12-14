/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

jQuery('#btnsearch').click(function(e) {

	callAjaxSearch();
});

function getFormData($form) {
	var unindexed_array = $form.serializeArray();
	var indexed_array = {};

	$.map(unindexed_array, function(n, i) {
		indexed_array[n['name']] = n['value'];
	});

	return indexed_array;
}

function prepareHeading(){
	var heading= "Supplier Search Result ";
		
	$("#supplierresult").html(heading);

return heading;
		
}

function callAjaxSearch() {
	drillDowntableContainer = jQuery("#resultTable");
	jQuery('.report-section').removeClass('display-hide');
	var heading1 = prepareHeading();
	reportdatatable = drillDowntableContainer
			.dataTable({
				ajax : {
					url : "/services/EGF/supplier/ajaxsearch/" + $('#mode').val(),
					type : "POST",
					"data" : getFormData(jQuery('form'))
				},
				"fnRowCallback" : function(row, data, index) {
					$(row).on(
							'click',
							function() {
								console.log(data.id);
								 var form = document.createElement("form");
								 form.setAttribute("method", "post");
								 form.setAttribute("action", '/services/EGF/supplier/' + $('#mode').val()+ '/' + data.id, '');
								 form.setAttribute("target", "NewFile");
								 document.body.appendChild(form);
								 window.open("post.htm", "NewFile", 'width=800, height=600');
								 form.submit();
								 document.body.removeChild(form);
							});
				},
				"bDestroy" : true,
				dom : "<'row'<'col-xs-12 pull-right'f>r>t<'row buttons-margin'<'col-md-2 col-xs-6'i><'col-md-2 ??col-xs-6'l><'col-md-5 col-xs-6'B><'col-md-3 col-xs-6 text-right'p>>",
				"aLengthMenu" : [ [ 10, 25, 50, -1 ], [ 10, 25, 50, "All" ] ],
				/*"oTableTools" : {
					"sSwfPath" : "../../../../../../egi/resources/global/swf/copy_csv_xls_pdf.swf",
					"aButtons" : [ "xls", "pdf", "print" ]
				},*/
				buttons :[
					{extend:'print',title: ""+heading1+"",filename: 'View Supplier'},
					{extend:'excel',title: ""+heading1+"",filename: 'View Supplier'},
					{ extend:'pdf',title: ""+heading1+"",filename: 'View Supplier'}],
				aaSorting : [],
				columns : [ {
					"data" : "name",
					"sClass" : "text-left"
				}, {
					"data" : "code",
					"sClass" : "text-left"
				}, {
					"data" : "status",
					"sClass" : "text-left"
				} ]
			});
}