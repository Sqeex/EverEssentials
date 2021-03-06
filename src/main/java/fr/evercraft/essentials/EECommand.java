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
package fr.evercraft.essentials;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.plugin.command.EParentCommand;

public class EECommand extends EParentCommand<EverEssentials> {
	
	public EECommand(final EverEssentials plugin) {
		super(plugin, "everessentials", "essentials");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.EVERESSENTIALS.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.DESCRIPTION.getText();
	}

	@Override
	public boolean testPermissionHelp(final CommandSource source) {
		return source.hasPermission(EEPermissions.HELP.get());
	}
}
