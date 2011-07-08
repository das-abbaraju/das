package com.picsauditing.dao;

/**
 * This is a simple extension of PicsDAO. We include this on PicsActionSupport so basic PicsDAO methods are available to
 * all action classes. The reason we don't include PicsDAO directly is because Spring can't tell the difference between
 * the super class and it's inherited classes.
 */
public class BasicDAO extends PicsDAO {

}
