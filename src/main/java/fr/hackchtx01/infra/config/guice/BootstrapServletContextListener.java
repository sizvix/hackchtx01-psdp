/**
 * 
 */
package fr.hackchtx01.infra.config.guice;

import java.util.List;

import javax.servlet.ServletContext;

import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;

import com.google.common.collect.Lists;
import com.google.inject.Module;

/**
 * Override of Resteasy + Guice config to be able to define modules manually instead of in web.xml custom resteasy param
 * @author yoan
 */
public class BootstrapServletContextListener extends GuiceResteasyBootstrapServletContextListener {
	@Override
	protected List<? extends Module> getModules(final ServletContext context) {
		return Lists.newArrayList(new PsdpWebModule(), new PsdpModule(context), new ShiroSecurityModule(context));
	}
}
