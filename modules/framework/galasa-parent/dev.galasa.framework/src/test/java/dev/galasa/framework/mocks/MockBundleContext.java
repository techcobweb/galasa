/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.mocks;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.osgi.framework.*;

public class MockBundleContext implements BundleContext{

    private Map<String,MockServiceReference<?>> services ;

    private List<Bundle> loadedBundles;

    /**
     * @param services A map. The key is the interface/class name. 
     */
    public MockBundleContext(Map<String,MockServiceReference<?>> services) {
        this(services,new ArrayList<>());
    }

    public MockBundleContext(
        Map<String,MockServiceReference<?>> services,
        List<Bundle> loadedBundles
    ) {
        this.services = services;
        this.loadedBundles = loadedBundles;
    }

    @Override
    public Bundle[] getBundles() {
        Bundle[] result = new Bundle[loadedBundles.size()];
        loadedBundles.toArray(result);
        return result;
    }
    
    @Override
    public ServiceReference<?>[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        int count = 0;
        for( Entry<String,MockServiceReference<?>> entry : this.services.entrySet() ) {
            if(entry.getKey().equals(clazz)) {
                count++;
            }
        }

        ServiceReference<?>[] allServiceReferences = new ServiceReference<?>[count];

        int i=0;
        for( Entry<String,MockServiceReference<?>> entry : this.services.entrySet() ) {
            if(entry.getKey().equals(clazz)) {
                allServiceReferences[i] = entry.getValue();
                i++;
            }
        }

        return allServiceReferences;
    }

    @Override
    public ServiceReference<?> getServiceReference(String clazz) {
        // logger.info("getServiceReference(String clazz="+clazz+")");
        ServiceReference<?> ref = this.services.get(clazz);
        return ref;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> ServiceReference<S> getServiceReference(Class<S> clazz) {
        // logger.info("getServiceReference(Class<S> clazz="+clazz.getName()+")");
        String className = clazz.getName();
        ServiceReference<S> result ;
        
        ServiceReference<?> rawRef = getServiceReference(className);

        result = (ServiceReference<S>)rawRef;
        return result;
    }

    @Override
    public <S> Collection<ServiceReference<S>> getServiceReferences(Class<S> clazz, String filter)
            throws InvalidSyntaxException {
        throw new UnsupportedOperationException("Unimplemented method 'getServiceReferences'");
    }

    @Override
    public <S> S getService(ServiceReference<S> reference) {
        // logger.info("getService(ServiceReference<S> reference="+reference.getClass().getName()+")");

        S result = ((MockServiceReference<S>)reference).getService();
        return result;
    }

    // --------------- un-implemented methods follow --------------------

    @Override
    public String getProperty(String key) {
        throw new UnsupportedOperationException("Unimplemented method 'getProperty'");
    }

    @Override
    public Bundle getBundle() {
        throw new UnsupportedOperationException("Unimplemented method 'getBundle'");
    }

    @Override
    public Bundle installBundle(String location, InputStream input) throws BundleException {
        throw new UnsupportedOperationException("Unimplemented method 'installBundle'");
    }

    @Override
    public Bundle installBundle(String location) throws BundleException {
        throw new UnsupportedOperationException("Unimplemented method 'installBundle'");
    }

    @Override
    public Bundle getBundle(long id) {
        throw new UnsupportedOperationException("Unimplemented method 'getBundle'");
    }
    
    @Override
    public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
        throw new UnsupportedOperationException("Unimplemented method 'addServiceListener'");
    }

    @Override
    public void addServiceListener(ServiceListener listener) {
        throw new UnsupportedOperationException("Unimplemented method 'addServiceListener'");
    }

    @Override
    public void removeServiceListener(ServiceListener listener) {
        throw new UnsupportedOperationException("Unimplemented method 'removeServiceListener'");
    }

    @Override
    public void addBundleListener(BundleListener listener) {
        throw new UnsupportedOperationException("Unimplemented method 'addBundleListener'");
    }

    @Override
    public void removeBundleListener(BundleListener listener) {
        throw new UnsupportedOperationException("Unimplemented method 'removeBundleListener'");
    }
    
    @Override
    public void addFrameworkListener(FrameworkListener listener) {
        throw new UnsupportedOperationException("Unimplemented method 'addFrameworkListener'");
    }

    @Override
    public void removeFrameworkListener(FrameworkListener listener) {
        throw new UnsupportedOperationException("Unimplemented method 'removeFrameworkListener'");
    }

    @Override
    public ServiceRegistration<?> registerService(String[] clazzes, Object service, Dictionary<String, ?> properties) {
        throw new UnsupportedOperationException("Unimplemented method 'registerService'");
    }

    @Override
    public ServiceRegistration<?> registerService(String clazz, Object service, Dictionary<String, ?> properties) {
        throw new UnsupportedOperationException("Unimplemented method 'registerService'");
    }

    @Override
    public <S> ServiceRegistration<S> registerService(Class<S> clazz, S service, Dictionary<String, ?> properties) {
        throw new UnsupportedOperationException("Unimplemented method 'registerService'");
    }

    @Override
    public <S> ServiceRegistration<S> registerService(Class<S> clazz, ServiceFactory<S> factory,
            Dictionary<String, ?> properties) {
        throw new UnsupportedOperationException("Unimplemented method 'registerService'");
    }

    @Override
    public ServiceReference<?>[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        throw new UnsupportedOperationException("Unimplemented method 'getServiceReferences'");
    }

    @Override
    public boolean ungetService(ServiceReference<?> reference) {
        throw new UnsupportedOperationException("Unimplemented method 'ungetService'");
    }

    @Override
    public <S> ServiceObjects<S> getServiceObjects(ServiceReference<S> reference) {
        throw new UnsupportedOperationException("Unimplemented method 'getServiceObjects'");
    }

    @Override
    public File getDataFile(String filename) {
        throw new UnsupportedOperationException("Unimplemented method 'getDataFile'");
    }

    @Override
    public Filter createFilter(String filter) throws InvalidSyntaxException {
        throw new UnsupportedOperationException("Unimplemented method 'createFilter'");
    }

    @Override
    public Bundle getBundle(String location) {
        throw new UnsupportedOperationException("Unimplemented method 'getBundle'");
    }
    
}