package com.tanglerootpetchance;

import com.google.inject.Provides;
import com.google.inject.Injector;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
	name = "Tangleroot Pet Chance",
	description = "Calculate cumulative Tangleroot pet chance for farm runs",
	tags = {"farming", "pet", "tangleroot", "calculator"}
)
public class TanglerootPetChancePlugin extends Plugin
{
	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private TanglerootPetChanceConfig config;

	@Inject
	private Injector injector;

	private TanglerootPanel panel;
	private NavigationButton navigationButton;

	@Override
	protected void startUp() throws Exception
	{
		panel = injector.getInstance(TanglerootPanel.class);
		panel.init();

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "tangleroot-icon.png");

		navigationButton = NavigationButton.builder()
			.tooltip("Tangleroot Pet Chance")
			.icon(icon)
			.priority(5)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navigationButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navigationButton);
	}

	@Provides
	TanglerootPetChanceConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TanglerootPetChanceConfig.class);
	}
}