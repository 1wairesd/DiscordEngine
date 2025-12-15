package com.wairesdindustries.discordengine.common.discord.config;

public record BotConfiguration(
    String token,
    ActivityConfig activity
) {}

record ActivityConfig(
    String text,
    String type,
    String url
) {}
