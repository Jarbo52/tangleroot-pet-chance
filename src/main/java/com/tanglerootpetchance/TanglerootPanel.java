package com.tanglerootpetchance;

import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

public class TanglerootPanel extends PluginPanel
{
	private final List<Crop> availableCrops = new ArrayList<>();
	private final List<Crop> selectedCrops = new ArrayList<>();
	private final DefaultListModel<Crop> selectedCropsModel = new DefaultListModel<>();
	
	private JComboBox<String> patchTypeComboBox;
	private JComboBox<Crop> cropComboBox;
	private JList<Crop> selectedCropsList;
	private JLabel chanceLabel;
	private JLabel fractionLabel;
	private JButton addButton;
	private JButton removeButton;
	private JButton clearButton;
	private JTextField farmingLevelField;

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
		Set<String> patchTypes = new LinkedHashSet<>();
		for (Crop crop : availableCrops)
		{
			patchTypes.add(crop.getPatchType());
		}
		
		patchTypeComboBox = new JComboBox<>();
		patchTypeComboBox.addItem("Select patch type...");
		for (String patchType : patchTypes)
		{
			patchTypeComboBox.addItem(patchType);
		}
		patchTypeComboBox.addActionListener(e -> updateCropDropdown());
		
		cropComboBox = new JComboBox<>();
		cropComboBox.addItem(null);

		selectedCropsList = new JList<>(selectedCropsModel);
		selectedCropsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		chanceLabel = new JLabel("0.000000%");
		fractionLabel = new JLabel("0");

		farmingLevelField = new JTextField("99", 5);
		farmingLevelField.addActionListener(e -> updateChanceDisplay());
		farmingLevelField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent event)
			{
				updateChanceDisplay();
			}

			@Override
			public void removeUpdate(DocumentEvent event)
			{
				updateChanceDisplay();
			}

			@Override
			public void changedUpdate(DocumentEvent event)
			{
				updateChanceDisplay();
			}
		});

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
		
		JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		inputPanel.add(new JLabel("Farming Level:"));
		inputPanel.add(farmingLevelField);
		
		JPanel cropPanel = new JPanel(new BorderLayout());
		
		JPanel patchTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		patchTypePanel.add(new JLabel("Patch Type:"));
		patchTypePanel.add(patchTypeComboBox);
		
		JPanel cropSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cropSelectionPanel.add(new JLabel("Crop:"));
		cropSelectionPanel.add(cropComboBox);
		
		cropPanel.add(patchTypePanel, BorderLayout.NORTH);
		cropPanel.add(cropSelectionPanel, BorderLayout.CENTER);
		
		topPanel.add(inputPanel, BorderLayout.NORTH);
		topPanel.add(cropPanel, BorderLayout.CENTER);
		
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

	private void updateCropDropdown()
	{
		cropComboBox.removeAllItems();
		
		String selectedPatchType = (String) patchTypeComboBox.getSelectedItem();
		if (selectedPatchType == null || selectedPatchType.equals("Select patch type..."))
		{
			cropComboBox.addItem(null);
			return;
		}
		
		cropComboBox.addItem(null);
		for (Crop crop : availableCrops)
		{
			if (crop.getPatchType().equals(selectedPatchType))
			{
				cropComboBox.addItem(crop);
			}
		}
	}

	private void updateChanceDisplay()
	{
		int farmingLevel;
		try
		{
			farmingLevel = Integer.parseInt(farmingLevelField.getText());
			if (farmingLevel < 1)
			{
				farmingLevel = 1;
			}
			if (farmingLevel > 99)
			{
				farmingLevel = 99;
			}
		}
		catch (NumberFormatException exception)
		{
			farmingLevel = 99;
		}

		double chance = ChanceCalculator.calculateCumulativeChance(selectedCrops, farmingLevel);
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