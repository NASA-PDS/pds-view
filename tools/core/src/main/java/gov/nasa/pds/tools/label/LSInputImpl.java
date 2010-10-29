//	Copyright 2009-2010, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//

package gov.nasa.pds.tools.label;

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.ls.LSInput;

/**
 * @author pramirez
 *
 */
public class LSInputImpl implements LSInput {
  protected String publicId;
  protected String systemId;
  protected String baseURI;
  protected InputStream byteStream;
  protected Reader characterStream;
  protected String stringData;
  protected String encoding;
  protected boolean certifiedText;
  
  public LSInputImpl() {
  }
  
  public LSInputImpl(String publicId, String systemId, InputStream byteStream) {
    this.publicId = publicId;
    this.systemId = systemId;
    this.byteStream = byteStream;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.ls.LSInput#getBaseURI()
   */
  @Override
  public String getBaseURI() {
    return this.baseURI;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.ls.LSInput#getByteStream()
   */
  @Override
  public InputStream getByteStream() {
    return this.byteStream;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.ls.LSInput#getCertifiedText()
   */
  @Override
  public boolean getCertifiedText() {
    return this.certifiedText;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.ls.LSInput#getCharacterStream()
   */
  @Override
  public Reader getCharacterStream() {
    return this.characterStream;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.ls.LSInput#getEncoding()
   */
  @Override
  public String getEncoding() {
    return this.encoding;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.ls.LSInput#getPublicId()
   */
  @Override
  public String getPublicId() {
    return this.publicId;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.ls.LSInput#getStringData()
   */
  @Override
  public String getStringData() {
    return this.stringData;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.ls.LSInput#getSystemId()
   */
  @Override
  public String getSystemId() {
    return this.systemId;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.ls.LSInput#setBaseURI(java.lang.String)
   */
  @Override
  public void setBaseURI(String baseURI) {
    this.baseURI = baseURI;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.ls.LSInput#setByteStream(java.io.InputStream)
   */
  @Override
  public void setByteStream(InputStream byteStream) {
    this.byteStream = byteStream;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.ls.LSInput#setCertifiedText(boolean)
   */
  @Override
  public void setCertifiedText(boolean certifiedText) {
    this.certifiedText = certifiedText;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.ls.LSInput#setCharacterStream(java.io.Reader)
   */
  @Override
  public void setCharacterStream(Reader characterStream) {
    this.characterStream = characterStream;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.ls.LSInput#setEncoding(java.lang.String)
   */
  @Override
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.ls.LSInput#setPublicId(java.lang.String)
   */
  @Override
  public void setPublicId(String publicId) {
    this.publicId = publicId;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.ls.LSInput#setStringData(java.lang.String)
   */
  @Override
  public void setStringData(String stringData) {
    this.stringData = stringData;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.ls.LSInput#setSystemId(java.lang.String)
   */
  @Override
  public void setSystemId(String systemId) {
    this.systemId = systemId;
  }

}
