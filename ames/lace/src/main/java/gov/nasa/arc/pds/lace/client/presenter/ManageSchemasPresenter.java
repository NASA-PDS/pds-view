package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.view.ManageSchemasView;

import javax.inject.Inject;

import com.google.inject.ImplementedBy;

/**
 * Implements the presenter for a widget that displays a dialog box (popup).
 */
public class ManageSchemasPresenter extends Presenter<ManageSchemasPresenter.Display> {
	
	@ImplementedBy(ManageSchemasView.class)
	public interface Display extends Presenter.Display<ManageSchemasPresenter> {
		
		/**
		 * Displays the view.
		 */
		void display();
		
		/**
		 * Hides the view.
		 */
		void hide();
		
	}
		
	@Inject
	public ManageSchemasPresenter(Display view) {
		super(view);
		view.setPresenter(this);
	}
	
	/**
	 * Handles a request to close the view.
	 */
	public void onRequestClose() {
		getView().hide();
	}
	
	/**
	 * Displays the view.
	 */
	public void display() {
		getView().display();
	}
	
}
