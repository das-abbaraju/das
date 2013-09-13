package com.picsauditing.selenium;

import com.picsauditing.util.Strings;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class Deleter {
    public abstract void execute() throws SQLException;
    protected String IDs;

    public Deleter(List<Integer> IDs) {
        this.IDs = Strings.implodeForDB(IDs);
    }

    public Deleter(int ID) {
        IDs = String.valueOf(ID);
    }

    public Deleter() {
    }

    public Deleter setIds(List<Integer> IDNumbers) {
        this.IDs = Strings.implodeForDB(IDNumbers);
        return this;
    }

    public Deleter setId(int ID) {
        IDs = String.valueOf(ID);
        return this;
    }
    protected static List<Integer> getIDNumbers(List<SeleniumDeletable> deletables, String testMethod) {
        List<Integer> IDs = new ArrayList<>();
        for (SeleniumDeletable deleteMe : deletables) {
            try {
                if ((Boolean) SeleniumDeletable.class.getDeclaredMethod(testMethod).invoke(deleteMe))
                    IDs.add(deleteMe.getID());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return IDs;
    }
}
