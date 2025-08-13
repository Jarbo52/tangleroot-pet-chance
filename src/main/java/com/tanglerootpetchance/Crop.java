package com.tanglerootpetchance;

public class Crop
{
	private final String name;
	private final int baseChance;

	public Crop(String name, int baseChance)
	{
		this.name = name;
		this.baseChance = baseChance;
	}

	public String getName()
	{
		return name;
	}

	public int getBaseChance()
	{
		return baseChance;
	}

	@Override
	public String toString()
	{
		return name;
	}
}