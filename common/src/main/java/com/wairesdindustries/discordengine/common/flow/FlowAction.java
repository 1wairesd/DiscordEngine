package com.wairesdindustries.discordengine.common.flow;

public interface FlowAction {

    void execute(FlowContext context) throws Exception;
}