package com.intuit.developer;	

import com.intuit.developer.adaptors.DumpQuickBookPayments;
import com.intuit.developer.adaptors.DumpUnMappedInvoices;
import com.intuit.developer.adaptors.Finished;
import com.intuit.developer.adaptors.GetContractorsForUpdate;
import com.intuit.developer.adaptors.GetInvoicesForUpdate;
import com.intuit.developer.adaptors.GetPaymentsForUpdate;
import com.intuit.developer.adaptors.InsertContractors;
import com.intuit.developer.adaptors.InsertInvoices;
import com.intuit.developer.adaptors.InsertPayments;
import com.intuit.developer.adaptors.MapUnMappedContractors;
import com.intuit.developer.adaptors.QBXmlAdaptor;
import com.intuit.developer.adaptors.UpdateContractors;
import com.intuit.developer.adaptors.UpdateInvoices;
import com.intuit.developer.adaptors.UpdatePayments;

public enum QBIntegrationWorkFlow {
	DumpUnMappedContractors(MapUnMappedContractors.class),
	RemoveDeletedContractors(QBXmlAdaptor.class),
	GetContractorsForUpdate(GetContractorsForUpdate.class),
	UpdateContractors(UpdateContractors.class),
	InsertContractors(InsertContractors.class),
	DumpUnMappedInvoices(DumpUnMappedInvoices.class),
	DumpPayments(DumpQuickBookPayments.class),
	GetInvoicesForUpdate(GetInvoicesForUpdate.class),
	UpdateInvoices(UpdateInvoices.class),
	InsertInvoices(InsertInvoices.class),
	GetPaymentsForUpdate(GetPaymentsForUpdate.class),
	UpdatePayments(UpdatePayments.class),
	InsertPayments(InsertPayments.class),
	Finished(Finished.class);

	
	private Class<? extends QBXmlAdaptor> adaptor;
	
	private QBIntegrationWorkFlow( Class<? extends QBXmlAdaptor> adaptor) {
		this.adaptor = adaptor;
	}
	
	public QBIntegrationWorkFlow incrementStep() {
		int current = this.ordinal();
		current++;
		
		for( QBIntegrationWorkFlow candidate : QBIntegrationWorkFlow.values() ) {
			if( candidate.ordinal() == current ) {
				return candidate;
			}
		}
		return Finished;
	}


	public Class<? extends QBXmlAdaptor> getAdaptor() {
		return adaptor;
	}

	public void setAdaptor(Class<? extends QBXmlAdaptor> adaptor) {
		this.adaptor = adaptor;
	}

	
	public QBXmlAdaptor getAdaptorInstance() {
		try {
			return (QBXmlAdaptor) getAdaptor().newInstance();
		}
		catch( Exception e ) {
			return null;
		}
	}
	
}
