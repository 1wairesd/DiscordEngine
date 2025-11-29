package com.wairesdindustries.discordengine.common.discord.entities.actions;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.api.discord.entities.command.DiscordCommandContext;

import net.dv8tion.jda.api.EmbedBuilder;

public class DiscordEmbedAction implements DiscordAction {

    private final String title;
    private final String description;
    private final String color;
    private final String thumbnail;
    private final String image;
    private final String footer;
    private final String author;
    private final List<Map<String, String>> fields;

    public DiscordEmbedAction(String title, String description, String color, 
                              String thumbnail, String image, String footer, 
                              String author, List<Map<String, String>> fields) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.thumbnail = thumbnail;
        this.image = image;
        this.footer = footer;
        this.author = author;
        this.fields = fields;
    }

    @Override
    public void execute(DiscordCommandContext context) {
        EmbedBuilder embed = new EmbedBuilder();
        
        if (title != null && !title.isEmpty()) {
            embed.setTitle(title);
        }
        
        if (description != null && !description.isEmpty()) {
            embed.setDescription(description);
        }
        
        if (color != null && !color.isEmpty()) {
            try {
                embed.setColor(Color.decode(color));
            } catch (NumberFormatException e) {
                embed.setColor(Color.BLUE);
            }
        }
        
        if (thumbnail != null && !thumbnail.isEmpty()) {
            embed.setThumbnail(thumbnail);
        }
        
        if (image != null && !image.isEmpty()) {
            embed.setImage(image);
        }
        
        if (footer != null && !footer.isEmpty()) {
            embed.setFooter(footer);
        }
        
        if (author != null && !author.isEmpty()) {
            embed.setAuthor(author);
        }
        
        if (fields != null) {
            for (Map<String, String> field : fields) {
                String name = field.getOrDefault("name", "Field");
                String value = field.getOrDefault("value", "");
                boolean inline = Boolean.parseBoolean(field.getOrDefault("inline", "false"));
                embed.addField(name, value, inline);
            }
        }
        
        context.replyEmbed(embed.build());
    }
}
