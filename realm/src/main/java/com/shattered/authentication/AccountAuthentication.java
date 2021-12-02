package com.shattered.authentication;

import com.shattered.ServerConstants;
import com.shattered.account.AccountInformation;
import com.shattered.database.mysql.MySQLEntry;
import com.shattered.database.mysql.MySQLFetch;
import com.shattered.account.responses.AccountResponses;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;

/**
 * @author JTlr Frost 7/25/18 : 12:47 PM
 */
@Data
@RequiredArgsConstructor
public class AccountAuthentication implements MySQLFetch, MySQLEntry {

    /**
     * Represents the Login Account Identification Type
     */
    private enum AccountIdentificationType {

        /**
         * Represents the Player Entered in an Account Name
         */
        NAME,

        /**
         * Represents the Player Entered in an Email
         */
        EMAIL,

        /**
         * Represents the Player is With Discord
         */
        DISCORD
    }

    /**
     * Represents the Account profile the authentication will provide
     */
    private AccountInformation informationResult;

    /**
     * Represents the Account name of Authentication
     */
    private final String accountRequest;

    /**
     * Represents the Account Password
     */
    private final String passwordRequest;

    /**
     * Represents if Login with Discord
     */
    private final boolean isDiscord;

    /**
     * Represents the Account Identification Type
     */
    private AccountIdentificationType identificationType;

    /**
     * Represents the Response Type for Authentication
     */
    private AccountResponses responseType;

    /**
     * Represents the Fetch Condition Options
     */
    private WhereConditionOption[] conditions;


    /**
     * Database name.
     */
    @Override
    public String getDatabaseName() {
        return "grizzly";
    }

    /**
     * Table name.
     */
    @Override
    public String getTableName() {
        return "users";
    }

    /**
     * The Gets the Entries
     */
    @Override
    public boolean fetch() {
        try {

            //Checks for the # name identifier, and sets the identification type, as well as the conditions to search for
            if (getAccountRequest().contains("@")) {
                setIdentificationType(AccountIdentificationType.EMAIL);
                setConditions(new WhereConditionOption[] { new WhereConditionOption("email", getAccountRequest()) });
            } else {
                setIdentificationType(AccountIdentificationType.NAME);
                setConditions(new WhereConditionOption[] { new WhereConditionOption("username", getAccountRequest()) });
            } /*else if (isDiscord) {

                try {
                    DiscordUser user = DiscordAPI.fetchUser(getAccountRequest());
                    if (user != null) {
                        String discordId = user.getId();
                        setIdentificationType(AccountIdentificationType.DISCORD);
                        setConditions(new WhereConditionOption[]{new WhereConditionOption("discord_id", discordId)});
                    }
                } catch (Exception e) {
                    setResponseType(AccountResponses.INVALID_ACCESS_TOKEN);
                }
            }*/

            //Fetches the Results and checks if they are valid
            ResultSet results = getResults();

            //If no results, it will set to not found.
            if (!hasResults()) {
                switch (identificationType) {
                    case DISCORD:
                        setResponseType(AccountResponses.DISCORD_NOT_LINKED);
                        break;
                    default:
                        setResponseType(AccountResponses.ACCOUNT_NOT_FOUND);
                        break;
                }
            }

            if (results.next()) {

                /* //////////////////////// Represents Account Name Checking \\\\\\\\\\\\\\\\\\\\\\\\\ */

                // Represents the Database Account Name / Identifier
                String accountIdentifier = null;

                //Represents the Account Id aka `PRIMARY KEY` Index
                int accountId = results.getInt("id");

                //Represents the Name of the Account
                String accountName = results.getString("username");

                //Represents the Email of the Account
                String accountEmail = results.getString("email");

                //Represents the Discord Id
                String discord_id = results.getString("discord_id");


                // Checks the Identifier and Sets the Type to search for name or email
                if (getIdentificationType() == AccountIdentificationType.NAME)
                    accountIdentifier = accountName;
                 else
                    accountIdentifier = accountEmail;


                //Checks to ensure the name matches the datatable name
                /*if (!accountIdentifier.equalsIgnoreCase(getAccountRequest()) && !isDiscord) {

Jobs in run #20211202.3
Start
CmdLine

View raw log


                    setResponseType(AccountResponses.ACCOUNT_INVALID_INFORMATION);
                    return false;
                }*/

                /* //////////////////////// Represents Password Checking \\\\\\\\\\\\\\\\\\\\\\\\\ */

                //Checks the Password
                String password = results.getString("password");
                if (ServerConstants.LIVE_DB) {
                   /* if (!BCrypt.checkpw(getPasswordRequest(), password)) {
                        setResponseType(AccountResponses.ACCOUNT_INVALID_INFORMATION);
                        return false;
                    }*/
                }

                /* //////////////////////// Represents Online Status Checking \\\\\\\\\\\\\\\\\\\\\\\\\ */

                //Checks the Online Status
                /*if (isOnline(accountId)) {
                    setResponseType(AccountResponses.ACCOUNT_ACTIVE_SESSION);
                    return false;
                }*/
                
                //Represents the Account level
                String rank = results.getString("rank");

                /* If the response type is currently null (ok) it will result the object */
                if (getResponseType() == null) {
                    setInformationResult(new AccountInformation(accountName, null, accountEmail, password, AccountInformation.AccountLevel.forRank(rank)));

                    //Ensures the Account gets assigned the Current Id `PRIMARY KEY`
                    getInformationResult().setAccountId(accountId);
                }
            }
        } catch (Exception e) {
            setResponseType(AccountResponses.ERR_LOADING_ACCOUNT);
            e.printStackTrace();
        }
        return true;
    }


    /**
     * Gets the fetching conditions for the result set.
     */
    @Override
    public WhereConditionOption[] getFetchConditions() {
        return conditions;
    }



}
