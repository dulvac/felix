package org.apache.felix.ipojo.handler.extender;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.util.Callback;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;

public class ExtenderManager implements SynchronousBundleListener {
    
    private String m_extension;
    private Callback m_onArrival;
    private Callback m_onDeparture;
    private PrimitiveHandler m_handler;
    private BundleContext m_context;
    private List m_bundles = new ArrayList();
    
    public ExtenderManager(ExtenderModelHandler handler, String extension, String bind, String unbind) {
        m_handler = handler;
        m_onArrival = new Callback(bind, new Class[] {Bundle.class, String.class}, false, m_handler.getInstanceManager());
        m_onDeparture = new Callback(unbind, new Class[] {Bundle.class}, false, m_handler.getInstanceManager());
        m_extension = extension;
        m_context = handler.getInstanceManager().getContext();
    }
    
    public void start() {
        synchronized (this) {
            // listen to any changes in bundles.
            m_context.addBundleListener(this);
            // compute already started bundles.
            for (int i = 0; i < m_context.getBundles().length; i++) {
                if (m_context.getBundles()[i].getState() == Bundle.ACTIVE) {
                    onArrival(m_context.getBundles()[i]);
                }
            }
        }
    }
    
    private void onArrival(Bundle bundle) {
        Dictionary headers = bundle.getHeaders();
        String header = (String )headers.get(m_extension);
        if (header != null) {
            m_bundles.add(bundle);
            try {
                m_onArrival.call(new Object[] {bundle, header});
            } catch (NoSuchMethodException e) {
                m_handler.error("The onArrival method " + m_onArrival.getMethod() + " does not exist in the class", e);
                m_handler.getInstanceManager().stop();
            } catch (IllegalAccessException e) {
                m_handler.error("The onArrival method " + m_onArrival.getMethod() + " cannot be called", e);
                m_handler.getInstanceManager().stop();
            } catch (InvocationTargetException e) {
                m_handler.error("The onArrival method " + m_onArrival.getMethod() + " has thrown an exception", e.getTargetException());
                m_handler.getInstanceManager().stop();
            }
        }
    }

    public void stop() {
        m_context.removeBundleListener(this);
        m_bundles.clear();
    }

    public void bundleChanged(BundleEvent event) {
        switch (event.getType()) {
            case BundleEvent.STARTED:
                onArrival(event.getBundle());
                break;
            case BundleEvent.STOPPING:
                onDeparture(event.getBundle());
                break;
            default: 
                break;
        }
        
    }

    private void onDeparture(Bundle bundle) {
        if (m_bundles.contains(bundle)) {
            try {
                m_onDeparture.call(new Object[] {bundle});
            } catch (NoSuchMethodException e) {
                m_handler.error("The onDeparture method " + m_onDeparture.getMethod() + " does not exist in the class", e);
                m_handler.getInstanceManager().stop();
            } catch (IllegalAccessException e) {
                m_handler.error("The onDeparture method " + m_onDeparture.getMethod() + " cannot be called", e);
                m_handler.getInstanceManager().stop();
            } catch (InvocationTargetException e) {
                m_handler.error("The onDeparture method " + m_onDeparture.getMethod() + " has thrown an exception", e.getTargetException());
                m_handler.getInstanceManager().stop();
            }
        }
    }

    

}
