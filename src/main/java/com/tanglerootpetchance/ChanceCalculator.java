package com.tanglerootpetchance;

import java.util.List;

public class ChanceCalculator
{
	public static double calculateCumulativeChance(List<Crop> selectedCrops)
	{
		if (selectedCrops.isEmpty())
		{
			return 0.0;
		}

		double probabilityOfAllFailures = 1.0;
		
		for (Crop crop : selectedCrops)
		{
			double individualChance = 1.0 / crop.getBaseChance();
			double failureChance = 1.0 - individualChance;
			probabilityOfAllFailures *= failureChance;
		}

		return 1.0 - probabilityOfAllFailures;
	}

	public static String formatPercentage(double probability)
	{
		return String.format("%.6f%%", probability * 100);
	}

	public static String formatFraction(double probability)
	{
		if (probability == 0.0)
		{
			return "0";
		}
		
		double oneInX = 1.0 / probability;
		return String.format("1 in %.0f", oneInX);
	}
}