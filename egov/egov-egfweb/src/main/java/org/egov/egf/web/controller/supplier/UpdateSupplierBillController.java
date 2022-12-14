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
package org.egov.egf.web.controller.supplier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.service.AccountdetailtypeService;
import org.egov.commons.service.ChartOfAccountsService;
import org.egov.commons.service.CheckListService;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.egf.masters.services.PurchaseOrderService;
import org.egov.egf.masters.services.SupplierService;
import org.egov.egf.supplierbill.service.SupplierBillService;
import org.egov.egf.utils.FinancialUtils;
import org.egov.egf.web.controller.expensebill.BaseBillController;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationException;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infstr.models.EgChecklists;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.bills.EgBillPayeedetails;
import org.egov.model.bills.EgBilldetails;
import org.egov.model.bills.EgBillregister;
import org.egov.utils.FinancialConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/supplierbill")
public class UpdateSupplierBillController extends BaseBillController {

    private static final String APPROVER_NAME = "approverName";

    private static final String DESIGNATION = "designation";

    private static final String APPROVAL_COMENT = "approvalComent";

    private static final String SUPPLIER = "Supplier";
    
    private static final String FILE = "file";

    private static final String PURCHASE_ORDER = "PurchaseOrder";

    private static final String SUPPLIER_ID = "supplierId";

    private static final String WORKFLOW_HISTORY = "workflowHistory";

    private static final String CURRENT_STATE = "currentState";

    private static final String STATE_TYPE = "stateType";

    private static final String NET_PAYABLE_CODES = "netPayableCodes";

    private static final String SUPPLIERS = "suppliers";

    private static final String NET_PAYABLE_AMOUNT = "netPayableAmount";

    private static final String APPROVAL_DESIGNATION = "approvalDesignation";

    private static final String EG_BILLREGISTER = "egBillregister";

    private static final String APPROVAL_POSITION = "approvalPosition";

    private static final String SUPPLIERBILL_VIEW = "supplierbill-view";
    
    private static final String SUPPLIERBILL_UPDATE_WORKFLOW = "supplierbill-update-Workflow";//added abhishek on 18042021

    private static final String NET_PAYABLE_ID = "netPayableId";
    @Autowired
    private DocumentUploadRepository documentUploadRepository;
    @Autowired
    private SupplierBillService supplierBillService;
    @Autowired
    @Qualifier("chartOfAccountsService")
    private ChartOfAccountsService chartOfAccountsService;
    @Autowired
    private FinancialUtils financialUtils;
    @Autowired
    private CheckListService checkListService;
    @Autowired
    private MicroserviceUtils microServiceUtil;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    @Autowired
    private AccountdetailtypeService accountdetailtypeService;

    public UpdateSupplierBillController(final AppConfigValueService appConfigValuesService) {
        super(appConfigValuesService);
    }

    @Override
    protected void setDropDownValues(final Model model) {
        super.setDropDownValues(model);
        model.addAttribute(SUPPLIERS, supplierService.getAllActiveSuppliers());
        model.addAttribute(NET_PAYABLE_CODES, chartOfAccountsService.getSupplierNetPayableAccountCodes());
    }

    @ModelAttribute(EG_BILLREGISTER)
    public EgBillregister getEgBillregister(@PathVariable String billId) {
        if (billId.contains("showMode")) {
            String[] billIds = billId.split("\\&");
            billId = billIds[0];
        }
        return supplierBillService.getById(Long.parseLong(billId));
    }

    @RequestMapping(value = "/update/{billId}", method = RequestMethod.GET)
    public String updateForm(final Model model, @PathVariable final String billId,
            final HttpServletRequest request) throws ApplicationException {
        final EgBillregister egBillregister = supplierBillService.getById(Long.parseLong(billId));
        final List<DocumentUpload> documents = documentUploadRepository.findByObjectId(Long.valueOf(billId));
        egBillregister.setDocumentDetail(documents);
        List<Map<String, Object>> budgetDetails = null;
        List<String>  validActions = Arrays.asList("Forward","SaveAsDraft");
        setDropDownValues(model);
        model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
        if (egBillregister.getState() != null)
            model.addAttribute(CURRENT_STATE, egBillregister.getState().getValue());
        model.addAttribute(WORKFLOW_HISTORY,
                financialUtils.getHistory(egBillregister.getState(), egBillregister.getStateHistory()));
        model.addAttribute(SUPPLIER_ID,
                purchaseOrderService.getByOrderNumber(egBillregister.getWorkordernumber()).getSupplier().getId());
        prepareWorkflow(model, egBillregister, new WorkflowContainer());
        egBillregister.getBillDetails().addAll(egBillregister.getEgBilldetailes());
        prepareBillDetailsForView(egBillregister);
        prepareSupplierBillDetailsForView(egBillregister);
        supplierBillService.validateSubledgeDetails(egBillregister);
        final List<CChartOfAccounts> supplierPayableAccountList = chartOfAccountsService
                .getNetPayableCodes();
        for (final EgBilldetails details : egBillregister.getBillDetails())
            if (supplierPayableAccountList != null && !supplierPayableAccountList.isEmpty()
                    && supplierPayableAccountList.contains(details.getChartOfAccounts())) {
                model.addAttribute(NET_PAYABLE_ID, details.getChartOfAccounts().getId());
                model.addAttribute(NET_PAYABLE_AMOUNT, details.getCreditamount());
            }
        prepareCheckListForEdit(egBillregister, model);

        String department = this.getDepartmentName(egBillregister.getEgBillregistermis().getDepartmentcode());

        if (department != null)
            egBillregister.getEgBillregistermis().setDepartmentName(department);

        if (egBillregister.getEgBillregistermis().getScheme() != null
                && egBillregister.getEgBillregistermis().getScheme().getId() != null) {
            egBillregister.getEgBillregistermis()
                    .setSchemeId(egBillregister.getEgBillregistermis().getScheme().getId().longValue());
        }

        if (egBillregister.getEgBillregistermis().getSubScheme() != null
                && egBillregister.getEgBillregistermis().getSubScheme().getId() != null) {
            egBillregister.getEgBillregistermis()
                    .setSubSchemeId(egBillregister.getEgBillregistermis().getSubScheme().getId().longValue());
        }

        model.addAttribute(EG_BILLREGISTER, egBillregister);

        if (egBillregister.getState() != null &&
                (FinancialConstants.WORKFLOW_STATE_REJECTED.equals(egBillregister.getState().getValue()) ||
                        financialUtils.isBillEditable(egBillregister.getState()))) {
            model.addAttribute("mode", "edit");
            return SUPPLIERBILL_UPDATE_WORKFLOW;
        }
        else if (egBillregister.getState() != null
                && (FinancialConstants.BUTTONSAVEASDRAFT.equals(egBillregister.getState().getValue()) )) {
            model.addAttribute("mode", "edit");
            model.addAttribute("validActionList", validActions);
            return "supplierbill-update";
        }
        else {
            model.addAttribute("mode", "edit");
            if (egBillregister.getEgBillregistermis().getBudgetaryAppnumber() != null &&
                    !egBillregister.getEgBillregistermis().getBudgetaryAppnumber().isEmpty()) {
                budgetDetails = supplierBillService.getBudgetDetailsForBill(egBillregister);
            }
            model.addAttribute("budgetDetails", budgetDetails);
            //return SUPPLIERBILL_VIEW;//comment by abhishek on 18042021
            return SUPPLIERBILL_UPDATE_WORKFLOW;
        }

    }

    private void prepareCheckListForEdit(final EgBillregister egBillregister, final Model model) {
        final List<EgChecklists> checkLists = checkListService.getByObjectId(egBillregister.getId());
        egBillregister.getCheckLists().addAll(checkLists);
        final StringBuilder selectedCheckList = new StringBuilder();
        for (final EgChecklists checkList : egBillregister.getCheckLists()) {
            selectedCheckList.append(checkList.getAppconfigvalue().getId());
            selectedCheckList.append("-");
            selectedCheckList.append(checkList.getChecklistvalue());
            selectedCheckList.append(",");
        }
        if (!checkLists.isEmpty())
            model.addAttribute("selectedCheckList", selectedCheckList.toString().substring(0, selectedCheckList.length() - 1));
    }
    
    private void populateSubLedgerDetails(final EgBillregister egBillregister, final BindingResult resultBinder) {
        EgBillPayeedetails payeeDetail = null;
        Boolean check = false;
        Boolean poExist = false;
        Boolean supplierExist = false;
        for (final EgBilldetails details : egBillregister.getEgBilldetailes()) {
            details.setEgBillPaydetailes(new HashSet<>());
            check = false;
            poExist = false;
            supplierExist = false;
            if (null!=details.getChartOfAccounts()&& details.getChartOfAccounts().getChartOfAccountDetails() != null
                    && !details.getChartOfAccounts().getChartOfAccountDetails().isEmpty()) {
                for (CChartOfAccountDetail cad : details.getChartOfAccounts().getChartOfAccountDetails()) {
                    if (cad.getDetailTypeId() != null) {
                        if (cad.getDetailTypeId().getName().equalsIgnoreCase(PURCHASE_ORDER)) {
                            poExist = true;
                        }
                        if (cad.getDetailTypeId().getName().equalsIgnoreCase(SUPPLIER)) {
                            supplierExist = true;
                        }
                        if (!cad.getDetailTypeId().getName().equalsIgnoreCase(PURCHASE_ORDER)
                                && !cad.getDetailTypeId().getName().equalsIgnoreCase(SUPPLIER)) {
                            check = true;
                        }
                        if (check) {
                            resultBinder.reject("msg.supplier.bill.wrong.sub.ledger.mapped",
                                    new String[] { details.getChartOfAccounts().getGlcode() }, null);
                        }
                    }
                }

                if (poExist || (poExist && supplierExist)) {
                    payeeDetail = new EgBillPayeedetails();
                    payeeDetail.setEgBilldetailsId(details);
                    if (details.getDebitamount() != null && details.getDebitamount().compareTo(BigDecimal.ZERO) == 1)
                        payeeDetail.setDebitAmount(details.getDebitamount());
                    if (details.getCreditamount() != null && details.getCreditamount().compareTo(BigDecimal.ZERO) == 1)
                        payeeDetail.setCreditAmount(details.getCreditamount());
                    payeeDetail.setAccountDetailTypeId(accountdetailtypeService.findByName(PURCHASE_ORDER).getId());
                    payeeDetail.setAccountDetailKeyId(
                            purchaseOrderService.getByOrderNumber(egBillregister.getWorkordernumber()).getId().intValue());
                } else if (supplierExist) {
                    payeeDetail = new EgBillPayeedetails();
                    payeeDetail.setEgBilldetailsId(details);
                    if (details.getDebitamount() != null && details.getDebitamount().compareTo(BigDecimal.ZERO) == 1)
                        payeeDetail.setDebitAmount(details.getDebitamount());
                    if (details.getCreditamount() != null && details.getCreditamount().compareTo(BigDecimal.ZERO) == 1)
                        payeeDetail.setCreditAmount(details.getCreditamount());
                    payeeDetail.setAccountDetailTypeId(accountdetailtypeService.findByName(SUPPLIER).getId());
                    payeeDetail.setAccountDetailKeyId(
                            purchaseOrderService.getByOrderNumber(egBillregister.getWorkordernumber()).getSupplier().getId()
                                    .intValue());
                }
                payeeDetail.setLastUpdatedTime(new Date());
                details.getEgBillPaydetailes().add(payeeDetail);
            }
        }
    }

    private void prepareSupplierBillDetailsForView(final EgBillregister egBillregister) {

        List<CChartOfAccounts> netPayableList = chartOfAccountsService.getSupplierNetPayableAccountCodes();
        Map<String, CChartOfAccounts> coaMap = new HashMap<>();
        for (CChartOfAccounts coa : netPayableList) {
            coaMap.put(coa.getGlcode(), coa);
        }
        egBillregister.setCreditDetails(new ArrayList<>());
        egBillregister.setDebitDetails(new ArrayList<>());
        egBillregister.setNetPayableDetails(new ArrayList<>());
        for (EgBilldetails bd : egBillregister.getEgBilldetailes()) {
            if (bd.getDebitamount() != null && bd.getDebitamount().compareTo(BigDecimal.ZERO) == 1) {
                egBillregister.getDebitDetails().add(bd);
            }

            if (bd.getCreditamount() != null && bd.getCreditamount().compareTo(BigDecimal.ZERO) == 1
                    && coaMap.get(bd.getChartOfAccounts().getGlcode()) == null) {
                egBillregister.getCreditDetails().add(bd);
            }

            if (bd.getCreditamount() != null && bd.getCreditamount().compareTo(BigDecimal.ZERO) == 1
                    && coaMap.get(bd.getChartOfAccounts().getGlcode()) != null) {
                egBillregister.getNetPayableDetails().add(bd);
            }

        }
    }

    @RequestMapping(value = "/update/{billId}", method = RequestMethod.POST)
    public String update(@ModelAttribute(EG_BILLREGISTER) final EgBillregister egBillregister,
            final BindingResult resultBinder, final RedirectAttributes redirectAttributes, final Model model,
            final HttpServletRequest request, @RequestParam final String workFlowAction)
            throws ApplicationException, IOException {

        String mode = "";
        EgBillregister updatedEgBillregister = null;
//adding upload file
        String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes(FILE);
        List<DocumentUpload> list = new ArrayList<>();
        UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles(FILE);
        String[] fileName = ((MultiPartRequestWrapper) request).getFileNames(FILE);
        if (uploadedFiles != null)
            for (int i = 0; i < uploadedFiles.length; i++) {

                Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
                byte[] fileBytes = Files.readAllBytes(path);
                ByteArrayInputStream bios = new ByteArrayInputStream(fileBytes);
                DocumentUpload upload = new DocumentUpload();
                upload.setInputStream(bios);
                upload.setFileName(fileName[i]);
                upload.setContentType(contentType[i]);
                list.add(upload);
            }

        egBillregister.setDocumentDetail(list);
        if (request.getParameter("mode") != null)
            mode = request.getParameter("mode");

        Long approvalPosition = 0l;
        String approvalComment = "";
        String apporverDesignation = "";

        if (request.getParameter(APPROVAL_COMENT) != null)
            approvalComment = request.getParameter(APPROVAL_COMENT);

        if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
            approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));

        if ((approvalPosition == null || approvalPosition.equals(Long.valueOf(0)))
                && request.getParameter(APPROVAL_POSITION) != null
                && !request.getParameter(APPROVAL_POSITION).isEmpty())
            approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
        
        if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
    	{
    		approvalPosition =populatePosition();    		
    	}
        
        if (request.getParameter(APPROVAL_DESIGNATION) != null && !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
            apporverDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));

        
        if (egBillregister.getState() != null
                && (FinancialConstants.WORKFLOW_STATE_REJECTED.equals(egBillregister.getState().getValue())
                        || financialUtils.isBillEditable(egBillregister.getState()))) {
            populateBillDetails(egBillregister);
            populateSubLedgerDetails(egBillregister, resultBinder);
            validateBillNumber(egBillregister, resultBinder);
            validateLedgerAndSubledger(egBillregister, resultBinder);
        } else if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT)){
            for(EgBilldetails b:egBillregister.getDebitDetails())
        	{
        		System.out.println("::::::::"+b.getCreditamount()+":::::: "+b.getDebitamount());
        	}
            if(egBillregister.getBillDetails()!=null)
        	{  
        		populateBillDetails(egBillregister);
            	populateSubLedgerDetails(egBillregister, resultBinder);
        	}
        	
           // validateBillNumber(egBillregister, resultBinder);
           // validateLedgerAndSubledger(egBillregister, resultBinder);
        	
            }else {
            	populateBillDetails(egBillregister);
                populateSubLedgerDetails(egBillregister, resultBinder);
                validateBillNumber(egBillregister, resultBinder);
                validateLedgerAndSubledger(egBillregister, resultBinder);
            }
        model.addAttribute(SUPPLIER_ID,
                purchaseOrderService.getByOrderNumber(egBillregister.getWorkordernumber()).getSupplier().getId());

        if (resultBinder.hasErrors()) {
            setDropDownValues(model);
            model.addAttribute(SUPPLIER_ID,
                    purchaseOrderService.getByOrderNumber(egBillregister.getWorkordernumber()).getSupplier().getId());
            model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
            prepareWorkflow(model, egBillregister, new WorkflowContainer());
            model.addAttribute(APPROVAL_DESIGNATION, request.getParameter(APPROVAL_DESIGNATION));
            model.addAttribute(APPROVAL_POSITION, request.getParameter(APPROVAL_POSITION));
            model.addAttribute(NET_PAYABLE_ID, request.getParameter(NET_PAYABLE_ID));
            model.addAttribute(NET_PAYABLE_AMOUNT, request.getParameter(NET_PAYABLE_AMOUNT));
            model.addAttribute(DESIGNATION, request.getParameter(DESIGNATION));
            if (egBillregister.getState() != null
                    && (FinancialConstants.WORKFLOW_STATE_REJECTED.equals(egBillregister.getState().getValue())
                            || financialUtils.isBillEditable(egBillregister.getState()))) {
                prepareValidActionListByCutOffDate(model);
                model.addAttribute("mode", "edit");
                return "supplierbill-update";
            } else {
                model.addAttribute("mode", "view");
                return SUPPLIERBILL_VIEW;
            }
        } else {
            try {
                if (null != workFlowAction)
                    updatedEgBillregister = supplierBillService.update(egBillregister, approvalPosition, approvalComment, null,
                            workFlowAction, mode, apporverDesignation);
            } catch (final ValidationException e) {
                setDropDownValues(model);
                model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
                prepareWorkflow(model, egBillregister, new WorkflowContainer());
                model.addAttribute(APPROVAL_DESIGNATION, request.getParameter(APPROVAL_DESIGNATION));
                model.addAttribute(APPROVAL_POSITION, request.getParameter(APPROVAL_POSITION));
                model.addAttribute(NET_PAYABLE_ID, request.getParameter(NET_PAYABLE_ID));
                model.addAttribute(NET_PAYABLE_AMOUNT, request.getParameter(NET_PAYABLE_AMOUNT));
                model.addAttribute(DESIGNATION, request.getParameter(DESIGNATION));
                if (egBillregister.getState() != null
                        && (FinancialConstants.WORKFLOW_STATE_REJECTED.equals(egBillregister.getState().getValue())
                                || financialUtils.isBillEditable(egBillregister.getState()))) {
                    prepareValidActionListByCutOffDate(model);
                    model.addAttribute("mode", "edit");
                    return "supplierbill-update";
                } else {
                    model.addAttribute("mode", "view");
                    return SUPPLIERBILL_VIEW;
                }
            }

            redirectAttributes.addFlashAttribute(EG_BILLREGISTER, updatedEgBillregister);

            // For Get Configured ApprovalPosition from workflow history
            if (approvalPosition == null || approvalPosition.equals(Long.valueOf(0)))
                approvalPosition = supplierBillService.getApprovalPositionByMatrixDesignation(
                        egBillregister, null, mode, workFlowAction);

            //final String approverName = String.valueOf(request.getParameter(APPROVER_NAME));
            String approverName = String.valueOf(request.getParameter("approverName"));
            if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
        	{
        		approverName =populateEmpName();
        	}
            else if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONREJECT))
        	{
            	/*if(approvalPosition==90)
            		approvalPosition=315l;*/
        		approverName =getEmployeeName(approvalPosition);
        	}
            final String approverDetails = financialUtils.getApproverDetails(workFlowAction,
                    updatedEgBillregister.getState(), updatedEgBillregister.getId(), approvalPosition, approverName);
            System.out.println("Supplier Approval Details "+approverDetails);
            return "redirect:/supplierbill/success?approverDetails=" + approverDetails + "&billNumber="
                    + updatedEgBillregister.getBillnumber();
        }
    }

    @RequestMapping(value = "/view/{billId}", method = RequestMethod.GET)
    public String view(final Model model, @PathVariable String billId,
            final HttpServletRequest request) throws ApplicationException {
        if (billId.contains("showMode")) {
            String[] billIds = billId.split("\\&");
            billId = billIds[0];
        }
        final EgBillregister egBillregister = supplierBillService.getById(Long.parseLong(billId));
        final List<DocumentUpload> documents = documentUploadRepository.findByObjectId(Long.valueOf(billId));
        egBillregister.setDocumentDetail(documents);
        Department dept = microServiceUtil.getDepartmentByCode(egBillregister.getEgBillregistermis().getDepartmentcode());
        egBillregister.getEgBillregistermis().setDepartmentName(dept.getName());
        setDropDownValues(model);
        egBillregister.getBillDetails().addAll(egBillregister.getEgBilldetailes());
        model.addAttribute("mode", "readOnly");
        prepareBillDetailsForView(egBillregister);
        prepareCheckList(egBillregister);
        final List<CChartOfAccounts> supplierPayableAccountList = chartOfAccountsService.getSupplierNetPayableAccountCodes();
        for (final EgBilldetails details : egBillregister.getBillDetails())
            if (supplierPayableAccountList != null && !supplierPayableAccountList.isEmpty()
                    && supplierPayableAccountList.contains(details.getChartOfAccounts()))
                model.addAttribute(NET_PAYABLE_AMOUNT, details.getCreditamount());
        model.addAttribute(EG_BILLREGISTER, egBillregister);
        return SUPPLIERBILL_VIEW;
    }

    private void prepareCheckList(final EgBillregister egBillregister) {
        final List<EgChecklists> checkLists = checkListService.getByObjectId(egBillregister.getId());
        egBillregister.getCheckLists().addAll(checkLists);
    }

    private String getDepartmentName(String departmentCode) {

        List<Department> deptlist = this.masterDataCache.get("egi-department");
        String departmentName = null;

        if (null != deptlist && !deptlist.isEmpty()) {

            List<Department> dept = deptlist.stream()
                    .filter(department -> departmentCode.equalsIgnoreCase(department.getCode()))
                    .collect(Collectors.toList());
            if (null != dept && dept.size() > 0)
                departmentName = dept.get(0).getName();
        }

        if (null == departmentName) {
            Department dept = this.microServiceUtil.getDepartmentByCode(departmentCode);
            if (null != dept)
                departmentName = dept.getName();
        }

        return departmentName;
    }

    private String populateEmpName() {
    	Long empId = ApplicationThreadLocals.getUserId();
    	String empName=null;
    	Long pos=null;
    	List<EmployeeInfo> employs = microServiceUtil.getEmployee(empId, null,null, null);
    	if(null !=employs && employs.size()>0 )
    	{
    		//pos=employs.get(0).getAssignments().get(0).getPosition();
    		empName=employs.get(0).getUser().getName();
    		
    	}
		return empName;
	}
    
    private Long populatePosition() {
    	Long empId = ApplicationThreadLocals.getUserId();
    	Long pos=null;
    	List<EmployeeInfo> employs = microServiceUtil.getEmployee(empId, null,null, null);
    	if(null !=employs && employs.size()>0 )
    	{
    		pos=employs.get(0).getAssignments().get(0).getPosition();
    		
    	}
    	//System.out.println("pos-----populatePosition---()----------------------"+pos);
		return pos;
	}
    
    public String getEmployeeName(Long empId){
        
        return microServiceUtil.getEmployee(empId, null, null, null).get(0).getUser().getName();
     }
    
}