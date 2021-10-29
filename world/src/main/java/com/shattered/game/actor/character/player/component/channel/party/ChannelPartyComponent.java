package com.shattered.game.actor.character.player.component.channel.party;

import com.shattered.account.Account;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.channel.ChannelComponent;
import com.shattered.game.actor.character.player.component.channel.ChannelComponents;
import com.shattered.game.actor.character.player.component.channel.ChannelType;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelPartyComponent extends ChannelComponent {

    /**
     * Represents a list of all party members
     */
    @Setter
    private Map<Integer, Player> members = new ConcurrentHashMap<>();

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public ChannelPartyComponent(Object gameObject) {
        super(gameObject);
    }

    /**
     * Sends a party message to players
     * @param message
     */
    public void sendMessage(String message) {
        if (!isInParty()) {
            sendDefaultMessage("You are not in a party.");
            return;
        }
        getMembers().values().stream().filter(Objects::nonNull).forEach(p -> p.sendMessage(PacketOuterClass.Opcode.SMSG_CHAT_MESSAGE, World.ChannelMessage.newBuilder().setFromIndex(getPlayer().getClientIndex() << 1 | 1).setFromName(getPlayer().getName()).setType(ChannelType.PARTY.ordinal()).setMessage(message).setPermissionLevel(getPlayer().getAccount().getAccountInformation().getAccountLevel().ordinal()).build()));
    }

    /**
     *
     * @param target
     */
    public void inviteMember(Player target) {
        if (target.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).isInParty()) {
            sendDefaultMessage(target.getName() + " is already in a party.");
            return;
        }
        if (members.containsValue(target)) {
            sendDefaultMessage(target.getName() + " is already in the party.");
            return;
        }
        target.sendMessage(PacketOuterClass.Opcode.SMSG_PARTY_INVITE, World.Invite.newBuilder().setFrom(getPlayer().getName()).build());
        target.component(ActorComponents.TRANS_VAR).setVarInt("party_invite_uuid", getPlayer().getClientIndex());
    }

    /**
     * Adds the player to the party
     * @param member
     */
    public void addMember(Player member) {
        if (member.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).isInParty()) {
            sendDefaultMessage(member.getName() + " is already in a party.");
            return;
        }
        if (members.containsValue(member)) {
            sendDefaultMessage(member.getName() + " is already in the party.");
            return;
        }
        if (members.isEmpty())
            members.put(findNextIndex(), getPlayer());
        members.values().stream().filter(Objects::nonNull).forEach(p -> p.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).add(member));
        member.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).onJoinedParty(members);
    }

    //JTlr Invites Brad
    //JTLR gets brad in his party
    //Brad joins party
    //Brad gets brad in party.

    /**
     * Adds a member for all other members (excludes the current instance)
     * @param member
     */
    public void add(Player member) {
        if (getMembers().containsValue(member))  return;
        int nextIndex = findNextIndex();
        if (getMembers().size() >= 4 || nextIndex == -1)
            return;
        getMembers().put(nextIndex, member);
        component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage(member.getName() + " has joined the party.");
        if (!member.getName().equalsIgnoreCase(getPlayer().getName()))
        sendMessage(PacketOuterClass.Opcode.SMSG_ADD_PARTY_MEMBER, World.PartyMember.newBuilder().setId(nextIndex).setName(member.getName()).setHealth(member.component(ActorComponents.VAR).getVarInt("health"))
                .setMaxHealth(member.component(ActorComponents.VAR).getVarInt("max_health")).setEnergy(member.component(ActorComponents.VAR).getVarInt("energy"))
                .setMaxEnergy(member.component(ActorComponents.VAR).getVarInt("max_energy")).build());
    }

    /**
     * Callback for player values have been updated
     */
    public void refreshValues() {
        if (!isInParty()) return;
        getMembers().values().stream().filter(Objects::nonNull).forEach(p -> p.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).updateMember(getPlayer()));
    }

    /**
     * Updates the specific member with their new values
     * @param member
     */
    public void updateMember(Player member) {
        sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_PARTY_MEMBER, World.UpdatePartyMember.newBuilder().setId(findIndexForPlayer(member)).setHealth(member.component(ActorComponents.VAR).getVarInt("health"))
                .setMaxHealth(member.component(ActorComponents.VAR).getVarInt("max_health")).setEnergy(member.component(ActorComponents.VAR).getVarInt("energy"))
                .setMaxEnergy(member.component(ActorComponents.VAR).getVarInt("max_energy")).build());
    }

    /**
     * Checks if a player is in the party
     * @param member
     * @return in the party
     */
    public boolean containsPlayer(Player member) {
        for (Player player : members.values()) {
            if (player.equals(member))
                return true;
        }
        return false;
    }

    /**
     * Makes the current player leave their own party
     */
    public void leave() {
        if (!isInParty()) return;
        members.values().stream().filter(Objects::nonNull).forEach(p -> p.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).remove(getPlayer()));
        onLeftParty();
        sendDefaultMessage("You have left the party.");
    }


    /**
     * Removes the player from the party
     * @param target
     */
    public void leave(Player target) {
        if (!isInParty()) return;
        if (!members.containsValue(target)) return;
        members.remove(target);
        members.values().stream().filter(Objects::nonNull).forEach(p -> p.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).remove(target));
        target.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).onLeftParty();
    }

    public void remove(Player member) {
        if (!getMembers().containsValue(member))  return;
        component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage(member.getName() + " has left the party.");
        sendMessage(PacketOuterClass.Opcode.SMSG_REMOVE_PARTY_MEMBER, World.PartyMember.newBuilder().setId(findIndexForPlayer(member)).build());
        getMembers().remove(member);
    }

    /**
     * You force the player out of the party.
     * @param member
     */
    public void kickMember(Player member) {
        if (!isInParty()) return;
        if (!members.containsValue(member)) return;
        component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You kicked " + member.getName() + " from the party.");
        sendMessage(PacketOuterClass.Opcode.SMSG_REMOVE_PARTY_MEMBER, World.PartyMember.newBuilder().setId(findIndexForPlayer(member)).build());
        members.values().stream().filter(Objects::nonNull).forEach(p -> p.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).remove(member));
        members.remove(member);
        member.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).onLeftParty();
        member.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("You were kicked from the party.");
    }

    /**
     * Sends a request to a player to join your party
     * @param target
     */
    public void sendRequest(Player target) {
        if (target.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.PARTY_CHANNEL).isInParty()) {
            sendDefaultMessage(target.getName()  + " is already in a party.");
            return;
        }
        target.sendMessage(PacketOuterClass.Opcode.SMSG_PARTY_INVITE, World.Invite.newBuilder().setFrom(getPlayer().getName()).build());
    }

    /**
     * Called upon a member joining the party
     * @param members
     */
    public void onJoinedParty(Map<Integer, Player> members) {
        setMembers(members);
        for (Player member : members.values()) {
            if (member == null) continue;
            if (member.getName().equalsIgnoreCase(getPlayer().getName())) {
                continue;
            }
            sendMessage(PacketOuterClass.Opcode.SMSG_ADD_PARTY_MEMBER, World.PartyMember.newBuilder().setId(findIndexForPlayer(member)).setName(member.getName()).setHealth(member.component(ActorComponents.VAR).getVarInt("health"))
                    .setMaxHealth(member.component(ActorComponents.VAR).getVarInt("max_health")).setEnergy(member.component(ActorComponents.VAR).getVarInt("energy"))
                    .setMaxEnergy(member.component(ActorComponents.VAR).getVarInt("max_energy")).build());
        }
        //getMembers().values().stream().filter(Objects::nonNull).forEach(p ->sendMessage(PacketOuterClass.Opcode.SMSG_ADD_PARTY_MEMBER, World.PartyMember.newBuilder().setName(p.getName()).build()));
    }

    /**
     * Leaves the party for the player.
     */
    public void onLeftParty() {
        members.clear();
        for (int index = 0; index <= 5; index++)
            sendMessage(PacketOuterClass.Opcode.SMSG_REMOVE_PARTY_MEMBER, World.PartyMember.newBuilder().setId(index).build());
    }

    /**
     * Finds the next available index for the member
     * @return the next available index
     */
    public int findNextIndex() {
        for (int index = 0; index < 5; index++) {
            if (members.get(index) == null)
                return index;
        }
        return -1;
    }

    /**
     * Finds the index for the member
     * @param player
     * @return the index
     */
    public int findIndexForPlayer(Player player) {
        for (int index = 0; index < 5; index++) {
            if (getMembers().get(index) == player)
                return  index;
        }
        return -1;
    }


    /**
     * Checks if the player is in a party
     * @return in a party
     */
    public boolean isInParty() {
        return !members.isEmpty();
    }

    @Override
    public void onFinish() {
        super.onFinish();
        leave();
    }

    /**
     * Gets the list of members
     * @return the members
     */
    public Map<Integer, Player> getMembers() {
        return members;
    }
}
