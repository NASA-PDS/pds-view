// Copyright 1999-2005 California Institute of Technology. ALL RIGHTS
// RESERVED. U.S. Government Sponsorship acknowledged.
//

package jpl.pds.parser;

import java.io.*;

import antlr.CommonAST;
import antlr.collections.AST;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/**
 * Creates a DOM document from an Antlr AST tree which was
 * created during the parsing of an ODL file.
 *
 * @author J. Crichton
*/

public class XMLDom {
    /**
     * Convert Antlr AST tree to XML DOM document.
     *
     * @param ast Antlr ast tree to convert.
     * @param names Token types.
     * @param xsdRootName Root name from Data Set xsd file.
     * @param runLog RunLog object used for writing messages to cmd window and output log.
     * @return XML DOM document.
     */
    public final Document convert(final AST ast, final String[] names, final String xsdRootName, final RunLog runLog) {
        this.names = names;
        this.runLog = runLog;
        if (ast == null) {
            runLog.errorMessage("Antlr Tree for label file is NULL", 4);
            return null;
        }
        if (names == null) {
            runLog.errorMessage("Antlr Tree Token Names is NULL", 4);
            return null;
        }

        try {
            initialize(ast, xsdRootName);
            ((CommonAST)ast).setVerboseStringConversion(true, names);
            convertRecurse(ast);
        } catch (Exception e) {
            runLog.errorMessage(e.getMessage(), 4);
            return null;
        }

        return document;
    }

    /**
     * Recursive routine to convert a XML ODL tree out of an Antlr AST tree.
     *
     * @param ast Antlr ast tree to convert.
     * @return Pointer to the OBJDESC XML ODL tree created.
     */
    private Node convertRecurse(final AST ast) {
        if (ast == null) return null;

        // Print out this node!
        String text = ast.getText();
        String typename = names[ast.getType()];

        if (MSGS) System.out.println("\n" + "Text is : " + text + "\n" + "Type is : " + typename);

        // CASE 1: This node is an assignment statement with
        // identifiers on left and right of assignment
        if (typename.equals("ASSIGNMENT_OPERATOR")) {
            // get first and second child information
            AST left = ast.getFirstChild();
            String lefttypename = names[left.getType()];
            String lefttext = left.getText();
            AST right = left.getNextSibling();
            String righttypename = names[right.getType()];
            String righttext = right.getText();

            // if there is a third child, its a units value
            String unitstext = null;
            AST units = right.getNextSibling();
            if (units != null) {
                unitstext = units.getText();
            }

            // identifier = value
            if (lefttypename.equals("IDENT") && righttypename.equals("IDENT")) {
                if (MSGS) System.out.println("FOUND " + lefttext + " = " + righttext);
                // add version or ident node
                if (lefttext.equalsIgnoreCase("PDS_VERSION_ID")) {
                    versionNode(lefttext, righttext);
                } else {
                    simplenode(lefttext, righttext, unitstext);
                }

            // identifier = quoted value
            } else if (lefttypename.equals("IDENT") && righttypename.equals("QUOTED")) {
                if (MSGS) System.out.println("FOUND " + lefttext + " = " + righttext);

                // add quoted node
                simplenode(lefttext, righttext, unitstext);

            // identifier = sequenced list of values
            } else if (lefttypename.equals("IDENT") && righttypename.equals("SEQUENCE_OPENING")) {
                if (MSGS) System.out.println("FOUND " + lefttext + " = " + righttext);

                // add item
                currentNode = sequenceNode(lefttext);

                // iterate through the children, adding them as subitems
                AST child = right.getFirstChild();
                int count = 0;
                while (child != null) {
                    AST nextChild = child.getNextSibling();
                    String childTypeName = names[child.getType()];
                    if (childTypeName.equals("IDENT") || childTypeName.equals("QUOTED")) {
                        count++;
                        subitemNode(lefttext+"-"+count, child, nextChild);
                    }
                    child = nextChild;
                }

                // move up and over to the next sibling
                currentNode = currentNode.getParentNode();      // up from *_VALUE_SET node
                AST nextchild = ast.getNextSibling();
                convertRecurse(nextchild);
                return null;

            // identifier = set of values (treat it the same as a list sequence)
            } else if (lefttypename.equals("IDENT") && righttypename.equals("SET_OPENING")) {
                if (MSGS) System.out.println("FOUND " + lefttext + " = " + righttext);

                // add item
                currentNode = setNode(lefttext);

                // iterate through the children, adding them as subitems
                AST child = right.getFirstChild();
                int count = 0;
                while (child != null) {
                    AST nextChild = child.getNextSibling();
                    String childTypeName = names[child.getType()];
                    if (childTypeName.equals("IDENT") || childTypeName.equals("QUOTED")) {
                        count++;
                        subitemNode(lefttext+"-"+count, child, nextChild);
                    }
                    child = nextChild;
                }

                // move up and over to the next sibling
                currentNode = currentNode.getParentNode();      // up from *_VALUE_SET node
                AST nextchild = ast.getNextSibling();
                convertRecurse(nextchild);
                return null;
            }

            // move to the next sibling
            AST child = ast.getNextSibling();
            convertRecurse(child);
            return null;

        } else if (typename.equals("COMMENT")) {
            commentNode(text);
            // move to the next sibling
            AST child = ast.getNextSibling();
            convertRecurse(child);
            return null;

        } else if (typename.equals("IDENT")) {
            // check to see if this is an object

            // get first child information
            String parent_nameGroupOrObject = nameGroupOrObject;
            AST child = ast.getFirstChild();
            if (child != null) {
                String childtext = child.getText();
                if (childtext.equals("OBJECT")) {
                    // add object node
                    String tag = createTag(text, ast);
                    currentNode = objectNode(tag);
                    nameGroupOrObject = tag + "_OBJECT";
                } else if (childtext.equals("GROUP")) {
                    // add group node
                    String tag = createTag(text, ast);
                    currentNode = groupNode(tag);
                    nameGroupOrObject = tag + "_GROUP";
                }
            }

            // recurse into the object
            child = child.getNextSibling();
            convertRecurse(child);

            // move up and over to the next sibling
            nameGroupOrObject = parent_nameGroupOrObject;
            currentNode = currentNode.getParentNode();
            child = ast.getNextSibling();
            convertRecurse(child);
            return null;

        } else if (typename.equals("POINT_OPERATOR")) {
            // get first pointchild information
            AST pointchild = ast.getFirstChild();
            if (pointchild != null) {
                String pointchildtypename = names[pointchild.getType()];
                if (pointchildtypename.equals("ASSIGNMENT_OPERATOR")) {
                    if (MSGS) System.out.println("     Getting assignment for point operator ");

                    // get first and second pointchild information
                    AST left = pointchild.getFirstChild();
                    String lefttypename = names[left.getType()];
                    String lefttext = left.getText();
                    AST right = left.getNextSibling();
                    String righttypename = names[right.getType()];
                    String righttext = right.getText();

                    if (righttypename.equals("IDENT") || righttypename.equals("QUOTED")) {
                        // Case PTR_NAME = "filename"
                        // Case PTR_NAME = number <units>
                        ptrNode("PTR_" + lefttext, right);
                    } else if (righttypename.equals("SEQUENCE_OPENING")) {
                        AST seqchild = right.getFirstChild();
                        while (seqchild != null) {
                            String tname = names[seqchild.getType()];
                            String txt = seqchild.getText();
                            if (tname.equals("IDENT") || tname.equals("QUOTED")) {
                                // Case PTR_NAME = (filename, number <units>)
                                // Case PTR_NAME = (..., filename, ...)
                                seqchild = ptrNode("PTR_" + lefttext, seqchild);
                            } else if (tname.equals("SEQUENCE_OPENING")) {
                                // Case PTR_NAME = (..., (filename, number <units>), ...)
                                ptrNode("PTR_" + lefttext, seqchild.getFirstChild());
                                seqchild = seqchild.getNextSibling();
                            }
                        }
                    }
                }
            }

            // next sibling
            AST nextchild = ast.getNextSibling();
            convertRecurse(nextchild);
            return null;

        } else {
            // Call this routine on the children
            AST child = ast.getFirstChild();
            while (child != null) {
                convertRecurse(child);
                child = ast.getNextSibling();
            }

            return null;
        }
    }

    /**
     * Create a  PTR XML node.
     * Sample XML created:
     *   &lt;PTR_tag type="DATA_ELEMENT"&gt;
     *       &lt;PTR_FILENAME type="DATA_ELEMENT"&gt;MER1_COMB.TAB&lt;/PTR_FILENAME&gt;
     *       &lt;PTR_COUNT type="DATA_ELEMENT"&gt;29&lt;/PTR_COUNT&gt;
     *   &lt;/PTR_tag&gt;
     *
     * @param tag Tag name for new node.
     * @param node Tree containing the ptr nodes.
     * @return Next AST sibling.
     */
    private AST ptrNode(String tag, AST node) {
        // Add <PTR_tag> node
        if (nameGroupOrObject.length() > 0) tag = nameGroupOrObject + "." + tag;
        Element ptr_genericElement = (Element) document.createElement(tag);
        ptr_genericElement.setAttribute("type", "DATA_ELEMENT");
        Node ptr_genericNode = currentNode.appendChild(ptr_genericElement);

        // Add <PTR_FILENAME> node
        String typename = names[node.getType()];
        if (typename.equals("QUOTED")) {
            Element filenameElement = (Element) document.createElement("PTR_FILENAME");
            filenameElement.setAttribute("type", "DATA_ELEMENT");
            Node filenameNode = ptr_genericNode.appendChild(filenameElement);
            Text text = (Text) document.createTextNode(node.getText());
            filenameElement.appendChild(text);
            node = node.getNextSibling();
            if (node != null) typename = names[node.getType()];
        }

        // Add <PTR_COUNT> node
        if (node != null && typename.equals("IDENT")) {
            Element countElement = (Element) document.createElement("PTR_COUNT");
            countElement.setAttribute("type", "DATA_ELEMENT");
            Node countNode = ptr_genericNode.appendChild(countElement);
            Text text = (Text) document.createTextNode(node.getText());
            countNode.appendChild(text);
            node = node.getNextSibling();
            if (node != null) {
                typename = names[node.getType()];
                if (typename.equals("UNITS")) {
                    countElement.setAttribute("units", node.getText());
                    node = node.getNextSibling();
                }
            }
        }
        return node;
    }

    /**
     * Create XML nodes that will contain a SEQUENCE of nodes for holding
     * an array of values. The SET allows the values to be in any order and the
     * SEQUNCE uses a fixed order. For SEQUENCE LIST ODL entry containing:
     *      SYSTEM_INDEX   = (3, 22, 0, 695, 63)
     * create:
     *      &lt;SYSTEM_INDEX type="DATA_ELEMENT"&gt;&lt;SYSTEM_INDEX_VALUE_SEQUENCE&gt;&lt;/SYSTEM_INDEX&gt;
     * The children will be added later to SYSTEM_INDEX_VALUE_SEQUENCE.
     *
     * @param tag XML tag.
     * @return Pointer to the XML node created.
     */
    private Node sequenceNode(String tag) {
        if (nameGroupOrObject.length() > 0) tag = nameGroupOrObject + "." + tag;
        Element element = (Element) document.createElement(tag + "_VALUE_SEQUENCE");
        element.setAttribute("type", "VALUE_SEQUENCE");
        currentNode.appendChild(element);
        return element;
    }

    /**
     * Create XML nodes that will contain a SET of nodes for holding
     * an array of values. The SET allows the values to be in any order and the
     * SEQUNCE uses a fixed order. For SET LIST ODL entry containing:
     *      SYSTEM_INDEX   = {3, 22, 0, 695, 63}
     * create:
     *      &lt;SYSTEM_INDEX type="DATA_ELEMENT"&gt;&lt;SYSTEM_INDEX_VALUE_SET&gt;&lt;/SYSTEM_INDEX&gt;
     * The children will be added later to SYSTEM_INDEX_VALUE_SET.
     *
     * @param tag XML tag used for new XML node.
     * @return Pointer to the XML node created.
     */
    private Node setNode(String tag) {
        if (nameGroupOrObject.length() > 0) tag = nameGroupOrObject + "." + tag;
        Element element = (Element) document.createElement(tag + "_VALUE_SET");
        element.setAttribute("type", "VALUE_SET");
        currentNode.appendChild(element);
        return element;
    }

    /**
     * Create a  child XML node for the nodes created in sequenceNode or setNode method.
     * SubitemNode method is called for each item in the ODL list.
     * Uses the parent element name with "_VALUE" appended.
     *
     * @param tag XML tag.
     * @param node SET or SEQUENCE AST node.
     * @param nextSibling Sibling of node.
     */
    private void subitemNode(final String tag, final AST node, final AST nextSibling) {
        String nodeTypeName = names[node.getType()];
        String nodeText = node.getText();
        nodeText = nodeText.replaceFirst("^\\s*","");

        String unitsText = null;
        if (nextSibling != null && names[nextSibling.getType()].equals("UNITS"))
            unitsText = nextSibling.getText();

        simplenode(tag, nodeText, unitsText);
    }

    /**
     * Create an XML node with attribute type="OBJECT", which is used
     * to describe an ODL object in XML.
     *
     * @param objectname Object name.
     * @return Pointer to the XML node created.
     */
    private Node objectNode(final String objectname) {
        Element element = (Element) document.createElement(objectname+"_OBJECT");
        element.setAttribute("type", "OBJECT");
        currentNode.appendChild(element);
        return element;
    }

    /**
     * Create an XML node with attribute type="GROUP", which is used
     * to describe an ODL group in XML.
     *
     * @param groupname Group name.
     * @return Pointer to the XML node created.
     */
    private Node groupNode(final String groupname) {
        Element element = (Element) document.createElement(groupname+"_GROUP");
        element.setAttribute("type", "GROUP");
        currentNode.appendChild(element);
        return element;
    }

    /**
     * Create tag for GROUP or OBJECT. If tag is a child of another GOURP or OBJECT
     * then append parant tag.  If duplicate tag then append 1 2 3 ... to make
     * the tag unique.
     *
     * @param tagName Tag name for the mew element.
     * @param child Child node from AST tree.
     * @return Tag name with Group or Object prefix and possible number postfix.
     */
   private String createTag(String tagName, final AST child) {
        // look ahead for siblings
        boolean hasSiblings = false;
        AST sibling = child.getNextSibling();
        if (sibling != null) hasSiblings = tagName.equals(sibling.getText());

        // tag is a child of another GROUP or OBJECT
        if (nameGroupOrObject.length() > 0) tagName = nameGroupOrObject + "." + tagName;

        // check for previous siblings in document and next sibling number
        int nextSibling = 1;
        NodeList nodeList = document.getElementsByTagName(tagName + nextSibling);
        while (nodeList.getLength() > 0) {
            hasSiblings = true;
            nextSibling++;
            nodeList = document.getElementsByTagName(tagName + nextSibling);
        }

        if (hasSiblings) tagName = tagName + nextSibling;
        return tagName;
    }

    /**
     *   Create an XML node called "COMMENT", which is used
     *   to describe an ODL comment in XML.
     *
     *   @param comment The comment string.
     */
    private void commentNode(final String comment) {
        Comment node = document.createComment(comment.trim());
        currentNode.appendChild(node);
    }

    /**
     * Create an XML node for the PDS version.
     *
     * @param tag String "PDS_VERSION_ID".
     * @param value Version id, such as "PDS3".
     */
    private void versionNode(final String tag, final String value) {
        Element element = (Element) document.createElement(tag);
        element.setAttribute("type", "DATA_ELEMENT");
        Text text = (Text) document.createTextNode(value);
        element.appendChild(text);
        currentNode.appendChild(element);
    }

    /**
     * Create an XML node which is used
     * to describe a basic ODL assignment statement.
     * Some examples of ODL assignment statements are:
     *    RECORD_TYPE = FIXED_LENGTH
     *    DATA_SET_ID = "GIO-C-HMC-3-RDR-HALLEY-V1.0"
     *    LINE_EXPOSURE_DURATION = 71.6800000 &lt;MILLISECONDS&gt;
     *
     * @param tag XML tag name for the node.
     * @param value Value, e.g. FIXED_LENGTH.
     * @param units Non-null string if units were specified, e.g. MILLISECONDS.
     * @return Pointer to the XML node created.
     */
    private Node simplenode(String tag, final String value, final String units) {
        if (nameGroupOrObject.length() > 0) tag = nameGroupOrObject + "." + tag;
        Element element = (Element) document.createElement(tag);
        element.setAttribute("type", "DATA_ELEMENT");
        if (units != null) element.setAttribute("units", units);

        // Replace white space [ \t\n\x0B\f\r] with single space 
        String normalizedValue = value.replaceAll("\\s+", " ").trim();

        Text text = (Text) document.createTextNode(normalizedValue);
        element.appendChild(text);
        currentNode.appendChild(element);
        return element;
    }

    /**
     * Initialized a new XML document.
     *
     * @param ast Source parsed into AST tree.
     * @param xsdRootName Root element name.
     * @throws Exception if error in newDocumentBuilder().
     */
    private void initialize(final AST ast, final String xsdRootName) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        document = db.newDocument();
        Comment commentNode = document.createComment("W3C Data Set product XML generated by PDS DSCreateSchema");
        document.appendChild(commentNode);

        // create the root tag using the DATA_SET_ID
        Element root = null;
        for (AST rootChild=ast; rootChild != null; rootChild=rootChild.getNextSibling()) {
            String text = rootChild.getText();
            String typename = names[rootChild.getType()];
            if (typename.equals("ASSIGNMENT_OPERATOR")) {
                AST left = rootChild.getFirstChild();
                String lefttext = left.getText();
                if (lefttext.equals("DATA_SET_ID")) {
                    AST right = left.getNextSibling();
                    String righttext = right.getText();
                    if (xsdRootName != null && !xsdRootName.equals(righttext)) {
                        throw new Exception("DATA_SET_ID does not match DS schema root name");
                    }
                    root = (Element) document.createElement(righttext);
                    break;
                }
            }
        }
        if (root == null) throw new Exception("DATA_SET_ID is missing from DS Product file");

        document.appendChild(root);
        currentNode = root;
    }

    /** Node in DOM document where to add children nodes. */
    private Node currentNode = null;

    /** DOM document. */
    private Document document = null;

    /** Token type names. */
    private String [] names;

    /** Prepend to new tag. */
    private String nameGroupOrObject = "";

    /** Debug display flag. */
    private static final boolean MSGS = false;

    /** Reference to RunLog for writing to the log buffer. */
    private RunLog runLog;
}
