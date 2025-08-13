package com.tanglerootpetchance;

public class CropEntry
{
	private final Crop crop;
	private int quantity;

	public CropEntry(Crop crop, int quantity)
	{
		this.crop = crop;
		this.quantity = quantity;
	}

	public Crop getCrop()
	{
		return crop;
	}

	public int getQuantity()
	{
		return quantity;
	}

	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}

	public void incrementQuantity()
	{
		this.quantity++;
	}

	public void decrementQuantity()
	{
		this.quantity--;
	}

	@Override
	public String toString()
	{
		if (quantity == 1)
		{
			return crop.getName();
		}
		else
		{
			return crop.getName() + " (" + quantity + ")";
		}
	}

	@Override
	public boolean equals(Object object)
	{
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		CropEntry cropEntry = (CropEntry) object;
		return crop.getName().equals(cropEntry.crop.getName());
	}

	@Override
	public int hashCode()
	{
		return crop.getName().hashCode();
	}
}