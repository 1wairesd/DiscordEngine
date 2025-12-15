package com.wairesdindustries.discordengine.common.flow.actions;

import com.wairesdindustries.discordengine.common.flow.FlowAction;
import com.wairesdindustries.discordengine.common.flow.FlowContext;

public class SendMessageAction implements FlowAction {
    private final String content;
    private final String key;

    public SendMessageAction(String content) {
        this.content = content;
        this.key = null;
    }

    public SendMessageAction(String content, String key) {
        this.content = content;
        this.key = key;
    }

    @Override
    public void execute(FlowContext context) throws Exception {
        String messageContent = content;

        if (key != null && !key.isEmpty()) {
            messageContent = "✅ Registration completed! Welcome to the server!";
        }

        if (messageContent == null || messageContent.trim().isEmpty()) {
            messageContent = "Action completed successfully!";
        }

        if (context.isSlashCommand()) {
            var slashEvent = context.asSlashCommand();
            if (slashEvent.isAcknowledged()) {
                slashEvent.getHook().sendMessage(messageContent).queue();
            } else {
                slashEvent.reply(messageContent).queue();
            }
        } else if (context.isButtonInteraction()) {
            var buttonEvent = context.asButtonInteraction();
            if (buttonEvent.isAcknowledged()) {
                buttonEvent.getHook().sendMessage(messageContent).queue();
            } else {
                buttonEvent.reply(messageContent).queue();
            }
        } else if (context.isModalInteraction()) {
            var modalEvent = context.asModalInteraction();
            if (modalEvent.isAcknowledged()) {
                modalEvent.getHook().sendMessage(messageContent).queue();
            } else {
                modalEvent.reply(messageContent).queue();
            }
        } else {
            throw new IllegalStateException("Unsupported interaction type for sending messages");
        }
    }
}