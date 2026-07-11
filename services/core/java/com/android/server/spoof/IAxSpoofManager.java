package com.android.server.spoof;

public interface IAxSpoofManager {
    default void systemReady() {}
    String getGamePropsConfig();
    String getPifSpoofPhotos();
}
