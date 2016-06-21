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
package fr.evercraft.essentials.commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.block.tileentity.CommandBlock;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.command.ECommand;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.server.player.EPlayer;

public class EETime extends ECommand<EverEssentials> {
	
	private static final int MAX_TIME = 24000;
	private static final int DIFF_TIME = -6000;
	private static final int DIFF_HOURS_TIME = 1000;
	private static final double DIFF_MINUTES_TIME = 1000d/60d;
	private static final DecimalFormat FORMAT = new DecimalFormat("00");
	
	private static final long TIME_DAY = 0;
	private static final long TIME_NIGHT = 14000;
	private static final long TIME_DAWN = 23000;

	public EETime(final EverEssentials plugin) {
		super(plugin, "time");
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TIME.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.TIME_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/time ").onClick(TextActions.suggestCommand("/time "))
				.append(Text.of("["))
				.append(Text.builder("day").onClick(TextActions.suggestCommand("/time day")).build())
				.append(Text.of("|"))
				.append(Text.builder("night").onClick(TextActions.suggestCommand("/time night")).build())
				.append(Text.of("|"))
				.append(Text.builder("dawn").onClick(TextActions.suggestCommand("/time dawn")).build())
				.append(Text.of("|"))
				.append(Text.builder("17:30").onClick(TextActions.suggestCommand("/time 17:30")).build())
				.append(Text.of("|"))
				.append(Text.builder("4000").onClick(TextActions.suggestCommand("/time 4000")).build())
				.append(Text.of("] [monde|*]"))
				.color(TextColors.RED).build();
	}

	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			suggests.add("day");
			suggests.add("night");
			suggests.add("dawn");
			suggests.add("17:30");
			suggests.add("4000");
		} else if (args.size() == 2) {
			for (World world : this.plugin.getEServer().getWorlds()) {
				if(world.getProperties().getDimensionType().equals(DimensionTypes.OVERWORLD)) {
					suggests.add(world.getName());
				}
			}
			suggests.add("*");
		}
		return suggests;
	}

	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandTime((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// On connais le joueur
		} else if (args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandTimeSet(source, parseTime(args.get(0)), ((EPlayer) source).getWorld());
			} else if (source instanceof CommandBlock) {
				resultat = commandTimeSet(source, parseTime(args.get(0)), ((CommandBlock) source).getWorld());
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// On connais le joueur
		} else if (args.size() == 2) {
			if(args.get(1).equals("*")){
				resultat = commandTimeSetAll(source, parseTime(args.get(0)));
			} else {
				Optional<World> optWorld = this.plugin.getEServer().getWorld(args.get(1));
				// Si le monde existe
				if (optWorld.isPresent()) {
					resultat = commandTimeSet(source, parseTime(args.get(0)), optWorld.get());
				} else {
					source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()));
				}
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}

	public boolean commandTime(final EPlayer player) {
		player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TIME_INFORMATION.get()
				.replaceAll("<world>", player.getWorld().getName())
				.replaceAll("<hours>", getTime(player.getWorld().getProperties().getWorldTime()))
				.replaceAll("<ticks>", String.valueOf(player.getWorld().getProperties().getWorldTime())));
		return false;
	}
	
	private boolean commandTimeSet(final CommandSource player, final Optional<Long> time, final World world) {
		if(time.isPresent()) {
			setWorldTime(world.getProperties(), time.get());
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TIME_SET_WORLD.get()
					.replaceAll("<world>", world.getName())
					.replaceAll("<hours>", getTime(time.get()))
					.replaceAll("<ticks>", String.valueOf(time))));
			return true;
		} else {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TIME_ERROR.get()));
		}
		return false;
	}
	
	private boolean commandTimeSetAll(final CommandSource player, final Optional<Long> time) {
		if(time.isPresent()) {
			for (World world : this.plugin.getEServer().getWorlds()) {
				if(world.getProperties().getDimensionType().equals(DimensionTypes.OVERWORLD)) {
					setWorldTime(world.getProperties(), time.get());
				}
			}
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TIME_SET_ALL_WORLD.get()
					.replaceAll("<hours>", getTime(time.get()))
					.replaceAll("<ticks>", String.valueOf(time.get()))));
			return true;
		} else {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TIME_ERROR.get()));
		}
		return false;
	}
	
	public void setWorldTime(WorldProperties world, long time) {
		world.setWorldTime(((long) (Math.ceil(world.getTotalTime()/(double)MAX_TIME)*MAX_TIME)) + time);
	}
	
	public Optional<Long> parseTime(final String arg){
		if(arg.equalsIgnoreCase("day")) {
			return Optional.of(TIME_DAY);
		} else if(arg.equalsIgnoreCase("night")) {
			return Optional.of(TIME_NIGHT);
		} else if(arg.equalsIgnoreCase("dawn")) {
			return Optional.of(TIME_DAWN);
		} else if(arg.contains(":")) {
			String args[] = arg.split(":", 2);
			if(args.length == 2) {
				return parseTime(args[0], args[1]);
			}
		} else if(arg.contains("h")) {
			String args[] = arg.split("h", 2);
			if(args.length == 2) {
				return parseTime(args[0], args[1]);
			}
		} else {
			try {
				long ticks = Integer.parseInt(arg);
				return Optional.of(ticks);
			} catch (NumberFormatException e) {}
		}
		
		return Optional.empty();
	}	
	
	public Optional<Long> parseTime(final String name_hours, final String name_minutes){
		try {
			Integer hours = Integer.parseInt(name_hours);
			Integer minutes = Integer.parseInt(name_minutes);
			if(hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59) {
				Double time = (hours * DIFF_HOURS_TIME) + (minutes * DIFF_MINUTES_TIME);
				time = time + DIFF_TIME;
				if(time < 0) {
					time = MAX_TIME + time;
				}
				return Optional.of(time.longValue());
			}
		} catch (NumberFormatException e) {}
		return Optional.empty();
	}
	
	public String getTime(long ticks){
		ticks = ticks - DIFF_TIME;
		ticks = ticks % MAX_TIME;
		
		double minutes = ticks % DIFF_HOURS_TIME;
		double hours = Math.floor((ticks - minutes) / DIFF_HOURS_TIME);
		minutes = Math.floor(minutes / DIFF_MINUTES_TIME);
		
		return EEMessages.TIME_FORMAT.get()
				.replaceAll("<hours>", FORMAT.format(hours))
				.replaceAll("<minutes>", FORMAT.format(minutes));
	}
}
