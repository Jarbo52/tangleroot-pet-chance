package com.tanglerootpetchance;

public class Crop
{
	private final String name;
	private final int baseChance;
	private final String patchType;

	public Crop(String name, int baseChance, String patchType)
	{
		this.name = name;
		this.baseChance = baseChance;
		this.patchType = patchType;
	}

	public String getName()
	{
		return name;
	}

	public int getBaseChance()
	{
		return baseChance;
	}

	public String getPatchType()
	{
		return patchType;
	}

	@Override
	public String toString()
	{
		return name;
	}
}