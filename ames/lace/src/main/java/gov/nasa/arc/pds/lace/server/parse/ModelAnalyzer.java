package gov.nasa.arc.pds.lace.server.parse;

import gov.nasa.arc.pds.lace.server.IDGenerator;
import gov.nasa.arc.pds.lace.server.schema.SchemaManager;
import gov.nasa.arc.pds.lace.shared.AttributeItem;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertOption;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.InsertionPoint.DisplayType;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;
import gov.nasa.arc.pds.lace.shared.WildcardType;

import java.net.URI;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSFacet;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSMultiValueFacet;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Implements a facility for reading an XML Schema and creating
 * model objects from types and elements in the schema.
 */
public class ModelAnalyzer {

	private static final String PLUS_DISPLAY_TYPE = DisplayType.PLUS_BUTTON.getDisplayType();

	private static final String LESS_OR_EQUAL = "\u2264";
	private static final String GREATER_OR_EQUAL = "\u2265";

	private Map<String, LabelItemType> knownTypes = new HashMap<String, LabelItemType>();
	private XSModel model;
	private IDGenerator idFactory;
	private SchemaManager schemaManager;

	/**
	 * Creates a new analyzer with a set of URIs to the schema documents.
	 *
	 * @param idFactory the unique ID generator for model items
	 * @param schemaManager the schema manager
	 * @param xmlSchemas a map from namespaces to schema document locations
	 */
	public ModelAnalyzer(IDGenerator idFactory, SchemaManager schemaManager, final Map<String, URI> xmlSchemas) {
		this.idFactory = idFactory;
		this.schemaManager = schemaManager;

		// Get DOM Implementation using DOM Registry
		System.setProperty(
				DOMImplementationRegistry.PROPERTY,
				"org.apache.xerces.dom.DOMXSImplementationSourceImpl"
		);

		DOMImplementationRegistry registry = null;
		try {
			registry = DOMImplementationRegistry.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Cannot instantiate DOMImplementationRegistry: " + e.getMessage());
		}

		final DOMImplementationLS domLS =
			(DOMImplementationLS) registry.getDOMImplementation("LS");

		final XSImplementation impl =
		    (XSImplementation) registry.getDOMImplementation("XS-Loader");

		XSLoader schemaLoader = impl.createXSLoader(null);

		LSResourceResolver resolver = new LSResourceResolver() {
			@Override
			public LSInput resolveResource(String type, String ns, String publicID, String systemID, String baseURI) {
				URI schemaURI = xmlSchemas.get(ns);
				LSInput input = domLS.createLSInput();

				if (schemaURI != null) {
					input.setSystemId(schemaURI.toString());
				}

				return input;
			}
		};

		DOMConfiguration config = schemaLoader.getConfig();
		config.setParameter("resource-resolver", resolver);

		StringList uriList = new StringListImpl();
		for (URI uri : xmlSchemas.values()) {
			((StringListImpl) uriList).add(uri.toString());
		}
		model = schemaLoader.loadURIList(uriList);
	}

	/**
	 * Gets a model container for a given element and expands the insertion points.
	 *
	 * @param elementName the name of the element
	 * @param elementNamespace the namespace of the element
	 * @return a container with model contents, or null if the element cannot be found
	 */
	public Container getContainerForElement(String elementName, String elementNamespace) {
		return getContainerForElement(elementName, elementNamespace, true);
	}

	/**
	 * Gets a label item for a desired DOM element.
	 *
	 * @param elementName the name of the complex element
	 * @param elementNamespace the namespace of the element
	 * @param expand flag to indicate whether to expand the insertion points or not
	 * @return a label item, with model contents if it is a container, or null if the element cannot be found
	 */
	public LabelElement getItemForElement(String elementName, String elementNamespace, boolean expand) {
		LabelItemType type = getTypeForElement(elementName, elementNamespace);

		if (!type.isComplex()) {
			return createSimpleItem(type);
		} else {
			Container container = createContainer(type);
			if (expand) {
				expandInsertionPoints(container);
			}
			return container;
		}
	}

	/**
	 * Gets a model container for a given element. The top-level element may
	 * have a simple type. In this case we create a container anyway, since it
	 * will be used as a top-level model element.
	 *
	 * @param elementName the name of the complex element
	 * @param elementNamespace the namespace of the element
	 * @param expand flag to indicate whether to expand the insertion points or not
	 * @return a container with model contents, or null if the element cannot be found
	 */
	public Container getContainerForElement(String elementName, String elementNamespace, boolean expand) {
		LabelElement item = getItemForElement(elementName, elementNamespace, expand);
		if (item instanceof Container) {
			return (Container) item;
		}

		// It's a simple item. Must create a container instead. No need
		// to expand, since it has no content.
		return createContainer(item.getType());
	}

	/**
	 * Gets a type for a given element.
	 *
	 * @param elementName the name of the (simple or complex) element
	 * @param elementNamespace  the namespace of the element
	 * @return the label item type
	 */
	public LabelItemType getTypeForElement(String elementName, String elementNamespace) {
		LabelItemType type = null;
		XSElementDeclaration element = model.getElementDeclaration(elementName, elementNamespace);
		assert element != null;

		XSTypeDefinition typeDefinition = element.getTypeDefinition();
		if (typeDefinition instanceof XSComplexTypeDefinition) {
			XSComplexTypeDefinition complexTypeDef = (XSComplexTypeDefinition) typeDefinition;
			List<AttributeItem> initialAttributes = getTypeInitialAttributes(complexTypeDef);
			List<LabelItem> initialContents = getTypeInitialContents(complexTypeDef);
			type = createLabelItemType(element, 1, 1, true, initialAttributes, initialContents, typeDefinition);
		} else {
			type = createLabelItemType(element, 1, 1, false, new ArrayList<AttributeItem>(), null, typeDefinition);
		}

		return type;
	}

	/**
	 * Gets a list of available namespaces.
	 *
	 * @return a list of namespaces
	 */
	public Collection<String> getNamespaces() {
		Set<String> result = new TreeSet<String>();
		StringList namespaces = model.getNamespaces();

		for (int i=0; i < namespaces.getLength(); ++i) {
			// Only add namespaces with at least one top-level element.
			String namespace = namespaces.item(i);
			XSNamedMap map = model.getComponentsByNamespace(XSConstants.ELEMENT_DECLARATION, namespace);
			if (map.getLength() > 0) {
				result.add(namespace);
			}
		}

		return result;
	}

	/**
	 * Gets a list of top-level element names that are defined within the specified namespace.
	 *
	 * @param namespace the namespace to which the element declaration belongs
	 * @return a list of top-level element names
	 */
	public Collection<String> getElementNamesByNamespace(String namespace) {
		Set<String> list = new TreeSet<String>();
		XSNamedMap map = model.getComponentsByNamespace(XSConstants.ELEMENT_DECLARATION, namespace);

		for (int i = 0; i< map.getLength(); i++) {
			list.add(map.item(i).getName());
		}

		return list;
	}

	private void storeKnownType(XSElementDeclaration element, XSTypeDefinition typeDef,
			int minOccurs, int maxOccurs, LabelItemType type) {
		if (shouldStoreType(typeDef)) {
			knownTypes.put(makeTypeKey(element, typeDef, minOccurs, maxOccurs), type);
		}
	}

	private LabelItemType findKnownType(XSElementDeclaration element, XSTypeDefinition typeDef, int minOccurs, int maxOccurs) {
		if (!shouldStoreType(typeDef)) {
			return null;
		} else {
			return knownTypes.get(makeTypeKey(element, typeDef, minOccurs, maxOccurs));
		}
	}

	private boolean shouldStoreType(XSTypeDefinition typeDef) {
		return (typeDef.getName()!=null && typeDef.getNamespace()!=null);
	}

	private String makeTypeKey(XSElementDeclaration element, XSTypeDefinition typeDef, int minOccurs, int maxOccurs) {
		return element.getNamespace()
			+ ":" + element.getName()
			+ ":" + typeDef.getNamespace()
			+ ":" + typeDef.getName()
			+ ":" + minOccurs
			+ ":" + maxOccurs;
	}

	/**
	 * Expands required insertion points.
	 *
	 * @param item the label item in which to expand the insertion points
	 */
	public void expandInsertionPoints(LabelItem item) {
		if (item instanceof Container) {
			List<LabelItem> contents = ((Container) item).getContents();
			expandInsertionPoints(contents);
			mergeInsertionPoints(contents);
		}
	}

	/**
	 * Expands any insertion points in a list of label items. If a required or
	 * optional insertion point is found, insert its alternative. And recursively
	 * expand insertion points in any child items of a container.
	 *
	 * @param contents a list of label items
	 */
	private void expandInsertionPoints(List<LabelItem> contents) {
		ListIterator<LabelItem> it = contents.listIterator();

		while (it.hasNext()) {
			LabelItem item = it.next();

			// Check for a "required" insertion point or a container.
			if (item instanceof Container) {
				expandInsertionPoints(item);
			} else if (item instanceof InsertionPoint) {
				InsertionPoint insPoint = (InsertionPoint) item;
				String display = insPoint.getDisplayType();

				// Insert the required element or an optional simple element
				// that have not already been inserted.
				if (display.equals(DisplayType.REQUIRED.getDisplayType()) ||
						display.equals(DisplayType.OPTIONAL.getDisplayType())) {

					List<InsertOption> alternatives = insPoint.getAlternatives();
					assert alternatives.size() == 1;

					InsertOption alternative = alternatives.get(0);
					assert alternative.getUsedOccurrences() == 0;

					List<LabelItemType> types = alternative.getTypes();
					assert types.size() == 1;

					LabelItemType type = types.get(0);

					if (display.equals(DisplayType.OPTIONAL.getDisplayType())) {
						if (!type.isComplex()) {
							// This insertion point represents an optional
							// repeating simple element. Do an insertion.
							doInsert(it, insPoint, alternative, type);
						}
					} else {
						// This insertion point represents a required
						// repeating element. Do an insertion.
						for (int i = 0; i < alternative.getMinOccurrences(); i++) {
							doInsert(it, insPoint, alternative, type);
						}
					}
				}
			}
		}
	}

	/**
	 * Performs an insertion of a required element from an insertion
	 * point by duplicating the insertion point, and inserting both
	 * the required element and the new insertion point just before
	 * the list iterator cursor. Then adjust the cursor so that the
	 * next element from the iterator will be the new insertion point,
	 * just after the new, required element.
	 *
	 * @param it a list iterator whose cursor is positioned just past the insertion point
	 * @param insPoint the insertion point at which to insert a required element
	 * @param alternative the InsertOption instance that holds the item which is to be inserted
	 * @param type the label item type to insert
	 */
	private void doInsert(ListIterator<LabelItem> it, InsertionPoint insPoint,
			InsertOption alternative, LabelItemType type) {

		// Change the display type to "plus" button if it's not of that type.
		if (!insPoint.getDisplayType().equals(PLUS_DISPLAY_TYPE)) {
			insPoint.setDisplayType(PLUS_DISPLAY_TYPE);
		}

		// Increase the "used" counter by 1 and make a new copy of the
		// insertion point (while keeping the insert option instances intact).
		alternative.setUsedOccurrences(alternative.getUsedOccurrences() + 1);
		InsertionPoint newInsPoint = insPoint.copy(false);

		// Create the new element and link it to the insert option.
		LabelElement newElement = createLabelElement(type);
		newElement.setInsertOption(alternative);

		// Now insert the new element and insertion point, and update the list cursor.
		it.add(newElement);
		it.add(newInsPoint);
		it.previous();

		// And, expand any insertion points in the newly inserted element.
		expandInsertionPoints(newElement);
	}

	/**
	 * Performs an insertion of a label element from an insertion point.
	 *
	 * @param insPoint the insertion point at which to insert an element
	 * @param alternativeIndex the index of the InsertOption instance that holds
	 * the item which is to be inserted
	 * @param type the label item type to insert
	 * @return
	 */
	public List<LabelItem> doInsert(InsertionPoint insPoint, int alternativeIndex, LabelItemType type) {
		List<InsertOption> alternatives = insPoint.getAlternatives();
		assert alternatives.size() > 0 && alternatives.size() <= 2;

		int index = 0;
		boolean merge = false;
		InsertionPoint insPointToSplit = insPoint;
		List<LabelItem> items = new ArrayList<LabelItem>();
		String displayType = insPoint.getDisplayType();

		// Increase the "used" counter by 1.
		InsertOption alternative = alternatives.get(alternativeIndex);
		alternative.setUsedOccurrences(alternative.getUsedOccurrences() + 1);

		// Create the new element and link it to the insert option.
		LabelElement newElement = createLabelElement(type);
		newElement.setInsertOption(alternative);

		if (displayType.equals(PLUS_DISPLAY_TYPE) && alternatives.size() > 1) {
			// Since there is more than one alternative (technically there are
			// only two), split the existing insertion point to two, with the
			// first one pointing to the first alternative and the second to
			// the second alternative.
			InsertionPoint insPoint1 = insPoint.copy(false);
			InsertionPoint insPoint2 = insPoint.copy(false);
			insPoint1.getAlternatives().remove(1);
			insPoint2.getAlternatives().remove(0);

			// Find the insertion point that contains the alternative
			// that we want to insert for further splitting.
			if (insPoint1.getAlternatives().contains(alternative)) {
				insPointToSplit = insPoint1;
				items.add(insPoint2);
			} else {
				insPointToSplit = insPoint2;
				items.add(insPoint1);
				index = 1;
			}

			merge = true;
		} else if (!displayType.equals(DisplayType.ANY.getDisplayType()) && !displayType.equals(PLUS_DISPLAY_TYPE)) {
			// Change the display type to "+", if it's not already of
			//  that type and if it's not of type 'any'.
			insPoint.setDisplayType(PLUS_DISPLAY_TYPE);
		}

		// Split the insertion point.
		InsertionPoint first = insPointToSplit.copy(false);
		InsertionPoint second = insPointToSplit.copy(false);

		// Insert the new element and insertion points.
		items.add(index++, first);
		items.add(index++, newElement);
		items.add(index, second);

		if (merge) {
			mergeInsertionPoints(items);
		}

		// And, expand any insertion points in the newly inserted element.
		expandInsertionPoints(newElement);

		return items;
	}

	/**
	 * Performs deletion of the specified label element.
	 *
	 * @param container the container that contains the element to be deleted
	 * @param element the label element to delete
	 */
	public void doDelete(Container container, LabelElement element) {
		InsertOption alternative = element.getInsertOption();
		int used = alternative.getUsedOccurrences();
		assert used > 0;

		--used;
		alternative.setUsedOccurrences(used);

		List<LabelItem> contents = container.getContents();
		contents.remove(element);
		mergeInsertionPoints(contents);
		splitInsertionPoint(contents, alternative);
	}

	/**
	 * Splits insertion points after they are merged post delete operation.
	 *
	 * @param labelItems
	 * @param option
	 */
	private void splitInsertionPoint(List<LabelItem> labelItems, InsertOption option) {
		List<LabelItem> items = new ArrayList<LabelItem>();
		for (LabelItem item : labelItems) {
			items.add(item);
		}

		LabelItem curItem = null;
		ListIterator<LabelItem> it = items.listIterator();

		while (it.hasNext()) {
			curItem = it.next();
			if (isSplittableInsertionPoint(curItem)) {
				int index = labelItems.indexOf(curItem);
				labelItems.remove(index);
				for (LabelItem item : doSplit((InsertionPoint) curItem)) {
					labelItems.add(index++, item);
				}
			}
		}
	}

	/**
	 * Splits the specified insertion point.
	 *
	 * @param insPoint
	 * @return
	 */
	private List<LabelItem> doSplit(InsertionPoint insPoint) {
		List<LabelItem> list = new ArrayList<LabelItem>();

		for (InsertOption alternative : insPoint.getAlternatives()) {
			list.add(createInsertionPoint(Arrays.asList(alternative), getDisplayType(alternative)));
		}

		return list;
	}

	/**
	 * Tests whether item is splittable. An insertion point that has a plus display type is splittable if
	 * it has an alternative with used==0.
	 *
	 * @param item the label item to test
	 * @return true if item is splittable, false, otherwise
	 */
	private boolean isSplittableInsertionPoint(LabelItem item) {
		if (item instanceof InsertionPoint) {
			InsertionPoint insPoint = ((InsertionPoint) item);
			List<InsertOption> alternatives = insPoint.getAlternatives();
			if (insPoint.getDisplayType().equals(DisplayType.PLUS_BUTTON.getDisplayType())) {
				for (InsertOption alternative : alternatives) {
					if (alternative.getUsedOccurrences() == 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private LabelElement createLabelElement(LabelItemType type) {
		if (!type.isComplex()) {
			return createSimpleItem(type);
		} else {
			return createContainer(type);
		}
	}

	/*
	 * Creates a LabelItemType object for the given element.
	 */
	private LabelItemType createLabelItemType(
			XSElementDeclaration element,
			int minOccurs,
			int maxOccurs,
			boolean isComplex,
			List<AttributeItem> initialAttributes,
			List<LabelItem> initialContents,
			XSTypeDefinition typeDefinition
	) {
		LabelItemType type = new LabelItemType();
		type.setElementName(element.getName());
		type.setElementNamespace(element.getNamespace());
		type.setMinOccurrences(minOccurs);
		type.setMaxOccurrences(maxOccurs);
		type.setComplex(isComplex);
		type.setInitialAttributes(initialAttributes);
		type.setInitialContents(initialContents);

		if (!isComplex) {
			setConstraints((XSSimpleTypeDefinition) typeDefinition, type);
		}

		createDocumentation(element, type, typeDefinition, minOccurs, maxOccurs);

		storeKnownType(element, typeDefinition, minOccurs, maxOccurs, type);

		return type;
	}

	private void createDocumentation(
			XSElementDeclaration element,
			LabelItemType type,
			XSTypeDefinition typeDefinition,
			int minOccurs, int maxOccurs
	) {
		HtmlBuilder builder = new HtmlBuilder();
		boolean hasAnnotations = false;
		if (element != null) {
			hasAnnotations = addAnnotations(builder, element.getAnnotations());
		}

		if (typeDefinition instanceof XSSimpleTypeDefinition) {
			addSimpleTypeDocumentation(hasAnnotations, builder, (XSSimpleTypeDefinition) typeDefinition, minOccurs, maxOccurs);
		} else {
			addComplexTypeDocumentation(hasAnnotations, builder, (XSComplexTypeDefinition) typeDefinition, minOccurs, maxOccurs);
		}

		type.setDocumentation(builder.toSafeHtml());
	}

	private boolean addAnnotations(HtmlBuilder builder, XSObjectList annotations) {
		AnnotationContentHandler handler = new AnnotationContentHandler(builder);

		for (int i=0; i < annotations.getLength(); ++i) {
			XSAnnotation annotation = (XSAnnotation) annotations.item(i);
			annotation.writeAnnotation(handler, XSAnnotation.SAX_CONTENTHANDLER);
		}

		return !builder.isEmpty();
	}

	private void addOccurrenceDocumentation(HtmlBuilder builder, int minOccurs, int maxOccurs) {
		if (maxOccurs != 1) {
			builder.beginTag("p");
			builder.beginTag("span", "class='constraintLabel'");
			builder.appendEscaped("Allowed occurrences:");
			builder.endTag();
			builder.appendEscaped(" ");
			builder.appendEscaped(Integer.toString(minOccurs));

			if (maxOccurs < 0) {
				builder.appendEscaped(" or more");
			} else {
				builder.appendEscaped(" to " );
				builder.appendEscaped(Integer.toString(maxOccurs));
			}
			builder.endTag();
		}
	}

	private void addSimpleTypeDocumentation(
			boolean hasAnnotations,
			HtmlBuilder builder,
			XSSimpleTypeDefinition typeDefinition,
			int minOccurs,
			int maxOccurs
	) {
		if (!hasAnnotations) {
			addAnnotations(builder, typeDefinition.getAnnotations());
		}

		addOccurrenceDocumentation(builder, minOccurs, maxOccurs);

		String minLength = getFacetValue(typeDefinition, XSSimpleTypeDefinition.FACET_MINLENGTH);
		String maxLength = getFacetValue(typeDefinition, XSSimpleTypeDefinition.FACET_MAXLENGTH);
		addLengthConstraintDocumentation(builder, "Required length:", minLength, maxLength);

		String minValue = null;
		if (getFacetValue(typeDefinition, XSSimpleTypeDefinition.FACET_MININCLUSIVE) != null) {
			minValue = GREATER_OR_EQUAL + getFacetValue(typeDefinition, XSSimpleTypeDefinition.FACET_MININCLUSIVE);
		} else if (getFacetValue(typeDefinition, XSSimpleTypeDefinition.FACET_MINEXCLUSIVE) != null) {
			minValue = ">" + getFacetValue(typeDefinition, XSSimpleTypeDefinition.FACET_MINEXCLUSIVE);
		}
		String maxValue = null;
		if (getFacetValue(typeDefinition, XSSimpleTypeDefinition.FACET_MAXINCLUSIVE) != null) {
			maxValue = LESS_OR_EQUAL + getFacetValue(typeDefinition, XSSimpleTypeDefinition.FACET_MAXINCLUSIVE);
		} else if (getFacetValue(typeDefinition, XSSimpleTypeDefinition.FACET_MAXEXCLUSIVE) != null) {
			maxValue = "<" + getFacetValue(typeDefinition, XSSimpleTypeDefinition.FACET_MAXEXCLUSIVE);
		}
		addRangeConstraintDocumentation(builder, "Valid range:", minValue, maxValue);

		List<String> patterns = getFacetValues(typeDefinition, XSSimpleTypeDefinition.FACET_PATTERN);
		if (patterns!=null && !patterns.isEmpty()) {
			builder.beginTag("p");
			builder.appendEscaped("Value must match one of these patterns:");
			builder.endTag();

			builder.beginTag("ul");
			for (String pat : patterns) {
				builder.beginTag("li");
				builder.appendEscaped(pat);
				builder.endTag();
			}
			builder.endTag();
		}
	}

	private void addLengthConstraintDocumentation(HtmlBuilder builder, String label, String minLength, String maxLength) {
		if (minLength!=null || maxLength!=null) {
			builder.beginTag("p");
			builder.beginTag("span", "class='constraintLabel'");
			builder.appendEscaped(label);
			builder.endTag();
			builder.appendEscaped(" ");

			if (minLength != null) {
				if (minLength.equals(maxLength)) {
					builder.appendEscaped("exactly ");
				}
				builder.appendEscaped(minLength);
				if (maxLength == null) {
					builder.appendEscaped(" or more");
				} else if (!minLength.equals(maxLength)){
					builder.appendEscaped(" to ");
				}
			}

			if (maxLength!=null && !maxLength.equals(minLength)) {
				if (minLength == null) {
					builder.appendEscaped("up to");
				}
				builder.appendEscaped(maxLength);
			}

			builder.appendEscaped(" characters");
			builder.endTag();
		}
	}

	private void addRangeConstraintDocumentation(HtmlBuilder builder, String label, String minValue, String maxValue) {
		if (minValue!=null || maxValue!=null) {
			builder.beginTag("p");
			builder.beginTag("span", "class='constraintLabel'");
			builder.appendEscaped(label);
			builder.endTag();
			builder.appendEscaped(" ");

			if (minValue != null) {
				builder.appendEscaped(minValue);
				if (maxValue != null) {
					builder.appendEscaped(" and ");
				}
			}

			if (maxValue != null) {
				builder.appendEscaped(maxValue);
			}

			builder.endTag();
		}
	}

	private void addComplexTypeDocumentation(
			boolean hasAnnotations,
			HtmlBuilder builder,
			XSComplexTypeDefinition typeDefinition,
			int minOccurs,
			int maxOccurs
	) {
		if (!hasAnnotations) {
			addAnnotations(builder, typeDefinition.getAnnotations());
		}

		addOccurrenceDocumentation(builder, minOccurs, maxOccurs);
	}

	/*
	 * Creates a LabelItemType object for the given element.
	 */
	private LabelItemType createAttributeType(XSAttributeUse use) {
		XSAttributeDeclaration decl = use.getAttrDeclaration();
		LabelItemType type = new LabelItemType();
		type.setElementName(decl.getName());
		type.setElementNamespace(decl.getNamespace());
		type.setMinOccurrences(use.getRequired() ? 1 : 0);
		type.setMaxOccurrences(1);
		type.setComplex(false);

		if (use.getConstraintType() == XSConstants.VC_DEFAULT) {
			type.setDefaultValue(use.getConstraintValue());
		}

		if (decl.getTypeDefinition() != null) {
			setConstraints(decl.getTypeDefinition(), type);
		}

		createDocumentation(null, type, decl.getTypeDefinition(), 1, 1);

		return type;
	}

	private WildcardType createWildcardType(List<String> namespaces, int minOccurs, int maxOccurs) {
		WildcardType type = new WildcardType();
		type.setNamespaces(namespaces);
		type.setMinOccurrences(minOccurs);
		type.setMaxOccurrences(maxOccurs);

		return type;
	}

	/**
	 * Gets the initial list of attributes for a complex type.
	 *
	 * @param complexType the complex type definition
	 * @return a list of attributes
	 */
	private List<AttributeItem> getTypeInitialAttributes(XSComplexTypeDefinition complexType) {
		return getAttributes(complexType.getAttributeUses());
	}

	/**
	 * Gets the initial contents of the type being constructed for the complex element by
	 * constructing a list of LabelItem objects, as determined by walking schema element definition.
	 *
	 * @param complexType the definition of the type for the root element
	 */
	private List<LabelItem> getTypeInitialContents(XSComplexTypeDefinition complexType) {
		List<LabelItem> labelItems = new ArrayList<LabelItem>();
		parseParticle(complexType.getParticle(), labelItems);
		mergeInsertionPoints(labelItems);
		return labelItems;
	}

	/**
	 * Parses the term for the particle which can be an element declaration, a model group
	 * or a wildcard.
	 *
	 * @param particle
	 * @param labelItems
	 */
	private void parseParticle(XSParticle particle, List<LabelItem> labelItems) {
		if (particle == null) {
			// Must be empty element content model.
			return;
		}

		XSTerm term = particle.getTerm();
		if (term instanceof XSElementDeclaration) {
			parseElementDefinition((XSElementDeclaration) term, particle, labelItems);
		} else if (term instanceof XSModelGroup) {
			parseModelGroup((XSModelGroup) term, particle, labelItems);
		} else {
			// term is an instance of XSWildcard.
			parseWildcard((XSWildcard) term, particle, labelItems);
		}
	}

	private void parseElementDefinition(XSElementDeclaration element, XSParticle particle, List<LabelItem> labelItems) {
		XSObjectList group = model.getSubstitutionGroup(element);

		if (group==null || group.getLength()==0) {
			parseSingleElementDefinition(element, particle, labelItems);
		} else {
			parseGroupElementDefinition(element, group, particle, labelItems);
		}
	}

	/**
	 * Parse all alternatives in a substitution group into a synthetic choice
	 * insertion point.
	 *
	 * @param element the original element declartion
	 * @param group a list of element declarations in the substitution group
	 * @param particle the original particle
	 * @param labelItems the result list of label items
	 */
	private void parseGroupElementDefinition(
			XSElementDeclaration element,
			XSObjectList group,
			XSParticle particle,
			List<LabelItem> labelItems
	) {
		List<LabelItemType> types = new ArrayList<LabelItemType>();
		if (!element.getAbstract()) {
			types.add(parseChoiceElementDefinition(element, particle));
		}
		for (int i=0; i < group.getLength(); ++i) {
			types.add(parseChoiceElementDefinition((XSElementDeclaration) group.item(i), particle));
		}

		labelItems.add(createInsertionPoint(types, particle));
	}

	/**
	 * If the element declaration is of type XSSimpleTypeDefinition, it's
	 * a simple named element type. Otherwise, it's a complex named type.
	 */
	private void parseSingleElementDefinition(XSElementDeclaration element, XSParticle particle, List<LabelItem> labelItems) {
		XSTypeDefinition typeDefinition = element.getTypeDefinition();

		if (typeDefinition instanceof XSSimpleTypeDefinition) {
			parseSimpleType(element, (XSSimpleTypeDefinition) typeDefinition, particle, labelItems);
		} else {
			parseComplexType(element, (XSComplexTypeDefinition) typeDefinition, particle, labelItems);
		}
	}

	private void parseSimpleType(XSElementDeclaration element, XSSimpleTypeDefinition typeDefinition,
			XSParticle particle, List<LabelItem> labelItems) {

		LabelItemType type = createSimpleItemType(element, typeDefinition, particle, new ArrayList<AttributeItem>());

		if (particle.getMaxOccursUnbounded() || particle.getMaxOccurs() > 1) {
			labelItems.add(createInsertionPoint(Collections.singletonList(type), particle));
		} else {
			labelItems.add(createSimpleItem(type));
		}
	}

	private void parseComplexType(XSElementDeclaration element, XSComplexTypeDefinition typeDefinition,
			XSParticle particle, List<LabelItem> labelItems) {

		LabelItemType type = findKnownType(element, typeDefinition, particle.getMinOccurs(), particle.getMaxOccurs());

		if (type == null) {
			// As of the 0300a schemas, the only element of the PDS4 schemas that
			// has an empty content model is Band_Bin_Set.
			//TODO: Make sure this handling of CONENTTYPE_EMPTY is correct, and also handle CONTENTTYPE_MIXED, perhaps by disallowing it.
			List<AttributeItem> attributes = getAttributes(typeDefinition.getAttributeUses());
			if (typeDefinition.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_EMPTY) {
				type = createLabelItemType(
						element,
						particle.getMinOccurs(),
						particle.getMaxOccurs(),
						true,
						attributes,
						new ArrayList<LabelItem>(),
						typeDefinition
				);
			} else if (typeDefinition.getParticle() == null) {
				type = createSimpleItemType(element, typeDefinition.getSimpleType(), particle, attributes);
			} else {
				type = createLabelItemType(
						element,
						particle.getMinOccurs(),
						particle.getMaxOccurs(),
						true,
						attributes,
						getTypeInitialContents(typeDefinition),
						typeDefinition
				);
			}
		}

		if (!type.isComplex()) {
			if (particle.getMaxOccursUnbounded() || particle.getMaxOccurs() > 1) {
				labelItems.add(createInsertionPoint(Collections.singletonList(type), particle));
			} else {
				labelItems.add(createSimpleItem(type));
			}
		} else if (particle.getMinOccurs() == 1 && particle.getMaxOccurs() == 1) {
			labelItems.add(createContainer(type));
		} else {
			labelItems.add(createInsertionPoint(Collections.singletonList(type), particle));
		}
	}

	private List<AttributeItem> getAttributes(XSObjectList attributeUses) {

		List<AttributeItem> attributes = new ArrayList<AttributeItem>();

		for (int i=0; i < attributeUses.getLength(); ++i) {
			LabelItemType type = createAttributeType((XSAttributeUse) attributeUses.item(i));
			attributes.add(createAttributeItem(type));
		}

		return attributes;
	}

	private Container createContainer(LabelItemType type) {
		Container container = new Container();
		container.setID(idFactory.getID());
		container.setType(type);
		setInitialAttributes(container);
		setInitialContents(container);
		return container;
	}

	private void setInitialAttributes(LabelElement element) {
		for (AttributeItem item : element.getType().getInitialAttributes()) {
			element.addAttribute(item.copy());
		}
	}

	protected void setInitialContents(Container container) {
		container.setContents(new ArrayList<LabelItem>());

		for (LabelItem item : container.getType().getInitialContents()) {
			LabelItem newItem = item.copy();

			// Ensure that the new items have unique IDs.
			if (newItem instanceof LabelElement) {
				((LabelElement) newItem).setID(idFactory.getID());
			}

			if (newItem instanceof Container) {
				setInitialContents((Container) newItem);
			}
			container.getContents().add(newItem);
		}
	}

	private SimpleItem createSimpleItem(LabelItemType type) {
		SimpleItem item = new SimpleItem();
		item.setID(idFactory.getID());
		item.setType(type);
		setInitialAttributes(item);

		if (type.getDefaultValue() != null) {
			item.setValue(type.getDefaultValue());
		}

		return item;
	}

	private AttributeItem createAttributeItem(LabelItemType type) {
		AttributeItem item = new AttributeItem();
		item.setID(idFactory.getID());
		item.setType(type);

		if (type.getDefaultValue() != null) {
			item.setValue(type.getDefaultValue());
		}

		return item;
	}

	private DisplayType getDisplayType(InsertOption alternative) {
		int minOccurs = alternative.getMinOccurrences();
		int usedOccurs = alternative.getUsedOccurrences();
		List<LabelItemType> types = alternative.getTypes();
		DisplayType	displayType = DisplayType.PLUS_BUTTON;

		if (types.size() == 1 && types.get(0).isWildcard()) {
			displayType = DisplayType.ANY;
		} else if (usedOccurs > 0) {
			displayType = DisplayType.PLUS_BUTTON;
		} else if (types.size() > 1) {
			displayType = DisplayType.CHOICE;
		} else if (minOccurs == 0) {
			displayType = DisplayType.OPTIONAL;
		} else if (minOccurs > 0) {
			displayType = DisplayType.REQUIRED;
		}

		return displayType;
	}

	private InsertionPoint createInsertionPoint(List<LabelItemType> types, XSParticle particle) {
		InsertOption insertOption = createInsertOption(types, particle.getMinOccurs(), particle.getMaxOccurs());
		List<InsertOption> alternatives = new ArrayList<InsertOption>();
		alternatives.add(insertOption);

		return createInsertionPoint(alternatives, getDisplayType(insertOption));
	}

	private InsertionPoint createInsertionPoint(List<InsertOption> alternatives, DisplayType displayType) {
		InsertionPoint insPoint = new InsertionPoint();
		insPoint.setAlternatives(alternatives);
		insPoint.setDisplayType(displayType.getDisplayType());

		return insPoint;
	}

	private InsertOption createInsertOption(List<LabelItemType> types, int minOccurs, int maxOccurs) {
		InsertOption insertOption = new InsertOption();
		insertOption.setUsedOccurrences(0);
		insertOption.setMinOccurrences(minOccurs);
		insertOption.setMaxOccurrences(maxOccurs);
		insertOption.setTypes(types);

		return insertOption;
	}

	private LabelItemType createSimpleItemType(XSElementDeclaration element, XSSimpleTypeDefinition type, XSParticle particle, List<AttributeItem> attributes) {
		LabelItemType newType = createLabelItemType(
				element,
				particle.getMinOccurs(),
				particle.getMaxOccurs(),
				false,
				attributes,
				null,
				type
		);

		newType.setID(type.getBuiltInKind() == XSConstants.ID_DT);
		newType.setIDREF(type.getBuiltInKind() == XSConstants.IDREF_DT);

		return newType;
	}

	private void setConstraints(XSSimpleTypeDefinition typeDefinition, LabelItemType type) {
		switch (typeDefinition.getVariety()) {
			case XSSimpleTypeDefinition.VARIETY_ATOMIC:
				setFacets(typeDefinition, type);
				break;

			case XSSimpleTypeDefinition.VARIETY_LIST:
				if (typeDefinition.getItemType() != null) {
					setConstraints(typeDefinition.getItemType(), type);
				}
				break;

			default:  //VARIETY_UNION
				XSObjectList memberTypes = typeDefinition.getMemberTypes();

				// Union types can only have pattern and enumeration constraints. Gather
				// all enumeration constraints into the valid values list.
				for (int i=0; i < memberTypes.getLength(); ++i) {
					XSSimpleTypeDefinition memberType = (XSSimpleTypeDefinition) memberTypes.item(i);
					addValidValues(memberType, type);
				}

				if (type.getValidValues() != null && type.getValidValues().size()==1) {
					type.setDefaultValue(type.getValidValues().get(0));
				}
				break;
		}
	}

	private void setFacets(XSSimpleTypeDefinition typeDefinition, LabelItemType type) {
		String value = getFacetValue(typeDefinition, XSSimpleTypeDefinition.FACET_MINLENGTH);
		if (value != null) {
			type.setMinLength(Integer.valueOf(value));
		}

		value = getFacetValue(typeDefinition, XSSimpleTypeDefinition.FACET_MAXLENGTH);
		if (value != null) {
			type.setMaxLength(Integer.valueOf(value));
		}

		value = getFacetValue(typeDefinition, XSSimpleTypeDefinition.FACET_WHITESPACE);
		if (value != null) {
			type.setWhitespacePreserved(value.equals("preserve"));
		}

		type.setPatterns(getFacetValues(typeDefinition, XSSimpleTypeDefinition.FACET_PATTERN));

		List<String> validValues = getFacetValues(typeDefinition, XSSimpleTypeDefinition.FACET_ENUMERATION);
		type.setValidValues(validValues.toArray(new String[validValues.size()]));
		if (validValues.size() == 1) {
			type.setDefaultValue(validValues.get(0));
		}
	}

	private void addValidValues(XSSimpleTypeDefinition typeDefinition, LabelItemType type) {
		List<String> validValues = getFacetValues(typeDefinition, XSSimpleTypeDefinition.FACET_ENUMERATION);
		type.addValidValues(validValues.toArray(new String[validValues.size()]));
	}

	/**
	 * Gets a list of enumeration and pattern constraining facet values for the specified simple type definition.
	 *
	 * @param typeDefinition
	 * @param facetType
	 * @return a list of enumeration or pattern values if it exists, otherwise an empty list
	 */
	private List<String> getFacetValues(XSSimpleTypeDefinition typeDefinition, int facetType) {
		List<String> values = new ArrayList<String>();
		XSObjectList objectList = typeDefinition.getMultiValueFacets();

		for (int i = 0; i < objectList.getLength(); i++) {
			XSObject object = objectList.item(i);
			if (object instanceof XSMultiValueFacet) {
				XSMultiValueFacet facet = ((XSMultiValueFacet) object);
				if (facet.getFacetKind() == (short) facetType) {
					StringList lexicalValues = facet.getLexicalFacetValues();

					// For pattern facets, need to use only those patterns in position
					// zero, from the current type, not patterns from the supertypes.
					if (facet.getFacetKind() == XSSimpleTypeDefinition.FACET_PATTERN) {
						// Split pattern from the current type combined by "|".
						if (lexicalValues.getLength() > 0) {
							for (String pat : PatternSplitter.splitPattern(lexicalValues.item(0))) {
								values.add(pat);
							}
						}
					} else {
						for (int j = 0; j < lexicalValues.getLength(); j++) {
							values.add(lexicalValues.item(j));
						}
					}
					break;
				}
			}
		}

		// Add enumeration for boolean values. XMLSchema allows true, false, 0, and 1,
		// but we'll just add true and false.
		if (values.size()==0 && typeDefinition.getBuiltInKind()==XSConstants.BOOLEAN_DT) {
			values.add("true");
			values.add("false");
		}

		return values;
	}

	/**
	 * Gets a value of a single constraining facet for the specified simple type definition.
	 *
	 * @param typeDefinition
	 * @param facetType
	 * @return a value of the facet if it exists, otherwise null
	 */
	private String getFacetValue(XSSimpleTypeDefinition typeDefinition, int facetType) {
		String value = null;
		XSObjectList objectList = typeDefinition.getFacets();

		for (int i = 0; i < objectList.getLength(); i++) {
			XSObject object = objectList.item(i);
			if (object instanceof XSFacet) {
				XSFacet facet = ((XSFacet) object);
				if (facet.getFacetKind() == (short) facetType) {
					value = facet.getLexicalFacetValue();
					break;
				}
			}
		}

		return value;
	}

	private void parseModelGroup(XSModelGroup group, XSParticle particle, List<LabelItem> labelItems) {
		switch (group.getCompositor()) {
		case XSModelGroup.COMPOSITOR_ALL:
			// Not supported in version 1
			break;
		case XSModelGroup.COMPOSITOR_CHOICE:
			// Always an insertion point, no matter the multiplicity.
			addInsertionPointForChoice(group, particle, labelItems);
			break;
		case XSModelGroup.COMPOSITOR_SEQUENCE:
			// Add the sequence contents to type being constructed.
			parseSequence(group, particle, labelItems);
			break;
		default:
			// No other options defined.
			break;
		}
	}

	/**
	 * Adds an InsertionPoint if sequence is optional. Otherwise, parses the contents of
	 * sequence and adds to the initialContents of the type currently being constructed.
	 *
	 * @param group the model group
	 * @param particle
	 * @param labelItems the list of label items
	 */
	private void parseSequence(XSModelGroup group, XSParticle particle, List<LabelItem> labelItems) {
		if (particle.getMinOccurs() == 0) {
			addInsertionPointForSequence(group, labelItems);
		} else {
			XSObjectList particles = group.getParticles();
			for (int i = 0; i < particles.getLength(); ++i) {
				parseParticle((XSParticle) particles.item(i), labelItems);
			}
		}
	}

	/**
	 * Parses the choice model group to create LabelItemType objects
	 * to form the alternatives list for the new insertion point.
	 *
	 * @param group the model group
	 * @param particle
	 * @param labelItems the list of label items
	 */
	private void addInsertionPointForChoice(XSModelGroup group, XSParticle particle, List<LabelItem> labelItems) {
		List<LabelItemType> types = new ArrayList<LabelItemType>();
		XSObjectList particles = group.getParticles();

		for (int i = 0; i < particles.getLength(); i++) {
			XSParticle itemParticle = (XSParticle) particles.item(i);

			// Assume the term for the particle inside the choice model group
			// is an element declaration.
			XSElementDeclaration element = (XSElementDeclaration) itemParticle.getTerm();
			types.add(parseChoiceElementDefinition(element, particle));
		}

		labelItems.add(createInsertionPoint(types, particle));
	}

	private LabelItemType parseChoiceElementDefinition(XSElementDeclaration element, XSParticle particle) {
		XSTypeDefinition typeDefinition = element.getTypeDefinition();

		LabelItemType type = findKnownType(element, typeDefinition, particle.getMinOccurs(), particle.getMaxOccurs());
		if (type != null) {
			return type;
		}

		if (typeDefinition instanceof XSSimpleTypeDefinition) {
			type = createLabelItemType(
					element,
					particle.getMinOccurs(),
					particle.getMaxOccurs(),
					false,
					new ArrayList<AttributeItem>(),
					null,
					typeDefinition
			);
		} else {
			type = createLabelItemType(
					element,
					particle.getMinOccurs(),
					particle.getMaxOccurs(),
					true,
					null,
					null,
					typeDefinition
			);
			type.setInitialAttributes(getAttributes(((XSComplexTypeDefinition) typeDefinition).getAttributeUses()));
			type.setInitialContents(getTypeInitialContents((XSComplexTypeDefinition) typeDefinition));
		}

		return type;
	}

	private void addInsertionPointForSequence(XSModelGroup group, List<LabelItem> labelItems) {
		// Not supported in version 1.
	}

	private void parseWildcard(XSWildcard wildcard, XSParticle particle, List<LabelItem> labelItems) {
		List<String> namespaces = new ArrayList<String>();
		StringList list = wildcard.getNsConstraintList();

		switch (wildcard.getConstraintType()) {
			case XSWildcard.NSCONSTRAINT_ANY:
				// Any namespace is allowed. So, add the available namespaces.
				namespaces = Arrays.asList(schemaManager.getNamespaces());
				break;

			case XSWildcard.NSCONSTRAINT_LIST:
				// The constraint list contains the allowed namespaces.
				for (int i = 0; i < list.getLength(); i++) {
					namespaces.add(list.item(i));
				}
				break;

			case XSWildcard.NSCONSTRAINT_NOT:
				// The constraint list contains disallowed namespaces. Thus,
				// exclude the namespaces in the constraint list from the list
				// of available namespaces to construct a list of allowed namesapces.
				String[] availableNs = schemaManager.getNamespaces();
				for (int i = 0; i < availableNs.length; i++) {
					if (!list.contains(availableNs[i])) {
						namespaces.add(availableNs[i]);
					}
				}
				break;
		}

		List<LabelItemType> types = new ArrayList<LabelItemType>();
		types.add(createWildcardType(namespaces, particle.getMinOccurs(), particle.getMaxOccurs()));
		labelItems.add(createInsertionPoint(types, particle));
	}

	/**
	 * Merges any adjacent "plus" insertion points.
	 *
	 * @param labelItems the list of label items in which to merge the insertion points
	 */
	public void mergeInsertionPoints(List<LabelItem> labelItems) {

		ListIterator<LabelItem> it = labelItems.listIterator();

		while (it.hasNext()) {
			LabelItem curItem = it.next();

			if (isMergableInsertionPoint(curItem)) {
				// Found an insertion point. Merge any adjacent insertion
				// points.
				while (it.hasNext()) {
					LabelItem nextItem = it.next();
					if (!isMergableInsertionPoint(nextItem)) {
						it.previous();
						break;
					}

					// OK, found an adjacent insertion point.
					//
					// For some reason we can't do simply:
					//   first.getAlternatives().addAll(second.getAlternatives());
					// It gives an UnsupportedOperationException. So we have
					// to add them to a new list.
					InsertionPoint first = (InsertionPoint) curItem;
					List<InsertOption> newAlternatives = new ArrayList<InsertOption>();
					newAlternatives.addAll(first.getAlternatives());
					for (InsertOption insertOption : ((InsertionPoint) nextItem).getAlternatives()) {
						if (!newAlternatives.contains(insertOption)) {
							newAlternatives.add(insertOption);
						}
					}
					first.setAlternatives(newAlternatives);

					// Replace the nextItem with the merged insertion point (first)
					// and go back in the list and remove curItem
					it.set(first.copy(false));
					it.previous();
					it.previous();
					it.remove();
					curItem = it.next();
				}
			}
		}
	}

	private boolean isMergableInsertionPoint(LabelItem item) {
		if (item instanceof InsertionPoint) {
			String displayType = ((InsertionPoint) item).getDisplayType();
			if (displayType.equals(PLUS_DISPLAY_TYPE) || displayType.equals(DisplayType.ANY.getDisplayType())) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	private static class StringListImpl extends AbstractList implements StringList {

		private List<Object> data = new ArrayList<Object>();

		@Override
		public boolean contains(String o) {
			return data.contains(o);
		}

		@Override
		public boolean add(Object e) {
			return data.add(e);
		}

		@Override
		public int getLength() {
			return size();
		}

		@Override
		public String item(int o) {
			return (String) get(o);
		}

		@Override
		public Object get(int o) {
			return data.get(o);
		}

		@Override
		public int size() {
			return data.size();
		}

	}

	private static class AnnotationContentHandler implements ContentHandler {

		private static final String XS_NS = "http://www.w3.org/2001/XMLSchema";
		private static final String DOCUMENTATION_TAG = "documentation";

		private HtmlBuilder builder;
		private StringBuilder currentText = new StringBuilder();
		private boolean inDocumentation = false;

		public AnnotationContentHandler(HtmlBuilder builder) {
			this.builder = builder;
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (inDocumentation) {
				currentText.append(new String(ch, start, length));
			}
		}

		@Override
		public void endDocument() throws SAXException {
			// ignore
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (inDocumentation && currentText.length() > 0) {
				builder.beginTag("p");
				builder.appendEscaped(currentText.toString().trim());
				builder.endTag();
			}
			inDocumentation = false;
		}

		@Override
		public void endPrefixMapping(String prefix) throws SAXException {
			// ignore
		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
			if (inDocumentation) {
				currentText.append(new String(ch, start, length));
			}
		}

		@Override
		public void processingInstruction(String target, String data) throws SAXException {
			// ignore
		}

		@Override
		public void setDocumentLocator(Locator locator) {
			// ignore
		}

		@Override
		public void skippedEntity(String name) throws SAXException {
			// ignore
		}

		@Override
		public void startDocument() throws SAXException {
			// ignore
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			if (DOCUMENTATION_TAG.equals(localName) && XS_NS.equals(uri)) {
				inDocumentation = true;
			}
		}

		@Override
		public void startPrefixMapping(String prefix, String uri) throws SAXException {
			// ignore
		}

	}

}
