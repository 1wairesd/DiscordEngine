package com.wairesdindustries.discordengine.api.data.config;

import java.util.List;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class BotConfigData {
    
    @Setting
    private String token = "your-bot-token";
    
    @Setting
    private Activity activity = new Activity();
    
    @Setting
    private Sources sources = new Sources();
    
    public String getToken() {
        return token;
    }
    
    public Activity getActivity() {
        return activity;
    }
    
    public Sources getSources() {
        return sources;
    }
    
    @ConfigSerializable
    public static class Activity {
        @Setting
        private String text = "Discord Engine";
        
        @Setting
        private String type = "PLAYING";
        
        @Setting
        private String url = "";
        
        public String getText() {
            return text;
        }
        
        public String getType() {
            return type;
        }
        
        public String getUrl() {
            return url;
        }
    }
    
    @ConfigSerializable
    public static class Sources {
        @Setting
        private Commands commands = new Commands();
        
        @Setting
        private Avatar avatar = new Avatar();
        
        @Setting
        private Lang lang = new Lang();
        
        public Commands getCommands() {
            return commands;
        }
        
        public Avatar getAvatar() {
            return avatar;
        }
        
        public Lang getLang() {
            return lang;
        }
    }
    
    @ConfigSerializable
    public static class Commands {
        @Setting
        private String mode = "both";
        
        @Setting
        private List<String> local = List.of("commands.yml");
        
        @Setting("global")
        private List<String> globalFiles = List.of("commands.yml");
        
        public String getMode() {
            return mode;
        }
        
        public List<String> getLocal() {
            return local;
        }
        
        public List<String> getGlobal() {
            return globalFiles;
        }
    }
    
    @ConfigSerializable
    public static class Avatar {
        @Setting
        private String mode = "local";
        
        @Setting
        private String file = "avatar.png";
        
        public String getMode() {
            return mode;
        }
        
        public String getFile() {
            return file;
        }
    }
    
    @ConfigSerializable
    public static class Lang {
        @Setting
        private String mode = "local";
        
        @Setting
        private String file = "en_US.yml";
        
        public String getMode() {
            return mode;
        }
        
        public String getFile() {
            return file;
        }
    }
}
