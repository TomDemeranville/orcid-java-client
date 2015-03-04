package uk.bl.odin.orcid.client;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Cachable search key
 *
 * @author tom
 */
public class SearchKey {

	// TODO Make fields final and remove setter in future versions
	private String query;
	private int page = -1;
	private int pagesize = -1;

	public SearchKey(String query, int page, int pageSize) {
		this.query = query;
		this.page = page;
		this.pagesize = pageSize;
	}

	public String getQuery() {
		return query;
	}

	/**
	 * @deprecated Use {@link #SearchKey(String, int, int)}
	 */
	@Deprecated
	public void setQuery(String query) {
		this.query = query;
	}

	public int getPage() {
		return page;
	}

	/**
	 * @deprecated Use {@link #SearchKey(String, int, int)}
	 */
	@Deprecated
	public void setPage(int page) {
		this.page = page;
	}

	public int getPagesize() {
		return pagesize;
	}

	/**
	 * @deprecated Use {@link #SearchKey(String, int, int)}
	 */
	@Deprecated
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	@Override
	public String toString() {
		return "SearchKey [query=" + query + ", page=" + page + ", pagesize=" + pagesize + "]";
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(1, 31).
				append(page).
				append(pagesize).
				append(query).
				toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		SearchKey rhs = (SearchKey) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(page, rhs.page)
				.append(pagesize, rhs.pagesize)
				.append(query, rhs.query)
				.isEquals();
	}

}