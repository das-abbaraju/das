// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.util;

import com.google.gson.Gson;
import com.klark.common.AbstractEntity;
import com.klark.common.AbstractEntityDTO;

/**
 * Description here!
 * 
 * 
 * @author
 */

public class ObjectUtils {

    public static AbstractEntity convert(AbstractEntityDTO dto) {
        Gson gson = new Gson();
        String json = gson.toJson(dto);
        AbstractEntity entity = gson.fromJson(json, AbstractEntity.class);
        return entity;
    }

    public static AbstractEntityDTO<?> convert(AbstractEntity entity) {
        Gson gson = new Gson();
        String json = gson.toJson(entity);
        AbstractEntityDTO dto = gson.fromJson(json, AbstractEntityDTO.class);
        return dto;
    }

    public static void convert(Class dto) {
        Gson gson = new Gson();
        String json = gson.toJson(dto);
        AbstractEntity entity = gson.fromJson(json, AbstractEntity.class);
        // return entity;
    }
}
