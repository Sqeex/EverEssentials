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
package fr.evercraft.essentials.command;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.command.ECommand;

public class EEReloadAll extends ECommand<EverEssentials>{
	
	public EEReloadAll(final EverEssentials plugin) {
        super(plugin, "reload", "rl");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission("sponge.command.plugins.reload");
	}

	public Text description(final CommandSource source) {
		return EEMessages.RELOAD_ALL_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() == 0) {
			resultat = commandReload(source);
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean commandReload(final CommandSource player) {
		this.plugin.getEServer().getBroadcastChannel().send(EEMessages.PREFIX.getText().concat(EEMessages.RELOAD_ALL_FIRST.getText()));
		this.plugin.getGame().getEventManager().post(SpongeEventFactory.createGameReloadEvent(Cause.of(NamedCause.source(player))));
		this.plugin.getEServer().getBroadcastChannel().send(EEMessages.PREFIX.getText().concat(EEMessages.RELOAD_ALL_END.getText()));
		return true;
	}
}