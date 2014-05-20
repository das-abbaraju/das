// =======================================================
// Copyright Mylife.com Inc., 2011. All rights reserved.
//
// =======================================================

package com.klark.common;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Abstract Entity DTO
 * 
 * 
 * @author
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public abstract class AbstractEntityDTO<E extends AbstractEntity> extends BaseObject {

    private static final long serialVersionUID = 1L;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void prepareForDisplay(E entity) {
        // override to prepare for display
    }

}
