/*
 * This file is part of EverEssentials.
 *
 * EverEssentials is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EverEssentials is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EverEssentials.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.evercraft.essentials.event;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everapi.server.player.EPlayer;

public class VanishEvent implements Event {	
	public static enum Action {
    	ADD,
    	REMOVE;
    }
	
	private final EPlugin plugin;
    private final Action action;
    private final EPlayer player;

    public VanishEvent(final EPlugin plugin, final EPlayer player, final Action action) {
    	this.plugin = plugin;
    	
    	this.player = player;
        this.action = action;
    }

    public Player getPlayer() {
        return this.player;
    }
    
    public EPlayer getEPlayer() {
        return this.player;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    @Override
	public Cause getCause() {
		return Cause.source(this.plugin).build();
	}
}
