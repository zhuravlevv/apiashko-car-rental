package com.epam.brest.courses.frontend.panels.list;

import com.epam.brest.courses.frontend.panels.BusinessPresenter;
import com.epam.brest.courses.frontend.panels.HasBusinessPresenter;
import com.epam.brest.courses.frontend.panels.MenuPanel;
import com.epam.brest.courses.frontend.panels.PanelSwitcher;
import com.epam.brest.courses.frontend.panels.edit.EditContainer;
import com.epam.brest.courses.frontend.panels.edit.EditCustomer;
import com.epam.brest.courses.frontend.panels.edit.EditProduct;
import com.epam.brest.courses.frontend.services.Services;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class ListContentPanel extends JPanel implements HasBusinessPresenter {

	private static final long serialVersionUID = 8776244976111640079L;
	private JToolBar toolbar;
	PanelSwitcher panelSwitcher;
	ListDataModel dataModel;
	JTable table;

	public ListContentPanel(PanelSwitcher cardSwitcher, ListDataModel dataModel) {
		this.panelSwitcher = cardSwitcher;
		this.dataModel = dataModel;
		toolbar = new JToolBar();
		setLayout(new BorderLayout());
		String imgLocation = "close.png";
		URL imageURL = EditContainer.class.getResource(imgLocation);

		JButton button = new JButton();
		button.setActionCommand("close");
		button.setToolTipText("Close");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				goToHome();
			}
		});

		if (imageURL != null) {
			ImageIcon defaultIcon = new ImageIcon(imageURL, "Close");
			button.setIcon(defaultIcon);
		} else {
			button.setText("Close");
		}
		toolbar.add(button);
		add(toolbar, BorderLayout.PAGE_START);
		table = new JTable(dataModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting() && table.getRowCount() > 0 && table.getSelectedRow() > -1)
							goToRecord(table.getValueAt(table.getSelectedRow(),
									0).toString());
					}
				});
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		table.setFillsViewportHeight(true);
		add(scrollPane, BorderLayout.CENTER);
	}

	private void goToRecord(String code) {
		String editViewName = getEditViewName(dataModel.getObjectType());
		HasBusinessPresenter panelOfClass = panelSwitcher
				.getPanelOfClass(editViewName);
		Object retValue = Services.readRecordByCode(code,
				dataModel.getObjectType());
		panelSwitcher.switchTo(editViewName);
		panelOfClass.getBusinessPresenter().bindToGUI(retValue);
	}

	private String getEditViewName(int type) {
		if (type == Services.TYPE_ORDER)
			return EditCustomer.class.getName();
		if (type == Services.TYPE_CAR)
			return EditProduct.class.getName();
//		return EditSalesOrder.class.getName();
		return "";
	}

	private void goToHome() 
	{
		panelSwitcher.switchTo(MenuPanel.class.getName());
	}

	public BusinessPresenter getBusinessPresenter()
	{
		return dataModel;
	}
}
