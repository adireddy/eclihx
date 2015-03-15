package eclihx.core.haxe.internal.configuration;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * A list of Haxe configuration for execution.
 */
public class HaxeConfigurationList implements Iterable<HaxeConfiguration>{
	
	private final LinkedList<HaxeConfiguration> configs = 
		new LinkedList<HaxeConfiguration>();

	/**
	 * Gets main configuration. By default it's a first configuration
	 * of the list
	 * @return Main Haxe configuration
	 * @throws InvalidConfigurationException If there's no configuration in the list. 
	 */
	public HaxeConfiguration getMainConfiguration() throws InvalidConfigurationException {
		if (configs.isEmpty()) {
			throw new InvalidConfigurationException("Should be at least one configuration");
		}
		
		return configs.getFirst();
	}
	
	/**
	 * Add one more configuration into Haxe configuration list.
	 * @param config New configuration. Null is NOT allowed.
	 * @return true (as specified by Collection.add(E))
	 */
	public boolean add(HaxeConfiguration config) {
		if (config == null)
		{
			throw new NullPointerException("config parameter can't be null");
		}
		
		return configs.add(config);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<HaxeConfiguration> iterator() {
		return configs.iterator();
	}	
}
