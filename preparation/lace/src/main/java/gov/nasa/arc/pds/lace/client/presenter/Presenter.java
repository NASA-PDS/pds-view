package gov.nasa.arc.pds.lace.client.presenter;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Defines an interface that all presenters must implement.
 *
 * @param <D> the type for the display
 */
public class Presenter<D extends Presenter.Display<?>> implements IsWidget, HasVisibility {

	/**
	 * Defines an interface that all views must implement.
	 *
	 * @param <T> the presenter type
	 */
	public interface Display<T> extends IsWidget, HasVisibility {
				
		/**
		 * Sets the presenter instance for this view.
		 *
		 * @param presenter the presenter
		 */
		void setPresenter(T presenter);
	}
	
	private D view;
	
	/**
	 * Creates a new instance of the presenter.
	 *
	 * @param view the view to use for user interaction and display
	 */
	public Presenter(D view) {
		this.view = view;
	}

	protected D getView() {
		return view;
	}
	
	@Override
	public boolean isVisible() {
		return view.isVisible();
	}

	@Override
	public void setVisible(boolean visible) {
		view.setVisible(visible);		
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}
	
	
}
