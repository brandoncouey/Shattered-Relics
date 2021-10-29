package com.shattered.utilities.cryptography;

import java.io.IOException;
import java.security.*;
import java.util.Base64;

public class GenerateKeyPair {


    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1048);
        KeyPair pair = keyGen.generateKeyPair();
        System.out.println("Public Key Pair=" + Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()));
        System.out.println("Private Key Pair=" + Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()));
        System.out.println(BCrypt.hashpw("$2y$10$qe8W3R55F3LlC8MAHdqx..u68PJSHaqZRTRTOTYVTksjx36lnSauK", BCrypt.gensalt()));
    }
}
