package com.shattered.http.discord;


import java.io.IOException;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;


/**
 * @author Brad
 */
public class DiscordAPI {

    /**
     * Represents the Discord API Link
     */
    public static final String BASE_URI = "https://discord.com/api";


    /**
     * Converts the GSON Object to The Discord Type Object
     * @param str
     * @param clazz
     * @param <T>
     * @return the discord type object
     */
    private static <T> T toObject(String str, Class<T> clazz)
    {
        return new GsonBuilder().serializeNulls().enableComplexMapKeySerialization().create().fromJson(str, clazz);
    }


    /**
     * Sets the Headers for the Discord API + Access Token
     * @param request
     * @throws IOException
     */
    private static void setHeaders(org.jsoup.Connection request, String accessToken) throws IOException
    {
        request.header("Authorization", "Bearer " + accessToken);
    }

    /**
     * Attempts to get the discord user path
     * @param path
     * @return the http request
     * @throws IOException
     */
    private static String handleGet(String path, String accessToken) throws IOException
    {
        org.jsoup.Connection request = Jsoup.connect(BASE_URI + path).ignoreContentType(true);
        setHeaders(request, accessToken);

        return request.get().body().text();
    }

    /**
     * Fetches the User
     * @return the discord user
     * @throws IOException
     */
    public static DiscordUser fetchUser(String accessToken) throws IOException
    {
        return toObject(handleGet("/users/@me", accessToken), DiscordUser.class);
    }



}
