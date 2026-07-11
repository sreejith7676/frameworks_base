package com.android.server;

import com.android.server.spoof.IAxSpoofManager;

public interface IAxExtServiceFactory {
    enum ExtType {
        AX_SPOOF_MANAGER(IAxSpoofManager.class);
        
        private final Class<?> clazz;
        ExtType(Class<?> clazz) { this.clazz = clazz; }
        public Class<?> getClazz() { return clazz; }
    }
}
