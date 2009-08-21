package com.picsauditing.gwt.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ModelChangeListenerList<T extends IsSerializable> implements IsSerializable{

    private T source;

    public ModelChangeListenerList(T source) {
        this.source = source;
    }
    
    public ModelChangeListenerList() {
	}

    private ArrayList<ModelChangeListener<T>> listeners = new ArrayList<ModelChangeListener<T>>();

    public void add(ModelChangeListener<T> listener) {
        listeners.add(listener);
    }
    
    public void remove(ModelChangeListener<T> listener) {
        listeners.remove(listener);
    }

    public void fireChangeEvent() {
        ArrayList<ModelChangeListener<T>> copy = (ArrayList<ModelChangeListener<T>>) listeners.clone();
        for (ModelChangeListener<T> listener : copy) {
            listener.onChange(source);
        }
    }


}