package com.urbaneats.service;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GcloudStorageService {

    private final String projectId = "urban-eats-30";
    private final String bucketName = "urban_eats-1";

    Map<String, String> getFoodImageUrlMap(String folderName) {

        Map<String, String> imageUrlMap = new HashMap<>();

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        Page<Blob> blobs =
                storage.list(
                        bucketName,
                        Storage.BlobListOption.prefix("urban_eats/" + folderName),
                        Storage.BlobListOption.currentDirectory());

        Iterable<Blob> values = blobs.getValues();

        for(Blob blob: values) {
            String mediaLink = blob.getMediaLink();
//            Had to change split[1] to split[2] after changing google account
            Try<String> imageId = Try.of(() -> blob.getName().split("/")[2].split("\\.")[0])
//                    .onFailure(throwable -> log.error("improper image url found, not able to able image id out of the url by the split algo in getFoodImageUrlMap: {}", blob.getName()))
                    ;

            if(imageId.isFailure()) continue;

            imageUrlMap.put(imageId.get(), mediaLink);
        }
        return imageUrlMap;
    }

    public String getImageIdForFetchingImageUrl(String imageId) {
        Try<String> imageIdFromDb = Try.of(() -> {
            String[] imgIdSplitArray = imageId.split("/");

            if(imgIdSplitArray.length == 1) return imgIdSplitArray[0];                  //for the cases where the exact imageId is present in the table
            return imgIdSplitArray[imgIdSplitArray.length - 1].split("\\.")[0];
        }).onFailure(throwable -> log.error("Failed to split imageId from db. ImageId: {}, error: {}", imageId, throwable.getMessage()));

        if(imageIdFromDb.isFailure())
            return StringUtils.EMPTY;

        return imageIdFromDb.get();
    }
}
