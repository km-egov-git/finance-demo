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
package org.egov.egf.web.actions.contra;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.billsaccounting.services.CreateVoucher;
import org.egov.billsaccounting.services.VoucherConstant;
import org.egov.commons.Accountdetailkey;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.Bankaccount;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.service.AccountDetailKeyService;
import org.egov.commons.service.AccountdetailtypeService;
import org.egov.egf.commons.EgovCommon;
import org.egov.egf.masters.services.OtherPartyService;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.contra.ContraBean;
import org.egov.model.contra.ContraJournalVoucher;
import org.egov.model.instrument.InstrumentHeader;
import org.egov.model.masters.OtherParty;
import org.egov.services.cheque.ChequeService;
import org.egov.services.instrument.InstrumentService;
import org.egov.services.payment.MiscbilldetailService;
import org.egov.services.payment.PaymentService;
import org.egov.services.voucher.ContraJournalVoucherService;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ContraBTBActionHelper {
	final private static Logger LOGGER = Logger.getLogger(ContraBTBActionHelper.class);
	private static final String MDC_CHEQUE = "cheque";
	private static final String MDC_OTHER = "RTGS/NEFT";
	private static final String MDC_PEX ="pex";
	private static final String EXCEPTION_WHILE_SAVING_DATA = "Exception while saving Data";
	private static final String TRANSACTION_FAILED = "Transaction failed";
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Constants.LOCALE);
	@Autowired
	private InstrumentService instrumentService;
	@Autowired
	private EgovCommon egovCommon;
	@Autowired
	private CreateVoucher createVoucher;
	@Autowired
	@Qualifier("chequeService")
	private ChequeService chequeService;
	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	@Autowired
	@Qualifier("contraJournalVoucherService")
	private ContraJournalVoucherService contraJournalVoucherService;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	@Qualifier("miscbilldetailService")
	private MiscbilldetailService miscbilldetailService;
	@Autowired
	private OtherPartyService otherPartyService;
	@Autowired
	private AccountdetailtypeService accountdetailtypeService;
	@Autowired
	private AccountDetailKeyService accountdetailkeyService;

	private CVoucherHeader voucherHeader2 = null;
	
        @Autowired
        protected MicroserviceUtils microserviceUtils;

	@Transactional
	public CVoucherHeader create(ContraBean contraBean, ContraJournalVoucher contraVoucher,
			CVoucherHeader voucherHeader) throws Exception {
		try {
			voucherHeader2 = null;
			 List<InstrumentHeader> instrumentList = null;
			 
				 instrumentList = instrumentService
							.addToInstrument(createInstruments(contraBean, contraVoucher, voucherHeader));
			
			/*if(contraBean.getModeOfCollection() !=null && !contraBean.getModeOfCollection().equalsIgnoreCase(MDC_PEX))
			{
			instrumentList = instrumentService
					.addToInstrument(createInstruments(contraBean, contraVoucher, voucherHeader));
			}*/
			if (contraBean.getToFundId() != null && !voucherHeader.getFundId().getId().equals(contraBean.getToFundId()))
				voucherHeader = callCreateVoucherForInterFund(voucherHeader, contraVoucher, contraBean);
			else
				voucherHeader = callCreateVoucher(voucherHeader, contraVoucher, contraBean);
			/*if(contraBean.getModeOfCollection() !=null && contraBean.getModeOfCollection().equalsIgnoreCase(MDC_PEX))
			{
				final Bankaccount ba = getBankaccount(contraVoucher.getFromBankAccountId().getId());
				contraVoucher = addOrupdateContraJournalVoucherPEX(contraVoucher,
						voucherHeader, contraBean); 
				
	        	paymentService.createPaymentHeaderForContra( voucherHeader,  ba,contraBean.getModeOfCollection(),contraBean.getAmount().toString());
	        	final Miscbilldetail miscBilldetail = new Miscbilldetail();
	        	miscBilldetail.setBillamount(contraBean.getAmount());
	        	miscBilldetail.setBillVoucherHeader(voucherHeader);
	        	miscBilldetail.setPaidamount(contraBean.getAmount());
	        	miscBilldetail.setPassedamount(contraBean.getAmount());
	        	 if(ba.getPayTo()!=null && !ba.getPayTo().equalsIgnoreCase(""))
	        	  {
	        	miscBilldetail.setPaidto(ba.getPayTo());
	        	 }else {
	        		 miscBilldetail.setPaidto(contraVoucher.getToBankAccountId().getBankbranch().getBank().getName());
	        	 }
	           miscBilldetail.setPaidby(null);
	           miscbilldetailService.create(miscBilldetail);
	           
	          
			}else {*/
			updateInstrument(instrumentList.get(0), voucherHeader);
			contraVoucher = addOrupdateContraJournalVoucher(contraVoucher, instrumentList.get(0), voucherHeader,
					contraBean);
			//}
			ContraJournalVoucher contraVoucher2 = null;
			if (voucherHeader2 != null) {
				final List<Map<String, Object>> createInstrumentMap = createInstrumentsForReceipt(contraBean,
						contraVoucher, voucherHeader);
				// set is pay cheque to 0 saying it is a receipt cheque
				if(contraBean.getModeOfCollection().equalsIgnoreCase(MDC_PEX))
				{
					
					contraVoucher = addOrupdateContraJournalVoucherPEX(contraVoucher,
							voucherHeader, contraBean);   
		        	
				}else {
				createInstrumentMap.get(0).put("Is pay cheque", "0");
				final List<InstrumentHeader> instrumentList2 = instrumentService.addToInstrument(createInstrumentMap);
				contraVoucher2 = new ContraJournalVoucher();
				
				contraVoucher2 = addOrupdateContraJournalVoucher(contraVoucher2, instrumentList2.get(0), voucherHeader2,
						contraBean);
				updateInstrument(instrumentList2.get(0), voucherHeader2);
				}
			} else {
				List<Map<String, Object>> iList = new ArrayList<Map<String, Object>>();
				/*if(contraBean.getModeOfCollection() !=null && contraBean.getModeOfCollection().equalsIgnoreCase(MDC_PEX))
				{
					
					contraVoucher = addOrupdateContraJournalVoucherPEX(contraVoucher,
							voucherHeader, contraBean);   
					
		        	
				}else {*/
				iList = createInstrumentsForReceipt(contraBean, contraVoucher, voucherHeader);
				final List<InstrumentHeader> receiptInstrumentList = instrumentService.addToInstrument(iList);
				updateInstrument(receiptInstrumentList.get(0), voucherHeader);
				contraVoucher = addOrupdateContraJournalVoucher(contraVoucher, receiptInstrumentList.get(0),
						voucherHeader, contraBean);
			//}
			}
		} catch (final ValidationException e) {
			e.printStackTrace();
			throw new ValidationException(Arrays
					.asList(new ValidationError(e.getErrors().get(0).getMessage(), e.getErrors().get(0).getMessage())));
		} catch (final Exception e) {
			throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
		}
		return voucherHeader;
	}
		 private Bankaccount getBankaccount(final Long bankAccountId) {
		        return (Bankaccount) persistenceService.find(
		                "from Bankaccount where id=?",bankAccountId);

		    }
	private List<Map<String, Object>> createInstrumentsForReceipt(final ContraBean cBean,
			final ContraJournalVoucher cVoucher, CVoucherHeader voucherHeader) {
		final List<Map<String, Object>> iList = new ArrayList<Map<String, Object>>();
		try {
			final Map<String, Object> iMap = new HashMap<String, Object>();
			Date dt = null;

			iMap.put("Instrument amount", Double.valueOf(cBean.getAmount().toString()));

			iMap.put("Bank code", cVoucher.getToBankAccountId().getBankbranch().getBank().getCode());
			iMap.put("Bank branch name", cVoucher.getToBankAccountId().getBankbranch().getBranchaddress1());
			iMap.put("Bank account id", cVoucher.getToBankAccountId().getId());

			if (cBean.getModeOfCollection() != null && cBean.getModeOfCollection().equalsIgnoreCase(MDC_CHEQUE)) {
				if (!egovCommon.isShowChequeNumber()) {

					try {
						iMap.put("Instrument number",
								chequeService.nextChequeNumber(cVoucher.getToBankAccountId().getId().toString(), 1,voucherHeader.getVouchermis().getDepartmentcode()));
					} catch (final ApplicationRuntimeException e) {
						throw new ValidationException(Arrays.asList(
								new ValidationError("Exception while getting Cheque Number  ", e.getMessage())));
					}

					iMap.put("Instrument date", new Date());
				} else {
					iMap.put("Instrument number", cBean.getChequeNumber());
					try {
						dt = sdf.parse(cBean.getChequeDate());
					} catch (final ParseException e) {
						throw new ValidationException(Arrays.asList(
								new ValidationError("Exception while formatting ChequeDate ", "TRANSACTION_FAILED")));
					}
					iMap.put("Instrument date", dt);
				}

				iMap.put("Instrument type", FinancialConstants.INSTRUMENT_TYPE_CHEQUE);

			} else if(cBean.getModeOfCollection() !=null && cBean.getModeOfCollection().equalsIgnoreCase(MDC_OTHER)){

                            iMap.put("Transaction number", cBean.getChequeNumber());
                            try {
                                    dt = sdf.parse(cBean.getChequeDate());
                            } catch (final ParseException e) {
                                    throw new ValidationException(Arrays.asList(
                                                    new ValidationError("Exception while formatting ChequeDate ", "TRANSACTION_FAILED")));
                            }
                            iMap.put("Transaction date", dt);
                            // change this to advice type later
                            iMap.put("Instrument type", FinancialConstants.INSTRUMENT_TYPE_BANK_TO_BANK);
                    } else if(cBean.getModeOfCollection() !=null && cBean.getModeOfCollection().equalsIgnoreCase(MDC_PEX)){

                        iMap.put("Transaction number", cBean.getChequeNumber());
                        try {
                                dt = sdf.parse(cBean.getChequeDate());
                        } catch (final ParseException e) {
                                throw new ValidationException(Arrays.asList(
                                                new ValidationError("Exception while formatting ChequeDate ", "TRANSACTION_FAILED")));
                        }
                        iMap.put("Transaction date", dt);
                        // change this to advice type later
                        iMap.put("Instrument type", FinancialConstants.INSTRUMENT_TYPE_PEX);
                    } else {

				iMap.put("Transaction number", cBean.getChequeNumber());
				try {
					dt = sdf.parse(cBean.getChequeDate());
				} catch (final ParseException e) {
					throw new ValidationException(Arrays.asList(
							new ValidationError("Exception while formatting ChequeDate ", "TRANSACTION_FAILED")));
				}
				iMap.put("Transaction date", dt);
				// change this to advice type later
				iMap.put("Instrument type", FinancialConstants.INSTRUMENT_TYPE_ADVICE);
			}
			iMap.put("Is pay cheque", "0");
			iList.add(iMap);
		} catch (final ValidationException e) {
			throw new ValidationException(Arrays
					.asList(new ValidationError(e.getErrors().get(0).getMessage(), e.getErrors().get(0).getMessage())));
		} catch (final Exception e) {
			throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
		}
		return iList;
	}

	/**
	 * @param oldInstrumentHeader
	 * @param oldInstrumentHeader2
	 */
	private List<Map<String, Object>> createInstruments(final ContraBean cBean, final ContraJournalVoucher cVoucher,
			CVoucherHeader voucherHeader) {
		final Map<String, Object> iMap = new HashMap<String, Object>();
		final List<Map<String, Object>> iList = new ArrayList<Map<String, Object>>();

		Date dt = null;

		iMap.put("Instrument amount", Double.valueOf(cBean.getAmount().toString()));

		iMap.put("Bank code", cVoucher.getFromBankAccountId().getBankbranch().getBank().getCode());
		iMap.put("Bank branch name", cVoucher.getFromBankAccountId().getBankbranch().getBranchaddress1());
		iMap.put("Bank account id", cVoucher.getFromBankAccountId().getId());

		if (cBean.getModeOfCollection()!=null && cBean.getModeOfCollection().equalsIgnoreCase(MDC_CHEQUE)) {
			if (!egovCommon.isShowChequeNumber()) {

				try {
					iMap.put("Instrument number",
							chequeService.nextChequeNumber(cVoucher.getFromBankAccountId().getId().toString(), 1,voucherHeader.getVouchermis().getDepartmentcode()));
				} catch (final ApplicationRuntimeException e) {
					LOGGER.error(e.getMessage(), e);
					throw new ValidationException(Arrays
							.asList(new ValidationError("Exception while getting Cheque Number  ", e.getMessage())));
				}

				iMap.put("Instrument date", new Date());
			} else {
				iMap.put("Instrument number", cBean.getChequeNumber());
				try {
					dt = sdf.parse(cBean.getChequeDate());
				} catch (final ParseException e) {
					LOGGER.error(e.getMessage(), e);
					throw new ValidationException(Arrays.asList(
							new ValidationError("Exception while formatting ChequeDate ", "TRANSACTION_FAILED")));
				}
				iMap.put("Instrument date", dt);
			}

			iMap.put("Instrument type", FinancialConstants.INSTRUMENT_TYPE_CHEQUE);

		} else {

			iMap.put("Transaction number", cBean.getChequeNumber());
			try {
				dt = sdf.parse(cBean.getChequeDate());
			} catch (final ParseException e) {
				LOGGER.error(e.getMessage(), e);
				throw new ValidationException(Arrays
						.asList(new ValidationError("Exception while formatting ChequeDate ", "TRANSACTION_FAILED")));
			}
			iMap.put("Transaction date", dt);
			// change this to advice type later
			iMap.put("Instrument type", FinancialConstants.INSTRUMENT_TYPE_ADVICE);
		}
		iMap.put("Is pay cheque", "1");
		iList.add(iMap);
		return iList;
	}

	@Transactional
	public CVoucherHeader callCreateVoucherForInterFund(CVoucherHeader voucher,
			final ContraJournalVoucher contraVoucher, ContraBean contraBean) {
		try {
			final Fund toFund = (Fund) persistenceService.find("from Fund where id=?", contraBean.getToFundId());
			org.egov.infra.microservice.models.Department toDepartment = new org.egov.infra.microservice.models.Department();
			if (contraBean.getToDepartment() != null && !contraBean.getToDepartment().equals(""))
				toDepartment = microserviceUtils.getDepartmentByCode(contraBean.getToDepartment());
			// validateInterFundAccount(voucherHeader.getFundId(),toFund);
			final HashMap<String, Object> headerDetails = createHeaderAndMisDetails(voucher);

			headerDetails.put(VoucherConstant.VOUCHERNAME, FinancialConstants.CONTRAVOUCHER_NAME_INTERFUND);
			if (voucher.getFundId().getCode().equalsIgnoreCase("03")) {
				final Department department = (Department) persistenceService.find("from Department where code=?", "Z");
				headerDetails.remove(VoucherConstant.DEPARTMENTCODE);
				headerDetails.put(VoucherConstant.DEPARTMENTCODE, department.getCode());
			}
			// update ContraBTB source path
			headerDetails.put(VoucherConstant.SOURCEPATH, "/services/EGF/contra/contraBTB-beforeView.action?voucherHeader.id=");

			HashMap<String, Object> detailMap = null;
			List<HashMap<String, Object>> accountdetails = new ArrayList<HashMap<String, Object>>();
			List<HashMap<String, Object>> subledgerDetails = new ArrayList<HashMap<String, Object>>();

			detailMap = new HashMap<String, Object>();
			detailMap.put(VoucherConstant.CREDITAMOUNT, contraBean.getAmount().toString());
			detailMap.put(VoucherConstant.DEBITAMOUNT, "0");
			detailMap.put(VoucherConstant.GLCODE,
					contraVoucher.getFromBankAccountId().getChartofaccounts().getGlcode());
			accountdetails.add(detailMap);
			// toFundCode = voucher.getFundId();

			detailMap = new HashMap<String, Object>();
			detailMap.put(VoucherConstant.FUNDCODE, toFund.getCode());
			detailMap.put(VoucherConstant.DEBITAMOUNT, contraBean.getAmount().toString());
			detailMap.put(VoucherConstant.CREDITAMOUNT, "0");
			detailMap.put(VoucherConstant.GLCODE, contraBean.getSourceGlcode()); // chang
			// e
			// here
			accountdetails.add(detailMap);
			
			detailMap = new HashMap<String, Object>();
			final Bankaccount ba = getBankaccount(contraVoucher.getToBankAccountId().getId());
			   OtherParty otherparty = new OtherParty();
	           otherparty.setCode(ba.getAccountnumber());
	           otherparty.setName(ba.getAccountnumber());
	           List<OtherParty> othePartyList = otherPartyService.search(otherparty);
	           otherparty = (OtherParty)othePartyList.get(0);
	        Accountdetailtype  accountdetailtype =  accountdetailtypeService.findByName(otherparty.getClass().getSimpleName());
	        List<Accountdetailkey>  accountdetailkeyList =accountdetailkeyService.findByDetailName(accountdetailtype.getId(),otherparty.getName());
            
	        detailMap.put(VoucherConstant.FUNCTIONCODE,"");            
	   
	        detailMap.put(VoucherConstant.DEBITAMOUNT, contraBean.getAmount().toString());
	        detailMap.put(VoucherConstant.DETAILTYPEID, accountdetailtype.getId());
	        detailMap.put(VoucherConstant.DETAILKEYID, accountdetailkeyList.get(0).getDetailkey());
	        detailMap.put(VoucherConstant.GLCODE, contraVoucher.getToBankAccountId().getChartofaccounts().getGlcode());

            subledgerDetails.add(detailMap);
			
			
			voucher = createVoucher.createVoucher(headerDetails, accountdetails, subledgerDetails);

			// update ContraBTB source path
			// headerDetails.put(VoucherConstant.SOURCEPATH,
			// "/services/EGF/contra/contraBTB!beforeView.action?voucherHeader.id=");

			accountdetails = new ArrayList<HashMap<String, Object>>();
			subledgerDetails= new ArrayList<HashMap<String, Object>>();
			// overriding voucherName
			headerDetails.put(VoucherConstant.VOUCHERNAME, FinancialConstants.CONTRAVOUCHER_NAME_INTERFUND);
			headerDetails.put(VoucherConstant.VOUCHERNAME, voucher.getName());
			headerDetails.put(VoucherConstant.FUNDCODE, toFund.getCode());
			if (toFund.getCode().equalsIgnoreCase("03")) {
				final Department department = (Department) persistenceService.find("from Department where code=?", "Z");
				headerDetails.remove(VoucherConstant.DEPARTMENTCODE);
				headerDetails.put(VoucherConstant.DEPARTMENTCODE, department.getCode());
			} else {
				headerDetails.remove(VoucherConstant.DEPARTMENTCODE);
				headerDetails.put(VoucherConstant.DEPARTMENTCODE, toDepartment == null ? "" : toDepartment.getCode());
			}
			headerDetails.remove(VoucherConstant.SCHEMECODE);
			headerDetails.remove(VoucherConstant.SUBSCHEMECODE);
			headerDetails.remove(VoucherConstant.FUNDSOURCECODE);
			headerDetails.remove(VoucherConstant.DIVISIONID);
			headerDetails.remove(VoucherConstant.FUNCTIONARYCODE);
			headerDetails.put(VoucherConstant.REFVOUCHER, voucher.getId());
			detailMap = new HashMap<String, Object>();
			detailMap.put(VoucherConstant.CREDITAMOUNT, contraBean.getAmount().toString());
			detailMap.put(VoucherConstant.DEBITAMOUNT, "0");
			detailMap.put(VoucherConstant.GLCODE, contraBean.getDestinationGlcode());
			accountdetails.add(detailMap);

			detailMap = new HashMap<String, Object>();
			detailMap.put(VoucherConstant.DEBITAMOUNT, contraBean.getAmount().toString());
			detailMap.put(VoucherConstant.CREDITAMOUNT, "0");
			detailMap.put(VoucherConstant.GLCODE, contraVoucher.getToBankAccountId().getChartofaccounts().getGlcode());
			accountdetails.add(detailMap);
		
				detailMap = new HashMap<String, Object>();
			 
		        detailMap.put(VoucherConstant.FUNCTIONCODE,"");            
		   
		        detailMap.put(VoucherConstant.DEBITAMOUNT, contraBean.getAmount().toString());
		        detailMap.put(VoucherConstant.DETAILTYPEID, accountdetailtype.getId());
		        detailMap.put(VoucherConstant.DETAILKEYID, accountdetailkeyList.get(0).getDetailkey());
		        detailMap.put(VoucherConstant.GLCODE, contraVoucher.getToBankAccountId().getChartofaccounts().getGlcode());

	            subledgerDetails.add(detailMap);
	            
			
            
			voucherHeader2 = createVoucher.createVoucher(headerDetails, accountdetails, subledgerDetails);

		} catch (final HibernateException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ValidationException(
					Arrays.asList(new ValidationError(EXCEPTION_WHILE_SAVING_DATA, TRANSACTION_FAILED)));
		} catch (final ApplicationRuntimeException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
		} catch (final ValidationException e) {
			throw new ValidationException(Arrays
					.asList(new ValidationError(e.getErrors().get(0).getMessage(), e.getErrors().get(0).getMessage())));
		} catch (final Exception e) {
			throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
		}
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Posted to Ledger " + voucher.getId());
		return voucher;

	}

	private HashMap<String, Object> createHeaderAndMisDetails(CVoucherHeader voucherHeader) throws ValidationException {
		final HashMap<String, Object> headerdetails = new HashMap<String, Object>();
		headerdetails.put(VoucherConstant.VOUCHERNAME, voucherHeader.getName());
		headerdetails.put(VoucherConstant.VOUCHERTYPE, voucherHeader.getType());
		headerdetails.put((String) VoucherConstant.VOUCHERSUBTYPE, voucherHeader.getVoucherSubType());
		headerdetails.put(VoucherConstant.VOUCHERNUMBER, voucherHeader.getVoucherNumber());
		headerdetails.put(VoucherConstant.VOUCHERDATE, voucherHeader.getVoucherDate());
		headerdetails.put(VoucherConstant.DESCRIPTION, voucherHeader.getDescription());
		//modified added by Prasanta
				if (voucherHeader.getFirstsignatory() != null)
		            headerdetails.put("firstsignatory", voucherHeader.getFirstsignatory());
		        if (voucherHeader.getSecondsignatory() != null)
		            headerdetails.put("secondsignatory", voucherHeader.getSecondsignatory());

		if (voucherHeader.getVouchermis().getDepartmentcode() != null)
			headerdetails.put(VoucherConstant.DEPARTMENTCODE, voucherHeader.getVouchermis().getDepartmentcode());
		if (voucherHeader.getFundId() != null)
			headerdetails.put(VoucherConstant.FUNDCODE, voucherHeader.getFundId().getCode());
		if (voucherHeader.getVouchermis().getSchemeid() != null)
			headerdetails.put(VoucherConstant.SCHEMECODE, voucherHeader.getVouchermis().getSchemeid().getCode());
		if (voucherHeader.getVouchermis().getSubschemeid() != null)
			headerdetails.put(VoucherConstant.SUBSCHEMECODE, voucherHeader.getVouchermis().getSubschemeid().getCode());
		if (voucherHeader.getVouchermis().getFundsource() != null)
			headerdetails.put(VoucherConstant.FUNDSOURCECODE, voucherHeader.getVouchermis().getFundsource().getCode());
		if (voucherHeader.getVouchermis().getDivisionid() != null)
			headerdetails.put(VoucherConstant.DIVISIONID, voucherHeader.getVouchermis().getDivisionid().getId());
		if (voucherHeader.getVouchermis().getFunctionary() != null)
			headerdetails.put(VoucherConstant.FUNCTIONARYCODE,
					voucherHeader.getVouchermis().getFunctionary().getCode());
		if (voucherHeader.getVouchermis().getFunction() != null)
			headerdetails.put(VoucherConstant.FUNCTIONCODE, voucherHeader.getVouchermis().getFunction().getCode());
		if (voucherHeader.getFileno() != null)
			headerdetails.put(VoucherConstant.FILENO, voucherHeader.getFileno());
		return headerdetails;
	}

	@Transactional
	public CVoucherHeader callCreateVoucher(CVoucherHeader voucher, final ContraJournalVoucher contraVoucher,
			ContraBean contraBean) {
		try {
			 LOGGER.info("Subledger Details inside callCreateVoucher==> ");
			final HashMap<String, Object> headerDetails = createHeaderAndMisDetails(voucher);
			// update ContraBTB source path
			headerDetails.put(VoucherConstant.SOURCEPATH, "/services/EGF/contra/contraBTB-beforeView.action?voucherHeader.id=");
			if (voucher.getFundId().getCode().equalsIgnoreCase("03")) {
				final Department department = (Department) persistenceService.find("from Department where code=?", "Z");
				headerDetails.remove(VoucherConstant.DEPARTMENTCODE);
				headerDetails.put(VoucherConstant.DEPARTMENTCODE, department.getCode());
			}
			HashMap<String, Object> detailMap = null;
			final List<HashMap<String, Object>> accountdetails = new ArrayList<HashMap<String, Object>>();
			final List<HashMap<String, Object>> subledgerDetails = new ArrayList<HashMap<String, Object>>();

			detailMap = new HashMap<String, Object>();
			detailMap.put(VoucherConstant.CREDITAMOUNT, contraBean.getAmount().toString());
			detailMap.put(VoucherConstant.DEBITAMOUNT, "0");
			detailMap.put(VoucherConstant.GLCODE,
					contraVoucher.getFromBankAccountId().getChartofaccounts().getGlcode());
			accountdetails.add(detailMap);

			
			detailMap = new HashMap<String, Object>();
			detailMap.put(VoucherConstant.DEBITAMOUNT, contraBean.getAmount().toString());
			detailMap.put(VoucherConstant.CREDITAMOUNT, "0");
			detailMap.put(VoucherConstant.GLCODE, contraVoucher.getToBankAccountId().getChartofaccounts().getGlcode());
			accountdetails.add(detailMap);
			
			if(contraBean.getModeOfCollection()!= null && contraBean.getModeOfCollection().equalsIgnoreCase(MDC_PEX))
			{
			detailMap = new HashMap<String, Object>();
			 LOGGER.info("Subledder Details getToBankAccountID==> " + contraVoucher.getToBankAccountId().getId());
			final Bankaccount ba = getBankaccount(contraVoucher.getToBankAccountId().getId());
			System.out.println(":::::"+ba.getAccountnumber()+"::::::"+ba.getAccounttype());
			   OtherParty otherparty = new OtherParty();
	           otherparty.setCode(ba.getAccountnumber());
	           otherparty.setName(ba.getAccountnumber());
	           LOGGER.info("Subledder Details BankAccount Number==> " +  ba.getAccountnumber());
	           List<OtherParty> othePartyList = otherPartyService.search(otherparty);
	           otherparty = (OtherParty)othePartyList.get(0);
	        Accountdetailtype  accountdetailtype =  accountdetailtypeService.findByName(otherparty.getClass().getSimpleName());
	        List<Accountdetailkey>  accountdetailkeyList =accountdetailkeyService.findByDetailName(accountdetailtype.getId(),otherparty.getName());
            
	        LOGGER.info("Subledder Details DETAILKEYID==> " +  accountdetailkeyList.get(0).getDetailkey());
	        detailMap.put(VoucherConstant.FUNCTIONCODE, "");            
	   
	        detailMap.put(VoucherConstant.DEBITAMOUNT, contraBean.getAmount().toString());
	        detailMap.put(VoucherConstant.DETAILTYPEID, accountdetailtype.getId());
	        detailMap.put(VoucherConstant.DETAILKEYID, accountdetailkeyList.get(0).getDetailkey());
	        detailMap.put(VoucherConstant.GLCODE, contraVoucher.getToBankAccountId().getChartofaccounts().getGlcode());

            subledgerDetails.add(detailMap);
            LOGGER.info("Subledger Details size==> " + subledgerDetails.size());
            for (final HashMap<String, Object> subdetailDetailMap : subledgerDetails) {
            	LOGGER.info("Subledger Details DETAILTYPEID==> " +	subdetailDetailMap.get(VoucherConstant.DETAILTYPEID).toString());
            }
            
			
			}
			voucher = createVoucher.createVoucher(headerDetails, accountdetails, subledgerDetails);
		} catch (final HibernateException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			throw new ValidationException(
					Arrays.asList(new ValidationError(EXCEPTION_WHILE_SAVING_DATA, TRANSACTION_FAILED)));
		} catch (final ApplicationRuntimeException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
		} catch (final ValidationException e) {
			throw new ValidationException(Arrays
					.asList(new ValidationError(e.getErrors().get(0).getMessage(), e.getErrors().get(0).getMessage())));
		} catch (final Exception e) {
			e.printStackTrace();
			throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
		}
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Posted to Ledger " + voucher.getId());
		return voucher;

	}

	@Transactional
	public void updateInstrument(final InstrumentHeader ih, final CVoucherHeader vh) {
		try {
			final Map<String, Object> iMap = new HashMap<String, Object>();
			final List<Map<String, Object>> iList = new ArrayList<Map<String, Object>>();
			iMap.put("Instrument header", ih);
			iMap.put("Voucher header", vh);
			iList.add(iMap);
			instrumentService.updateInstrumentVoucherReference(iList);
		} catch (final ValidationException e) {
			throw new ValidationException(Arrays
					.asList(new ValidationError(e.getErrors().get(0).getMessage(), e.getErrors().get(0).getMessage())));
		} catch (final Exception e) {
			throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
		}
	}

	@Transactional
	public ContraJournalVoucher addOrupdateContraJournalVoucher(ContraJournalVoucher cjv, final InstrumentHeader ih,
			final CVoucherHeader vh, ContraBean contraBean) {
		try {
			cjv.setInstrumentHeaderId(ih);
			cjv.setVoucherHeaderId(vh);
			cjv = getHibObjectsFromContraBean(contraBean, cjv);
			if (cjv.getId() != null) {
				contraJournalVoucherService.applyAuditing(cjv);
				contraJournalVoucherService.update(cjv);
				if (LOGGER.isInfoEnabled())
					LOGGER.info("Contra Journal Voucher Updated");
			} else {
				contraJournalVoucherService.applyAuditing(cjv);
				contraJournalVoucherService.persist(cjv);
				if (LOGGER.isInfoEnabled())
					LOGGER.info("Contra Journal Voucher created");
			}
		} catch (final ValidationException e) {
			throw new ValidationException(Arrays
					.asList(new ValidationError(e.getErrors().get(0).getMessage(), e.getErrors().get(0).getMessage())));
		} catch (final Exception e) {
			throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
		}
		return cjv;
	}
	@Transactional
	public ContraJournalVoucher addOrupdateContraJournalVoucherPEX(ContraJournalVoucher cjv, 
			final CVoucherHeader vh, ContraBean contraBean) {
		try {

			cjv.setVoucherHeaderId(vh);
			cjv = getHibObjectsFromContraBean(contraBean, cjv);
			if (cjv.getId() != null) {
				contraJournalVoucherService.applyAuditing(cjv);
				contraJournalVoucherService.update(cjv);
				if (LOGGER.isInfoEnabled())
					LOGGER.info("Contra Journal Voucher Updated");
			} else {
				contraJournalVoucherService.applyAuditing(cjv);
				contraJournalVoucherService.persist(cjv);
				if (LOGGER.isInfoEnabled())
					LOGGER.info("Contra Journal Voucher created");
			}
		} catch (final ValidationException e) {
			throw new ValidationException(Arrays
					.asList(new ValidationError(e.getErrors().get(0).getMessage(), e.getErrors().get(0).getMessage())));
		} catch (final Exception e) {
			throw new ValidationException(Arrays.asList(new ValidationError(e.getMessage(), e.getMessage())));
		}
		return cjv;
	}
	private ContraJournalVoucher getHibObjectsFromContraBean(final ContraBean contraBean,
			final ContraJournalVoucher contraVoucher) {
		final String bankQry = "from Bankaccount where id=?";
		if (contraBean != null && contraBean.getFromBankAccountId() != null
				&& !contraBean.getFromBankAccountId().equals("-1"))
			contraVoucher.setFromBankAccountId(
					(Bankaccount) persistenceService.find(bankQry, Long.valueOf(contraBean.getFromBankAccountId())));
		if (contraBean != null && contraBean.getToBankAccountId() != null
				&& !contraBean.getFromBankAccountId().equals("-1"))
			contraVoucher.setToBankAccountId(
					(Bankaccount) persistenceService.find(bankQry, Long.valueOf(contraBean.getToBankAccountId())));
		return contraVoucher;
	}

}