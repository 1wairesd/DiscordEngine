package com.wairesdindustries.discordengine.common.flow.modal;

import java.util.List;

public class ModalDefinition {
    private final String id;
    private final String title;
    private final List<Input> inputs;

    public ModalDefinition(String id, String title, List<Input> inputs) {
        this.id = id;
        this.title = title;
        this.inputs = inputs;
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