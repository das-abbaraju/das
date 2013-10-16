package com.picsauditing.dao;

import com.picsauditing.database.domain.UpdatableListItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CollectionDAO {

    // UPDATE must be Overridden in the inheriting class
    public static <T extends UpdatableListItem> Collection<T> insertUpdateDeleteManaged(Collection<T> dbLinkedList,
                                                                                         Collection<T> changes) {
        // update/delete
        Iterator<T> dbIterator = dbLinkedList.iterator();
        Collection<T> removalList = new ArrayList<T>();

        while (dbIterator.hasNext()) {
            T fromDB = dbIterator.next();
            T found = null;

            for (T change : changes) {
                if (fromDB.equals(change)) {
                    fromDB.update(change);
                    found = change;
                }
            }

            if (found != null) {
                changes.remove(found); // update was performed
            } else {
                removalList.add(fromDB);
            }
        }

        // merging remaining changes (updates/inserts)
        dbLinkedList.addAll(changes);

        return removalList;
    }

    // UPDATE must be Overridden in the inheriting class
    // IMPORTANT NOTE: Only use this as a necessity. Performance using this
    // operation is severely degraded
    // compared to the hibernate managed insert/update/delete above
    public static <T extends UpdatableListItem> void insertUpdateDeleteExplicit(Collection<T> unLinkedList,
                                                                                 Collection<T> changes, BaseTableDAO dao) {
        // update/delete
        Collection<T> deletes = new ArrayList<T>();
        Iterator<T> dbIterator = unLinkedList.iterator();
        while (dbIterator.hasNext()) {
            T fromDB = dbIterator.next();
            T found = null;

            for (T change : changes) {
                if (fromDB.equals(change)) {
                    fromDB.update(change);
                    found = change;
                }
            }

            if (found != null) {
                changes.remove(found); // update was performed
            } else {
                deletes.add(fromDB);
                dbIterator.remove();
            }
        }

        // merging remaining changes (updates/inserts)
        unLinkedList.addAll(changes);
        for (T insertOrUpdate : unLinkedList) {
            dao.save(insertOrUpdate);
        }

        // performing deletes
        for (T delete : deletes) {
            dao.remove(delete);
        }
    }

}
