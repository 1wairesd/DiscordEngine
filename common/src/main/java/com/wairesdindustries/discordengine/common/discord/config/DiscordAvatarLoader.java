package com.wairesdindustries.discordengine.common.discord.config;

import com.wairesdindustries.discordengine.common.DiscordEngine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class DiscordAvatarLoader {

    private static final String BOT_AVATAR_FOLDER = "discord/bot/avatar";
    private static final String GLOBAL_AVATAR_FOLDER = "discord/global/avatar";
    private final DiscordEngine api;

    public DiscordAvatarLoader(DiscordEngine api) {
        this.api = api;
    }

    public File getAvatarFile(String avatarFileName, String mode) throws IOException {
        if (avatarFileName == null || avatarFileName.isBlank()) {
            avatarFileName = "avatar-discordengine-nofon.png";
        }

        String avatarFolderPath;
        if ("global".equalsIgnoreCase(mode)) {
            avatarFolderPath = GLOBAL_AVATAR_FOLDER;
        } else {
            avatarFolderPath = BOT_AVATAR_FOLDER;
        }

        File avatarDir = new File(api.getPlatform().getDataFolder(), avatarFolderPath);
        if (!avatarDir.exists()) avatarDir.mkdirs();

        File avatar = new File(avatarDir, avatarFileName);

        if (!avatar.exists()) {
            if (!"global".equalsIgnoreCase(mode)) {
                File globalAvatarDir = new File(api.getPlatform().getDataFolder(), GLOBAL_AVATAR_FOLDER);
                File globalAvatar = new File(globalAvatarDir, avatarFileName);
                
                if (globalAvatar.exists()) {
                    Files.copy(globalAvatar.toPath(), avatar.toPath());
                    api.getPlatform().getLogger().info("Copied avatar from global folder: " + globalAvatar.getAbsolutePath());
                } else {
                    try (InputStream is = api.getPlatform().getResource(GLOBAL_AVATAR_FOLDER + "/" + avatarFileName)) {
                        if (is != null) {
                            Files.copy(is, avatar.toPath());
                            api.getPlatform().getLogger().info("Copied avatar from resource: " + GLOBAL_AVATAR_FOLDER + "/" + avatarFileName);
                        } else {
                            api.getPlatform().getLogger().warning("Avatar resource not found: " + avatarFileName);
                        }
                    }
                }
            } else {
                try (InputStream is = api.getPlatform().getResource(GLOBAL_AVATAR_FOLDER + "/" + avatarFileName)) {
                    if (is != null) {
                        Files.copy(is, avatar.toPath());
                        api.getPlatform().getLogger().info("Copied avatar from resource: " + GLOBAL_AVATAR_FOLDER + "/" + avatarFileName);
                    } else {
                        api.getPlatform().getLogger().warning("Avatar resource not found: " + avatarFileName);
                    }
                }
            }
        }

        return avatar;
    }

    public File getAvatarFile(String avatarFileName) throws IOException {
        return getAvatarFile(avatarFileName, "local");
    }
}
