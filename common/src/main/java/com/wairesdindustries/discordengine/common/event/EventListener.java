package com.wairesdindustries.discordengine.common.event;

import com.wairesdindustries.discordengine.api.event.Subscriber;
import com.wairesdindustries.discordengine.common.DiscordEngine;

public class EventListener implements Subscriber {

    private final DiscordEngine api;

    public EventListener(DiscordEngine api) {
        this.api = api;
    }

}