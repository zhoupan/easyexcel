package com.alibaba.excel.support;

import java.util.AbstractMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jodd.bean.BeanUtil;
import jodd.bean.BeanWalker;
import jodd.bean.BeanWalker.BeanWalkerCallback;

/**
 * BeanMap (Replace for net.sf.cglib.beans.BeanMap)
 * 
 * @author zhoupan
 *
 */
public class BeanMap extends AbstractMap<String, Object> implements Map<String, Object>, Cloneable, BeanWalkerCallback {

    /**
     * Replace for (net.sf.cglib.beans.BeanMap.create)
     *
     * @param bean the bean
     * @return the bean map
     */
    public static BeanMap create(Object bean) {
        BeanMap gen = new BeanMap(bean);
        return gen;
    }

    /** The bean. */
    private transient Object bean;

    /** Constructs a new empty <code>BeanMap</code>. */
    public BeanMap() {}

    /**
     * Constructs a new <code>BeanMap</code> that operates on the specified bean. If the given bean is
     * <code>null</code>, then this map will be empty.
     * 
     *
     * @param bean
     *            the bean for this map to operate on
     */
    public BeanMap(Object bean) {
        this.bean = bean;
        initialise();
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "BeanMap<" + String.valueOf(bean) + ">";
    }

    /**
     * Clone.
     *
     * @return the object
     * @throws CloneNotSupportedException the clone not supported exception
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        BeanMap newMap = new BeanMap();
        if (this.bean != null) {
            Class<?> beanClass = null;
            try {
                beanClass = bean.getClass();
                newMap.setBean(beanClass.newInstance());
                newMap.putAll(this.holder);
            } catch (Exception e) {
                throw new UnsupportedOperationException("Could not create new instance of class: " + beanClass);
            }
        }
        return newMap;
    }

    /**
     * This method reinitializes the bean map to have default values for the bean's properties. This is accomplished by
     * constructing a new instance of the bean which the map uses as its underlying data source. This behavior for
     * <code>clear()</code> differs from the Map contract in that the mappings are not actually removed from the map
     * (the mappings for a BeanMap are fixed).
     */
    @Override
    public void clear() {
        if (bean == null) {
            return;
        }
        Class<?> beanClass = null;
        try {
            beanClass = bean.getClass();
            bean = beanClass.newInstance();
            this.initialise();
        } catch (Exception e) {
            throw new UnsupportedOperationException("Could not create new instance of class: " + beanClass);
        }
    }

    /**
     * Returns true if the bean defines a property with the given name.
     *
     * <p>
     * The given name must be a <code>String</code>; if not, this method returns false. This method will also return
     * false if the bean does not define a property with that name.
     *
     * <p>
     * Write-only properties will not be matched as the test operates against property read methods.
     *
     * @param name
     *            the name of the property to check
     * @return false if the given name is null or is not a <code>String</code>; false if the bean does not define a
     *         property with that name; or true if the bean does define a property with that name
     */
    @Override
    public boolean containsKey(Object name) {
        if (name == null) {
            return false;
        }
        return this.holder.containsKey(name);
    }

    /**
     * Contains value.
     *
     * @param value the value
     * @return true, if successful
     */
    @Override
    public boolean containsValue(Object value) {
        return this.holder.containsValue(value);
    }

    /**
     * Gets the.
     *
     * @param name the name
     * @return the object
     */
    public Object get(String name) {
        return BeanUtil.silent.getProperty(this.bean, name.toString());
    }

    /**
     * Put.
     *
     * @param name the name
     * @param value the value
     * @return the object
     * @throws IllegalArgumentException the illegal argument exception
     * @throws ClassCastException the class cast exception
     */
    @Override
    public Object put(String name, Object value) throws IllegalArgumentException, ClassCastException {
        if (bean != null) {
            Object oldValue = get(name);
            try {
                BeanUtil.pojo.setProperty(this.bean, name.toString(), value);
                this.holder.put(name, value);
                return oldValue;
            } catch (Throwable e) {
                // ignore
            }
            throw new IllegalArgumentException(
                "The bean of type: " + bean.getClass().getName() + " has no property called: " + name);
        }
        return null;
    }

    /**
     * Returns the number of properties defined by the bean.
     *
     * @return the number of properties defined by the bean
     */
    @Override
    public int size() {
        return this.holder.size();
    }

    // Helper methods
    // -------------------------------------------------------------------------

    /**
     * Returns the type of the property with the given name.
     *
     * @param name
     *            the name of the property
     * @return the type of the property, or <code>null</code> if no such property exists
     */
    public Class<?> getType(String name) {
        return BeanUtil.pojo.getPropertyType(this.bean, name);
    }

    // Properties
    // -------------------------------------------------------------------------

    /**
     * Returns the bean currently being operated on. The return value may be null if this map is empty.
     *
     * @return the bean being operated on by this map
     */
    public Object getBean() {
        return bean;
    }

    /**
     * Sets the bean to be operated on by this map. The given value may be null, in which case this map will be empty.
     *
     * @param newBean
     *            the new bean to operate on
     */
    public void setBean(Object newBean) {
        bean = newBean;
        reinitialise();
    }

    /**
     * Reinitializes this bean. Called during {@link #setBean(Object)}. Does introspection to find properties.
     */
    protected void reinitialise() {
        initialise();
    }

    /**
     * Initialise.
     */
    private void initialise() {
        if (getBean() == null) {
            return;
        }
        this.holder.clear();
        BeanWalker walker = BeanWalker.walk(this);
        walker.bean(this.getBean());
    }

    /** The holder. */
    private Map<String, Object> holder = new HashMap<String, Object>();

    /**
     * Visit property.
     *
     * @param name the name
     * @param value the value
     */
    @Override
    public void visitProperty(String name, Object value) {
        this.holder.put(name, value);
    }

    /**
     * Entry set.
     *
     * @return the sets the
     */
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return this.holder.entrySet();
    }

}
