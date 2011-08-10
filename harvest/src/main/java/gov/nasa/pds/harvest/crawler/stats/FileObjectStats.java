package gov.nasa.pds.harvest.crawler.stats;

public class FileObjectStats {
  private int numRegistered;

  private int numNotRegistered;

  private int numSkipped;

  public FileObjectStats() {
    numRegistered = 0;
    numNotRegistered = 0;
    numSkipped = 0;
  }

  public void clear() {
    numRegistered = 0;
    numNotRegistered = 0;
    numSkipped = 0;
  }

  public int getNumRegistered() {
    return numRegistered;
  }

  public void addNumRegistered(int numRegistered) {
    this.numRegistered += numRegistered;
  }

  public int getNumNotRegistered() {
    return this.numNotRegistered;
  }

  public void addNumNotRegistered(int numNotRegistered) {
    this.numNotRegistered += numNotRegistered;
  }

  public int numSkipped() {
    return numSkipped;
  }

  public void addNumSkipped(int numSkipped) {
    this.numSkipped += numSkipped;
  }
}
