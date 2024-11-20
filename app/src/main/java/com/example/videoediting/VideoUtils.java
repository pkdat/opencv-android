package com.example.videoediting;

import android.media.MediaMetadataRetriever;

import java.io.IOException;

public class VideoUtils {
    public static double[] getVideoProperties(String videoPath) {
        try(MediaMetadataRetriever retriever = new MediaMetadataRetriever()) {
            retriever.setDataSource(videoPath);

            double width = Double.parseDouble(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            double height = Double.parseDouble(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            double fps = Double.parseDouble(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE));

            return new double[] {width, height, fps};
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
