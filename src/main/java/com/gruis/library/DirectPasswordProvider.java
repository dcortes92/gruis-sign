package com.gruis.library;

import xades4j.providers.impl.KeyStoreKeyingDataProvider;

import java.security.cert.X509Certificate;

public class DirectPasswordProvider implements KeyStoreKeyingDataProvider.KeyStorePasswordProvider, KeyStoreKeyingDataProvider.KeyEntryPasswordProvider {
    private String password;
    DirectPasswordProvider(String password) {
        this.password = password;
    }
    public char[] getPassword() {
        return password.toCharArray();
    }
    public char[] getPassword(String entryAlias, X509Certificate entryCert) {
        return password.toCharArray();
    }
}
