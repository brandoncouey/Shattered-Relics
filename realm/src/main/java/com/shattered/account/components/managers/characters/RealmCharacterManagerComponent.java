package com.shattered.account.components.managers.characters;

import com.shattered.account.RealmAccount;
import com.shattered.account.components.RealmAccountComponents;
import com.shattered.account.Account;
import com.shattered.account.character.PlayerInformation;
import com.shattered.database.mysql.MySQLColumn;
import com.shattered.database.mysql.MySQLCommand;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.RealmProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Realm;
import com.shattered.utilities.ecs.Component;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JTlr Frost 9/5/2019 : 10:59 PM
 */
@Log
public class  RealmCharacterManagerComponent extends Component {

    /**
     * Represents the Character Information
     */
    @Getter
    @Setter
    private PlayerInformation characterInformation;


    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public RealmCharacterManagerComponent(Object gameObject) {
        super(gameObject);
    }

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
        //Registers the Request of CharacterInformation List

        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_CREATE_CHARACTER_REQUEST, new RealmProtoListener<Realm.CreateCharacterRequest>() {
            /**
             * @param message
             * @param account
             */
            @Override
            public void handle(Realm.CreateCharacterRequest message, RealmAccount account) {

                try {

                    String characterName = message.getCharacterName();
                    boolean isMale = message.getMale();
                    int bodyColor = message.getBodyColor();
                    int hairStyle = message.getHairStyle() >> 5;
                    int hairColor = message.getHairStyle() & 0x1f;
                    int eyebrowStyle = message.getEyebrowStyle() >> 5;
                    int eyebrowColor = message.getEyeColor() & 0x1f;
                    int eyeColor = message.getEyeColor();
                    int beardStyle = message.getBeardStyle() >> 5;
                    int beardColor = message.getBeardStyle() & 0x1f;

                    if (characterName.length() < 3) {
                        account.sendMessage(PacketOuterClass.Opcode.SMSG_CHARACTER_CREATION_RESPONSE, Realm.CharacterCreationResponse.newBuilder().setResponseId(CharacterCreationResponse.INSUFFICIENT_CHARACTERS.ordinal()).build());
                        return;
                    }

                    if (characterName.length() > 15) {
                        account.sendMessage(PacketOuterClass.Opcode.SMSG_CHARACTER_CREATION_RESPONSE, Realm.CharacterCreationResponse.newBuilder().setResponseId(CharacterCreationResponse.INSUFFICIENT_CHARACTERS.ordinal()).build());
                        return;
                    }

                    ResultSet current = getResults(getDatabaseName(), getTableName(), new WhereConditionOption[]{new WhereConditionOption("account_id", getAccount().getAccountInformation().getAccountId())});

                    if (current.next()) {
                        account.sendMessage(PacketOuterClass.Opcode.SMSG_CHARACTER_CREATION_RESPONSE, Realm.CharacterCreationResponse.newBuilder().setResponseId(CharacterCreationResponse.SYSTEM_UNAVAILABLE.ordinal()).build());
                        return;
                    }

                    ResultSet name = getResults(getDatabaseName(), getTableName(), new WhereConditionOption[]{new WhereConditionOption("name", characterName)});

                    if (name.next()) {
                        account.sendMessage(PacketOuterClass.Opcode.SMSG_CHARACTER_CREATION_RESPONSE, Realm.CharacterCreationResponse.newBuilder().setResponseId(CharacterCreationResponse.NAME_IN_USE.ordinal()).build());
                        return;
                    }

                    account.component(RealmAccountComponents.CHARACTER_MANAGER).createCharacter(new PlayerInformation(-1, characterName, "Unavailable"), isMale, bodyColor, hairStyle, hairColor, eyebrowStyle, eyebrowColor, eyeColor, beardStyle, beardColor);
                } catch (Exception e) {
                    account.sendMessage(PacketOuterClass.Opcode.SMSG_CHARACTER_CREATION_RESPONSE, Realm.CharacterCreationResponse.newBuilder().setResponseId(CharacterCreationResponse.SYSTEM_UNAVAILABLE.ordinal()).build());
                    e.printStackTrace();
                }
            }

        }, Realm.CreateCharacterRequest.getDefaultInstance());

    }



    /**
     * Initializes Once the 'ChannelLine' Has been Awakened.
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {

    }

    /**
     * Called once per world cycle per each instance.
     */
    @Override
    public void onTick(long deltaSeconds) {

    }

    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {

    }

    /**
     * Database name.
     */
    @Override
    public String getDatabaseName() {
        return "shatteredrelics";
    }

    /**
     * Table name.
     */
    @Override
    public String getTableName() {
        return "information";
    }

    /**
     * Conditions.
     */
    @Override
    public WhereConditionOption[] getFetchConditions() {
        return new WhereConditionOption[] { new WhereConditionOption("account_id",  getAccount().getAccountInformation().getAccountId())} ;

    }

    @Override
    public boolean fetch() {
        try {
            ResultSet results = getResults();

            //Checks if the Account has results, to prevent sending a empty packet.
            if (!hasResults())  return false;
            if (results.next()) {
                int id = results.getInt("id");
                String name = results.getString("name");
                setCharacterInformation(new PlayerInformation(id, name, ""));
            }

            Realm.CharacterInformation.Builder modelInformation = Realm.CharacterInformation.newBuilder();

            ResultSet modelResults = getResults(getDatabaseName(), "model", new WhereConditionOption[] { new WhereConditionOption("character_id",  getCharacterInformation().getId())});

            if (modelResults.next()) {
                int race = modelResults.getInt("race");
                boolean male = modelResults.getInt("race") == 1;
                int bodyColor = modelResults.getInt("body_color");
                int hairStyle = modelResults.getInt("hair_style");
                int hairColor = modelResults.getInt("hair_color");
                int eyebrowStyle = modelResults.getInt("eyebrow_style");
                int eyebrowColor = modelResults.getInt("eyebrow_color");
                int eyeColor = modelResults.getInt("eye_color");
                int beardStyle = modelResults.getInt("beard_style");
                int beardColor = modelResults.getInt("beard_color");
                modelInformation.setName(getCharacterInformation().getName());
                modelInformation.setMale(male);
                modelInformation.setRace(race << 5 | bodyColor);
                modelInformation.setHairStyle(hairStyle << 5 | hairColor);
                modelInformation.setEyebrowStyle(eyebrowStyle << 5 | eyebrowColor);
                modelInformation.setEyeColor(eyeColor);
                modelInformation.setBeardStyle(beardStyle << 5 | beardColor);

            }

            ResultSet equipmentResults = getResults(getDatabaseName(), "equipment", new WhereConditionOption[] { new WhereConditionOption("character_id",  getCharacterInformation().getId())});

            if (equipmentResults.next()) {
                int headSlot = equipmentResults.getInt("head_slot");
                int shoulderSlot = equipmentResults.getInt("shoulders_slot");
                int backSlot = equipmentResults.getInt("back_slot");
                int chestSlot = equipmentResults.getInt("chest_slot");
                int pantsSlot = equipmentResults.getInt("pants_slot");
                int wristsSlot = equipmentResults.getInt("wrists_slot");
                int glovesSlot = equipmentResults.getInt("gloves_slot");
                int bootsSlot = equipmentResults.getInt("boots_slot");
                int mainHandSlot = equipmentResults.getInt("main_hand_slot");
                int offHandSlot = equipmentResults.getInt("off_hand_slot");
                modelInformation.setHeadSlotId(headSlot);
                modelInformation.setShouldersSlotId(shoulderSlot);
                modelInformation.setBackSlotId(backSlot);
                modelInformation.setChestSlotId(chestSlot);
                modelInformation.setPantsSlotId(pantsSlot);
                modelInformation.setWristsSlotId(wristsSlot);
                modelInformation.setGlovesSlotId(glovesSlot);
                modelInformation.setBackSlotId(bootsSlot);
                modelInformation.setMainhandSlotId(mainHandSlot);
                modelInformation.setOffhandSlotId(offHandSlot);
            }

            getCharacterInformation().setModelInformation(modelInformation);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param characterInformation
     */
    public void createCharacter(PlayerInformation characterInformation, boolean isMale, int bodyColor, int hairStyle, int hairColor, int eyebrowStyle, int eyebrowColor, int eyeColor, int beardStyle, int beardColor) {
        try {
            List<MySQLColumn> insert = new ArrayList<>();
            insert.add(new MySQLColumn("account_id", getAccount().getAccountInformation().getAccountId()));
            insert.add(new MySQLColumn("name", characterInformation.getName()));
            entry(getDatabaseName(), getTableName(), insert, MySQLCommand.INSERT);

            fetch();

            insert = new ArrayList<>();
            insert.add(new MySQLColumn("character_id", getCharacterInformation().getId()));
            insert.add(new MySQLColumn("race", 1));
            insert.add(new MySQLColumn("male", isMale ? 1 : 0));
            insert.add(new MySQLColumn("body_color", bodyColor));
            insert.add(new MySQLColumn("hair_style", hairStyle));
            insert.add(new MySQLColumn("hair_color", hairColor));
            insert.add(new MySQLColumn("eyebrow_style", eyebrowStyle));
            insert.add(new MySQLColumn("eyebrow_color", eyebrowColor));
            insert.add(new MySQLColumn("eye_color", eyeColor));
            insert.add(new MySQLColumn("beard_style", beardStyle));
            insert.add(new MySQLColumn("beard_color", beardColor));
            entry(getDatabaseName(), "model", insert, MySQLCommand.INSERT);
            //SUCCESS
            getAccount().sendMessage(PacketOuterClass.Opcode.SMSG_CHARACTER_CREATION_RESPONSE, Realm.CharacterCreationResponse.newBuilder().setResponseId(CharacterCreationResponse.SUCCESSFUL.ordinal()).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the GameRealm Account
     * @return
     */
    public RealmAccount getAccount() {
        return (RealmAccount) gameObject;
    }


}