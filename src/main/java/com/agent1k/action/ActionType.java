package com.agent1k.action;

import com.agent1k.action.impl.ActionBarAction;
import com.agent1k.action.impl.MessageAction;
import com.agent1k.action.impl.SoundAction;
import com.agent1k.action.impl.TitleAction;
import java.util.Arrays;
import java.util.Optional;

public enum ActionType {
    MESSAGE("[message]", new MessageAction()),
    ACTIONBAR("[actionbar]", new ActionBarAction()),
    SOUND("[sound]", new SoundAction()),
    TITLE("[title]", new TitleAction());

    private final String tag;
    private final Actions action;

    ActionType(String tag, Actions action) {
        this.tag = tag;
        this.action = action;
    }

    public Actions action() {
        return action;
    }

    public static Optional<ActionType> fromTag(String tag) {
        return Arrays.stream(values()).filter(type -> type.tag.equalsIgnoreCase(tag)).findFirst();
    }
}
