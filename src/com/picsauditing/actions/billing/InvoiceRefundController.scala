package com.picsauditing.actions.billing

import com.opensymphony.xwork2.Preparable
import com.picsauditing.actions.contractors.ContractorActionSupport
import com.picsauditing.dao.{CreditMemoDAO, InvoiceDAO}
import org.springframework.beans.factory.annotation.Autowired
import com.picsauditing.jpa.entities.{RefundItem, CreditMemoAppliedToInvoice, Invoice}
import scala.beans.BeanProperty
import scala.collection.JavaConversions._
import com.picsauditing.access.{OpPerms, RequiredPermission}
import scala.Some

@RequiredPermission(OpPerms.Billing)
class InvoiceRefundController extends ContractorActionSupport with Preparable {

  @Autowired
  private val creditMemoDAO: CreditMemoDAO = null
  @Autowired
  private val invoiceDAO: InvoiceDAO = null

  @BeanProperty
  var refunds: java.util.List[java.lang.Integer] = null.asInstanceOf[java.util.List[java.lang.Integer]]
  @BeanProperty
  var invoice: Invoice = null

  private val REDIRECT = ""

  def prepare = {
    invoice = invoiceDAO.find(getParameter("invoice.id"))
  }

  override def execute = "SUCCESS"

  def doRefund = if (refunds != null && !refunds.isEmpty) {
    save(refundFor(invoice))
    setUrlForRedirect(REDIRECT)
  } else {
    addActionError("You must select items to refund.")
    execute
  }

  private def refundFor(invoice: Invoice) = {
    val refund = CreditMemoAppliedToInvoice.from(invoice)
    selectedRefundItemsFor(invoice) foreach { item => item match {
        case Some(refundItem) => refund.getCreditMemo.getItems.add(refundItem)
        case None => {} // Do nothing.
      }
    }
    refund
  }

  private def selectedRefundItemsFor(invoice: Invoice): List[Option[RefundItem]] = {
    refunds map { id => invoice.getItems.find(item => item.getId == id ) match {
        case Some(item) => {
          item.setRefunded(true)
          Some(new RefundItem(item))
        }
        case None => {
          addActionError("Item number " + id + " did not exist on this invoice.")
          None
        }
      }
    }
  }.toList

  private def save(creditMemo: CreditMemoAppliedToInvoice) = {
    if (!creditMemo.getCreditMemo.getItems.isEmpty)
      creditMemoDAO.save(creditMemo)
  }

}
