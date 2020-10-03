/*
 *   @author TeamHardcore
 *
 *   Copyright (c) 2020 by TeamHardcore to present. All rights reserved
 */

package de.potera.teamhardcore.others.fakeentity.options;

import de.potera.teamhardcore.others.fakeentity.FakeEntity;
import de.potera.teamhardcore.others.fakeentity.FakeEntityOptionBase;
import de.potera.teamhardcore.others.fakeentity.FakeEntityOptionImpl;
import org.bukkit.entity.Player;

public class CommandFakeEntityOption extends FakeEntityOptionImpl {

    private final String command;

    public CommandFakeEntityOption(FakeEntityOptionBase option, FakeEntity entity, String... params) {
        super(option, entity, params);

        StringBuilder builder = new StringBuilder();
        for (String param : params)
            builder.append(param).append(" ");
        this.command = builder.substring(0, builder.length() - 1);
    }

    @Override
    public void executeOnClick(Player player) {
        if (this.command.isEmpty()) return;
        player.performCommand(this.command);
    }

    @Override
    public String toString() {
        return super.toString() + " (/" + this.command + ")";
    }
}
