package eclihx.core.util.console.parser.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

// TODO 5 Implement default implementation for file parameter

/**
 * My (Nikolay Krasko) own attempt to implement console parameter parser. It's a little bit naive but gives 
 * an ability to parse in strong-typed manner. Probably it should be removed and replaced with the one of the 
 * well-known and well-supported third-party parser of input parameters.  
 */
public class Parser {
	
	private Hashtable<String, Parameter> parametersStore = null;
	
	/**
	 * Initialize parser with parameters.
     *
	 * @param parameters List of allowed parameters
	 * @throws InitializeParseError Error of parser initialization.
	 */
	public void initialize(Parameter[] parameters) throws InitializeParseError {

		parametersStore = new Hashtable<String, Parameter>(parameters.length);
		
		for (Parameter parameter : parameters) {
			if (parametersStore.containsKey(parameter.getPrefix())) {
				
				parametersStore.clear();
				throw new InitializeParseError(
					String.format(
						"Invalid parameters set: '%1$s' prefix appears more than once.",
						parameter.getPrefix()
					)
				);
				
			} else {
				parametersStore.put(parameter.getPrefix(), parameter);
			}
		}
	}
	
	/**
	 * Get list of parser parameters key.
	 * 
	 * @return An arbitrary sorted list of non-empty parameters keys. 
	 */
	public Iterable<String> getParametersKeys()
	{
		if (parametersStore == null)
		{
			return new ArrayList<String>();
		}
		
		Set<String> keys = parametersStore.keySet();
		keys.remove("");
		
		return keys;
	}
	
	/**
	 * Parse an array of input arguments.
	 * 
	 * @param args Console parameters arguments.
	 * @throws ParseError Error in parsing.
	 */
	public void parse(String[] args) throws ParseError {
		
		HashSet<String> notActivatedParams = new HashSet<String>(parametersStore.keySet());
		
		int i = 0; // current argument index
		
		while (i < args.length) {
			
			String currentPrefix = args[i];
			Parameter param = parametersStore.get(currentPrefix);
			
			if (param != null) {
				// We found parameter. Let's index pass prefix
				++i;
			} else {
				// We didn't find the parameter. Try an empty prefix
				currentPrefix = "";
				param = parametersStore.get(currentPrefix);
			}
			
			if (param != null) {
				
				// Process parameter and move index
				int newIndex = Math.min(i + param.numberOfParameters(), args.length);
				param.parse(Arrays.copyOfRange(args, i, newIndex));
				
				notActivatedParams.remove(currentPrefix);
				
				i = newIndex; 				
				
			} else {
				// Bad thing here...We don't know how to proceed
				// TODO 5: Add recovery ability
				throw new ParseError(String.format("Uknown prefix '%1$s'", args[i]));
			}
		}	
		
		for (String prefix : notActivatedParams) {

			// Because we have built notActivatedParams from prefixes 
			assert(parametersStore.get(prefix) != null);			
			parametersStore.get(prefix).absence();
		}
	}
}
