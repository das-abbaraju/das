package com.picsauditing.eula.model;

import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.User;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "eula_agreement")
public class EulaAgreement extends BaseTable {
    private User user;
    private Eula eula;

    @ManyToOne
    @JoinColumn(name = "userId")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "eulaId")
    public Eula getEula() {
        return eula;
    }

    public void setEula(Eula eula) {
        this.eula = eula;
    }
}
