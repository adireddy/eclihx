package eclihx.ui.internal.ui.editors.hx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import eclihx.core.haxe.internal.ContentInfo;

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
		
		List<ContentInfo> filteredInfos = new ArrayList<ContentInfo>();
		for (ContentInfo contentInfo : cachedContentInfos) {
			if (contentInfo.getName().startsWith(identPart)) {
				filteredInfos.add(contentInfo);
			}
		}
		
		return filteredInfos;
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
}
