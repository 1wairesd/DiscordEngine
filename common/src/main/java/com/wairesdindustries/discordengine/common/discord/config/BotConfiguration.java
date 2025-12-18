package com.wairesdindustries.discordengine.common.discord.config;

import java.util.List;

public record BotConfiguration(
    String token,
    ActivityConfig activity,
    SourcesConfig sources
) {}

record ActivityConfig(
    String text,
    String type,
    String url
) {}

record SourcesConfig(
    CommandsSource commands,
    AvatarSource avatar,
    LangSource lang
) {}

record CommandsSource(
    String mode,
    List<String> local,
    List<String> global
) {}

record AvatarSource(
    String mode,
    String file
) {}

record LangSource(
    String mode,
    String file
) {}
