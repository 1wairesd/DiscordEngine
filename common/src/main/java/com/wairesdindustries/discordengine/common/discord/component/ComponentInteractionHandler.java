package com.wairesdindustries.discordengine.common.discord.component;

import java.util.List;
import com.wairesdindustries.discordengine.api.discord.component.ComponentReference;
import com.wairesdindustries.discordengine.api.discord.component.DiscordComponent;
import com.wairesdindustries.discordengine.api.discord.entities.actions.DiscordAction;
import com.wairesdindustries.discordengine.common.DiscordEngine;
import com.wairesdindustries.discordengine.common.discord.entities.command.DiscordCommandContextImpl;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ComponentInteractionHandler extends ListenerAdapter {
    private final DiscordEngine api;

    public ComponentInteractionHandler(DiscordEngine api) {
        this.api = api;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String componentId = event.getComponentId();
        
        if (componentId.startsWith("ref:")) {
            String reference = componentId.substring(4);
            handleComponentReference(event, reference, true);
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String modalId = event.getModalId();
        
        if (modalId.startsWith("modal:")) {
            String reference = modalId.substring(6);
            handleComponentReference(event, reference, false);
        }
    }

    private void handleComponentReference(Object event, String reference, boolean isButton) {
        try {
            ComponentReference ref = new ComponentReference(reference);
            DiscordComponent component = api.getComponentLoader().getComponent(ref);

            if (component == null) {
                if (event instanceof ButtonInteractionEvent btnEvent) {
                    btnEvent.reply("Component not found").setEphemeral(true).queue();
                } else if (event instanceof ModalInteractionEvent modalEvent) {
                    modalEvent.reply("Component not found").setEphemeral(true).queue();
                }
                return;
            }

            DiscordCommandContextImpl context = createContext(event);
            if (context == null) {
                return;
            }

            if (!checkPermissions(context, component)) {
                if (event instanceof ButtonInteractionEvent btnEvent) {
                    btnEvent.reply("You don't have permission").setEphemeral(true).queue();
                } else if (event instanceof ModalInteractionEvent modalEvent) {
                    modalEvent.reply("You don't have permission").setEphemeral(true).queue();
                }
                return;
            }

            List<DiscordAction> actions = isButton ? component.getOnClickActions() : component.getOnSubmitActions();
            
            if (actions != null && !actions.isEmpty()) {
                for (DiscordAction action : actions) {
                    action.execute(context);
                }
            } else {
                if (event instanceof ButtonInteractionEvent btnEvent) {
                    btnEvent.reply("No actions configured").setEphemeral(true).queue();
                } else if (event instanceof ModalInteractionEvent modalEvent) {
                    modalEvent.reply("Submitted successfully").setEphemeral(true).queue();
                }
            }
        } catch (Exception e) {
            api.getPlatform().getLogger().severe("Error handling component interaction: " + e.getMessage());
            if (event instanceof ButtonInteractionEvent btnEvent) {
                btnEvent.reply("An error occurred").setEphemeral(true).queue();
            } else if (event instanceof ModalInteractionEvent modalEvent) {
                modalEvent.reply("An error occurred").setEphemeral(true).queue();
            }
        }
    }

    private DiscordCommandContextImpl createContext(Object event) {
        if (event instanceof ButtonInteractionEvent btnEvent) {
            return new DiscordCommandContextImpl(btnEvent);
        } else if (event instanceof ModalInteractionEvent modalEvent) {
            return new DiscordCommandContextImpl(modalEvent);
        }
        return null;
    }

    private boolean checkPermissions(DiscordCommandContextImpl context, DiscordComponent component) {
        if (component.getRequiredPermissions() == null || component.getRequiredPermissions().isEmpty()) {
            return true;
        }

        for (String permission : component.getRequiredPermissions()) {
            if (!context.hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }
}
