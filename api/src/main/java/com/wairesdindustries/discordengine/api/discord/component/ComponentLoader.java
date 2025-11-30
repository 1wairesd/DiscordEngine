package com.wairesdindustries.discordengine.api.discord.component;

import java.util.Map;

import com.wairesdindustries.discordengine.api.config.Loadable;

public interface ComponentLoader extends Loadable {
    DiscordComponent getComponent(String reference);
    
    DiscordComponent getComponent(ComponentReference reference);
    
    Map<String, DiscordComponent> getAllComponents();
}
