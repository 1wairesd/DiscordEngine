package com.wairesdindustries.discordengine.common.discord.flow.modal;

import java.util.List;

public class ModalDefinition {
    private final String id;
    private final String title;
    private final List<Input> inputs;
    private final OnSubmit onSubmit;

    public ModalDefinition(String id, String title, List<Input> inputs) {
        this(id, title, inputs, null);
    }

    public ModalDefinition(String id, String title, List<Input> inputs, OnSubmit onSubmit) {
        this.id = id;
        this.title = title;
        this.inputs = inputs;
        this.onSubmit = onSubmit;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public OnSubmit getOnSubmit() {
        return onSubmit;
    }

    public static class OnSubmit {
        private final String flowId;
        private final String stepId;

        public OnSubmit(String flowId, String stepId) {
            this.flowId = flowId;
            this.stepId = stepId;
        }

        public String getFlowId() {
            return flowId;
        }

        public String getStepId() {
            return stepId;
        }
    }

    public static class Input {
        private final String id;
        private final String label;
        private final String style;
        private final String placeholder;
        private final boolean required;

        public Input(String id, String label, String style, String placeholder, boolean required) {
            this.id = id;
            this.label = label;
            this.style = style;
            this.placeholder = placeholder;
            this.required = required;
        }

        public String getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        public String getStyle() {
            return style;
        }

        public String getPlaceholder() {
            return placeholder;
        }

        public boolean isRequired() {
            return required;
        }
    }
}