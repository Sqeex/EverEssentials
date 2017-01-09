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
package fr.evercraft.essentials.command.spawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.SpawnService;

public class EESpawnSet extends ECommand<EverEssentials> {
	
	public EESpawnSet(final EverEssentials plugin) {
        super(plugin, "setspawn");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SETSPAWN.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.SETSPAWN_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_GROUP.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		ArrayList<String> suggest = new ArrayList<String>();
		if (args.size() == 1 && this.plugin.getEverAPI().getManagerService().getPermission().isPresent()) {
			for (Subject group : this.plugin.getEverAPI().getManagerService().getPermission().get().getGroupSubjects().getAllSubjects()) {
				suggest.add(group.getIdentifier());
			}
		}
		return suggest;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException, ServerDisableException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandSetSpawn((EPlayer) source, SpawnService.DEFAULT);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// Si on ne connait pas le joueur
		} else if (args.size() == 1) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				
				if (this.plugin.getEverAPI().getManagerService().getPermission().isPresent()) {
					Subject group = this.plugin.getEverAPI().getManagerService().getPermission().get().getGroupSubjects().get(args.get(0));
					if (group != null) {
						resultat = this.commandSetSpawn((EPlayer) source, group.getIdentifier());
					} else {
						EEMessages.SETSPAWN_ERROR_GROUP.sender()
							.replace("<name>", args.get(0))
							.sendTo(source);
					}
				} else {
					resultat = this.commandSetSpawn((EPlayer) source, SpawnService.DEFAULT);
				}
				
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}
	
	private boolean commandSetSpawn(final EPlayer player, final String group_name) throws ServerDisableException {
		Optional<Transform<World>> group = this.plugin.getManagerServices().getSpawn().get(group_name);
		
		if (group.isPresent()) {
			
			if (this.plugin.getManagerServices().getSpawn().update(group_name, player.getTransform())) {
				EEMessages.SETSPAWN_REPLACE.sender()
					.replace("<name>", this.getButtonSpawn(group_name, player.getLocation()))
					.sendTo(player);
				return true;
			} else {
				EAMessages.COMMAND_ERROR.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(player);
			}
			
		} else {
			
			if (this.plugin.getManagerServices().getSpawn().add(group_name, player.getTransform())) {
				EEMessages.SETSPAWN_NEW.sender()
					.replace("<name>", this.getButtonSpawn(group_name, player.getLocation()))
					.sendTo(player);
				return true;
			} else {
				EAMessages.COMMAND_ERROR.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(player);
			}
			
		}
		return false;
	}

	private Text getButtonSpawn(final String name, final Location<World> location){
		return EEMessages.SETSPAWN_NAME.getFormat().toText("<name>", name).toBuilder()
					.onHover(TextActions.showText(EEMessages.SETSPAWN_NAME_HOVER.getFormat().toText(
							"<name>", name,
							"<world>", location.getExtent().getName(),
							"<x>", String.valueOf(location.getBlockX()),
							"<y>", String.valueOf(location.getBlockY()),
							"<z>", String.valueOf(location.getBlockZ()))))
					.build();
	}
}
