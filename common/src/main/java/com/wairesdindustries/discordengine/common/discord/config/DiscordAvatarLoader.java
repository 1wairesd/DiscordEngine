package com.wairesdindustries.discordengine.common.discord.config;

import com.wairesdindustries.discordengine.common.DiscordEngine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class DiscordAvatarLoader {

    private static final String AVATAR_FOLDER = "discord/bot/avatar";
    private final DiscordEngine api;

    public DiscordAvatarLoader(DiscordEngine api) {
        this.api = api;
    }

    public File getAvatarFile(String avatarFileName) throws IOException {
        if (avatarFileName == null || avatarFileName.isBlank()) {
            avatarFileName = "avatar-discordengine-nofon.png";
        }

        File avatarDir = new File(api.getPlatform().getDataFolder(), AVATAR_FOLDER);
        if (!avatarDir.exists()) avatarDir.mkdirs();

        File avatar = new File(avatarDir, avatarFileName);

        if (!avatar.exists()) {
            try (InputStream is = api.getPlatform().getResource(AVATAR_FOLDER + "/" + avatarFileName)) {
                if (is == null) {
                    api.getPlatform().getLogger().warning("Avatar resource not found: " + avatarFileName);
                    return avatar;
                }
                Files.copy(is, avatar.toPath());
            }
        }

        return avatar;
    }
}
