package com.tanglerootpetchance;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CropLoader
{
	private static final Gson gson = new Gson();

	public static List<Crop> loadCrops()
	{
		List<Crop> crops = new ArrayList<>();
		
		try (InputStream inputStream = CropLoader.class.getResourceAsStream("/crops.json"))
		{
			if (inputStream == null)
			{
				throw new RuntimeException("Could not find crops.json resource");
			}

			JsonObject jsonObject = gson.fromJson(new InputStreamReader(inputStream), JsonObject.class);
			JsonArray cropsArray = jsonObject.getAsJsonArray("crops");

			for (JsonElement element : cropsArray)
			{
				JsonObject cropObject = element.getAsJsonObject();
				String name = cropObject.get("name").getAsString();
				int baseChance = cropObject.get("baseChance").getAsInt();
				String patchType = cropObject.get("patchType").getAsString();
				crops.add(new Crop(name, baseChance, patchType));
			}
		}
		catch (IOException exception)
		{
			throw new RuntimeException("Failed to load crops data", exception);
		}

		return crops;
	}
}