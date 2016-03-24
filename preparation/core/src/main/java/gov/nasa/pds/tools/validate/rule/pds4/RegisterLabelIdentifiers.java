package gov.nasa.pds.tools.validate.rule.pds4;

import gov.nasa.pds.tools.validate.TargetRegistrar;
import gov.nasa.pds.tools.validate.rule.AbstractValidationRule;
import gov.nasa.pds.tools.validate.rule.ValidationTest;

import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Registers identifiers defined within a label, and verifies that
 * the same identifier is not registered twice.
 */
public class RegisterLabelIdentifiers extends AbstractValidationRule {

    private static final String PDS4_NS = "http://pds.nasa.gov/pds4/pds/v1";

    private static final String IDENTIFIERS_PATH
        = "//*:Identification_Area[namespace-uri()='" + PDS4_NS + "']"
        + "/*:logical_identifier[namespace-uri()='" + PDS4_NS + "']";

    private XPathFactory xPathFactory;

    /**
     * Creates a new instance.
     */
    public RegisterLabelIdentifiers() {
        xPathFactory = new net.sf.saxon.xpath.XPathFactoryImpl();
    }

    @Override
    public boolean isApplicable(String location) {
        // The rule is applicable if a label has been parsed.
        return getContext().containsKey(PDS4Context.LABEL_DOCUMENT);
    }

    /**
     * Tests that label identifiers are uniquely defined.
     *
     * @throws XPathExpressionException if there is an error processing the XPath to
     *   the label logical identifier
     */
    @ValidationTest
    public void registerIdentifiers() throws XPathExpressionException {
        // We have a reference to the current target, since it is a label.
        getRegistrar().setTargetIsLabel(getTarget().getAbsolutePath(), true);

        Document label = getContext().getContextValue(PDS4Context.LABEL_DOCUMENT, Document.class);
        DOMSource source = new DOMSource(label);

        NodeList identifiers = (NodeList) xPathFactory.newXPath().evaluate(IDENTIFIERS_PATH, source, XPathConstants.NODESET);
        for (int i=0; i < identifiers.getLength(); ++i) {
            Node name = identifiers.item(i);
            registerIdentifier(name.getTextContent());
        }
    }

    private void registerIdentifier(String id) {
        TargetRegistrar registrar = getRegistrar();
        if (registrar.getTargetForIdentifier(id) == null) {
            registrar.setTargetIdentifier(getTarget().getAbsolutePath(), id);
        } else {
            String message = String.format("Identifier %s already defined (old location: %s)",
                    id, registrar.getTargetForIdentifier(id));
            reportError(PDS4Problems.DUPLICATE_LOGICAL_IDENTIFIER, getTarget(), -1, -1, message);
        }
    }

}
