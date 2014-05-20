// =======================================================
// Copyright Mylife.com Inc., 2010. All rights reserved.
//
// =======================================================

package com.klark.common;

/**
 *
 */
public abstract class AbstractEntity extends BaseObject {

    /**
     * Serial version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * @return entity id
     */
    public abstract Long getId();

    /**
     * @return <code>true</code> if this is a new entity (not persisted yet).
     */
}
