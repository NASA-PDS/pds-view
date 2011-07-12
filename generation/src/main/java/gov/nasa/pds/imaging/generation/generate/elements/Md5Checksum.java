package gov.nasa.pds.imaging.generation.generate.elements;

public class Md5Checksum implements Element {
	
	private String md5Checksum;
	
	public Md5Checksum() {
		this.md5Checksum = "My Checksum";
	}
	
	@Override
	public String getValue() {
		return this.md5Checksum;
	}
	
	@Override
	public String getUnits() {
		return this.md5Checksum;
	}
	
	@Override
	public void setParameters() {
		
	}
}
