package dev.voras.common.openstack.manager.internal.properties;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.logging.LogFactory;

import dev.voras.common.linux.OperatingSystem;
import dev.voras.framework.spi.ConfigurationPropertyStoreException;
import dev.voras.framework.spi.IConfigurationPropertyStoreService;
import dev.voras.framework.spi.cps.CpsProperties;

/**
 * OpenStack Linux images
 * <p>
 * A comma separated list of what images are available to build servers from. 
 * </p><p>
 * The cascading properties can be:-<br><br>
 * openstack.linux.[os].[version].images=ubuntu-withjava,ubuntu-k8s<br>
 * openstack.linux.[os].images=ubuntu-withjava,ubuntu-k8s<br>
 * openstack.linux.images=ubuntu-withjava,ubuntu-k8s<br>
 * Where os = the operating system {@link OperatingSystem} and version is version string<br>
 * Example openstack.linux.ubuntu.16-04.images=ubuntu-1604
 * </p>
 * <p>
 * There are no defaults
 * </p>
 * 
 * @author Michael Baylis
 *
 */
public class LinuxImages extends CpsProperties {
	
	public static @NotNull List<String> get(
			@NotNull IConfigurationPropertyStoreService cps,
			@NotNull OperatingSystem operatingSystem, 
			String version) 
					throws ConfigurationPropertyStoreException {
		
		if (version != null) {
			return getStringList(cps, 
		               LogFactory.getLog(LinuxImages.class), 
		               "linux", 
		               "images",
		               operatingSystem.name(),
		               version);
		}
		
		return getStringList(cps, 
	               LogFactory.getLog(LinuxImages.class), 
	               "linux", 
	               "images",
	               operatingSystem.name());
		
		
		
	}

}
