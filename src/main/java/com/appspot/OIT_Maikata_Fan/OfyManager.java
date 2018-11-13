package com.appspot.OIT_Maikata_Fan;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import java.io.Closeable;
import java.util.List;

public class OfyManager implements Closeable {
    private static final OfyManager ourInstance = new OfyManager();

    private OfyManager() {
    }

    public static OfyManager getInstance() {
        return ourInstance;
    }

    public <E> void makePersistent(E entity) {
        ObjectifyService.ofy().save().entity(entity).now();
    }

    public <E> E getObjectById(Class<E> type, String id) {
        return ObjectifyService.ofy().load().type(type).id(id).now();
    }

    public void deletePersistent(Object entity) {
        ObjectifyService.ofy().delete().entity(entity).now();
    }

    public <E> List<E> queryObjectByKey(Class<E> type, String condition, String value) {
        return ObjectifyService.ofy().load().type(type).filterKey(condition, Key.create(type, value)).list();
    }

    @Override
    public void close() {

    }
}
