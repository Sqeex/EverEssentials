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
package fr.evercraft.essentials.command.itemname;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEItemNameClear extends ESubCommand<EverEssentials> {
	public EEItemNameClear(final EverEssentials plugin, final EEItemName command) {
        super(plugin, command, "clear");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.ITEM_NAME.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.ITEM_NAME_CLEAR_DESCRIPTION.getText();
	}
	
	@Override
	public Collection<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public boolean subExecute(final CommandSource source, final List<String> args) {
		if(args.size() == 0){
			if(source instanceof EPlayer){
				return this.commandItemNameClear((EPlayer) source);
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
				return false;
			}
		} else {
			this.help(source);
			return false;
		}
	}

	private boolean commandItemNameClear(final EPlayer player) {
		Optional<ItemStack> item = player.getItemInMainHand();
		if(!item.isPresent()) {
			EAMessages.EMPTY_ITEM_IN_HAND.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return false;
		}
		
		if(!item.get().get(Keys.DISPLAY_NAME).isPresent()) {
			EEMessages.ITEM_NAME_CLEAR_ERROR.sender()
				.replace("<item>", EChat.getButtomItem(item.get(), EEMessages.ITEM_NAME_CLEAR_COLOR.getColor()))
				.sendTo(player);
			return false;
		}
		
		EEMessages.ITEM_NAME_CLEAR_NAME.sender()
			.replace("<item>", EChat.getButtomItem(item.get(), EEMessages.ITEM_NAME_CLEAR_COLOR.getColor()))
			.sendTo(player);
		item.get().remove(Keys.DISPLAY_NAME);
		player.setItemInMainHand(item.get());
		return true;
	}
}