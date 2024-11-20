package com.example.videoediting;

import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;

public class VideoProcessor {

    public static void processVideo(String inputPath, String outputPath) {
        // Initialize VideoCapture to read the video
        VideoCapture videoCapture = new VideoCapture(inputPath);

        if (!videoCapture.isOpened()) {
            Log.d("khac.dat", "Error: Cannot open video file.");
            return;
        }

        Log.d("khac.dat", "Get Video properties");
        // Get video properties
        double[] videoProps = VideoUtils.getVideoProperties(inputPath);
        int frameWidth = (int) videoProps[0];
        int frameHeight = (int) videoProps[1];
        double fps = videoProps[2];
        Log.d("khac.dat", "w: " + frameWidth + ", h: " + frameHeight + ", fps: " + fps);

        // Initialize VideoWriter to save the processed video
        VideoWriter videoWriter = new VideoWriter(
                outputPath,
                VideoWriter.fourcc('M', 'J', 'P', 'G'), // Codec (e.g., XVID, MJPG)
                fps,
                new Size(frameWidth, frameHeight),
                false // False for grayscale output
        );

        if (!videoWriter.isOpened()) {
            Log.d("khac.dat","Error: Cannot open video writer.");
            videoCapture.release();
            return;
        }

        Mat frame = new Mat();


        int currentFrame = 0;
        while (videoCapture.read(frame)) {
            Log.d("khac.dat", "channels: " + frame.channels() + ", " + frame.rows() + ", " +frame.cols());
//            Mat grayFrame = new Mat();
//            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
//             Imgproc.resize(frame, grayFrame, new Size(frameWidth, frameHeight));;

            videoWriter.write(frame);

            // Print progress
            currentFrame++;
            Log.d("khac.dat","Processing frame " + currentFrame);
        }

        // Release resources
        videoCapture.release();
        videoWriter.release();

        Log.d("khac.dat","Video processing completed.");
    }
}

