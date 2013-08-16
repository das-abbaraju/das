package com.picsauditing.dao

import com.picsauditing.jpa.entities.CreditMemoAppliedToInvoice
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.annotation.Propagation;

class CreditMemoDAO extends PicsDAO {

  @Transactional(propagation = Propagation.NESTED)
  def save(o: CreditMemoAppliedToInvoice) = {
    if (o.getId == 0) {
      em.persist(o)
      o
    }
    else em.merge(o)
  }

}
