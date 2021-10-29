package com.shattered.game.actor.character.player.component.model;

import com.shattered.database.mysql.MySQLColumn;
import com.shattered.database.mysql.MySQLCommand;
import com.shattered.datatable.tables.AnimSequenceUDataTable;
import com.shattered.game.GameObject;
import com.shattered.account.Account;
import com.shattered.game.actor.character.player.component.container.equipment.EquipmentSlot;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.flags.FlagType;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.component.WorldComponent;
import lombok.Getter;
import lombok.Setter;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JTlr Frost 10/25/2019 : 12:16 PM
 */
public class PlayerModelComponent extends WorldComponent {

    /**
     * Represents the Model of the Character and it's Attributes.
     */
    @Getter
    @Setter
    private Model model;
    

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public PlayerModelComponent(GameObject gameObject) {
        super(gameObject);
        setModel(new Model());
        getModel().setMale(true);
    }

    public int getBlendId() {
        if (getPlayer().component(ActorComponents.MOVEMENT).isMounted())
            return AnimSequenceUDataTable.BlendPoses.MOUNTED.ordinal();
        //TODO swimming
        if (getPlayer().container(Containers.EQUIPMENT).get(EquipmentSlot.MAIN_HAND.ordinal()) == null)
            return AnimSequenceUDataTable.BlendPoses.NORMAL.ordinal();
        return getPlayer().container(Containers.EQUIPMENT).get(EquipmentSlot.MAIN_HAND.ordinal()).getTable().getBlendPoseForWeapon();
    }


    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
        
    }

    /**
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {
        component(ActorComponents.FLAG_UPDATE).flag(FlagType.MODEL_BLOCK);
    }


    /**
     * Called once per world cycle per each instance.
     */
    @Override
    public void onTick(long deltaTime) {

    }

    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {
        
    }

    @Override
    public String getDatabaseName() {
        return "shatteredrelics";
    }

    @Override
    public String getTableName() {
        return "model";
    }

    @Override
    public WhereConditionOption[] getFetchConditions() {
        return new WhereConditionOption[] { new WhereConditionOption("character_id",  getPlayer().getId())};
    }

    @Override
    public WhereConditionOption[] getUpdateConditions() {
        return new WhereConditionOption[] { new WhereConditionOption("character_id", getPlayer().getId()) };
    }

    @Override
    public boolean insert() {
        try {
            List<MySQLColumn> columns = new ArrayList<>();
            columns.add(new MySQLColumn("character_id", getPlayer().getId()));
            columns.add(new MySQLColumn("race", getModel().getRace().ordinal()));
            columns.add(new MySQLColumn("male", getModel().isMale() ? 1 : 0));
            columns.add(new MySQLColumn("body_color", getModel().getBodyColor()));
            columns.add(new MySQLColumn("hair_style", getModel().getHairStyle()));
            columns.add(new MySQLColumn("hair_color", getModel().getHairColor()));
            columns.add(new MySQLColumn("eyebrow_style", getModel().getEyebrowStyle()));
            columns.add(new MySQLColumn("eyebrow_color", getModel().getEyebrowColor()));
            columns.add(new MySQLColumn("eye_color", getModel().getEyeColor()));
            columns.add(new MySQLColumn("beard_style", getModel().getBeardStyle()));
            columns.add(new MySQLColumn("beard_color", getModel().getBeardColor()));
            entry(getDatabaseName(), getTableName(), columns, MySQLCommand.INSERT);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update() {
        try {
            List<MySQLColumn> columns = new ArrayList<>();
            columns.add(new MySQLColumn("character_id", getPlayer().getId()));
            columns.add(new MySQLColumn("race", getModel().getRace().ordinal()));
            columns.add(new MySQLColumn("male", getModel().isMale() ? 1 : 0));
            columns.add(new MySQLColumn("body_color", getModel().getBodyColor()));
            columns.add(new MySQLColumn("hair_style", getModel().getHairStyle()));
            columns.add(new MySQLColumn("hair_color", getModel().getHairColor()));
            columns.add(new MySQLColumn("eyebrow_style", getModel().getEyebrowStyle()));
            columns.add(new MySQLColumn("eyebrow_color", getModel().getEyebrowColor()));
            columns.add(new MySQLColumn("eye_color", getModel().getEyeColor()));
            columns.add(new MySQLColumn("beard_style", getModel().getBeardStyle()));
            columns.add(new MySQLColumn("beard_color", getModel().getBeardColor()));
            entry(getDatabaseName(), getTableName(), columns, MySQLCommand.UPDATE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean fetch() {
        try {
            ResultSet results = getResults();

            if (!hasResults()) {
                insert();
                return true;
            }

            if (results.next()) {
                int race = results.getInt("race");
                boolean male = results.getInt("race") == 1;
                int bodyColor = results.getInt("body_color");
                int hairStyle = results.getInt("hair_style");
                int hairColor = results.getInt("hair_color");
                int eyebrowStyle = results.getInt("eyebrow_style");
                int eyebrowColor = results.getInt("eyebrow_color");
                int eyeColor = results.getInt("eye_color");
                int beardStyle = results.getInt("beard_style");
                int beardColor = results.getInt("beard_color");
                getModel().setRace(Model.Race.forId(race));
                getModel().setBodyColor(bodyColor);
                getModel().setMale(male);
                getModel().setHairStyle(hairStyle);
                getModel().setHairColor(hairColor);
                getModel().setEyebrowStyle(eyebrowStyle);
                getModel().setEyebrowColor(eyebrowColor);
                getModel().setEyeColor(eyeColor);
                getModel().setBeardStyle(beardStyle);
                getModel().setBeardColor(beardColor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
