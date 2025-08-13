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
	private final List<CropEntry> selectedCropEntries = new ArrayList<>();
	private final DefaultListModel<CropEntry> selectedCropsModel = new DefaultListModel<>();
	
	private JComboBox<String> patchTypeComboBox;
	private JComboBox<Crop> cropComboBox;
	private JList<CropEntry> selectedCropsList;
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

		selectedCropsList = new JList<>(selectedCropsModel);
		selectedCropsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedCropsList.setVisibleRowCount(6);
		selectedCropsList.setPreferredSize(new Dimension(200, 120));

		chanceLabel = new JLabel("0.000000%", SwingConstants.CENTER);
		chanceLabel.setFont(chanceLabel.getFont().deriveFont(Font.BOLD, 24f));
		
		fractionLabel = new JLabel("0", SwingConstants.CENTER);
		fractionLabel.setFont(fractionLabel.getFont().deriveFont(Font.BOLD, 24f));

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

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		
		JLabel cropsLabel = new JLabel("Selected crops:");
		cropsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		centerPanel.add(cropsLabel);
		
		JScrollPane scrollPane = new JScrollPane(selectedCropsList);
		scrollPane.setPreferredSize(new Dimension(200, 360));
		scrollPane.setMaximumSize(new Dimension(200, 360));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		centerPanel.add(scrollPane);

		JPanel resultPanel = new JPanel(new GridLayout(4, 1));
		resultPanel.add(new JLabel("Percentage:", SwingConstants.CENTER));
		resultPanel.add(chanceLabel);
		resultPanel.add(new JLabel("Fraction:", SwingConstants.CENTER));
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
			return;
		}
		
		List<Crop> matchingCrops = new ArrayList<>();
		for (Crop crop : availableCrops)
		{
			if (crop.getPatchType().equals(selectedPatchType))
			{
				matchingCrops.add(crop);
			}
		}
		
		for (Crop crop : matchingCrops)
		{
			cropComboBox.addItem(crop);
		}
		
		if (!matchingCrops.isEmpty())
		{
			cropComboBox.setSelectedItem(matchingCrops.get(0));
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

		List<Crop> expandedCrops = new ArrayList<>();
		for (CropEntry entry : selectedCropEntries)
		{
			for (int i = 0; i < entry.getQuantity(); i++)
			{
				expandedCrops.add(entry.getCrop());
			}
		}

		double chance = ChanceCalculator.calculateCumulativeChance(expandedCrops, farmingLevel);
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
				CropEntry existingEntry = null;
				for (CropEntry entry : selectedCropEntries)
				{
					if (entry.getCrop().getName().equals(selectedCrop.getName()))
					{
						existingEntry = entry;
						break;
					}
				}

				if (existingEntry != null)
				{
					existingEntry.incrementQuantity();
					int index = selectedCropsModel.indexOf(existingEntry);
					selectedCropsModel.setElementAt(existingEntry, index);
				}
				else
				{
					CropEntry newEntry = new CropEntry(selectedCrop, 1);
					selectedCropEntries.add(newEntry);
					selectedCropsModel.addElement(newEntry);
				}
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
				CropEntry entryToRemove = selectedCropsModel.getElementAt(selectedIndex);
				entryToRemove.decrementQuantity();
				
				if (entryToRemove.getQuantity() <= 0)
				{
					selectedCropEntries.remove(entryToRemove);
					selectedCropsModel.removeElementAt(selectedIndex);
				}
				else
				{
					selectedCropsModel.setElementAt(entryToRemove, selectedIndex);
				}
				updateChanceDisplay();
			}
		}
	}

	private class ClearAllListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{
			selectedCropEntries.clear();
			selectedCropsModel.clear();
			updateChanceDisplay();
		}
	}
}