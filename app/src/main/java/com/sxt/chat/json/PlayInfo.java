package com.sxt.chat.json;

public class PlayInfo {
    /**
     * {
     * "Format":"mp4",
     * "StreamType":"video",
     * "PreprocessStatus":"PreprocessSucceed",
     * "ModificationTime":"2018-08-06T04:03:44Z",
     * "Height":768,
     * "PlayURL":"http://video.izhaohu.com/baa55f3da3d1448a9f9c8f1374a767dd/399459672768e561396503b2c234a62c-ld.mp4",
     * "CreationTime":"2018-08-06T04:01:22Z",
     * "Status":"Normal",
     * "Duration":"834.4560",
     * "JobId":"d1ef9a66989d44c7803347a296f4c86c",
     * "Encrypt":0,
     * "Width":1024,
     * "Fps":"25",
     * "Bitrate":"1320.796",
     * "Size":137768310,
     * "Definition":"LD"
     * }
     */
    public String Format;
    public String StreamType;
    public String PreprocessStatus;
    public String ModificationTime;
    public int Height;
    public int Width;
    public String PlayURL;
    public String CreationTime;
    public String Status;
    public String Duration;
    public String JobId;
    public int Encrypt;
    public String Bitrate;
    public long Size;
    public String Definition;
}