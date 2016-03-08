package gov.nasa.arc.pds.lace.server.parse;

import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;
import gov.nasa.arc.pds.lace.shared.InsertionPoint.DisplayType;

import java.net.URI;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

/**
 * Implements a facility for reading an XML Schema and creating
 * model objects from types and elements in the schema.
 */
public class ModelAnalyzer {

	private Map<String, LabelItemType> knownTypes = new HashMap<String, LabelItemType>();
	private XSModel model;

	/**
	 * Creates a new analyzer with a set of URIs to the schema documents.
	 *
	 * @param schemaURIs an array of schema document locations
	 */
	public ModelAnalyzer(URI... schemaURIs) {
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

		XSImplementation impl =
		    (XSImplementation) registry.getDOMImplementation("XS-Loader");

		XSLoader schemaLoader = impl.createXSLoader(null);

		StringList uriList = new StringListImpl();
		for (URI uri : schemaURIs) {
			((StringListImpl) uriList).add(uri.toString());
		}
		model = schemaLoader.loadURIList(uriList);
	}

	/**
	 * Gets a model container for a given element.
	 *
	 * @param elementName the name of the element
	 * @param elementNamespace the namespace of the element
	 * @return a container with model contents, or null if the element cannot be found
	 */
	public Container getContainerForElement(String elementName, String elementNamespace) {
		XSElementDeclaration element = model.getElementDeclaration(elementName, elementNamespace);
		XSTypeDefinition typeDefinition = element.getTypeDefinition();

		List<LabelItem> initialContents = getTypeInitialContents((XSComplexTypeDefinition) typeDefinition);
		LabelItemType type = createLabelItemType(element, 1, 1, true, initialContents, typeDefinition);

		return createContainer(type, initialContents);
	}

	private void storeKnownType(XSElementDeclaration element, XSTypeDefinition typeDef, LabelItemType type) {
		if (shouldStoreType(typeDef)) {
			knownTypes.put(makeTypeKey(element, typeDef), type);
		}
	}

	private LabelItemType findKnownType(XSElementDeclaration element, XSTypeDefinition typeDef) {
		if (!shouldStoreType(typeDef)) {
			return null;
		} else {
			return knownTypes.get(makeTypeKey(element, typeDef));
		}
	}

	private boolean shouldStoreType(XSTypeDefinition typeDef) {
		return (typeDef.getName()!=null && typeDef.getNamespace()!=null);
	}

	private String makeTypeKey(XSElementDeclaration element, XSTypeDefinition typeDef) {
		return element.getNamespace()
			+ ":" + element.getName()
			+ ":" + typeDef.getNamespace()
			+ ":" + typeDef.getName();
	}

	/**
	 * Expands non-choice insertion points that indicate required elements.
	 *
	 * @param item the label item in which to expand the insertion points
	 */
	public void expandInsertionPoints(LabelItem item) {
		if (item instanceof Container) {
			expandInsertionPoints(((Container) item).getContents());
		}
	}

	/**
	 * Expand any insertion points in a list of label items. If an
	 * insertion point is found, insert any required elements that
	 * have not already been inserted. And recursively expand
	 * insertion points in any child items of a container.
	 *
	 * @param contents a list of label items
	 */
	private void expandInsertionPoints(List<LabelItem> contents) {
		ListIterator<LabelItem> it = contents.listIterator();

		while (it.hasNext()) {
			LabelItem item = it.next();

			// Check for a "plus" insert point or a container.
			if (item instanceof Container) {
				expandInsertionPoints(item);
			} else if ((item instanceof InsertionPoint)
					&& ((InsertionPoint) item).getDisplayType().equals(DisplayType.PLUS_BUTTON.getDisplayType())) {
				InsertionPoint insPoint = (InsertionPoint) item;

				// Check for required elements within the insertable range
				// that have not already been inserted.
				for (int i=insPoint.getInsertFirst(); i <= insPoint.getInsertLast(); ++i) {
					if (insPoint.getAlternatives().get(i).getMinOccurrences() > 0
							&& i > insPoint.getUsedBefore()
							&& i < insPoint.getUsedAfter()) {
						// A required element. Do an insertion.
						doInsert(it, insPoint, i);
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
	 * <p>The insertion and used indices of the old and new insertion
	 * point are updated to reflect the insertion.
	 *
	 * @param it a list iterator whose cursor is positioned just past the insertion point
	 * @param insPoint the insertion point at which to insert the required element
	 * @param alternativeIndex the alternative index to insert
	 */
	private void doInsert(ListIterator<LabelItem> it, InsertionPoint insPoint,
			int alternativeIndex) {

		// Create the new item.
		LabelItemType type = insPoint.getAlternatives().get(alternativeIndex);
		LabelItem newItem;
		if (!type.isComplex()) {
			newItem = createSimpleItem(type);
		} else {
			newItem = createContainer(type, type.getInitialContents());
		}

		// Clone the insertion point.
		InsertionPoint newInsPoint = (InsertionPoint) insPoint.copy();

		//TODO: Decide whether removing unavailable alternatives is correct.

		// Remove unavailable alternatives.
		int removeBefore = insPoint.getAlternatives().size() - alternativeIndex - 1;
		int removeAfter = alternativeIndex;
		int origSize = insPoint.getAlternatives().size();

		for (int i=0; i < removeBefore; ++i) {
			insPoint.getAlternatives().remove(insPoint.getAlternatives().size() - 1);
		}
		for (int i=0; i < removeAfter; ++i) {
			newInsPoint.getAlternatives().remove(0);
		}

		assert insPoint.getAlternatives().size() == alternativeIndex + 1;
		assert newInsPoint.getAlternatives().size() == origSize - alternativeIndex;

		// Adjust indices.
		insPoint.setInsertLast(alternativeIndex);
		insPoint.setUsedAfter(alternativeIndex);

		newInsPoint.setInsertFirst(0);
		newInsPoint.setInsertLast(newInsPoint.getInsertLast() - removeAfter);
		newInsPoint.setUsedBefore(0);
		newInsPoint.setUsedAfter(newInsPoint.getUsedAfter() - removeAfter);

		// Now insert the new item and insertion point, and update the
		// list cursor.
		it.add(newItem);
		it.add(newInsPoint);
		it.previous();

		// And, expand any insertion points in the newly inserted item.
		expandInsertionPoints(newItem);
	}

	/*
	 * Creates a LabelItemType object for the given element.
	 */
	private LabelItemType createLabelItemType(XSElementDeclaration element, int minOccurs, int maxOccurs,
			boolean isComplex, List<LabelItem> initialContents, XSTypeDefinition typeDefinition) {
		LabelItemType type = new LabelItemType();
		type.setElementName(element.getName());
		type.setElementNamespace(element.getNamespace());
		type.setMinOccurrences(minOccurs);
		type.setMaxOccurrences(maxOccurs);
		type.setComplex(isComplex);
		type.setInitialContents(initialContents);

		storeKnownType(element, typeDefinition, type);

		return type;
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

	/*
	 * Parses the term for the particle which can be an element declaration or a model group.
	 */
	private void parseParticle(XSParticle particle, List<LabelItem> labelItems) {
		if (particle == null) {
			// TODO: use logger to log errors.
			System.out.println("Particle is null.");
			return;
		}

		XSTerm term = particle.getTerm();
		if (term instanceof XSElementDeclaration) {
			parseElementDefinition((XSElementDeclaration) term, particle, labelItems);
		} else if (term instanceof XSModelGroup) {
			parseModelGroup((XSModelGroup) term, particle, labelItems);
		} else {
			// TODO: term is an instance of XSWildcard.
		}
	}

	/*
	 * If the element declaration is of type XSSimpleTypeDefinition, it's
	 * a simple named element type. Otherwise, it's a complex named type.
	 */
	private void parseElementDefinition(XSElementDeclaration element, XSParticle particle, List<LabelItem> labelItems) {
		XSTypeDefinition typeDefinition = element.getTypeDefinition();

		if (typeDefinition instanceof XSSimpleTypeDefinition) {
			parseSimpleType(element, (XSSimpleTypeDefinition) typeDefinition, particle, labelItems);
		} else {
			parseComplexType(element, (XSComplexTypeDefinition) typeDefinition, particle, labelItems);
		}
	}

	private void parseSimpleType(XSElementDeclaration element, XSSimpleTypeDefinition typeDefinition,
			XSParticle particle, List<LabelItem> labelItems) {

		LabelItemType type = createSimpleItemType(element, typeDefinition, particle);
		labelItems.add(createSimpleItem(type));
	}

	private void parseComplexType(XSElementDeclaration element, XSComplexTypeDefinition typeDefinition, XSParticle particle, List<LabelItem> labelItems) {

		LabelItemType type = findKnownType(element, typeDefinition);

		if (type == null) {
			// As of the 0300a schemas, the only element of the PDS4 schemas that
			// has an empty content model is Band_Bin_Set.
			//TODO: Make sure this handling of CONENTTYPE_EMPTY is correct, and also handle CONTENTTYPE_MIXED, perhaps by disallowing it.
			if (typeDefinition.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_EMPTY) {
				type = createLabelItemType(
						element,
						particle.getMinOccurs(),
						particle.getMaxOccurs(),
						true,
						new ArrayList<LabelItem>(),
						typeDefinition
				);
			} else if (typeDefinition.getParticle() == null) {
				type = createSimpleItemType(element, typeDefinition.getSimpleType(), particle);
			} else {
				type = createLabelItemType(
						element,
						particle.getMinOccurs(),
						particle.getMaxOccurs(),
						true,
						getTypeInitialContents(typeDefinition),
						typeDefinition
				);
			}
		}

		if (!type.isComplex()) {
			labelItems.add(createSimpleItem(type));
		} else if (particle.getMinOccurs() < 1 || particle.getMaxOccursUnbounded() || particle.getMaxOccurs() > 1) {
			labelItems.add(createInsertionPoint(Collections.singletonList(type), particle));
		} else {
			labelItems.add(createContainer(type, type.getInitialContents()));
		}
	}

	/*
	 * Creates a Container object for the given type and contents.
	 */
	private Container createContainer(LabelItemType type, List<LabelItem> contents) {
		Container container = new Container();
		container.setType(type);
		container.setContents(contents);
		return container;
	}

	private SimpleItem createSimpleItem(LabelItemType type) {
		SimpleItem item = new SimpleItem();
		item.setType(type);
		return item;
	}

	private InsertionPoint createInsertionPoint(List<LabelItemType> alternatives, XSParticle particle) {
		DisplayType displayType = DisplayType.PLUS_BUTTON;
		if (alternatives.size() > 1) {
			displayType = DisplayType.CHOICE;
		} else if (particle.getMinOccurs()==0) {
			displayType = DisplayType.OPTIONAL;
		}
		InsertionPoint insPoint = new InsertionPoint();
		insPoint.setAlternatives(alternatives);
		insPoint.setInsertFirst(0);
		insPoint.setInsertLast(alternatives.size() - 1);
		insPoint.setUsedBefore(-1);
		insPoint.setUsedAfter(alternatives.size());
		insPoint.setDisplayType(displayType.getDisplayType());
		return insPoint;
	}

	/*
	 * Creates a SimpleItem object for the given simple type element to
	 * the initialContents of the type currently being constructed.
	 */
	private LabelItemType createSimpleItemType(XSElementDeclaration element, XSSimpleTypeDefinition type, XSParticle particle) {
		return createLabelItemType(
				element,
				particle.getMinOccurs(),
				particle.getMaxOccurs(),
				false,
				null,
				type
		);
	}

	/*
	 * Parses the choice or sequence model group.
	 */
	private void parseModelGroup(XSModelGroup group, XSParticle particle, List<LabelItem> labelItems) {
		switch (group.getCompositor()) {
		case XSModelGroup.COMPOSITOR_ALL:
			// Not supported in version 1
			break;
		case XSModelGroup.COMPOSITOR_CHOICE:
			// Always an insertion point, no matter the multiplicity.
			addInsertionPointForChoice(group, labelItems, particle);
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

	/*
	 * Adds an InsertionPoint if the sequence is optional. Otherwise, parses the contents of
	 * the sequence and adds to the initialContents of the type currently being constructed.
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

	/*
	 * Adds an InsertionPoint. Parses the choice model group to create LabelItemType objects
	 * to form the alternatives list for the insertion point. May not need to support 1..1
	 * or 0..1 in version 1.
	 */
	private void addInsertionPointForChoice(XSModelGroup group, List<LabelItem> labelItems, XSParticle particle) {
		XSObjectList particles = group.getParticles();
		List<LabelItemType> alternatives = new ArrayList<LabelItemType>();

		for (int i = 0; i < particles.getLength(); i++) {
			XSParticle itemParticle = (XSParticle) particles.item(i);
			// Assume the term for each particle inside the choice
			// model group is an element declaration.
			XSElementDeclaration element = (XSElementDeclaration) itemParticle.getTerm();

			// TODO: remove this check?
			if (findKnownType(element, element.getTypeDefinition()) != null) {
				break;
			}

			LabelItemType type = parseChoiceElementDefinition(element, particle);
			alternatives.add(type);
		}

		labelItems.add(createInsertionPoint(alternatives, particle));
	}

	private LabelItemType parseChoiceElementDefinition(XSElementDeclaration element, XSParticle particle) {
		LabelItemType type = null;
		XSTypeDefinition typeDefinition = element.getTypeDefinition();

		// TODO: remove this check?
		if (findKnownType(element, typeDefinition) != null) {
			return findKnownType(element, typeDefinition);
		}

		if (typeDefinition instanceof XSSimpleTypeDefinition) {
			type = createLabelItemType(
					element,
					particle.getMinOccurs(),
					particle.getMaxOccurs(),
					false,
					null,
					typeDefinition
			);
		} else {
			type = createLabelItemType(
					element,
					particle.getMinOccurs(),
					particle.getMaxOccurs(),
					true,
					getTypeInitialContents((XSComplexTypeDefinition) typeDefinition),
					typeDefinition
			);
		}

		return type;
	}

	/*
	 * Parses the sequence model group to create a LabelItemType
	 * object to form the single element in the alternatives list.
	 */
	private void addInsertionPointForSequence(XSModelGroup group, List<LabelItem> labelItems) {
		// Not supported in version 1.
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
					InsertionPoint first = (InsertionPoint) curItem;
					InsertionPoint second = (InsertionPoint) nextItem;

					// For some reason we can't do simply:
					//   first.getAlternatives().addAll(second.getAlternatives());
					// It gives an UnsupportedOperationException. So we have
					// to add them to a new list.
					List<LabelItemType> newAlternatives = new ArrayList<LabelItemType>();
					newAlternatives.addAll(first.getAlternatives());
					for (LabelItemType type : second.getAlternatives()) {
						if (!newAlternatives.contains(type)) {
							newAlternatives.add(type);
						}
					}

					first.setAlternatives(newAlternatives);
					first.setInsertLast(first.getAlternatives().size() - 1);
					first.setUsedAfter(first.getAlternatives().size());

					it.remove();
				}
			}
		}
	}

	private boolean isMergableInsertionPoint(LabelItem item) {
		return (item instanceof InsertionPoint)
			&& ((InsertionPoint) item).getDisplayType().equals(DisplayType.PLUS_BUTTON.getDisplayType());
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

}
