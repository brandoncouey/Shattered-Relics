package com.shattered.datatable.tables.types;

import lombok.Data;

import java.util.Locale;

@Data
public class VariableRequirement {

    /**
     * Represents the Var Type
     */
    public enum VarType { Int, String, Boolean }

    /**
     * Represents the name of the variable
     */
    private String name;

    /**
     * Represents the type of the variable
     */
    private VarType type = VarType.Int;

    /**
     * Represents the value of the variable
     */
    private String value;

    /**
     * Represents the description of the variable required
     */
    private String description;

    /**
     * Converts Type to VarType
     * @param type
     * @return the var type
     */
    public static VarType forType(String type) {
        for (VarType vt : VarType.values()) {
            if (vt.name().equalsIgnoreCase(type))
                return vt;

        }
        return VarType.Int;
    }
}
