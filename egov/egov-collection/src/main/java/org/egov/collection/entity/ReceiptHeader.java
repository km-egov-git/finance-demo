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
package org.egov.collection.entity;

import static org.egov.infra.utils.DateUtils.toDefaultDateFormat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Transient;

import org.egov.collection.constants.CollectionConstants;
import org.egov.commons.DocumentUploads;
//import org.egov.commons.DocumentUploads;
import org.egov.commons.EgwStatus;
import org.egov.infra.admin.master.entity.Location;
import org.egov.infra.persistence.entity.Auditable;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.instrument.InstrumentHeader;

public class ReceiptHeader extends StateAware implements Auditable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private ReceiptHeader receiptHeader;
    private Set<ReceiptHeader> receiptHeaders = new HashSet<>();

    private String referencenumber;
    private Long dateOfDeposit;
    private String depositAmount;
    private String bankAccountNumber;
    private BigDecimal totalreciptAmount;
    private BigDecimal totalDepositAmount;
    private String payerAddress;
    private Date referencedate;
    private String consumerCode;
    /* The details of associated bill that billing application has sent */
    private String referenceDesc;
    private char receipttype;
    private String receiptnumber;
    private Date receiptdate;
    private String receiptdatenew;
    private String manualreceiptnumber;
    private Date manualreceiptdate;
    private Boolean isModifiable;

    private String service;
    private String serviceBusinessName;
    private Character collectiontype;
    private Set<ReceiptDetail> receiptDetails = new LinkedHashSet<>();
    private ReceiptMisc receiptMisc;
    private Set<InstrumentHeader> receiptInstrument = new HashSet<>();
    private Set<ReceiptVoucher> receiptVoucher = new HashSet<>();
    private Location location;
    private Boolean isReconciled;
    private EgwStatus status;
    private String reasonForCancellation;
    private String paidBy;
    private Boolean overrideAccountHeads;
    private Boolean partPaymentAllowed;
    private Boolean callbackForApportioning;
    private String collModesNotAllwd;
    private String displayMsg;
    private BigDecimal totalAmount;
    private BigDecimal principalAmount;
    private BigDecimal gstAmount;
    private BigDecimal minimumAmount;
    private BigDecimal totalAmountToBeCollected;
    private Date voucherDate;
    private String voucherNum;
    private OnlinePayment onlinePayment;
    private Challan challan;
    private String payeeName;
    private String payeeAddress;
    private String workflowUserName = "NA";
    private String instrumentsAsString;
    private String source;
    private String remittanceReferenceNumber;
    private String payeeEmail;
    private String consumerType;
    private Long version;
    private String serviceCategory;
    private BigDecimal totalcramount;
    private String remittanceVoucherNumber;
    private String curretnStatus;
    private String currentreceipttype;
    private String modOfPayment;
    private String createdUser;
    private String serviceIdText;
    private String g8data;
    private String paymentId;
    private String ifsc;
    private String rDate;
    private Date rrDate;
    private String subdivison;
    private String gstno;
    private String createdByName;
	private String department;
	private String depositDate;
	@Transient
    private List<DocumentUpload> documentDetail = new ArrayList<>();
    
	private String serviceType;
	private String chequeddno;
	private String chequedddate;
	private String bank;
	
    public String getRemittanceVoucherNumber() {
		return remittanceVoucherNumber;
	}

	public void setRemittanceVoucherNumber(String remittanceVoucherNumber) {
		this.remittanceVoucherNumber = remittanceVoucherNumber;
	}

    public List<DocumentUpload> getDocumentDetail() {
		return documentDetail;
	}

	public void setDocumentDetail(List<DocumentUpload> documentDetail) {
		if (documentDetail != null)
            this.documentDetail.addAll(documentDetail);
	}

	public ReceiptHeader() {
    }

    public ReceiptHeader(final String referencenumber, final Date referencedate, final String consumerCode,
            final String referenceDesc, final BigDecimal totalAmount, final BigDecimal minimumAmount,
            final Boolean partPaymentAllowed, final Boolean overrideAccountHeadsAllowed,
            final Boolean callbackForApportioning, final String displayMsg, final String service,
            final String collModesNotAllwd, final String payeeName, final String payeeAddress, final String payeeEmail,
            final String consumerType) {
        this.referencenumber = referencenumber;
        this.referencedate = referencedate;
        this.consumerCode = consumerCode;
        this.referenceDesc = referenceDesc;
        this.totalAmount = totalAmount;
        this.minimumAmount = minimumAmount;
        this.partPaymentAllowed = partPaymentAllowed;
        overrideAccountHeads = overrideAccountHeadsAllowed;
        this.callbackForApportioning = callbackForApportioning;
        this.displayMsg = displayMsg;
        this.service = service;
        this.collModesNotAllwd = collModesNotAllwd;
        this.payeeName = payeeName;
        this.payeeAddress = payeeAddress;
        this.payeeEmail = payeeEmail;
        this.consumerType = consumerType;
    }

    public ReceiptHeader(final Boolean isReconciled, final Boolean isModifiable, final char receiptType,
            final Character collectionType, final String paidBy, final String serviceDetails,
            final String referencenumber, final String referenceDesc, final BigDecimal totalAmount) {
        this.isReconciled = isReconciled;
        this.isModifiable = isModifiable;
        receipttype = receiptType;
        collectiontype = collectionType;
        this.paidBy = paidBy;
        service = serviceDetails;
        this.referencenumber = referencenumber;
        this.referenceDesc = referenceDesc;
        this.totalAmount = totalAmount;
    }

    public String getReferencenumber() {
        return referencenumber;
    }

    public void setReferencenumber(final String referencenumber) {
        this.referencenumber = referencenumber;
    }

    public Date getReferencedate() {
        return null == referencedate ? null : referencedate;
    }

    public void setReferencedate(final Date referencedate) {
        this.referencedate = referencedate;
    }

    public String getReferenceDesc() {
        return referenceDesc;
    }

    public void setReferenceDesc(final String referenceDesc) {
        this.referenceDesc = referenceDesc;
    }

    public char getReceipttype() {
        return receipttype;
    }

    public void setReceipttype(final char receipttype) {
        this.receipttype = receipttype;
    }

    public String getReceiptnumber() {
        return receiptnumber;
    }

    public void setReceiptnumber(final String receiptnumber) {
        this.receiptnumber = receiptnumber;
    }

    public String getManualreceiptnumber() {
        return manualreceiptnumber;
    }

    public void setManualreceiptnumber(final String manualreceiptnumber) {
        this.manualreceiptnumber = manualreceiptnumber;
    }

    public Date getManualreceiptdate() {
        return null == manualreceiptdate ? null : manualreceiptdate;
    }

    public void setManualreceiptdate(final Date manualreceiptdate) {
        this.manualreceiptdate = manualreceiptdate;
    }

    public Boolean getIsModifiable() {
        return isModifiable;
    }

    public void setIsModifiable(final Boolean isModifiable) {
        this.isModifiable = isModifiable;
    }

    public String getService() {
        return service;
    }

    public void setService(final String service) {
        this.service = service;
    }

    public Character getCollectiontype() {
        return collectiontype;
    }

    public void setCollectiontype(final Character collectiontype) {
        this.collectiontype = collectiontype;
    }

    public Set<ReceiptDetail> getReceiptDetails() {
        return receiptDetails;
    }

    public void setReceiptDetails(final Set<ReceiptDetail> receiptDetails) {
        this.receiptDetails = receiptDetails;
    }

    public void addReceiptDetail(final ReceiptDetail receiptDetail) {
        getReceiptDetails().add(receiptDetail);
    }

    public Date getReceiptDate() {
        return getCreatedDate();
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(final Location location) {
        this.location = location;
    }

    /**
     * Returns instrument type of receipts associated with the receipt. Since multiple modes of payment for a receipt are not
     * allowed (as of now), this method will return the type of the first instrument associated with this receipt.
     *
     * @return instrument type of instruments associated with the receipt
     */
    public String getInstrumentType() {
        if (!receiptInstrument.isEmpty())
            return receiptInstrument.iterator().next().getInstrumentType().getType();
        else
            return "";
    }

    public void addInstrument(final InstrumentHeader instrumentMaster) {
        receiptInstrument.add(instrumentMaster);
    }

    /**
     * @return the receiptInstrument
     */
    public Set<InstrumentHeader> getReceiptInstrument() {
        return receiptInstrument;
    }

    /**
     * @param receiptInstrument the receiptInstrument to set
     */
    public void setReceiptInstrument(final Set<InstrumentHeader> receiptInstrument) {
        this.receiptInstrument = receiptInstrument;
    }

    /**
     * @param type - the Instrument type
     * @return Returns list of instruments of this instrument type. Useful to get all cheque instruments or all bank instruments.
     */
    public List<InstrumentHeader> getInstruments(final String type) {
        final ArrayList<InstrumentHeader> instrumentList = new ArrayList<>();
        for (final InstrumentHeader instrument : getReceiptInstrument())
            if (type.equals(instrument.getInstrumentType().getType()))
                instrumentList.add(instrument);
        return instrumentList;
    }

    /**
     * @return the receiptVoucher
     */
    public Set<ReceiptVoucher> getReceiptVoucher() {
        return receiptVoucher;
    }

    /**
     * @param receiptVoucher the receiptVoucher to set
     */
    public void setReceiptVoucher(final Set<ReceiptVoucher> receiptVoucher) {
        this.receiptVoucher = receiptVoucher;
    }

    public void addReceiptVoucher(final ReceiptVoucher receiptVoucher) {
        this.receiptVoucher.add(receiptVoucher);
    }

    /**
     * @return the isReconciled
     */
    public Boolean getIsReconciled() {
        return isReconciled;
    }

    /**
     * @param isReconciled the isReconciled to set
     */
    public void setIsReconciled(final Boolean isReconciled) {
        this.isReconciled = isReconciled;
    }

    /**
     * @return the egwStatus
     */
    public EgwStatus getStatus() {
        return status;
    }

    /**
     * @param status the egwStatus to set
     */
    public void setStatus(final EgwStatus status) {
        this.status = status;
    }

    /**
     * @return the reasonforcancellation
     */
    public String getReasonForCancellation() {
        return reasonForCancellation;
    }

    /**
     * @param reasonforcancellation the reasonforcancellation to set
     */
    public void setReasonForCancellation(final String reasonforcancellation) {
        reasonForCancellation = reasonforcancellation;
    }

    public ReceiptMisc getReceiptMisc() {
        return receiptMisc;
    }

    public void setReceiptMisc(final ReceiptMisc receiptMisc) {
        this.receiptMisc = receiptMisc;
    }

    /**
     * @return the paidBy
     */
    public String getPaidBy() {
        return paidBy;
    }

    /**
     * @param paidBy the paidBy to set
     */
    public void setPaidBy(final String paidBy) {
        this.paidBy = paidBy;
    }

    /**
     * @return the overRideAccountHeads
     */
    public Boolean getOverrideAccountHeads() {
        return overrideAccountHeads;
    }

    /**
     * @param overRideAccountHeads the overRideAccountHeads to set
     */
    public void setOverrideAccountHeads(final Boolean overrideAccountHeads) {
        this.overrideAccountHeads = overrideAccountHeads;
    }

    /**
     * @return the callbackForApportioning
     */
    public Boolean getCallbackForApportioning() {
        return callbackForApportioning;
    }

    /**
     * @param callbackForApportioning flag indicating if billing system should do the apportioning
     */
    public void setCallbackForApportioning(final Boolean callbackForApportioning) {
        this.callbackForApportioning = callbackForApportioning;
    }

    /**
     * @return the partPaymentAllowed
     */
    public Boolean getPartPaymentAllowed() {
        return partPaymentAllowed;
    }

    /**
     * @param partPaymentAllowed the partPaymentAllowed to set
     */
    public void setPartPaymentAllowed(final Boolean partPaymentAllowed) {
        this.partPaymentAllowed = partPaymentAllowed;
    }

    /**
     * @return the string
     */
    @Override
    public String myLinkId() {
        final StringBuilder linkId = new StringBuilder();

        linkId.append(getCurrentState().getNextAction()).append(CollectionConstants.SEPARATOR_HYPHEN).append(service)
                .append(CollectionConstants.SEPARATOR_HYPHEN)
                /* .append(getCreatedBy().getUsername()) */.append(CollectionConstants.SEPARATOR_HYPHEN)
                .append(toDefaultDateFormat(getReceiptdate()))
                .append(location == null ? "" : CollectionConstants.SEPARATOR_HYPHEN + location.getId())
                .append(CollectionConstants.SEPARATOR_HYPHEN).append(receipttype)
                .append(CollectionConstants.SEPARATOR_HYPHEN).append(CollectionConstants.ALL);
        return linkId.toString();
    }

    public String myLinkIdForChallanMisc() {
        final StringBuilder linkId = new StringBuilder();

        linkId.append(getCurrentState().getNextAction()).append(CollectionConstants.SEPARATOR_HYPHEN)
                /* .append(getCreatedBy().getUsername()) */.append(CollectionConstants.SEPARATOR_HYPHEN)
                .append(toDefaultDateFormat(getReceiptdate()))
                .append(location == null ? "" : CollectionConstants.SEPARATOR_HYPHEN + location.getId());

        return linkId.toString();
    }

    /**
     * @return the state details
     */
    @Override
    public String getStateDetails() {
        return service
                + CollectionConstants.SEPARATOR_HYPHEN /*
                                                        * + getCreatedBy(). getUsername()
                                                        */
                + (location == null ? "" : CollectionConstants.SEPARATOR_HYPHEN + location.getName());
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(final BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(final BigDecimal minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public String getDisplayMsg() {
        return displayMsg;
    }

    public void setDisplayMsg(final String displayMsg) {
        this.displayMsg = displayMsg;
    }

    public BigDecimal getTotalAmountToBeCollected() {
        return totalAmountToBeCollected;
    }

    public void setTotalAmountToBeCollected(final BigDecimal totalAmountToBeCollected) {
        this.totalAmountToBeCollected = totalAmountToBeCollected;
    }

    /**
     * @return the voucherDate
     */
    public Date getVoucherDate() {
        return null == voucherDate ? null : voucherDate;
    }

    /**
     * @param voucherDate the voucherDate to set
     */
    public void setVoucherDate(final Date voucherDate) {
        this.voucherDate = voucherDate;
    }

    /**
     * @return the voucherNumber
     */
    public String getVoucherNum() {
        return voucherNum;
    }

    /**
     * @param voucherNumber the voucherNumber to set
     */
    public void setVoucherNum(final String voucherNum) {
        this.voucherNum = voucherNum;
    }

    public String getCollModesNotAllwd() {
        return collModesNotAllwd;
    }

    public void setCollModesNotAllwd(final String collModesNotAllwd) {
        this.collModesNotAllwd = collModesNotAllwd;
    }

    public String getConsumerCode() {
        return consumerCode;
    }

    public void setConsumerCode(final String consumerCode) {
        this.consumerCode = consumerCode;
    }

    @Override
    public String toString() {

        return new StringBuilder().append("ReceiptHeader( Id : ").append(getId()).append("ReceiptNo: ")
                .append(receiptnumber).append(",totalAmount : ").append(totalAmount).append(",referencenumber: ")
                .append(referencenumber).append(",paidBy: ").append(paidBy).append(",service: ").append(service)
                .append(",receiptMisc: ").append(receiptMisc).append(",receiptDetails ").append(receiptDetails)
                .append(" , Challan : ").append(challan).append("]").toString();
    }

    /**
     * @return the onlinePayment
     */
    public OnlinePayment getOnlinePayment() {
        return onlinePayment;
    }

    /**
     * @param onlinePayment the onlinePayment to set
     */
    public void setOnlinePayment(final OnlinePayment onlinePayment) {
        this.onlinePayment = onlinePayment;
    }

    public Challan getChallan() {
        return challan;
    }

    public void setChallan(final Challan challan) {
        this.challan = challan;
    }

    /**
     * @return the receiptHeader
     */
    public ReceiptHeader getReceiptHeader() {
        return receiptHeader;
    }

    /**
     * @param receiptHeader the receiptHeader to set
     */
    public void setReceiptHeader(final ReceiptHeader receiptHeader) {
        this.receiptHeader = receiptHeader;
    }

    /**
     * @return the receiptHeaders
     */
    public Set<ReceiptHeader> getReceiptHeaders() {
        return receiptHeaders;
    }

    /**
     * @param receiptHeaders the receiptHeaders to set
     */
    public void setReceiptHeaders(final Set<ReceiptHeader> receiptHeaders) {
        this.receiptHeaders = receiptHeaders;
    }

    /**
     * This method returns Challan Number associated with the receipt In case of Cancelled receipts, get the receipt object
     * created in liu of old receipt object and returns the challan number associated with the new receipt object. this method is
     * invoked from serach receipt UI screen
     *
     * @return String
     */
    public String getReceiptChallanNumber() {
        String stringObj = null;
        if (receipttype == CollectionConstants.RECEIPT_TYPE_CHALLAN)
            if (getChallan() == null) {
                Set<ReceiptHeader> receiptHeadersSet = getReceiptHeaders();
                Iterator<ReceiptHeader> iter = receiptHeadersSet.iterator();
                while (iter.hasNext()) {
                    final ReceiptHeader receiptHeaderObj = iter.next();

                    if (receiptHeaderObj.getChallan() == null) {
                        receiptHeadersSet = receiptHeaderObj.getReceiptHeaders();
                        iter = receiptHeadersSet.iterator();
                        continue;
                    } else {
                        stringObj = receiptHeaderObj.getChallan().getChallanNumber();
                        break;
                    }
                }
            } else
                stringObj = getChallan().getChallanNumber();
        return stringObj;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(final String payeeName) {
        this.payeeName = payeeName;
    }

    public String getPayeeAddress() {
        return payeeAddress;
    }

    public void setPayeeAddress(final String payeeAddress) {
        this.payeeAddress = payeeAddress;
    }

    public Date getReceiptdate() {
        return null == receiptdate ? null : receiptdate;
    }

    public void setReceiptdate(final Date receiptdate) {
        this.receiptdate = receiptdate;
    }

    /**
     * @return the workflowUserName
     */
    public String getWorkflowUserName() {
        return workflowUserName;
    }

    /**
     * @param workflowUserName the workflowUserName to set
     */
    public void setWorkflowUserName(final String workflowUserName) {
        this.workflowUserName = workflowUserName;
    }

    public String getInstrumentsAsString() {
        return instrumentsAsString;
    }

    public void setInstrumentsAsString(final String instrumentsAsString) {
        this.instrumentsAsString = instrumentsAsString;
    }

    public String getInstrumentDetailAsString() {
        final StringBuilder instrumentDetailsBuilder = new StringBuilder();

        for (final InstrumentHeader instrument : receiptInstrument) {
            if (instrumentDetailsBuilder.length() > 0)
                instrumentDetailsBuilder.append(", ");

            final String instrumentType = instrument.getInstrumentType().getType();
            instrumentDetailsBuilder.append(instrumentType);

            if (instrumentType.equals(CollectionConstants.INSTRUMENTTYPE_DD)
                    || instrumentType.equals(CollectionConstants.INSTRUMENTTYPE_CHEQUE)
                    || instrumentType.equals(CollectionConstants.INSTRUMENTTYPE_CARD))
                // For DD/Cheque/Card, add instrument number
                instrumentDetailsBuilder.append(" # " + instrument.getInstrumentNumber());
            if (instrumentType.equals(CollectionConstants.INSTRUMENTTYPE_BANK)
                    || instrumentType.equals(CollectionConstants.INSTRUMENTTYPE_ONLINE))
                // For bank , add transaction number (challan number)
                instrumentDetailsBuilder.append(" # " + instrument.getTransactionNumber());
            if (instrumentType.equals(CollectionConstants.INSTRUMENTTYPE_DD)
                    || instrumentType.equals(CollectionConstants.INSTRUMENTTYPE_CHEQUE))
                // For DD/Cheque, add instrument date as well
                instrumentDetailsBuilder.append(" - " + toDefaultDateFormat(instrument.getInstrumentDate()));

            instrumentDetailsBuilder
                    .append(" - " + instrument.getInstrumentAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        return instrumentDetailsBuilder.toString();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public String getRemittanceReferenceNumber() {
        return remittanceReferenceNumber;
    }

    public void setRemittanceReferenceNumber(final String remittanceReferenceNumber) {
        this.remittanceReferenceNumber = remittanceReferenceNumber;
    }

    public String getPayeeEmail() {
        return payeeEmail;
    }

    public void setPayeeEmail(final String payeeEmail) {
        this.payeeEmail = payeeEmail;
    }

    public String getConsumerType() {
        return consumerType;
    }

    public void setConsumerType(String consumerType) {
        this.consumerType = consumerType;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public BigDecimal getTotalcramount() {
        return totalcramount;
    }

    public void setTotalcramount(BigDecimal totalcramount) {
        this.totalcramount = totalcramount;
    }

    public String getCurretnStatus() {
        return curretnStatus;
    }

    public void setCurretnStatus(String curretnStatus) {
        this.curretnStatus = curretnStatus;
    }

    public String getCurrentreceipttype() {
        return currentreceipttype;
    }

    public void setCurrentreceipttype(String currentreceipttype) {
        this.currentreceipttype = currentreceipttype;
    }

    public String getModOfPayment() {
        return modOfPayment;
    }

    public void setModOfPayment(String modOfPayment) {
        this.modOfPayment = modOfPayment;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public String getServiceIdText() {
        return serviceIdText;
    }

    public void setServiceIdText(String serviceIdText) {
        this.serviceIdText = serviceIdText;
    }

    public String getG8data() {
        return g8data;
    }

    public void setG8data(String g8data) {
        this.g8data = g8data;
    }

    public String getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}
	
	public Long getDateOfDeposit() {
		return dateOfDeposit;
	}

	public void setDateOfDeposit(Long dateOfDeposit) {
		this.dateOfDeposit = dateOfDeposit;
	}

	public String getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(String depositAmount) {
		this.depositAmount = depositAmount;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	public BigDecimal getTotalreciptAmount() {
		return totalreciptAmount;
	}

	public void setTotalreciptAmount(BigDecimal totalreciptAmount) {
		this.totalreciptAmount = totalreciptAmount;
	}

	public BigDecimal getTotalDepositAmount() {
		return totalDepositAmount;
	}

	public void setTotalDepositAmount(BigDecimal totalDepositAmount) {
		this.totalDepositAmount = totalDepositAmount;
	}
	
	public String getPayerAddress() {
		return payerAddress;
	}
	
	public void setPayerAddress(String payerAddress) {
		this.payerAddress = payerAddress;
	}

	public String getrDate() {
		return rDate;
	}

	public void setrDate(String rDate) {
		this.rDate = rDate;
	}

	public Date getRrDate() {
		return rrDate;
	}

	public void setRrDate(Date rrDate) {
		this.rrDate = rrDate;
	}

	public BigDecimal getPrincipalAmount() {
		return principalAmount;
	}

	public void setPrincipalAmount(BigDecimal principalAmount) {
		this.principalAmount = principalAmount;
	}

	public BigDecimal getGstAmount() {
		return gstAmount;
	}

	public void setGstAmount(BigDecimal gstAmount) {
		this.gstAmount = gstAmount;
	}

	public String getSubdivison() {
		return subdivison;
	}

	public void setSubdivison(String subdivison) {
		this.subdivison = subdivison;
	}

	public String getGstno() {
		return gstno;
	}

	public void setGstno(String gstno) {
		this.gstno = gstno;
	}

	public String getServiceBusinessName() {
		return serviceBusinessName;
	}

	public void setServiceBusinessName(String serviceBusinessName) {
		this.serviceBusinessName = serviceBusinessName;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	
	public String getReceiptdatenew() {
		return receiptdatenew;
	}

	public void setReceiptdatenew(String receiptdatenew) {
		this.receiptdatenew = receiptdatenew;
	}
	
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDepositDate() {
		return depositDate;
	}

	public void setDepositDate(String depositDate) {
		this.depositDate = depositDate;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getChequeddno() {
		return chequeddno;
	}

	public void setChequeddno(String chequeddno) {
		this.chequeddno = chequeddno;
	}

	public String getChequedddate() {
		return chequedddate;
	}

	public void setChequedddate(String chequedddate) {
		this.chequedddate = chequedddate;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	

}