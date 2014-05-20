// =======================================================
// Copyright Mylife.com Inc., 2011. All rights reserved.
//
// =======================================================

package com.klark.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.HibernateProxyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;
import org.springframework.util.StringUtils;

import com.klark.common.AbstractEntity;
import com.klark.common.AbstractEntityDTO;
import com.klark.common.IgnoreFieldTransform;
import com.klark.exception.NotFoundException;
import com.klark.message.dao.DaoBase;

/**
 * Description: Utility for settings fields in entity from DTO and vice-versa.
 * 
 * @author michaelc
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ModelUtil {

    private static Logger logger = LoggerFactory.getLogger(ModelUtil.class);

    // the maximum depth to which we will descend when populating embedded DTO objects
    // NOTE: unless cascade=true in api, we will still not load embedded collections of DTO objects
    private static final int MAX_DEPTH = 1;

    /**
     * Copy data from entity -> DTO
     * 
     * @param dto
     *            the dto
     * @param entity
     *            the entity
     * @param dao
     *            a dao
     * @param cascade
     *            indicate whether or not to cascade entity -> DTO conversions
     */
    public static final void setDTOFieldsFromEntity(final AbstractEntityDTO dto, final AbstractEntity entity, final DaoBase dao, boolean cascade) {
        doSetDTOFieldsFromEntity(dto, entity, dao, cascade, 0, new HashMap<AbstractEntity, AbstractEntityDTO>());
    }

    private static final void doSetDTOFieldsFromEntity(final AbstractEntityDTO dto, final AbstractEntity entityOrProxy, final DaoBase dao, final boolean cascade, final int depth,
            final Map<AbstractEntity, AbstractEntityDTO> alreadySeen) {

        final AbstractEntity entity = unproxy(entityOrProxy);
        final Class entityClass = HibernateProxyHelper.getClassWithoutInitializingProxy(entity);

        // First hydrate the fields that exist in the entity and the DTO
        ReflectionUtils.doWithFields(dto.getClass(), new FieldCallback() {

            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

                if (Modifier.toString(field.getModifiers()).contains("static"))
                    return;
                Field entityField = ReflectionUtils.findField(entityClass, field.getName());
                if (entityField != null) {

                    IgnoreFieldTransform ignoreFieldTransform = entityField.getAnnotation(IgnoreFieldTransform.class);

                    if (ignoreFieldTransform != null) {
                        return;
                    }
                    ReflectionUtils.makeAccessible(entityField);
                    ReflectionUtils.makeAccessible(field);
                    // Class entityCollectionType =
                    // ModelReflectUtil.getCollectionGenericType(entityField);
                    Class collectionType = ModelReflectUtil.getCollectionGenericType(field);

                    Object entityValue = entityField.get(entity);
                    Object value = null;

                    try {

                        if (entityValue != null) {

                            // get hibernate out of our DTO objects :)
                            if (PersistentCollection.class.isAssignableFrom(entityValue.getClass())) {
                                if (!cascade && depth > 0)
                                    return;
                                PersistentCollection pc = (PersistentCollection) entityValue;
                                pc.forceInitialization();
                                entityValue = pc.getValue();
                            }

                            if (entityField.getType().isEnum()) {
                                if (String.class.isAssignableFrom(field.getType())) {
                                    value = ((Enum) entityValue).name();
                                } else if (field.getType().isEnum()) {
                                    value = entityValue;
                                } else {
                                    logger.warn("Enum can only be converted to enum or string type");
                                }
                            } else if (AbstractEntity.class.isAssignableFrom(entityField.getType()) && AbstractEntityDTO.class.isAssignableFrom(field.getType())) {
                                // embedded DTOs within DTOs
                                value = toEmbeddedDTO(field.getType(), (AbstractEntity) entityField.get(entity), dao, cascade, depth, alreadySeen);
                            } else if (Set.class.isAssignableFrom(entityValue.getClass())) {
                                value = new HashSet();
                                copyCollection(collectionType, (Set) value, (Set) entityValue, dao, cascade, depth, alreadySeen);
                            } else if (List.class.isAssignableFrom(entityValue.getClass())) {
                                value = new ArrayList();
                                copyCollection(collectionType, (List) value, (List) entityValue, dao, cascade, depth, alreadySeen);
                            } else if (Hashtable.class.isAssignableFrom(entityValue.getClass())) {
                                value = new Hashtable((Map) entityValue);
                            } else if (Map.class.isAssignableFrom(entityValue.getClass())) {
                                value = new HashMap((Map) entityValue); // TODO: handle map of DTOs
                            } else {
                                value = entityValue;
                            }
                            field.set(dto, value);
                        }
                    } catch (Exception e) {
                        logger.error("Problem populating DTO field: " + field + ", " + e);
                    }
                }

            }
        });
        final Class dtoClass = dto.getClass();
        // Then dereference any fields that are marked as @IgnoreFieldTransform fieldNames
        ReflectionUtils.doWithFields(dtoClass, new FieldCallback() {

            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                ReflectionUtils.makeAccessible(field);

                // id field
                // if it is not a type we know how to handle throw an exception
                throw new RuntimeException("@HyrdateFrom can only reference Long or List<Long> or Set<Long> fields!");
            }
        }, new FieldFilter() {

            public boolean matches(Field field) {
                ReflectionUtils.makeAccessible(field);
                try {
                    return false;
                } catch (IllegalArgumentException e) {
                    logger.error("Problem checking for hydratefrom annotation", e);
                    return false;
                }
            }
        });
    }

    /**
     * Copy data from DTO -> entity
     * 
     * @param entity
     *            the entity
     * @param dto
     *            the dto
     * @param dao
     *            a dao
     */
    public static final void setEntityFieldsFromDTO(AbstractEntity entity, final AbstractEntityDTO dto, DaoBase dao) {

        boolean isUpdate = dto.getId() != null;

        final Class dtoClass = dto.getClass();

        final List<Field> entityFields = new ArrayList<Field>();

        // Get all of the fields in the entity
        ReflectionUtils.doWithFields(entity.getClass(), new FieldCallback() {

            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                // exclude: all static fields
                if (!Modifier.toString(field.getModifiers()).contains("static"))
                    entityFields.add(field);
            }
        });

        // for each field set the corresponding
        // field in the entity
        for (Field field : entityFields) {
            Field dtoField = ReflectionUtils.findField(dtoClass, field.getName());
            if (dtoField == null)
                continue;
            ReflectionUtils.makeAccessible(dtoField);
            if (isUpdate)
                continue;
            ReflectionUtils.makeAccessible(field);
            if (!isUpdate && field.getName().equalsIgnoreCase("id") && field.getType().isAssignableFrom(Long.class)) {
                // DO NOT SET THE ID FOR AN INSERT!!!
                continue;
            }

            Object value = null;

            try {
                // check to see if we have to hydrate anything
                if ((field.getType().isAssignableFrom(List.class) || field.getType().isAssignableFrom(Set.class))
                        && AbstractEntity.class.isAssignableFrom(ModelReflectUtil.getCollectionGenericType(field))) {
                    // convert list or set of dto -> list or set of entity
                    if (field.getType().isAssignableFrom(List.class)) {
                        value = new ArrayList();
                    } else {
                        value = new HashSet();
                    }
                    Class collectionType = ModelReflectUtil.getCollectionGenericType(field);
                    Collection dtoList = (Collection) dtoField.get(dto);
                    for (Object o : dtoList) {
                        AbstractEntityDTO cdto = (AbstractEntityDTO) o;
                        Long cid = cdto.getId();
                        AbstractEntity item = null;
                        if (cid != null) {
                            item = dao.hydrate(collectionType, cid);
                            if (item == null) {
                                throw new NotFoundException("not found for id: " + cid);
                            }
                        } else {
                            item = ModelReflectUtil.instantiateEntity(cdto);
                        }
                        ModelUtil.setEntityFieldsFromDTO(item, cdto, dao);
                        ((Collection) value).add(item);
                    }

                }// if the field is an enum or a constant be need to look up the value as well if it
                 // is an enum
                else if (field.getType().isEnum()) {
                    if (dtoField.get(dto) != null) {
                        Class type = field.getType();
                        if (dtoField.getType().isAssignableFrom(String.class)) {
                            value = Enum.valueOf(type, (String) dtoField.get(dto));
                        } else if (dtoField.getType().isEnum()) {
                            value = dtoField.get(dto);
                        } else {
                            throw new RuntimeException("Enums can only be derefenced from a String or enum value!");
                        }
                    }
                } else {
                    value = dtoField.get(dto);
                }
                if (isUpdate && isNullOrEmpty(value))
                    continue;
                // else just set the value we have, like an id
                field.set(entity, value);
            } catch (Exception e) {
                logger.error("Problem setting value hydrated from db for field: " + field, e);
            }
        }
    }

    private static AbstractEntityDTO toEmbeddedDTO(Class dtoClass, AbstractEntity entity, DaoBase dao, final boolean cascade, final int depth, Map<AbstractEntity, AbstractEntityDTO> alreadySeen) {

        if (entity == null)
            return null;

        if (!cascade && depth > 0)
            return null;

        AbstractEntityDTO embeddedDTO = alreadySeen.get(entity);
        if (embeddedDTO != null) {
            return embeddedDTO;
        }

        if (depth > MAX_DEPTH)
            return null;

        try {
            embeddedDTO = (AbstractEntityDTO) dtoClass.newInstance();
            ModelUtil.doSetDTOFieldsFromEntity(embeddedDTO, entity, dao, cascade, depth + 1, alreadySeen);
            embeddedDTO.prepareForDisplay(entity);
            alreadySeen.put(entity, embeddedDTO);
        } catch (Exception e) {
            logger.error("Problem populating embedded DTO: " + dtoClass + ", " + e);
        }
        return embeddedDTO;
    }

    private static void copyCollection(Class collectionType, Collection newCollection, Collection oldCollection, DaoBase dao, final boolean cascade, final int depth,
            Map<AbstractEntity, AbstractEntityDTO> alreadySeen) {
        if (AbstractEntityDTO.class.isAssignableFrom(collectionType)) {
            if (!cascade && depth > 0)
                return;
            for (Object entity : oldCollection) {
                AbstractEntityDTO dto = toEmbeddedDTO(collectionType, (AbstractEntity) entity, dao, cascade, depth, alreadySeen);
                if (dto != null)
                    newCollection.add(dto);
            }
        } else {
            newCollection.addAll(oldCollection);
        }
    }

    public static Long idOfAbstractEntity(AbstractEntity entity) {
        if (entity != null) {
            return entity.getId();
        }
        return null;
    }

    public static Collection<Long> idsOfAbstractEntitys(Collection<? extends AbstractEntity> entities) {
        Collection<Long> ids = null;
        if (entities instanceof Set) {
            ids = new HashSet<Long>();
        } else {
            ids = new ArrayList<Long>();
        }
        if (entities != null) {
            for (AbstractEntity entity : entities) {
                ids.add(entity.getId());
            }
        }
        return ids;
    }

    public static boolean isNullOrEmpty(Object o) {
        if (o instanceof Collection) {
            return CollectionUtils.isEmpty((Collection) o);
        } else if (o instanceof Map) {
            return CollectionUtils.isEmpty((Map) o);
        } else if (o instanceof String) {
            return !StringUtils.hasText((String) o);
        } else {
            return o == null;
        }
    }

    public static <T> T unproxy(T entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof HibernateProxy) {
            Hibernate.initialize(entity);
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
        return entity;
    }
}
