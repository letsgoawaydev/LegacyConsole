package com.letsgoaway.legacyconsole.resourcepack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Map;

import javax.crypto.KeyGenerator;

import org.bukkit.entity.Player;



import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.*;
import fi.iki.elonen.NanoHTTPD.Response.Status;


public class ResourcePackServer extends NanoHTTPD {
    public static String url = "http://localhost:8080/";

    public ResourcePackServer() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
    }

    public void sendPack(Player player, String pack) {
        try
        {
            player.setResourcePack(url + "?pack=" + pack, createSha1(new File("plugins/LegacyConsole/packs/" + pack + ".zip")));
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }

    }

    public static void main(String[] args) {
        try
        {
            new ResourcePackServer();
        }
        catch (IOException ioe)
        {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    public byte[] createSha1(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        InputStream fis = new FileInputStream(file);
        int n = 0;
        byte[] buffer = new byte[8192];
        while (n != -1)
        {
            n = fis.read(buffer);
            if (n > 0)
            {
                digest.update(buffer, 0, n);
            }
        }
        return digest.digest();
    }

    @Override
    public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parameters, Map<String, String> files) {
        String answer = "";

        FileInputStream fis = null;
        File file;
        switch (parameters.get("pack")) {
            case "battle":
                file = new File("plugins/LegacyConsole/packs/battle.zip");
            default:
                file = new File("plugins/LegacyConsole/packs/battle.zip");
        }

        try
        {
            fis = new FileInputStream(file);

        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return newFixedLengthResponse(Status.OK, "application/zip", fis, file.length());
    }
}