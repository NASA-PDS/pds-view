package gov.nasa.pds.registry.ui.client;

import java.util.Collection;
import java.util.Iterator;

import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A special version of SerializableResponse<RowType> that supports direct
 * access to values rather than access to an iterator. As the list is
 * encapsulated in an instance of ViewProducts, and extension of ArrayList that
 * has a size separate from count representing objects in the registry, we can
 * get the total number of objects expected to page through.
 * 
 * @see gov.nasa.pds.registry.ui.client.ProductTableModel#requestRows()
 * @see gov.nasa.pds.registry.ui.shared.ViewProducts
 * 
 * @author jagander
 */
public class SerializableProductResponse<RowType extends IsSerializable>
		extends SerializableResponse<RowType> implements IsSerializable {
	/**
	 * The {@link Collection} of row values.
	 */
	private Collection<RowType> rowValues;

	/**
	 * Default constructor used for RPC.
	 */
	public SerializableProductResponse() {
		this(null);
	}

	/**
	 * Create a new {@link SerializableResponse}.
	 */
	public SerializableProductResponse(Collection<RowType> rowValues) {
		this.rowValues = rowValues;
	}

	/**
	 * An iterator on the values
	 */
	@Override
	public Iterator<RowType> getRowValues() {
		return this.rowValues.iterator();
	}

	/**
	 * Get the values directly.
	 * 
	 * NOTE: In this case the values should be castable to an instance of
	 * ViewProducts
	 */
	public Collection<RowType> getValues() {
		return this.rowValues;
	}
}
