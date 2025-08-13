package com.tanglerootpetchance;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TanglerootPetChancePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(TanglerootPetChancePlugin.class);
		RuneLite.main(args);
	}
}