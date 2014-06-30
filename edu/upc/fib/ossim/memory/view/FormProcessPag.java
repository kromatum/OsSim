package edu.upc.fib.ossim.memory.view;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneLayout;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableColumnModel;

import edu.upc.fib.ossim.memory.MemoryPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.FormTemplate;
import edu.upc.fib.ossim.utils.AppTableModel;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.Translation;

/**
 * Process creation and update form in pagination memory management (non
 * contiguous). A table paints block specific information representing process
 * pages: page number, and initial load state into memory.<br/>
 * When updating a process, its pages are initialized with process values
 * 
 * @author Alex Macia
 * 
 * @see FormTemplate
 * @see FormProcess
 */
public class FormProcessPag extends FormProcess {
	private static final long serialVersionUID = 1L;
	private static final int TABLE_WIDTH = 100;
	private static final int TABLE_HEIGHT = 100;

	private JLabel lblocks;
	private AppTableModel tablemodel;
	private String blockTitle;

	private JTextField textField;
	protected JSpinner quantum;

	/**
	 * Constructs a form process (pagination)
	 * 
	 * @param presenter
	 *            event manager
	 * @param title
	 *            form title
	 * @param help
	 *            help icon
	 * @param values
	 *            creating a new process: pid, updating an existing process:
	 *            pid, name, size, duration and color
	 * @param blockTitle
	 *            pages information title
	 * 
	 */
	public FormProcessPag(Presenter presenter, String title, JLabel help,
			Vector<Object> values, String blockTitle) {
		super(presenter, title, help, values);
		this.blockTitle = blockTitle;

	}

	/**
	 * Creates and initialize pages table
	 * 
	 * @param values
	 *            value with index 5 contains pages information data
	 *            <code>Vector<Vector<Object>></code>
	 */
	@SuppressWarnings("unchecked")
	public void initBlocks(Vector<Object> values) {

		duration.setEnabled(false);
		size.addChangeListener(presenter); // Update page table

		lblocks = new JLabel(blockTitle);
		lblocks.setAlignmentX(JLabel.LEFT);
		pn.add(lblocks);

		Vector<Object> header = ((MemoryPresenter) presenter)
				.getFormTableHeader();
		Vector<Vector<Object>> data = null;

		if (values.size() > 1)
			data = (Vector<Vector<Object>>) values.get(5);
		else
			data = ((MemoryPresenter) presenter).getFormTableInitData();

		tablemodel = new AppTableModel(data, header, true);
		JTable table = new JTable(tablemodel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Hide column size
		DefaultTableColumnModel modelColumna = (DefaultTableColumnModel) table
				.getColumnModel();
		modelColumna.removeColumn(table.getColumnModel().getColumn(1));

		JScrollPane scroll = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		scroll.setLayout(new ScrollPaneLayout());
		scroll.setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));

		pn.add(scroll);
	}
	/**
	 * Creates and initialize pages' orders, 
	 * with ";' distinguishes every quantum and "," distinguishes every page
	 * 
	 * @param values
	 *            value with index 6 contains pages orders data
	 *            <code>Vector<Vector<Object>></code>
	 */
	public void initPageOrder(Vector<Object> values) {

		JPanel pageOrder = new JPanel();
		Object data = null;
		if (values.size() > 1)
			data = (Object) values.get(6);
		else
			data = (String) "0,0;0,0,0;0;";
		JLabel label = new JLabel("PageOrder");
		textField = new JTextField((String) data);
		textField.addFocusListener(presenter);
		pageOrder.add(label);
		pageOrder.add(textField);
		pn.add(pageOrder);
	}

	/**
	 * Creates and initialize number of quantums, maximum value 10
	 * 
	 * @param values
	 *            value with index 7 contains pages orders data
	 *            <code>Vector<Vector<Object>></code>
	 */
	public void initQuantum(Vector<Object> values) {
		grid.add(new JLabel("quantum"));
		SpinnerModel spmodel;
		if (values.size() > 1)
			spmodel = new SpinnerNumberModel(new Integer(values.get(7)
					.toString()).intValue(), 1, 10, 1);
		else
			spmodel = new SpinnerNumberModel(3, 1, 10, 1);
		quantum = new JSpinner(spmodel);
		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) quantum
				.getEditor();
		editor.getTextField().addFocusListener(presenter);
		quantum.setName("quantum");
		grid.add(quantum);
		Functions.getInstance().makeCompactGrid(grid, 6, 2, 6, 6, 6, 6);
		pn.add(grid);

	}

	/**
	 * Updates pages table rows number.
	 * 
	 * @param pages
	 *            total pages
	 */
	public void updatePageTable(int pages) {
		if (pages != tablemodel.getRowCount()) {
			if (pages > tablemodel.getRowCount()) {
				Vector<Object> page;
				for (int i = tablemodel.getRowCount(); i < pages; i++) {
					page = new Vector<Object>();
					page.add(i);
					page.add("");
					page.add(true);
					tablemodel.addRow(page);
				}
			} else {
				for (int i = tablemodel.getRowCount() - 1; i >= pages; i--) {
					tablemodel.removeRow(i);
				}
			}
		}
	}

	/**
	 * No concrete block validation is needed 
	 * 
	 * @return true
	 */
	public boolean validateFieldsBlock() {
		return validateFieldsOrder();
	}

	/**
	 * Needs input fields validation for quantum and pages' orders
	 * 
	 */
	public boolean validateFieldsOrder() {
		String orders = textField.getText();
		if ("".equals(orders) || orders == null) {
			JOptionPane.showMessageDialog(this.getParent(), Translation
					.getInstance().getError("all_10"), "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		int pageNumbers = tablemodel.getRowCount();

		for (int i = 0; i < orders.length(); i++) {
			if (orders.charAt(i) != ',' && orders.charAt(i) != ';'
					&& (orders.charAt(i) < '0' || orders.charAt(i) > '9')) {
				JOptionPane.showMessageDialog(this.getParent(), Translation
						.getInstance().getError("all_13"), "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

		String[] s = orders.split(";");
		if (s.length != ((Integer) quantum.getValue()).intValue()) {
			JOptionPane.showMessageDialog(this.getParent(), Translation
					.getInstance().getError("all_11"), "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		for (int i = 0; i < s.length; i++) {
			String[] order = s[i].split(",");
			for (int j = 0; j < order.length; j++) {
				if (Integer.parseInt(order[j]) < 0
						|| Integer.parseInt(order[j]) >= pageNumbers) {
					JOptionPane.showMessageDialog(this.getParent(), Translation
							.getInstance().getError("all_12"), "Error",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}
		return true;

	}

	public Vector<Object> getSpecificData() {
		Vector<Object> data = super.getSpecificData();
		data.add(getOrderListData());
		data.add(getQuantumData());
		return data;
	}

	/**
	 * Returns a table containing pages data
	 * 
	 * @return form pages data
	 */
	@SuppressWarnings("unchecked")
	public Vector<Vector<Object>> getComponentsData() {
		// Program blocks data.
		return tablemodel.getDataVector();
	}

	/**
	 * Returns a string containing pages orders
	 */
	public Object getOrderListData() {
		return textField.getText();
	}
	/**
	 * Returns the quantum number
	 */
	public Object getQuantumData() {
		return quantum.getValue();
	}
}