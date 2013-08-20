package com.picsauditing.actions.billing

import com.opensymphony.xwork2.{Action, Preparable}
import com.picsauditing.actions.contractors.ContractorActionSupport
import com.picsauditing.dao.{CreditMemoDAO, InvoiceDAO}
import org.springframework.beans.factory.annotation.Autowired
import com.picsauditing.jpa.entities.{InvoiceItem, RefundItem, CreditMemoAppliedToInvoice, Invoice}
import scala.beans.BeanProperty
import scala.collection.JavaConversions._
import com.picsauditing.access.{OpPerms, RequiredPermission}
import scala.Some
import java.lang.{Integer => int}
import java.util.{List => StrutsList}

@RequiredPermission(OpPerms.Billing)
class InvoiceReturnItemsController extends ContractorActionSupport with Preparable {

  @Autowired
  private val creditMemoDAO: CreditMemoDAO = null
  @Autowired
  private val invoiceDAO: InvoiceDAO = null

  @BeanProperty
  var refunds: StrutsList[int] = null.asInstanceOf[StrutsList[int]]
  @BeanProperty
  var invoice: Invoice = null

  private lazy val REDIRECT = "InvoiceDetail.action?invoice.id="

  def prepare = {
    invoice = invoiceDAO.find(getParameter("invoice.id"))
  }

  override def execute = Action.SUCCESS

  def doRefund = {
    try { if (refunds != null && !refunds.isEmpty) {
      save(refundFor(invoice))
      setUrlForRedirect(REDIRECT + invoice.getId)
      Action.INPUT
    } else {
      addActionError("You must select items to refund.")
      execute
    }} catch {
      case e : Throwable => addActionError("There has been a problem creating your refund.")
      Action.ERROR
    }
  }

  private def refundFor(invoice: Invoice) = {
    val refund = CreditMemoAppliedToInvoice.from(invoice)
    refundItemsPickedFrom(invoice) foreach {
      case Some(refundItem) => {
        refund.getCreditMemo.setAuditColumns(permissions)
        refund.getCreditMemo.getItems.add(refundItem)
        refundItem.setCreditMemo(refund.getCreditMemo)
      }
      case None => {} // Do nothing.
    }
    refund
  }

  private def refundItemsPickedFrom(invoice: Invoice): List[Option[RefundItem]] = {
    refunds map { proposedRefundId => generatedReturnItem { foundInvoiceItemFor(proposedRefundId) }}}.toList

  private def foundInvoiceItemFor(proposedRefundItemId: int) = {
    val matchingItem: InvoiceItem => Boolean = existingUnrefundedInvoiceItem(_)(proposedRefundItemId)

    invoice.getItems find matchingItem match {
      case Some(item) => Right(item)
      case None => Left(proposedRefundItemId)
    }
  }

  private def existingUnrefundedInvoiceItem(invoiceItem: InvoiceItem)(lookingFor: int) = {
    invoiceItem.getId == lookingFor && !invoiceItem.isRefunded
  }

  private def generatedReturnItem(invoiceItem: Either[int, InvoiceItem]) = invoiceItem match {
    case Right(item) => {
      item.setRefunded(true)
      Some(new RefundItem(item))
    }
    case Left(id) => {
      addActionError("Item number " + id + " did not exist on this invoice.")
      None
    }
  }

  private def save(creditMemo: CreditMemoAppliedToInvoice) = {
    if (!creditMemo.getCreditMemo.getItems.isEmpty)
      creditMemo.getCreditMemo.updateAmountApplied()
      creditMemo.setAmount(creditMemo.getCreditMemo.getAmountApplied)
      creditMemo.setAuditColumns(permissions)
      creditMemoDAO.save(creditMemo)
  }

}
