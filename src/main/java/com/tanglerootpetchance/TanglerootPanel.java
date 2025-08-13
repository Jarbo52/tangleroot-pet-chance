package com.tanglerootpetchance;

import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class TanglerootPanel extends PluginPanel
{
	private final List<Crop> availableCrops = new ArrayList<>();
	private final List<Crop> selectedCrops = new ArrayList<>();
	private final DefaultListModel<Crop> selectedCropsModel = new DefaultListModel<>();
	
	private JComboBox<Crop> cropComboBox;
	private JList<Crop> selectedCropsList;
	private JLabel chanceLabel;
	private JLabel fractionLabel;
	private JButton addButton;
	private JButton removeButton;
	private JButton clearButton;

	public TanglerootPanel()
	{
		super(false);
	}

	public void init()
	{
		try
		{
			availableCrops.addAll(CropLoader.loadCrops());
			initializeComponents();
			layoutComponents();
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			removeAll();
			add(new PluginErrorPanel());
			revalidate();
		}
	}

	private void initializeComponents()
	{
		cropComboBox = new JComboBox<>();
		for (Crop crop : availableCrops)
		{
			cropComboBox.addItem(crop);
		}

		selectedCropsList = new JList<>(selectedCropsModel);
		selectedCropsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		chanceLabel = new JLabel("0.000000%");
		fractionLabel = new JLabel("0");

		addButton = new JButton("Add Crop");
		addButton.addActionListener(new AddCropListener());

		removeButton = new JButton("Remove Selected");
		removeButton.addActionListener(new RemoveCropListener());

		clearButton = new JButton("Clear All");
		clearButton.addActionListener(new ClearAllListener());
	}

	private void layoutComponents()
	{
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(new JLabel("Select crops for farm run:"), BorderLayout.NORTH);
		topPanel.add(cropComboBox, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		buttonPanel.add(clearButton);
		
		topPanel.add(buttonPanel, BorderLayout.SOUTH);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(new JLabel("Selected crops:"), BorderLayout.NORTH);
		centerPanel.add(new JScrollPane(selectedCropsList), BorderLayout.CENTER);

		JPanel resultPanel = new JPanel(new GridLayout(4, 1));
		resultPanel.setBorder(BorderFactory.createTitledBorder("Tangleroot Chance"));
		resultPanel.add(new JLabel("Percentage:"));
		resultPanel.add(chanceLabel);
		resultPanel.add(new JLabel("Fraction:"));
		resultPanel.add(fractionLabel);

		add(topPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(resultPanel, BorderLayout.SOUTH);
	}

	private void updateChanceDisplay()
	{
		double chance = ChanceCalculator.calculateCumulativeChance(selectedCrops);
		chanceLabel.setText(ChanceCalculator.formatPercentage(chance));
		fractionLabel.setText(ChanceCalculator.formatFraction(chance));
	}

	private class AddCropListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{
			Crop selectedCrop = (Crop) cropComboBox.getSelectedItem();
			if (selectedCrop != null)
			{
				selectedCrops.add(selectedCrop);
				selectedCropsModel.addElement(selectedCrop);
				updateChanceDisplay();
			}
		}
	}

	private class RemoveCropListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{
			int selectedIndex = selectedCropsList.getSelectedIndex();
			if (selectedIndex != -1)
			{
				Crop cropToRemove = selectedCropsModel.getElementAt(selectedIndex);
				selectedCrops.remove(cropToRemove);
				selectedCropsModel.removeElementAt(selectedIndex);
				updateChanceDisplay();
			}
		}
	}

	private class ClearAllListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{
			selectedCrops.clear();
			selectedCropsModel.clear();
			updateChanceDisplay();
		}
	}
}