package com.wairesdindustries.discordengine.common.discord.flow;

public interface FlowAction {

    void execute(FlowContext context) throws Exception;
}