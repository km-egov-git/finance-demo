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
package org.egov.services.voucher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.egov.billsaccounting.services.CreateVoucher;
import org.egov.common.contstants.CommonConstants;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.DocumentUploads;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.commons.utils.DocumentUtils;
import org.egov.egf.expensebill.service.ExpenseBillService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.StringUtils;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.workflow.entity.State;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.EgBillregister;
import org.egov.model.bills.Miscbilldetail;
import org.egov.model.voucher.WorkflowBean;
import org.egov.services.bills.EgBillRegisterService;
import org.egov.services.payment.MiscbilldetailService;
import org.egov.utils.FinancialConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exilant.exility.common.TaskFailedException;

@Transactional(readOnly = true)
@Service
public class PreApprovedActionHelper {
    @Autowired
    @Qualifier("journalVoucherActionHelper")
    private JournalVoucherActionHelper journalVoucherActionHelper;
    @Autowired
    @Qualifier("voucherService")
    private VoucherService voucherService;
    @Autowired
    @Qualifier("createVoucher")
    private CreateVoucher createVoucher;
    
    
    @Autowired
    EgBillRegisterService egBillRegisterService;
    
    @Autowired
    EgwStatusHibernateDAO egwStatusHibernateDAO;

//    @Autowired
//    PositionMasterService positionMasterService;

    @Autowired
    private MicroserviceUtils microserviceUtils;
    
    @Autowired
    SecurityUtils securityUtils;
    
    @Autowired
    @Qualifier("miscbilldetailService")
    private MiscbilldetailService miscbilldetailService;
    
    @Autowired
    private ExpenseBillService expenseBillService;
    @Autowired
    private EgwStatusHibernateDAO egwStatusDAO;
    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
	@Autowired
    private DocumentUtils docUtils;
    
    
    @Transactional
    public CVoucherHeader createVoucherFromBill(CVoucherHeader voucherHeader, WorkflowBean workflowBean, Long billId,
            String voucherNumber, Date voucherDate) throws ApplicationRuntimeException, SQLException, TaskFailedException {
        try {
			/*
			 * Long voucherHeaderId = createVoucher.createVoucherFromBill(billId.intValue(),
			 * null, voucherNumber, voucherDate);
			 */
            Long voucherHeaderId = createVoucher.createVoucherFromBillNew(billId.intValue(), null,
                    voucherNumber, voucherDate,voucherHeader);
            voucherHeader = voucherService.findById(voucherHeaderId, false);
            voucherHeader = sendForApproval(voucherHeader, workflowBean);
        }catch (final ValidationException e) {
            if (e.getErrors().get(0).getMessage() != null && !e.getErrors().get(0).getMessage().equals(StringUtils.EMPTY))
                throw new ValidationException(e.getErrors().get(0).getMessage(), e.getErrors().get(0).getMessage());
            else
                throw new ValidationException("Voucher creation failed", "Voucher creation failed");

        } catch (final Exception e) {

            final List<ValidationError> errors = new ArrayList<>();
            errors.add(new ValidationError("exp", e.getMessage()));
            throw new ValidationException(errors);
        }
        return voucherHeader;
    }

    @Transactional
    public CVoucherHeader sendForApproval(CVoucherHeader voucherHeader, WorkflowBean workflowBean)
    {
    	 
         
    	EgBillregister expenseBill = null;
    	expenseBill =  egBillRegisterService.getBillsByWorkBillNo(voucherHeader.getBillNumber());
        try {

            if (FinancialConstants.CREATEANDAPPROVE.equalsIgnoreCase(workflowBean.getWorkFlowAction())
                    && voucherHeader.getState() == null)
            {
                voucherHeader.setStatus(FinancialConstants.CREATEDVOUCHERSTATUS);
            }
            else
            {
                voucherHeader = journalVoucherActionHelper.transitionWorkFlow(voucherHeader, workflowBean);
                voucherService.applyAuditing(voucherHeader.getState());
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date currentDate = calendar.getTime(); 
            if(workflowBean.getWorkFlowAction().equals("Approve"))
            {
            	if(voucherHeader != null && voucherHeader.getBackdateentry() != null && voucherHeader.getBackdateentry().equalsIgnoreCase("N"))
            	{
            		voucherHeader.setVoucherDate(currentDate);
            	}
            	
            	if(expenseBill!=null)
            	{
            		expenseBill.setStatus(egwStatusHibernateDAO.getEgwStatusByCodeAndModuleType("EXPENSEBILL" ,"Voucher Approved"));
            		System.out.println(expenseBill.getStatus().getCode());
            	}
            }
            voucherService.persist(voucherHeader);
            if(workflowBean.getWorkFlowAction().equals("Forward") && expenseBill!=null)
            {
            	expenseBill.setStatus(egwStatusHibernateDAO.getEgwStatusByCodeAndModuleType("EXPENSEBILL" ,"Voucher Created"));
            	System.out.println("voucher state "+voucherHeader.getState().getId());
				/*
				 * System.out.println("expense state "+expenseBill.getState());
				 * expenseBill.getState().setId(voucherHeader.getState().getId());
				 * expenseBillService.create(expenseBill);
				 */
       		 	persistenceService.getSession().flush();
            }
        } catch (final ValidationException e) {
        	System.out.println("Error "+e.getMessage());
            final List<ValidationError> errors = new ArrayList<>();
            errors.add(new ValidationError("exp", e.getErrors().get(0).getMessage()));
            throw new ValidationException(errors);
        } catch (final Exception e) {
        	System.out.println("Error "+e.getMessage());
            final List<ValidationError> errors = new ArrayList<>();
            errors.add(new ValidationError("exp", e.getMessage()));
            throw new ValidationException(errors);
        }
        return voucherHeader;
    }
    @Transactional
    public void saveDocuments(CVoucherHeader voucherHeader)
    {
 	   List<DocumentUploads> files = voucherHeader.getDocumentDetail() == null ? null : voucherHeader.getDocumentDetail();
        final List<DocumentUploads> documentDetails;
        documentDetails = docUtils.getDocumentDetails(files, voucherHeader,
                CommonConstants.JOURNAL_VOUCHER_OBJECT);
        if (!documentDetails.isEmpty()) {
        	voucherHeader.setDocumentDetail(documentDetails);
        	voucherService.persistDocuments(documentDetails);
        }
    }
    private Boolean validateOwner(State state) {
//        List<Position> positionsForUser = positionMasterService.getPositionsForEmployee(securityUtils.getCurrentUser().getId());
//        return positionsForUser.contains(state.getOwnerPosition());
        Boolean check = false;
//        
        List<Long> positions = new ArrayList();
        Long empId = ApplicationThreadLocals.getUserId();
        List<EmployeeInfo> employs = microserviceUtils.getEmployee(empId, null,null, null);
        
        if(null !=employs && employs.size()>0 )
                
        employs.get(0).getAssignments().forEach(assignment->{
                positions.add(assignment.getPosition());
        });
        
        for (final Long pos : positions)
            if (state.getOwnerPosition()==pos) {
                check = true;
            }
        return check;

    }


}