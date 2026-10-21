package com.intecx.inscope.service;

public interface BunnyStorageService {

    void upload(String storagePath, byte[] content, String contentType);

    byte[] download(String storagePath);

    void delete(String storagePath);
}
