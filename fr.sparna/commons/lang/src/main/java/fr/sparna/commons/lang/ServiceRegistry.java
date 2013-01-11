package fr.sparna.commons.lang;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class ServiceRegistry<K,S> {

	protected Map<K,S> services;

	protected ServiceRegistry(Class serviceClass) {
		this.services = new HashMap();
		Iterator services = javax.imageio.spi.ServiceRegistry.lookupProviders(serviceClass, serviceClass.getClassLoader());
		try {
			while(services.hasNext()) {
				S service = (S)services.next();
				Object oldService = add(service);
			}
		} catch(Error e) {
			System.err.println("Failed to instantiate service");
			e.printStackTrace();
		}
	}

	protected abstract K getKey(S obj);

	public Object add(S service) {
		return services.put(getKey(service), service);
	}

	public void remove(S service) {
		services.remove(getKey(service));
	}

	public S get(K key) {
		return (S)services.get(key);
	}

	public boolean has(K key) {
		return services.containsKey(key);
	}

	public Collection<S> getAll() {
		return Collections.unmodifiableCollection(services.values());
	}

	public Set<K> getKeys() {
		return Collections.unmodifiableSet(services.keySet());
	}

}
