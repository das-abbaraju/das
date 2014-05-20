// =======================================================
// Copyright Mylife.com Inc., 2011. All rights reserved.
//
// =======================================================

package com.klark.common;

import java.io.Serializable;

/**
 * Base class for Model objects. Child objects may override <tt>toString()</tt>, <tt>equals()</tt>
 * and <tt>hashCode()</tt>.
 */
public abstract class BaseObject implements Serializable {

    /**
     * Base object's serial version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * <p>
     * String uniquely identifying this object.
     * </p>
     * 
     * @return a String representation of the bean.
     */
    /**
     * <p>
     * Compares object equality.
     * </p>
     * 
     * <p>
     * When using Hibernate, the primary key should not be a part of this comparison.
     * </p>
     * 
     * @param obj
     *            object to compare to
     * @return true/false based on equality tests
     * 
     *         /**
     *         <p>
     *         Return hash code for this object.
     *         </p>
     * 
     *         <p>
     *         When you override equals, you should override hashCode. See
     *         "Why are equals() and hashCode() importation" for more information:
     *         http://www.hibernate.org/109.html
     *         </p>
     * 
     * @return hashCode
     */
}
