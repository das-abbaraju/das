package com.picsauditing.flagcalculator.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.Trade")
@Table(name = "ref_trade")
//@SqlResultSetMapping(name = "matchingTradeResults", entities = @EntityResult(entityClass = com.picsauditing.flagcalculator.entities.Trade.class), columns = @ColumnResult(name = "matching"))
public class Trade extends BaseTable {

	static public final int TOP_ID = 5;
	static public final Trade TOP = new Trade(TOP_ID);

	private Trade parent = TOP;
	private Float naicsTRIR;
	private Float naicsLWCR;

	private List<Trade> children = new ArrayList<>();

	public Trade() {
	}

	public Trade(int id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "parentID")
	public Trade getParent() {
		return parent;
	}

	public void setParent(Trade parent) {
		this.parent = parent;
	}

	@OneToMany(mappedBy = "parent")
	public List<Trade> getChildren() {
		return children;
	}

	public void setChildren(List<Trade> children) {
		this.children = children;
	}

	public Float getNaicsTRIR() {
		return naicsTRIR;
	}

	public void setNaicsTRIR(Float naicsTRIR) {
		this.naicsTRIR = naicsTRIR;
	}

<<<<<<< HEAD
    @Transient
    public Float getNaicsTRIRI() {
        if (naicsTRIR != null && naicsTRIR != 0.0) {
            return naicsTRIR;
        } else if (parent != null) {
            return parent.getNaicsTRIRI();
        } else {
            return Float.valueOf(4);
        }
    }

    //	@ReportField(importance = FieldImportance.Average, type = FieldType.Float)
=======
>>>>>>> 7ae760b... US831 Deprecated old FDC
	public Float getNaicsLWCR() {
		return naicsLWCR;
	}

	public void setNaicsLWCR(Float naicsLWCR) {
		this.naicsLWCR = naicsLWCR;
	}
<<<<<<< HEAD

	@Transient
	public Float getNaicsLWCRI() {
		if (naicsLWCR != null && naicsLWCR != 0.0) {
			return naicsLWCR;
		} else if (parent != null) {
			return parent.getNaicsLWCRI();
		} else {
			return Float.valueOf(4);
		}
	}
//
//	@Transient
//	public boolean isRemoved() {
//		return false;
//	}
=======
>>>>>>> 7ae760b... US831 Deprecated old FDC
}