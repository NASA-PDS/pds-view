package gov.nasa.pds.web.ui.containers;

public class GoogleVisRow {

	private final String XVal;

	private final int YVal;

	private String event;

	public GoogleVisRow(final String XVal, final int YVal) {
		this.XVal = XVal;
		this.YVal = YVal;
	}

	public String getXVal() {
		return this.XVal;
	}

	public int getYVal() {
		return this.YVal;
	}

	public String getEvent() {
		return this.event;
	}

	public void setEvent(final String event) {
		this.event = event;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GoogleVisRow)) {
			return false;
		}
		GoogleVisRow other = (GoogleVisRow) o;
		if (!this.XVal.equals(other.getXVal())) {
			return false;
		}
		if (!(this.YVal == other.getYVal())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		String hashString = this.XVal + this.YVal;
		return hashString.hashCode();
	}

}
