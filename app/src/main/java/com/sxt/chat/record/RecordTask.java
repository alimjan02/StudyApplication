package com.sxt.chat.record;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.sxt.chat.App;
import com.sxt.chat.utils.Prefs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by sxt on 2019/2/28.
 */
public class RecordTask extends AsyncTask<String, Void, String> {

    private RecordCallBackListener recordCallBackListener;
    private MediaMuxer mediaMuxer;
    private String RECORD_FOLDER;
    private String AUDIO_FOLDER;
    private String TAG = "Record";
    private String resultAudioPath;

    public RecordTask() {
        RECORD_FOLDER = Prefs.getInstance(App.getCtx()).getRecordFolder();
        AUDIO_FOLDER = Prefs.getInstance(App.getCtx()).getAudioFolder();

        File recordFolder = new File(RECORD_FOLDER);
        if (!recordFolder.exists()) {
            recordFolder.mkdirs();
        }
        File audioFolder = new File(AUDIO_FOLDER);
        if (!audioFolder.exists()) {
            audioFolder.mkdirs();
        }
    }

    @Override
    protected String doInBackground(final String... strings) {
        resultAudioPath = null;
        if (strings != null && strings.length > 1) {
            //遍历录音文件夹中的所有音频文件,筛选出本次工单ID的文件
            //每次的录音 唯一标识为 :   工单id + 时间戳 , 方便上传的时候拼接为一个录音文件
            final String record_ID_CARD = String.format("%s-", strings[1]);
            File recordFolder = new File(strings[0]);
            if (recordFolder.exists()) {
                ArrayList<String> recordFiles = new ArrayList<>();
                File[] files = recordFolder.listFiles();
                for (File file1 : files) {
                    if (file1.getName().contains(record_ID_CARD) && file1.getName().endsWith(".mp4")) {
                        recordFiles.add(file1.getPath());
                    }
                }
                if (recordFiles.size() > 0) {//本次工单有录制好的音频文件,根据录制的时间排序 , 升序
                    Collections.sort(recordFiles, new Comparator<String>() {
                        @Override
                        public int compare(String path1, String path2) {
                            String secondsStr0 = path1.substring(path1.indexOf(record_ID_CARD) + 1, path1.indexOf(".mp4"));
                            String secondsStr1 = path2.substring(path2.indexOf(record_ID_CARD) + 1, path2.indexOf(".mp4"));
                            long seconds0 = Long.parseLong(secondsStr0);
                            long seconds1 = Long.parseLong(secondsStr1);

                            if (seconds0 > seconds1) {
                                return -1;
                            } else if (seconds0 < seconds1) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                    List<String> audioPathList = new ArrayList<>();
                    for (String filePath : recordFiles) {
                        audioPathList.add(muxerAudio(filePath));
                        Log.e(TAG, "分离音频");
                    }

                    //合并的最终音频文件这里定为以工单ID为文件名
                    VideoCombineManager.getInstance().startVideoCombiner(audioPathList, AUDIO_FOLDER + File.separator + strings[1] + ".mp3", new VideoCombiner.VideoCombineListener() {
                        @Override
                        public void onCombineStart() {
                            Log.e(TAG, "合并音频 -> Start");
                        }

                        @Override
                        public void onCombineProcessing(int current, int sum) {
                            Log.e(TAG, "合并音频 -> " + ((((double) current) / sum) * 100) + "%");
                        }

                        @Override
                        public void onCombineFinished(boolean success, String mDestPath) {
                            Log.e(TAG, "合并音频 -> End ->" + success);
                            resultAudioPath = mDestPath;
                            if (success) {
                                //和并成功后,将产生的多余文件删除
                                deleteFolderFile(strings[0], true);
                            }
                        }
                    });

                }
            }
        }
        return resultAudioPath;
    }

    // 按目录删除文件夹文件方法
    private boolean deleteFolderFile(String filePath, boolean deleteThisPath) {
        try {
            File file = new File(filePath);
            if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (File file1 : files) {
                    deleteFolderFile(file1.getAbsolutePath(), true);
                }
            }
            if (deleteThisPath) {
                if (!file.isDirectory()) {
                    file.delete();
                } else {
                    if (file.listFiles().length == 0) {
                        file.delete();
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 分离
     */
    private void exactorMedia(String mp4FilePath) {
        MediaExtractor mediaExtractor = new MediaExtractor();
        FileOutputStream videoOutputStream = null;
        FileOutputStream audioOutputStream = null;
        try {
            File videoFile = new File(RECORD_FOLDER, "output_video.mp4");
            if (!videoFile.exists()) {
                videoFile.createNewFile();
            }

            File audioFile = new File(RECORD_FOLDER, "output_audio");
            videoOutputStream = new FileOutputStream(videoFile);
            audioOutputStream = new FileOutputStream(audioFile);
            mediaExtractor.setDataSource(mp4FilePath);
            int trackCount = mediaExtractor.getTrackCount();
            int audioTrackIndex = -1;
            int videoTrackIndex = -1;
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                Log.e(TAG, trackFormat.toString());
                String mineType = trackFormat.getString(MediaFormat.KEY_MIME);

                if (mineType.startsWith("video/")) {
                    videoTrackIndex = i;

                }

                if (mineType.startsWith("audio/")) {
                    audioTrackIndex = i;
                }
            }

            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            mediaExtractor.selectTrack(videoTrackIndex);
            while (true) {
                int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleCount < 0) {
                    break;
                }

                byte[] buffer = new byte[readSampleCount];
                byteBuffer.get(buffer);
                videoOutputStream.write(buffer);
                byteBuffer.clear();
                mediaExtractor.advance();
            }

            mediaExtractor.selectTrack(audioTrackIndex);
            while (true) {
                int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleCount < 0) {
                    break;
                }

                byte[] buffer = new byte[readSampleCount];
                byteBuffer.get(buffer);
                audioOutputStream.write(buffer);
                byteBuffer.clear();
                mediaExtractor.advance();
            }

            Log.e(TAG, "finish");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mediaExtractor.release();
            try {
                if (videoOutputStream != null) {
                    videoOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 分离视频
     */
    private void muxerMedia() {
        int videoIndex = -1;
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(RECORD_FOLDER + "/input.mp4");
            int trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                String mimeType = trackFormat.getString(MediaFormat.KEY_MIME);
                if (mimeType.startsWith("video/")) {
                    videoIndex = i;
                }
            }

            mediaExtractor.selectTrack(videoIndex);
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(videoIndex);
            mediaMuxer = new MediaMuxer(RECORD_FOLDER + "/output_video", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int trackIndex = mediaMuxer.addTrack(trackFormat);
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 500);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            mediaMuxer.start();
            long videoSampleTime;
            {
                mediaExtractor.readSampleData(byteBuffer, 0);
                //skip first I frame
                if (mediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC)
                    mediaExtractor.advance();
                mediaExtractor.readSampleData(byteBuffer, 0);
                long firstVideoPTS = mediaExtractor.getSampleTime();
                mediaExtractor.advance();
                mediaExtractor.readSampleData(byteBuffer, 0);
                long SecondVideoPTS = mediaExtractor.getSampleTime();
                videoSampleTime = Math.abs(SecondVideoPTS - firstVideoPTS);
                Log.d(TAG, "videoSampleTime is " + videoSampleTime);
            }

            mediaExtractor.unselectTrack(videoIndex);
            mediaExtractor.selectTrack(videoIndex);
            while (true) {
                int readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleSize < 0) {
                    break;
                }
                mediaExtractor.advance();
                bufferInfo.size = readSampleSize;
                bufferInfo.offset = 0;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.presentationTimeUs += videoSampleTime;

                mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo);
            }
            mediaMuxer.stop();
            mediaExtractor.release();
            mediaMuxer.release();

            Log.e(TAG, "finish");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分离音频
     */
    private String muxerAudio(String filePath) {
        int audioIndex = -1;
        String outPutAudioPath = null;
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(filePath);
            int trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                    audioIndex = i;
                }
            }
            mediaExtractor.selectTrack(audioIndex);
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(audioIndex);
            outPutAudioPath = RECORD_FOLDER + "/output_audio" + System.currentTimeMillis();
            mediaMuxer = new MediaMuxer(outPutAudioPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int writeAudioIndex = mediaMuxer.addTrack(trackFormat);
            mediaMuxer.start();
            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

            long stampTime = 0;
            //获取帧之间的间隔时间
            {
                mediaExtractor.readSampleData(byteBuffer, 0);
                if (mediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    mediaExtractor.advance();
                }
                mediaExtractor.readSampleData(byteBuffer, 0);
                long secondTime = mediaExtractor.getSampleTime();
                mediaExtractor.advance();
                mediaExtractor.readSampleData(byteBuffer, 0);
                long thirdTime = mediaExtractor.getSampleTime();
                stampTime = Math.abs(thirdTime - secondTime);
                Log.e(TAG, stampTime + "");
            }

            mediaExtractor.unselectTrack(audioIndex);
            mediaExtractor.selectTrack(audioIndex);
            while (true) {
                int readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleSize < 0) {
                    break;
                }
                mediaExtractor.advance();

                bufferInfo.size = readSampleSize;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.offset = 0;
                bufferInfo.presentationTimeUs += stampTime;

                mediaMuxer.writeSampleData(writeAudioIndex, byteBuffer, bufferInfo);
            }
            mediaMuxer.stop();
            mediaMuxer.release();
            mediaExtractor.release();
            Log.e(TAG, "finish");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        return outPutAudioPath;
    }

    /**
     * 合并
     */
    private void combineVideo() {
        try {
            MediaExtractor videoExtractor = new MediaExtractor();
            videoExtractor.setDataSource(RECORD_FOLDER + "/output_video");
            MediaFormat videoFormat = null;
            int videoTrackIndex = -1;
            int videoTrackCount = videoExtractor.getTrackCount();
            for (int i = 0; i < videoTrackCount; i++) {
                videoFormat = videoExtractor.getTrackFormat(i);
                String mimeType = videoFormat.getString(MediaFormat.KEY_MIME);
                if (mimeType.startsWith("video/")) {
                    videoTrackIndex = i;
                    break;
                }
            }

            MediaExtractor audioExtractor = new MediaExtractor();
            audioExtractor.setDataSource(RECORD_FOLDER + "/output_audio");
            MediaFormat audioFormat = null;
            int audioTrackIndex = -1;
            int audioTrackCount = audioExtractor.getTrackCount();
            for (int i = 0; i < audioTrackCount; i++) {
                audioFormat = audioExtractor.getTrackFormat(i);
                String mimeType = audioFormat.getString(MediaFormat.KEY_MIME);
                if (mimeType.startsWith("audio/")) {
                    audioTrackIndex = i;
                    break;
                }
            }

            videoExtractor.selectTrack(videoTrackIndex);
            audioExtractor.selectTrack(audioTrackIndex);

            MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
            MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();

            MediaMuxer mediaMuxer = new MediaMuxer(RECORD_FOLDER + "/output", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int writeVideoTrackIndex = mediaMuxer.addTrack(videoFormat);
            int writeAudioTrackIndex = mediaMuxer.addTrack(audioFormat);
            mediaMuxer.start();
            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            long sampleTime = 0;
            {
                videoExtractor.readSampleData(byteBuffer, 0);
                if (videoExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    videoExtractor.advance();
                }
                videoExtractor.readSampleData(byteBuffer, 0);
                long secondTime = videoExtractor.getSampleTime();
                videoExtractor.advance();
                long thirdTime = videoExtractor.getSampleTime();
                sampleTime = Math.abs(thirdTime - secondTime);
            }
            videoExtractor.unselectTrack(videoTrackIndex);
            videoExtractor.selectTrack(videoTrackIndex);

            while (true) {
                int readVideoSampleSize = videoExtractor.readSampleData(byteBuffer, 0);
                if (readVideoSampleSize < 0) {
                    break;
                }
                videoBufferInfo.size = readVideoSampleSize;
                videoBufferInfo.presentationTimeUs += sampleTime;
                videoBufferInfo.offset = 0;
                videoBufferInfo.flags = videoExtractor.getSampleFlags();
                mediaMuxer.writeSampleData(writeVideoTrackIndex, byteBuffer, videoBufferInfo);
                videoExtractor.advance();
            }

            while (true) {
                int readAudioSampleSize = audioExtractor.readSampleData(byteBuffer, 0);
                if (readAudioSampleSize < 0) {
                    break;
                }

                audioBufferInfo.size = readAudioSampleSize;
                audioBufferInfo.presentationTimeUs += sampleTime;
                audioBufferInfo.offset = 0;
                audioBufferInfo.flags = videoExtractor.getSampleFlags();
                mediaMuxer.writeSampleData(writeAudioTrackIndex, byteBuffer, audioBufferInfo);
                audioExtractor.advance();
            }

            mediaMuxer.stop();
            mediaMuxer.release();
            videoExtractor.release();
            audioExtractor.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String path) {
        super.onPostExecute(path);
        if (recordCallBackListener != null) {
            if (!TextUtils.isEmpty(path)) {
                recordCallBackListener.onSuccessed(path);
            } else {
                recordCallBackListener.onFailed("非法参数异常,请检查文件是否存在");
            }
        }
    }

    public RecordTask setRecordCallBackListener(RecordCallBackListener recordCallBackListener) {
        this.recordCallBackListener = recordCallBackListener;
        return this;
    }

    public interface RecordCallBackListener {

        void onSuccessed(String filePath);

        void onFailed(String error);
    }
}
