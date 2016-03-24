package gov.nasa.pds.tools.validate.rule.pds4;

import gov.nasa.pds.tools.validate.rule.AbstractValidationRule;
import gov.nasa.pds.tools.validate.rule.ValidationTest;

import java.io.File;

import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Registers file references from the label, as well as an implied
 * reference to the label itself.
 */
public class RegisterTargetReferences extends AbstractValidationRule {

    private static final String PDS4_NS = "http://pds.nasa.gov/pds4/pds/v1";

    private static final String FILE_NAMES_PATH
        = "//*:File[namespace-uri()='" + PDS4_NS + "']"
        + "/*:file_name[namespace-uri()='" + PDS4_NS + "']";

    private static final String DOCUMENT_FILE_NAMES_PATH
        = "//*:Document_File[namespace-uri()='" + PDS4_NS + "']"
        + "/*:file_name[namespace-uri()='" + PDS4_NS + "']";

    private XPathFactory xPathFactory;

    /**
     * Creates a new instance.
     */
    public RegisterTargetReferences() {
        xPathFactory = new net.sf.saxon.xpath.XPathFactoryImpl();
    }

    @Override
    public boolean isApplicable(String location) {
        // The rule is applicable if a label has been parsed.
        return getContext().containsKey(PDS4Context.LABEL_DOCUMENT);
    }

    @ValidationTest
    public void registerFileReferences() throws XPathExpressionException {
        // We have a reference to the current target, since it is a label.
        getRegistrar().setTargetIsLabel(getTarget().getAbsolutePath(), true);

        Document label = getContext().getContextValue(PDS4Context.LABEL_DOCUMENT, Document.class);
        DOMSource source = new DOMSource(label);

        NodeList fileNames = (NodeList) xPathFactory.newXPath().evaluate(FILE_NAMES_PATH, source, XPathConstants.NODESET);
        for (int i=0; i < fileNames.getLength(); ++i) {
            Node name = fileNames.item(i);
            registerReference(new File(getTarget().getParentFile(), name.getTextContent()));
        }
    }

    @ValidationTest
    public void registerDocumentFileReferences() throws XPathExpressionException {
        // We have a reference to the current target, since it is a label.
        getRegistrar().setTargetIsLabel(getTarget().getAbsolutePath(), true);

        Document label = getContext().getContextValue(PDS4Context.LABEL_DOCUMENT, Document.class);
        DOMSource source = new DOMSource(label);

        NodeList fileNames = (NodeList) xPathFactory.newXPath().evaluate(DOCUMENT_FILE_NAMES_PATH, source, XPathConstants.NODESET);
        for (int i=0; i < fileNames.getLength(); ++i) {
            Node name = fileNames.item(i);
            Node directory = getSiblingNode(name, "directory_path_name");
            if (directory == null) {
                File referencedFile = new File(getTarget().getParentFile(), name.getTextContent());
                registerReference(referencedFile);
            } else {
                File documentDir = new File(getTarget().getParentFile(), directory.getTextContent());
                File referencedFile = new File(documentDir, name.getTextContent());
                registerReference(referencedFile);
            }
        }
    }

    private Node getSiblingNode(Node child, String nodeName) {
        NodeList siblings = child.getParentNode().getChildNodes();
        for (int i=0; i < siblings.getLength(); ++i) {
            Node sibling = siblings.item(i);
            if (sibling!=child && sibling.getNodeName().equals(nodeName)) {
                return sibling;
            }
        }

        return null;
    }

    private void registerReference(File referencedFile) {
        getRegistrar().addTargetReference(getTarget().getAbsolutePath(), referencedFile.getAbsolutePath());
    }

}
