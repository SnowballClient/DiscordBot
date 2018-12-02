package org.golde.snowball.discordbot;

import java.util.List;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.ReactionEmojiObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionRemoveEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.DiscordException;

public class DiscordBot {

	public static final DiscordBot INSTANCE = new DiscordBot();

	private IDiscordClient client;
	private IGuild guild; //This bot is only being used on one server

	private IRole ROLE_USER;
	private IRole ROLE_NOTIFICATIONS;
	
	private final long RR_NOTIFICATIONS = 518231942819479563L;

	public void start() {
		client = new ClientBuilder().withToken(Private.TOKEN).login();
		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(this);
		log("Instance created. Logging in...");
	}

	private void log(String msg) {
		System.out.println("[INFO] " + msg);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@EventSubscriber
	public void onReady(ReadyEvent e) {
		client.changePresence(StatusType.ONLINE);
		log("Bot ready!");
		guild = client.getGuilds().get(0);
		ROLE_USER = guild.getRoleByID(517910427452178456L);
		ROLE_NOTIFICATIONS = guild.getRoleByID(517929226020585472L);
	}

	@EventSubscriber
	public void chat(MessageEvent e) {

	}

	@EventSubscriber
	public void onUserJoin(UserJoinEvent e) {
		if(!e.getUser().isBot()) {
			guild.editUserRoles(e.getUser(), new IRole[] {ROLE_USER});
		}
	}

	@EventSubscriber
	public void reactionAddEvent(ReactionAddEvent e) {
		final IUser user = e.getUser();
		final IReaction reaction = e.getReaction();

		if(e.getMessageID() == RR_NOTIFICATIONS) {
			if(reaction.getEmoji().getName().equals("IWantNotifications")) {
				user.getOrCreatePMChannel().sendMessage("You have subscribed to notifications. If you wish to unsubscribe from recieving notifications, please remove your reaction from #info .");
				List<IRole> userRoles = user.getRolesForGuild(guild);
				userRoles.add(ROLE_NOTIFICATIONS);
				guild.editUserRoles(user, userRoles.toArray(new IRole[] {}));
			}
			else {
				e.getMessage().removeReaction(user, reaction);
			}
		}
	}


	@EventSubscriber
	public void reactionRemoveEvent(ReactionRemoveEvent e) {
		final IUser user = e.getUser();
		final IReaction reaction = e.getReaction();
		if(e.getMessageID() == RR_NOTIFICATIONS) {
			if(reaction.getEmoji().getName().equals("IWantNotifications")) {
				user.getOrCreatePMChannel().sendMessage("You have unsubscribed to notifications. If you wish to subscribe to recieve notifications again, please add back your reaction in #info .");
				List<IRole> userRoles = user.getRolesForGuild(guild);
				userRoles.remove(ROLE_NOTIFICATIONS);
				guild.editUserRoles(user, userRoles.toArray(new IRole[] {}));
			}
		}
	}

}
