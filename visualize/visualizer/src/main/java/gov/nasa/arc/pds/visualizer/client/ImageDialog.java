package gov.nasa.arc.pds.visualizer.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Image;

public class ImageDialog extends DialogBox  {
	public ImageDialog(Image img) {

		DockPanel dock = new DockPanel();
		Button dismissButton = new Button("Close");
		dismissButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ImageDialog.this.hide();
				
			}
		});	
		setText(img.getTitle());
		dock.add(img, DockPanel.NORTH);
		dock.add(dismissButton, DockPanel.SOUTH);
		add(dock);
	}
}

