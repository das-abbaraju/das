// =======================================================
// Copyright Mylife.com Inc., 2011. All rights reserved.
//
// =======================================================

package com.klark.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import com.klark.common.AbstractEntity;
import com.klark.common.AbstractEntityDTO;

/**
 * Description: Externalize some reflection code from ModelUtil to make it easier to read.
 * 
 * @author michaelc
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ModelReflectUtil {

    private static Logger logger = LoggerFactory.getLogger(ModelReflectUtil.class);

    /**
     * Get the generic type for a field (if applicable)
     * 
     * @param field
     * @return generic type (e.g. entity, DTO, etc)
     */
    public static Class getCollectionGenericType(Field field) {
        if (field == null)
            return null;
        if (!field.getType().isAssignableFrom(List.class) && !field.getType().isAssignableFrom(Set.class))
            return null;
        if (!Arrays.asList(field.getGenericType().getClass().getInterfaces()).contains(ParameterizedType.class))
            return null;
        return (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    /**
     * Get the entity class from the DTO class
     * 
     * @param dtoClass
     * @return entityClass
     */
    public static Class entityClass(Class dtoClass) {
        if (dtoClass == null)
            return null;
        if (dtoClass.getGenericSuperclass() instanceof ParameterizedType) {
            return ((Class) ((ParameterizedType) dtoClass.getGenericSuperclass()).getActualTypeArguments()[0]);
        } else {
            return ((Class) ((ParameterizedType) dtoClass.getSuperclass().getGenericSuperclass()).getActualTypeArguments()[0]);
        }
    }

    /**
     * Get the entity class from DTO instance
     * 
     * @param dto
     *            instance
     * @return entityClass
     */
    public static Class entityClass(AbstractEntityDTO dto) {

        try {
            return ModelReflectUtil.entityClass(dto.getClass());
        } catch (Exception e) {
            logger.error("Problem getting entity class for DTO: " + dto, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Instantiate entity instance from DTO instance
     * 
     * @param DTO
     *            instance
     * @param dao
     * @return entity instance
     */
    public static AbstractEntity instantiateEntity(AbstractEntityDTO dto) {

        try {
            Constructor constructor = entityClass(dto).getDeclaredConstructor();
            if (constructor == null)
                return null;
            ReflectionUtils.makeAccessible(constructor);
            return (AbstractEntity) constructor.newInstance();
        } catch (Exception e) {
            logger.error("Problem instantiating entity for DTO: " + dto, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Instantiate DTO instance from entity and serviceClass
     * 
     * @param entity
     *            the entity
     * @param serviceClass
     *            the service class
     * @return the DTO instance
     */
    public static final AbstractEntityDTO instantiateDTO(AbstractEntity entity, Class serviceClass) {
        Class dtoClass = null;
        Type typeArgument = null;
        if (((ParameterizedType) serviceClass.getGenericSuperclass()).getActualTypeArguments().length > 1) {
            typeArgument = ((ParameterizedType) serviceClass.getGenericSuperclass()).getActualTypeArguments()[1];
        } else {
            typeArgument = ((ParameterizedType) serviceClass.getSuperclass().getGenericSuperclass()).getActualTypeArguments()[1];
        }
        if (typeArgument.getClass().isAssignableFrom(Class.class)) {
            dtoClass = (Class) typeArgument;
        } else if (ParameterizedType.class.isAssignableFrom(typeArgument.getClass())) {
            dtoClass = (Class) ((ParameterizedType) typeArgument).getRawType();
        }
        if (Modifier.isAbstract(dtoClass.getModifiers())) {
            // if the service declares an abstract DTO
            // then we will take a shot at it here using the entity class name
            // FIXME: add DTO info to entity and use that instead
            try {
                dtoClass = Class.forName(dtoClass.getPackage().getName() + "." + entity.getClass().getSimpleName() + "DTO");
            } catch (ClassNotFoundException e1) {
                throw new RuntimeException(e1);
            }
        }

        try {
            try {

                Constructor constructor = dtoClass.getDeclaredConstructor(entity.getClass());
                if (constructor != null) {
                    ReflectionUtils.makeAccessible(constructor);
                    return (AbstractEntityDTO) constructor.newInstance(entity);
                }
            } catch (NoSuchMethodException e) {
            }
            try {
                Constructor constructor = dtoClass.getDeclaredConstructor();
                if (constructor != null) {
                    ReflectionUtils.makeAccessible(constructor);
                    return (AbstractEntityDTO) constructor.newInstance();
                }
            } catch (NoSuchMethodException e) {
            }
            logger.error("No DTO constructor found for service: " + serviceClass);
            return null;
        } catch (Exception e) {
            logger.error("Problem instantiating DTO for service: " + serviceClass, e);
            throw new RuntimeException(e);
        }

    }

}
