package com.wairesdindustries.discordengine.api.data.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class BotConfigData {
    
    @Setting
    private String token = "your-bot-token";
    
    @Setting
    private Activity activity = new Activity();
    
    @Setting
    private Avatar avatar = new Avatar();
    
    public String getToken() {
        return token;
    }
    
    public Activity getActivity() {
        return activity;
    }
    
    public Avatar getAvatar() {
        return avatar;
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
    public static class Avatar {
        @Setting
        private String file = "avatar-discordengine-nofon.png";
        
        public String getFile() {
            return file;
        }
    }
    

}
