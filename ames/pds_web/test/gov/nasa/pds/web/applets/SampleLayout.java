package gov.nasa.pds.web.applets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class SampleLayout extends JFrame {

	private JLabel volumeName;
	
	private JLabel problemCount;
	
	private JLabel message;
	
	private JButton validateButton;
	
	private JButton cancelButton;

	public static void main(String[] args) {
		(new SampleLayout()).initGUI();
	}

	private void initGUI() {
		setSize(400, 200);
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(Color.RED));
		createGUI(panel);
		getContentPane().add(panel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void init() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createGUI(SampleLayout.this);
			}
		});
	}
	
	private void createGUI(Container parent) {
		volumeName = new JLabel();
		problemCount = new JLabel("0");
    	message = new JLabel("hello");
    	
    	validateButton = new JButton("Validate Volume...");
    	validateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				message.setText("This is a very long status message that won't fit in the comopnent.");
			}
    	});
    	
    	cancelButton = new JButton("Cancel");
    	
/*		FormLayout layout = new FormLayout(
				"right:100px, 10px, 290px",
				""
		);
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);

		builder.append("Errors count:", problemCount);
		builder.nextLine();
		builder.append("Status:", message);
		builder.nextLine();

		builder.append("");
		builder.add(Forms.buttonBar(validateButton, cancelButton), CC.rcw(builder.getRow(), 1, 3, "bottom, center"));

		add(builder.getContainer());
    	
    	cancelButton.setEnabled(false);
*/

    	JPanel buttonPane = new JPanel();
    	GridBagBuilder buttonBuilder = new GridBagBuilder(buttonPane);
    	buttonBuilder.with(validateButton).add();
    	buttonBuilder.with(cancelButton).remainder().add();
    	
    	JPanel statusPane = new JPanel();
    	GridBagBuilder statusBuilder = new GridBagBuilder(statusPane);
    	statusBuilder.with(new JLabel("Volume:")).anchor(GridBagConstraints.EAST).add();
    	statusBuilder.with(volumeName).weightx(1.0).fill(GridBagConstraints.HORIZONTAL).remainder().add();
    	statusBuilder.with(new JLabel("Error count:")).anchor(GridBagConstraints.EAST).add();
    	statusBuilder.with(problemCount).weightx(1.0).fill(GridBagConstraints.HORIZONTAL).remainder().add();
    	statusBuilder.with(new JLabel("Status:")).anchor(GridBagConstraints.NORTHEAST).weighty(1.0).fill(GridBagConstraints.VERTICAL).add();
    	statusBuilder.with(message).anchor(GridBagConstraints.NORTHWEST).weightx(1.0).weighty(1.0).fill(GridBagConstraints.BOTH).add();
    	
    	GridBagBuilder mainBuilder = new GridBagBuilder(parent);
    	mainBuilder.with(buttonPane).anchor(GridBagConstraints.WEST).weightx(1.0).fill(GridBagConstraints.HORIZONTAL).remainder().add();
    	mainBuilder.with(statusPane).anchor(GridBagConstraints.NORTHWEST).weightx(1.0).weighty(1.0).fill(GridBagConstraints.BOTH).add();
	}
	
	private static class GridBagPlacer {
		
		private GridBagConstraints constraints = new GridBagConstraints();

		private Container container;
		
		private GridBagLayout layout;
		
		private Component component;
		
		public GridBagPlacer(Container container, GridBagLayout layout, Component component) {
			this.container = container;
			this.layout = layout;
			this.component = component;
		}
		
		public GridBagPlacer insets(int top, int right, int bottom, int left) {
			constraints.insets = new Insets(top, left, bottom, right);
			return this;
		}
		
		public GridBagPlacer relative() {
			constraints.gridwidth = GridBagConstraints.RELATIVE;
			return this;
		}
		
		public GridBagPlacer remainder() {
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			return this;
		}
		
		public GridBagPlacer weightx(double weight) {
			constraints.weightx = weight;
			return this;
		}
		
		public GridBagPlacer weighty(double weight) {
			constraints.weighty = weight;
			return this;
		}
		
		public GridBagPlacer width(int columns) {
			constraints.gridwidth = columns;
			return this;
		}
		
		public GridBagPlacer height(int rows) {
			constraints.gridheight = rows;
			return this;
		}
		
		public GridBagPlacer anchor(int anchor) {
			constraints.anchor = anchor;
			return this;
		}
		
		public GridBagPlacer fill(int fill) {
			constraints.fill = fill;
			return this;
		}
		
		public GridBagPlacer preferredWidth(int width) {
			Dimension preferredSize = component.getPreferredSize();
			preferredSize.width = width;
			component.setPreferredSize(preferredSize);
			return this;
		}
		
		public void add() {
			container.add(component, constraints);
		}
		
	}
	
	private static class GridBagBuilder {
		
		private Container container;
		
		private GridBagLayout layout;
		
		public GridBagBuilder(Container container) {
			this.container = container;
			layout = new GridBagLayout();
			container.setLayout(layout);
		}
		
		public GridBagPlacer with(Component component) {
			return new GridBagPlacer(container, layout, component);
		}
		
	}

}
