package com.picsauditing.actions.billing

import com.opensymphony.xwork2.{Action, Preparable}
import com.picsauditing.actions.contractors.ContractorActionSupport
import com.picsauditing.dao.{CreditMemoDAO, InvoiceDAO}
import org.springframework.beans.factory.annotation.Autowired
import com.picsauditing.jpa.entities._
import scala.beans.BeanProperty
import scala.collection.JavaConversions._
import com.picsauditing.access.{OpPerms, RequiredPermission}
import scala.Some
import java.lang.{Integer => int}
import java.util.{List => StrutsList}
import com.picsauditing.util.SapAppPropertyUtil
import java.io.PrintStream
import scala.Some

@RequiredPermission(OpPerms.Billing)
class InvoiceReturnItemsController extends ContractorActionSupport with Preparable {

  @Autowired
  private val creditMemoDAO: CreditMemoDAO = null
  @Autowired
  private val invoiceDAO: InvoiceDAO = null

  var sapAppPropertyUtil: SapAppPropertyUtil = null

  @BeanProperty
  var returns: StrutsList[int] = null.asInstanceOf[StrutsList[int]]
  @BeanProperty
  var invoice: Invoice = null

  private lazy val REDIRECT = "InvoiceDetail.action?invoice.id="

  def prepare = {
    Option(sapAppPropertyUtil) match {
      case Some(_) => Unit
      case None => sapAppPropertyUtil = SapAppPropertyUtil.factory
    }
    invoice = invoiceDAO.find(getParameter("invoice.id"))
  }

  override def execute = Action.SUCCESS

  def doReturn = {
    try { if (returns != null && !returns.isEmpty) {
      val returnForInvoice = returnFor(invoice)
      save(returnForInvoice)
      setUrlForRedirect(REDIRECT + invoice.getId)
      Action.INPUT
    } else {
      addActionError("You must select items to return.")
      execute
    }} catch {
      case e : Exception => {
        addActionError("There has been a problem creating your return.")
        println(e.getMessage)
        e.printStackTrace
      }
      Action.ERROR
    }
  }

  private def returnFor(invoice: Invoice) = {
    val returnForInvoice = CreditMemoAppliedToInvoice.from(invoice)
    returnItemsPickedFrom(invoice) foreach {
      case Some(returnItem) => {
        returnForInvoice.getCreditMemo.setAuditColumns(permissions)
        returnForInvoice.getCreditMemo.getItems.add(returnItem)
        returnItem.setCreditMemo(returnForInvoice.getCreditMemo)
      }
      case None => {} // Do nothing.
    }
    returnForInvoice
  }

  private def returnItemsPickedFrom(invoice: Invoice): List[Option[ReturnItem]] = {
    returns map { proposedReturnId => generatedReturnItem { foundInvoiceItemFor(proposedReturnId) }}}.toList

  private def foundInvoiceItemFor(proposedReturnItemId: int) = {
    val matchingItem: InvoiceItem => Boolean = existingUnreturnedInvoiceItem(_)(proposedReturnItemId)

    invoice.getItems find matchingItem match {
      case Some(item) => Right(item)
      case None => Left(proposedReturnItemId)
    }
  }

  private def existingUnreturnedInvoiceItem(invoiceItem: InvoiceItem)(lookingFor: int) = {
    invoiceItem.getId == lookingFor && !invoiceItem.setReturned
  }

  private def generatedReturnItem(invoiceItem: Either[int, InvoiceItem]) = invoiceItem match {
    case Right(item) => {
      item.setReturned(true)
      Some(new ReturnItem(item))
    }
    case Left(id) => {
      addActionError("Item number " + id + " has already been returned on this invoice.")
      None
    }
  }

  private def save(creditMemo: CreditMemoAppliedToInvoice) = {
    if (!creditMemo.getCreditMemo.getItems.isEmpty) {
      creditMemo.updateAmountApplied()
      creditMemo.setAmount(creditMemo.getCreditMemo.getAmountApplied)
      creditMemo.setAuditColumns(permissions)
      if (sapAppPropertyUtil.isSAPBusinessUnitSetSyncTrueEnabled(creditMemo.getInvoice.getAccount.getCountry.getBusinessUnit.getId)) {
        creditMemo.getCreditMemo.setSapSync(true)
      }

      if (invoice.getPayments.size() > 0) {
        val diff = invoice.getTotalAmount.subtract(invoice.getAmountApplied)

        if (diff.doubleValue() < creditMemo.getAmount.doubleValue()) {
          val refundApplied = RefundAppliedToCreditMemo.from(creditMemo.getCreditMemo)
          refundApplied.setAmount(creditMemo.getAmount)
          refundApplied.setAuditColumns(permissions)
          if (sapAppPropertyUtil.isSAPBusinessUnitSetSyncTrueEnabled(creditMemo.getInvoice.getAccount.getCountry.getBusinessUnit.getId)) {
            refundApplied.getRefund.setSapSync(true)
          }
          creditMemoDAO.save(refundApplied)
        }
      }
    }

    creditMemoDAO.save(creditMemo)
  }

}
