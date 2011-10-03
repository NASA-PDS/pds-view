package gov.nasa.arc.pds.visualizer.client;

/**
 * Interface to represent the messages contained in resource bundle:
 * 	/Users/issmith1/gogo/visualizer/src/main/resources/gov/nasa/arc/pds/visualizer/client/Messages.properties'.
 */
public interface Messages extends com.google.gwt.i18n.client.Messages {
  
  /**
   * Translated "Enter your name".
   * 
   * @return translated "Enter your name"
   */
  @DefaultMessage("Enter your name")
  @Key("nameField")
  String nameField();

  /**
   * Translated "Send".
   * 
   * @return translated "Send"
   */
  @DefaultMessage("Send")
  @Key("sendButton")
  String sendButton();
}
