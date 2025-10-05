package com.wairesdindustries.discordengine.api.platform;

import com.wairesdindustries.discordengine.api.DEAPI;
import com.wairesdindustries.discordengine.api.addon.Addon;
import com.wairesdindustries.discordengine.api.tools.DETools;

import java.util.logging.Logger;

public interface Platform extends Addon {

    String getName();

    String getIdentifier();

    String getVersion();

    DETools getTools();

	DEAPI getAPI();

	Logger getLogger();
}