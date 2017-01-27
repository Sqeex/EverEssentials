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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEFeed extends ECommand<EverEssentials> {
	
	public EEFeed(final EverEssentials plugin) {
        super(plugin, "feed", "eat");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.FEED.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.FEED_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.FEED_OTHERS.get())){
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.getString() + "|*]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
		}
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source.hasPermission(EEPermissions.FEED_OTHERS.get())){
			return this.getAllPlayers(source, true);
		}
		return Arrays.asList();
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandFeed((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// On connais le joueur
		} else if (args.size() == 1) {
			
			// Si il a la permission
			if (source.hasPermission(EEPermissions.FEED_OTHERS.get())){
				// Pour tous les joueurs
				if (args.get(0).equals("*")) {
					resultat = this.commandFeedAll(source);
				// Pour un joueur
				} else {
					Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
					// Le joueur existe
					if (optPlayer.isPresent()){
						resultat = this.commandFeedOthers(source, optPlayer.get());
					} else {
						EAMessages.PLAYER_NOT_FOUND.sender()
							.prefix(EEMessages.PREFIX)
							.sendTo(source);
					}
				}
			// Il n'a pas la permission
			} else {
				EAMessages.NO_PERMISSION.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}
	
	private boolean commandFeed(final EPlayer player) {
		player.setFood(20);
		player.setSaturation(20);
		
		EEMessages.FEED_PLAYER.sendTo(player);
		return true;
	}
	
	private boolean commandFeedOthers(final CommandSource staff, final EPlayer player) throws CommandException {
		// La source et le joueur sont identique
		if (player.equals(staff)) {
			return this.commandFeed(player);
		}
		
		player.setFood(20);
		player.setSaturation(20);
		
		EEMessages.FEED_OTHERS_STAFF.sender()
			.replace("<player>", player.getName())
			.sendTo(player);
		EEMessages.FEED_OTHERS_PLAYER.sender()
			.replace("<staff>", staff.getName())
			.sendTo(player);
		return true;
	}
	
	private boolean commandFeedAll(final CommandSource staff) {
		// Pour tous les joueurs connecté
		this.plugin.getEServer().getOnlineEPlayers().forEach(player -> {
			player.setFood(20);
			player.setSaturation(20);
			
			// La source et le joueur sont différent
			if (!staff.equals(player)) {
				EEMessages.FEED_OTHERS_PLAYER.sender()
					.replace("<staff>", staff.getName())
					.sendTo(player);
			}
		});
		
		EEMessages.FEED_ALL_STAFF.sendTo(staff);
		return true;
	}
}
