package gov.nasa.pds.web.ui.containers;

public class GoogleVisColumn {

	private String id;

	private String label;

	private String pattern;

	private String type;

	@SuppressWarnings("nls")
	protected GoogleVisColumn(final String id, final String label,
			final String pattern, final String type) {
		this.id = id == null ? "" : id;
		this.label = label == null ? "" : label;
		this.pattern = pattern == null ? "" : pattern;
		this.type = type == null ? "" : type;
	}

	public String getId() {
		return this.id;
	}

	public String getLabel() {
		return this.label;
	}

	public String getPattern() {
		return this.pattern;
	}

	public String getType() {
		return this.type;
	}

}
