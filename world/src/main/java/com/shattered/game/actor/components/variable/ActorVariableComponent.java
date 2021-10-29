package com.shattered.game.actor.components.variable;

import com.shattered.account.Account;
import com.shattered.database.mysql.MySQLColumn;
import com.shattered.database.mysql.MySQLCommand;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.game.component.WorldComponent;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.World;
import com.shattered.utilities.ecs.PriorityComponent;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JTlr Frost 11/6/2019 : 5:49 PM
 */
@PriorityComponent
public class ActorVariableComponent extends WorldComponent {

    /**
     * Represents Integer Variable Values
     */
    private Map<Integer, Integer> INT_VARS = new ConcurrentHashMap<>();

    /**
     * Represents String Variable Values
     */
    private Map<Integer, String> STR_VARS = new ConcurrentHashMap<>();

    /**
     * Represents Boolean Variable Values
     */
    private Map<Integer, Boolean> BOOL_VARS = new ConcurrentHashMap<>();


    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public ActorVariableComponent(Object gameObject) {
        super(gameObject);

        for (Map.Entry<Integer, String> type : PlayerVariableRepository.getVarInts().entrySet()) {
            INT_VARS.put(type.getKey(), PlayerVariableRepository.getVarIntsDefaults().get(type.getKey()));
        }
        for (Map.Entry<Integer, String> type : PlayerVariableRepository.getVarStrs().entrySet()) {
            STR_VARS.put(type.getKey(), "Unavailable");
        }
        for (Map.Entry<Integer, String> type : PlayerVariableRepository.getVarBools().entrySet()) {
            BOOL_VARS.put(type.getKey(), false);
        }
    }



    /**
     * Sets an Integer Variable
     * @param varId
     * @param value
     */
    public void setVarInt(int varId, int value, boolean sync) {
        INT_VARS.put(varId, value);
        if (sync) {
            if (isNPC()) {
                for (Player player : component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllPlayers()) {
                    player.sendMessage(PacketOuterClass.Opcode.SMSG_LNPC_VAR_INT, World.ActorCharVarInt.newBuilder().setClientIndex(getNPC().getClientIndex()).setId(varId).setValue(value).build());
                }
            } else if (isPlayer()) {
                for (Player player : component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllPlayers()) {
                    if (player == getPlayer()) continue;//We skip ours, because we will set our own, using 1 less byte.
                    player.sendMessage(PacketOuterClass.Opcode.SMSG_LCHARACTER_VAR_INT, World.ActorCharVarInt.newBuilder().setClientIndex(getPlayer().getClientIndex()).setId(varId).setValue(value).build());
                }
                sendMessage(PacketOuterClass.Opcode.SMSG_CHAR_VAR_INT, World.CharVarInt.newBuilder().setId(varId).setValue(value).build());
            }
        } else
            sendMessage(PacketOuterClass.Opcode.SMSG_CHAR_VAR_INT, World.CharVarInt.newBuilder().setId(varId).setValue(value).build());
    }

    /**
     *
     * @param reference
     * @param value
     */
    public void setVarInt(String reference, int value) {
        setVarInt(reference, value, false);
    }

    /**
     *
     * @param reference
     * @param value
     * @param sync
     */
    public void setVarInt(String reference, int value, boolean sync) {
        for (Map.Entry<Integer, String> type : PlayerVariableRepository.getVarInts().entrySet()) {
            if (reference.replace(" ", "_").toLowerCase().equalsIgnoreCase(type.getValue())) {
                setVarInt(type.getKey(), value, sync);
                return;
            }
        }
    }

    /**
     * Increments a Var Int + 1 value for the variable with the provided var id.
     * @param varId
     */
    public void incrementVarInt(int varId) {
        incrementVarInt(varId, false);
    }

    /**
     * Increments a Var Int + 1 value for the variable with the provided var id.
     * @param varId
     */
    public void incrementVarInt(int varId, boolean sync) {
        setVarInt(varId, getVarInt(varId) + 1, sync);
    }

    /**
     * Increments a Var Int + 1 value for the variable with the provided var name
     * @param reference
     */
    public void incrementVarInt(String reference) {
        incrementVarInt(reference, false);
    }

    /**
     * Increments a Var Int + 1 value for the variable with the provided var name
     * @param reference
     * @param sync
     */
    public void incrementVarInt(String reference, boolean sync) {
        setVarInt(reference, getVarInt(reference) + 1, sync);
    }

    /**
     * Increments a Var Int with the Specified Value
     * @param reference
     * @param value
     */
    public void incrementVarInt(String reference, int value) {
        setVarInt(reference, getVarInt(reference) + value);
    }

    /**
     * Increments a Var Int with the Specified Value
     * @param reference
     * @param value
     */
    public void incrementVarInt(String reference, int value, boolean sync) {
        setVarInt(reference, getVarInt(reference) + value, sync);
    }

    /**
     * Decrements a variable with the default value of 1
     * @param reference
     */
    public void decrementVarInt(String reference) {
        decrementVarInt(reference, 1);
    }

    /**
     * Decrements a Var int with the specified value
     * @param reference
     * @param value
     */
    public void decrementVarInt(String reference, int value) {
        setVarInt(reference, getVarInt(reference) - value);
    }

    /**
     * Decrements a variable with the default value of 1
     * @param reference
     * @param sync
     */
    public void decrementVarInt(String reference, boolean sync) {
        decrementVarInt(reference, 1, sync);
    }

    /**
     * Decrements a Var int with the specified value
     * @param reference
     * @param value
     * @param sync
     */
    public void decrementVarInt(String reference, int value, boolean sync) {
        setVarInt(reference, getVarInt(reference) - value, sync);
    }

    /**
     *
     * @param varId
     * @return
     */
    public int getVarInt(int varId) {
        if (INT_VARS.get(varId) == null) return -1;
        return INT_VARS.get(varId);
    }

    /**
     *
     * @param reference
     * @return
     */
    public int getVarInt(String reference) {
        for (Map.Entry<Integer, String> type : PlayerVariableRepository.getVarInts().entrySet()) {
            if (reference.replace(" ", "_").toLowerCase().equalsIgnoreCase(type.getValue()))
                return getVarInt(type.getKey());
        }
        return -1;
    }

    /**
     * Sets a String Variable
     * @param varId
     * @param value
     */
    public void setVarString(int varId, String value, boolean sync) {
        STR_VARS.put(varId, value);
        if (sync) {
            if (isNPC()) {
                for (Player player : component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllPlayers()) {
                    player.sendMessage(PacketOuterClass.Opcode.SMSG_LNPC_VAR_STRING, World.ActorCharVarString.newBuilder().setClientIndex(getNPC().getClientIndex()).setId(varId).setValue(value).build());
                }
            } else if (isPlayer()) {
                for (Player player : component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllPlayers()) {
                    if (player == getPlayer()) continue;//We skip ours, because we will set our own, using 1 less byte.
                    player.sendMessage(PacketOuterClass.Opcode.SMSG_LCHARACTER_VAR_STRING, World.ActorCharVarString.newBuilder().setClientIndex(getPlayer().getClientIndex()).setId(varId).setValue(value).build());
                }
                sendMessage(PacketOuterClass.Opcode.SMSG_CHAR_VAR_STRING, World.CharVarString.newBuilder().setId(varId).setValue(value).build());
            }
        } else
            sendMessage(PacketOuterClass.Opcode.SMSG_CHAR_VAR_STRING, World.CharVarString.newBuilder().setId(varId).setValue(value).build());
    }

    /**
     *
     * @param varId
     * @return
     */
    public String getVarString(int varId) {
        if (STR_VARS.get(varId) == null) return "Unavailable";
        return STR_VARS.get(varId);
    }

    /**
     *
     * @param reference
     * @param value
     */
    public void setVarString(String reference, String value) {
        setVarString(reference, value, false);
    }

    /**
     *
     * @param reference
     * @param value
     */
    public void setVarString(String reference, String value, boolean sync) {
        for (Map.Entry<Integer, String> type : PlayerVariableRepository.getVarStrs().entrySet()) {
            if (reference.replace(" ", "_").toLowerCase().equalsIgnoreCase(type.getValue())) {
                setVarString(type.getKey(), value, sync);
                return;
            }
        }
    }

    /**
     * Gets the Var Boolean
     * @param varId
     * @return the var value
     */
    public boolean getVarBool(int varId) {
        if (BOOL_VARS.get(varId) == null) return false;
        return BOOL_VARS.get(varId);
    }

    /**
     *
     * @param reference
     * @param value
     */
    public void setVarBool(String reference, boolean value) {
        setVarBool(reference, value, false);
    }

    /**
     *
     * @param reference
     * @param value
     * @param sync
     */
    public void setVarBool(String reference, boolean value, boolean sync) {
        for (Map.Entry<Integer, String> type : PlayerVariableRepository.getVarBools().entrySet()) {
            if (reference.replace(" ", "_").toLowerCase().equalsIgnoreCase(type.getValue())) {
                setVarBool(type.getKey(), value, sync);
                return;
            }
        }
    }

    /**
     * Sets a String Variable
     * @param varId
     * @param value
     */
    public void setVarBool(int varId, boolean value, boolean sync) {
        BOOL_VARS.put(varId, value);
        if (sync) {
            if (isNPC()) {
                for (Player player : component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllPlayers()) {
                    player.sendMessage(PacketOuterClass.Opcode.SMSG_LNPC_VAR_BOOL, World.ActorCharVarBool.newBuilder().setClientIndex(getNPC().getClientIndex()).setId(varId).setValue(value).build());
                }
            } else if (isPlayer()) {
                for (Player player : component(GameObjectComponents.ZONE_COMPONENT).getNode().getAllPlayers()) {
                    if (player == getPlayer()) continue;//We skip ours, because we will set our own, using 1 less byte.
                    player.sendMessage(PacketOuterClass.Opcode.SMSG_LCHARACTER_VAR_BOOL, World.ActorCharVarBool.newBuilder().setClientIndex(getPlayer().getClientIndex()).setId(varId).setValue(value).build());
                }
                sendMessage(PacketOuterClass.Opcode.SMSG_CHAR_VAR_BOOL, World.CharVarBool.newBuilder().setId(varId).setValue(value).build());
            }
        } else
            sendMessage(PacketOuterClass.Opcode.SMSG_CHAR_VAR_BOOL, World.CharVarBool.newBuilder().setId(varId).setValue(value).build());
    }


    /**
     *
     * @param reference
     * @return
     */
    public String getVarString(String reference) {
        for (Map.Entry<Integer, String> type : PlayerVariableRepository.getVarStrs().entrySet()) {
            if (reference.replace(" ", "_").toLowerCase().equalsIgnoreCase(type.getValue()))
                return getVarString(type.getKey());
        }
        return "Unavailable";
    }

    /**
     * Gets the var bool
     * @param reference
     * @return
     */
    public boolean getVarBool(String reference) {
        for (Map.Entry<Integer, String> type : PlayerVariableRepository.getVarBools().entrySet()) {
            if (reference.replace(" ", "_").toLowerCase().equalsIgnoreCase(type.getValue()))
                return getVarBool(type.getKey());
        }
        return false;
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
        if (isPlayer()) {
            for (Map.Entry<Integer, Integer> intVals : INT_VARS.entrySet()) {
                if (intVals == null) continue;
                getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_CHAR_VAR_INT, World.CharVarInt.newBuilder().setId(intVals.getKey()).setValue(intVals.getValue()).build());
            }
            for (Map.Entry<Integer, String> strVals : STR_VARS.entrySet()) {
                if (strVals == null) continue;
                getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_CHAR_VAR_STRING, World.CharVarString.newBuilder().setId(strVals.getKey()).setValue(strVals.getValue()).build());
            }
            for (Map.Entry<Integer, Boolean> boolVars : BOOL_VARS.entrySet()) {
                if (boolVars == null) continue;
                getPlayer().sendMessage(PacketOuterClass.Opcode.SMSG_CHAR_VAR_BOOL, World.CharVarBool.newBuilder().setId(boolVars.getKey()).setValue(boolVars.getValue()).build());
            }
        }
    }

    /**
     * Sends a Player Var Integer Variable
     * @param clientIndex
     * @param variable
     * @param value
     */
    public void sendPlayerVarInt(int clientIndex, String variable, int value) {
        int varId = PlayerVariableRepository.forVarIntId(variable);
        if (varId > 0) {
            sendMessage(PacketOuterClass.Opcode.SMSG_LCHARACTER_VAR_INT, World.ActorCharVarInt.newBuilder().setClientIndex(clientIndex).setId(varId).setValue(value).build());
        }
    }

    /**
     * Sends a Player Var String Variable
     * @param clientIndex
     * @param variable
     * @param value
     */
    public void sendPlayerVarString(int clientIndex, String variable, String value) {
        int varId = PlayerVariableRepository.forVarStrId(variable);
        if (varId > 0) {
            sendMessage(PacketOuterClass.Opcode.SMSG_LCHARACTER_VAR_STRING, World.ActorCharVarString.newBuilder().setClientIndex(clientIndex).setId(varId).setValue(value).build());
        }
    }

    /**
     * Sends a Player Var Boolean Variable
     * @param clientIndex
     * @param variable
     * @param value
     */
    public void sendPlayerVarBool(int clientIndex, String variable, boolean value) {
        int varId = PlayerVariableRepository.forVarBoolId(variable);
        if (varId > 0) {
            sendMessage(PacketOuterClass.Opcode.SMSG_LCHARACTER_VAR_BOOL, World.ActorCharVarBool.newBuilder().setClientIndex(clientIndex).setId(varId).setValue(value).build());
        }
    }

    /**
     * Sends a NPC Var Integer Variable
     * @param clientIndex
     * @param variable
     * @param value
     */
    public void sendNPCVarInt(int clientIndex, String variable, int value) {
        int varId = PlayerVariableRepository.forVarIntId(variable);
        if (varId > 0) {
            sendMessage(PacketOuterClass.Opcode.SMSG_LNPC_VAR_INT, World.ActorCharVarInt.newBuilder().setClientIndex(clientIndex).setId(varId).setValue(value).build());
        }
    }

    /**
     * Sends a NPC Var String Variable
     * @param clientIndex
     * @param variable
     * @param value
     */
    public void sendNPCVarString(int clientIndex, String variable, String value) {
        int varId = PlayerVariableRepository.forVarStrId(variable);
        if (varId > 0) {
            sendMessage(PacketOuterClass.Opcode.SMSG_LNPC_VAR_STRING, World.ActorCharVarString.newBuilder().setClientIndex(clientIndex).setId(varId).setValue(value).build());
        }
    }

    /**
     * Sends a NPC Var Boolean Variable
     * @param clientIndex
     * @param variable
     * @param value
     */
    public void sendNPCVarBool(int clientIndex, String variable, boolean value) {
        int varId = PlayerVariableRepository.forVarBoolId(variable);
        if (varId > 0) {
            sendMessage(PacketOuterClass.Opcode.SMSG_LNPC_VAR_BOOL, World.ActorCharVarBool.newBuilder().setClientIndex(clientIndex).setId(varId).setValue(value).build());
        }
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
        return "variables";
    }

    @Override
    public WhereConditionOption[] getFetchConditions() {
        return new WhereConditionOption[] { new WhereConditionOption("characterId", getPlayer().getId()) };
    }

    @Override
    public WhereConditionOption[] getUpdateConditions() {
        return new WhereConditionOption[] { new WhereConditionOption("characterId", getPlayer().getId()) };
    }

    @Override
    public boolean insert() {
        try {
            List<MySQLColumn> columns = new ArrayList<>();

            StringBuilder varInt = new StringBuilder();
            StringBuilder varStr = new StringBuilder();
            StringBuilder varBool = new StringBuilder();

            for (Map.Entry<Integer, Integer> varInts : INT_VARS.entrySet()) {
                varInt.append(varInts.getKey()).append("=").append(varInts.getValue()).append(",");
            }

            for (Map.Entry<Integer, String> varStrs : STR_VARS.entrySet()) {
                varStr.append(varStrs.getKey()).append("=").append(varStrs.getValue()).append(",");
            }

            for (Map.Entry<Integer, Boolean> varBools : BOOL_VARS.entrySet()) {
                varBool.append(varBools.getKey()).append("=").append(varBools.getValue()).append(",");
            }

            columns.add(new MySQLColumn("var_int", varInt.toString()));

            columns.add(new MySQLColumn("var_str", varStr.toString()));

            columns.add(new MySQLColumn("var_bool", varBool.toString()));

            columns.add(new MySQLColumn("characterId", getPlayer().getId()));
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


            StringBuilder varInt = new StringBuilder();
            StringBuilder varStr = new StringBuilder();
            StringBuilder varBool = new StringBuilder();

            for (Map.Entry<Integer, Integer> varInts : INT_VARS.entrySet()) {
                varInt.append(varInts.getKey()).append("=").append(varInts.getValue()).append(",");
            }
            for (Map.Entry<Integer, String> varStrs : STR_VARS.entrySet()) {
                varStr.append(varStrs.getKey()).append("=").append(varStrs.getValue()).append(",");
            }
            for (Map.Entry<Integer, Boolean> varBools : BOOL_VARS.entrySet()) {
                varBool.append(varBools.getKey()).append("=").append(varBools.getValue()).append(",");
            }

            if (!varInt.toString().isEmpty())
                columns.add(new MySQLColumn("var_int", varInt.toString()));

            if (!varStr.toString().isEmpty())
                columns.add(new MySQLColumn("var_str", varStr.toString()));

            if (!varBool.toString().isEmpty())
                columns.add(new MySQLColumn("var_bool", varBool.toString()));

            entry(getDatabaseName(), getTableName(), columns, MySQLCommand.UPDATE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean fetch() {
        ResultSet resultSet = getResults();
        try {
            if (resultSet.next()) {
                String varInts = resultSet.getString("var_int");
                String varStrs = resultSet.getString("var_str");
                String varBools = resultSet.getString("var_bool");

                String[] varI = varInts.split(",");
                for (String x : varI) {
                    String[] result = x.split("=");
                    int id = Integer.parseInt(result[0]);
                    int value = Integer.parseInt(result[1]);
                    INT_VARS.put(id, value);
                }

                if (!varStrs.isEmpty()) {
                    String[] varS = varStrs.split(",");
                    for (String x : varS) {
                        String[] result = x.split("=");
                        int id = Integer.parseInt(result[0]);
                        String value = result[1];
                        STR_VARS.put(id, value);
                    }
                }
                if (!varBools.isEmpty()) {
                    String[] varB = varBools.split(",");
                    for (String x : varB) {
                        String[] result = x.split("=");
                        int id = Integer.parseInt(result[0]);
                        boolean value = Boolean.parseBoolean(result[1]);
                        BOOL_VARS.put(id, value);
                    }
                }
                return true;
            } else {
                insert();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
