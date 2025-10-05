package com.wairesdindustries.discordengine.api.data.subcommand;

/**
 * Class to define command type
 */

public enum SubCommandType {

    /**
     * User with player rights can use, and see this command in tab completer (discordengine.player)
     */
    PLAYER("discordengine.player"),
    /**
     * User with moder rights can use, and see this command in tab completer (discordengine.moder)
     */
    MODER("discordengine.moder"),
    /**
     * User with admin rights can use, and see this command in tab completer (discordengine.admin)
     */
    ADMIN("discordengine.admin");

    public final String permission;

    SubCommandType(String permission) {
        this.permission = permission;
    }

}