package com.shattered.http.discord;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author Brad
 */
@Data
public class DiscordUser {

    /**
     * Represents the Premium Type
     */
    public enum PremiumType { NONE, NITRO_CLASSIC, NITRO }

    /**
     * Represents the Discord UUID
     */
    private String id;

    /**
     * Represents the Discord Username Without Discriminator
     */
    private String username;

    /**
     * Represents their avatar
     */
    private String avatar;

    /**
     * Represents their #XXXX Tag
     */
    private String discriminator;

    /**
     * Represents if they have MFA Enabled
     */
    @SerializedName("mfa_enabled")
    private Boolean mfaEnabled;

    /**
     * Represents their Locale (Location)
     */
    private String locale;

    /**
     * Represents their discord premium type
     */
    @SerializedName("premium_type")
    private Integer premiumType;

    /**
     * Combines discord username with discriminator
     * @return full username
     */
    public String getFullUsername()
    {
        return username + "#" + discriminator;
    }

    /**
     * Gets their Discord Premium Type
     * @return premium tier
     */
    public PremiumType forType() {
        switch (premiumType) {
            case 0:
                return PremiumType.NONE;
            case 1:
                return PremiumType.NITRO_CLASSIC;
            case 2:
                return PremiumType.NITRO;
            default:
                return PremiumType.NONE;
        }
    }
}
