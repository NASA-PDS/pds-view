package gov.nasa.pds.search.core.registry.objects;

import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.Slot;

/**
 * Implementation of Decorator Pattern to provide {@link ExtrinsicObject}
 * functions and methods down stream
 * 
 * @author jpadams
 *
 */
abstract class ExtrinsicObjectDecorator extends ExtrinsicObject {

	private static final long serialVersionUID = 2346145994576653308L;

    protected ExtrinsicObject decoratedExtrinsic; // the Window being decorated
    
    public ExtrinsicObjectDecorator (ExtrinsicObject decoratedExtrinsic) {
        this.decoratedExtrinsic = decoratedExtrinsic;
    }
    
    @Override
	public String getLid() {
		return this.decoratedExtrinsic.getLid();
	}
    
    @Override
	public String getName() {
		return this.decoratedExtrinsic.getName();
	}
	
    @Override
	public Slot getSlot(String slotName) {
		return this.decoratedExtrinsic.getSlot(slotName);
	}
    
    @Override
	public String getObjectType() {
		return this.decoratedExtrinsic.getObjectType();
	}
}
