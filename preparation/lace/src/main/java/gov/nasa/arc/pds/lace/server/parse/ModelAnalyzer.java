package gov.nasa.arc.pds.lace.server.parse;

import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertOption;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.InsertionPoint.DisplayType;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;

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
import org.apache.xerces.xs.XSWildcard;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

/**
 * Implements a facility for reading an XML Schema and creating
 * model objects from types and elements in the schema.
 */
public class ModelAnalyzer {

	private final static String PLUS_DISPLAY_TYPE = DisplayType.PLUS_BUTTON.getDisplayType();
	
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

		return createContainer(type);
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
	 * Expands any insertion points in a list of label items. If a
	 * required insertion point is found, insert its only alternative.
	 * And recursively expand insertion points in any child items of a container.
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
		InsertionPoint newInsPoint = (InsertionPoint) insPoint.copy(false);
		
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
	 * @param insPoint the insertion point at which to insert a required element
	 * @param insertOption the InsertOption instance that holds the item which is to be inserted
	 * @param type the label item type to insert
	 * @return
	 */
	public List<LabelItem> doInsert(InsertionPoint insPoint, int alternativeIndex, int typeIndex) {
		int index;
		InsertionPoint insPointToSplit;		
		List<LabelItem> items = new ArrayList<LabelItem>();
		List<InsertOption> alternatives = insPoint.getAlternatives();
		assert alternatives.size() > 0 && alternatives.size() <= 2;

		// Increase the "used" counter by 1.
		InsertOption alternative = alternatives.get(alternativeIndex);
		alternative.setUsedOccurrences(alternative.getUsedOccurrences() + 1);
		
		// Create the new element and link it to the insert option.
		LabelElement newElement = createLabelElement(alternative.getTypes().get(typeIndex));
		newElement.setInsertOption(alternative);
		
		if (insPoint.getDisplayType().equals(PLUS_DISPLAY_TYPE) && alternatives.size() > 1) {
			// Need to merge after splitting and inserting.
			InsertionPoint insPoint1 = (InsertionPoint) insPoint.copy(false);		
			InsertionPoint insPoint2 = (InsertionPoint) insPoint.copy(false);
			insPoint1.getAlternatives().remove(1);
			insPoint2.getAlternatives().remove(0);
			
			items.add(insPoint1);
			items.add(insPoint2);

			if (insPoint1.getAlternatives().contains(alternative)) {
				insPointToSplit = insPoint1;
			} else {
				insPointToSplit = insPoint2;
			}
						
			index = items.indexOf(insPointToSplit);
			items.remove(index);
		} else {
			// Change the display type to "plus" button if it's not already of that type.
			if (!insPoint.getDisplayType().equals(PLUS_DISPLAY_TYPE)) {
				insPoint.setDisplayType(PLUS_DISPLAY_TYPE);
			}
			insPointToSplit = insPoint;
			index = 0;
		}

		// Split the existing insertion point.
		InsertionPoint first = (InsertionPoint) insPointToSplit.copy(false);		
		InsertionPoint second = (InsertionPoint) insPointToSplit.copy(false);
		
		// Insert the new element and insertion points.			
		items.add(index++, first);
		items.add(index++, newElement);
		items.add(index, second);
					
		// And, expand any insertion points in the newly inserted element.
		expandInsertionPoints(newElement);

		return items;
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
	private LabelItemType createLabelItemType(XSElementDeclaration element, int minOccurs, int maxOccurs,
			boolean isComplex, List<LabelItem> initialContents, XSTypeDefinition typeDefinition) {
		LabelItemType type = new LabelItemType();
		type.setElementName(element.getName());
		type.setElementNamespace(element.getNamespace());
		type.setMinOccurrences(minOccurs);
		type.setMaxOccurrences(maxOccurs);
		type.setComplex(isComplex);
		type.setInitialContents(initialContents);

		storeKnownType(element, typeDefinition, minOccurs, maxOccurs, type);

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

	/**
	 * Parses the term for the particle which can be an element declaration or a model group.
	 * 
	 * @param particle
	 * @param labelItems	
	 */
	private void parseParticle(XSParticle particle, List<LabelItem> labelItems) {
		if (particle == null) {
			System.out.println("Particle is null.");
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

	/**
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

	private Container createContainer(LabelItemType type) {
		Container container = new Container();
		container.setType(type);
		setInitialContents(container);
		return container;
	}

	private void setInitialContents(Container container) {
		container.setContents(new ArrayList<LabelItem>());

		for (LabelItem item : container.getType().getInitialContents()) {
			LabelItem newItem = item.copy();

			if (newItem instanceof Container) {
				setInitialContents((Container) newItem);
			}
			container.getContents().add(newItem);
		}
	}
	
	private SimpleItem createSimpleItem(LabelItemType type) {
		SimpleItem item = new SimpleItem();
		item.setType(type);
		// TODO: should we set the value to "" here?
		return item;
	}
	
	private InsertionPoint createInsertionPoint(List<LabelItemType> types, XSParticle particle) {
		int minOccurs = particle.getMinOccurs();
		DisplayType	displayType = DisplayType.PLUS_BUTTON;
		
		if (types.size() > 1) {
			displayType = DisplayType.CHOICE;
		} else if (minOccurs == 0) {
			displayType = DisplayType.OPTIONAL;
		} else if (minOccurs > 0) {
			displayType = DisplayType.REQUIRED;
		}
				
		List<InsertOption> alternatives = new ArrayList<InsertOption>();
		alternatives.add(createInsertOption(types, minOccurs, particle.getMaxOccurs()));
		return createInsertionPoint(alternatives, displayType);
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
					typeDefinition
			);			
			type.setInitialContents(getTypeInitialContents((XSComplexTypeDefinition) typeDefinition));
		}

		return type;
	}

	private void addInsertionPointForSequence(XSModelGroup group, List<LabelItem> labelItems) {
		// Not supported in version 1.
	}
	
	private void parseWildcard(XSWildcard group, XSParticle particle, List<LabelItem> labelItems) {
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
					
					// For some reason we can't do simply:
					//   first.getAlternatives().addAll(second.getAlternatives());
					// It gives an UnsupportedOperationException. So we have
					// to add them to a new list.
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
					it.set(((InsertionPoint) curItem).copy(false));
					it.previous();
					it.previous();
					it.remove();
					curItem = it.next();
				}			
			}
		}
	}

	private boolean isMergableInsertionPoint(LabelItem item) {
		return (item instanceof InsertionPoint)
			&& ((InsertionPoint) item).getDisplayType().equals(PLUS_DISPLAY_TYPE);
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
