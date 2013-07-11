package com.picsauditing.actions.billing

import com.opensymphony.xwork2.Preparable
import com.picsauditing.actions.contractors.ContractorActionSupport
import com.picsauditing.dao.{InvoiceFeeCountryDAO, InvoiceFeeDAO, InvoiceDAO, PaymentDAO}
import org.springframework.beans.factory.annotation.Autowired
import com.picsauditing.jpa.entities.Invoice
import com.picsauditing.util.SpringUtils
import scala.beans.BeanProperty
import scala.collection.JavaConversions._

object InvoiceRefundController {
  lazy private val allFees = SpringUtils.getBean[InvoiceFeeDAO]("InvoiceFeeDAO").findAll()
}

class InvoiceRefundController extends ContractorActionSupport with Preparable {

  @Autowired
  private val invoiceDAO: InvoiceDAO = null
  @Autowired
  private val paymentDAO: PaymentDAO = null

//  @BeanProperty
//  var fees: java.util.List = null.asInstanceOf[java.util.List]
  private var invoice: Invoice = null

  def prepare = {
    invoice = invoiceDAO.find(getParameter("invoice.id"))
  }

  override def execute = {
//    invoice.getItems.map
    "SUCCESS"
  }
}
