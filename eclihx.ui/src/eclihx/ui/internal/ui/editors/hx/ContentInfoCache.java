package eclihx.ui.internal.ui.editors.hx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.runtime.Assert;

import eclihx.core.haxe.contentassist.ContentInfo;

/**
 * Stores a list of Haxe compiler content informations for the given file offset.
 */
public class ContentInfoCache {
	
	/**
	 * Cached proposals which will be used in one session.
	 */
	final private List<ContentInfo> cachedContentInfos = new ArrayList<ContentInfo>();
	
	/**
	 * File offset this cache was constructed. 
	 */
	private int cachedOffset = -1; 
	
	/**
	 * Retrieves a list of content informations starting with the given identifier part. 
	 * 
	 * @param identPart the part each information should start with.
	 * @return a list with filtered informations.
	 */
	public List<ContentInfo> getFilteredInfos(String identPart) {
		
		Assert.isTrue(isValid());
		
		if (identPart.isEmpty()) {
			return cachedContentInfos;
		}
		
		// Filtered collection with priorities - the infos with greater priority will be at the beginning
		TreeMap<Integer, TreeSet<ContentInfo>> filteredInfos = 
				new TreeMap<Integer, TreeSet<ContentInfo>>(Collections.reverseOrder());
		
		for (ContentInfo contentInfo : cachedContentInfos) {
			int priority = getMatchPriority(identPart, contentInfo);
			if (priority > 0) {
				if (!filteredInfos.containsKey(priority)) {
					filteredInfos.put(priority, new TreeSet<ContentInfo>(ContentInfo.getNameComparator()));
				}
				
				filteredInfos.get(priority).add(contentInfo);
			}
		}
		
		return collect(filteredInfos.values());
	}
	
	private int getMatchPriority(String identPart, ContentInfo contentInfo) {
		
		final String contentInfoName = contentInfo.getName();
		final String identLowCase = identPart.toLowerCase();
		
		if (contentInfoName.toLowerCase().startsWith(identLowCase)) {
			return 100;
		}
		
		if (contentInfoName.toLowerCase().contains(identLowCase)) {
			return 50;
		}
		
		// Looking for abbreviation
		if (getAbbreviation(contentInfoName).toLowerCase().contains(identLowCase)) { 
			return 10;
		}
		
		return -1;
	}
	
	private String getAbbreviation(String name) {
		if (name.isEmpty() || !Character.isUpperCase(name.charAt(0))) {
			return "";
		}
		
		StringBuilder abbreviation = new StringBuilder();
		for (char ch : name.toCharArray()) {
			if (Character.isUpperCase(ch)) {
				abbreviation.append(ch);
			}			
		}
		
		return abbreviation.toString();
	}
	
	/**
	 * Caches all given objects for the further usage in the same session.
	 * 
	 * @param offset the file offset this cache is going to be built.
	 * @param contentInfos proposals
	 */
	public void build(int offset, final List<ContentInfo> contentInfos) {
		Assert.isTrue(offset >= 0);
		Assert.isTrue(cachedContentInfos.isEmpty());
		
		cachedContentInfos.clear();
		
		for (ContentInfo contentInfo : contentInfos) {
			cachedContentInfos.add(contentInfo);
		}
		
		Collections.sort(contentInfos, ContentInfo.getNameComparator());
		
		cachedOffset = offset;
	}
	
	/**
	 * Destroy the cache information. 
	 */
	public void invalidate() {
		cachedContentInfos.clear();
		cachedOffset = -1;
	}
	
	/**
	 * Update state of the cache according to given offset. This function can invalidate the
	 * the cache.
	 * 
	 * @param newOffset current offset for checking the state.
	 */
	void updateState(final int newOffset) {
		if (newOffset != cachedOffset) {
			invalidate();
		}
	}
	
	/**
	 * Is cache has information and ready.
	 * 
	 * @return true if the cache is still valid.
	 */
	public boolean isValid() {
		return cachedOffset != -1;
	}
	
	private static <E, T extends Collection<E>> List<E> collect(Collection<T> collections) {
		ArrayList<E> result = new ArrayList<E>();
		for (Collection<E> collection : collections) {
			result.addAll(collection);
		}
		
		return result;
	}
}
