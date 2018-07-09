package com.sxt.chat.json;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 11837 on 2018/6/5.
 */

public class OCRObject implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final int SUCCESS=100;
    public static final int FAILD=101;
    private int code;

    private Integer errorcode;//返回状态值
    private String errormsg;//返回错误消息

    /**
     * 身份证
     */
    private String session_id;//应请求的session标识符，可用于结果查询
    private String name;//证件姓名
    private Integer[] name_confidence_all;//证件姓名置信度
    private String sex;//性别
    private Integer[] sex_confidence_all;//性别置信度
    private String nation;//民族
    private Integer[] nation_confidence_all;//民族置信度
    private String birth;//出生年月
    private Integer[] birth_confidence_all;//出生日期置信度
    private String address;//地址
    private Integer[] address_confidence_all;//地址置信度
    private String id;//身份证号
    private Integer[] id_confidence_all;//身份证号置信度
    private String frontimage;//OCR识别的身份证正面照片
    private Integer[] frontimage_confidence_all;//正面照片置信度
    private Integer watermask_status;//水印是否存在(暂时不提供)
    private Integer[] watermask_confidence_all;//水印置信度
    private String valid_date;//证件的有效期
    private Integer[] valid_date_confidence_all;//证件的有效期置信度
    private String authority;//发证机关
    private Integer[] authority_confidence_all;//发证机关置信度
    private String backimage;//OCR识别的证件身份证反面照片
    private Integer[] backimage_confidence_all;//反面照片置信度
    private Integer[] detail_errorcode;//详细的错误原因
    private String[] detail_errormsg;//详细的错误原因说明
    private String seq;//标示识别请求的序列号

    /**
     * 银行卡
     */
    private List<Item> items;

    public class Item {
        private String item;//字段名称
        private ItemCoord itemcoord;//字段在图像中的像素坐标，包括左上角坐标x,y，以及宽、高width, height
        private Float itemconf;//识别结果对应的置信度
        private String itemstring;//字段结果
        private Object[] coords;
        private Object[] words;
        private Object[] candword;

        public class ItemCoord {
            private Integer x;
            private Integer y;
            private Integer width;
            private Integer height;
        }

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public ItemCoord getItemcoord() {
            return itemcoord;
        }

        public void setItemcoord(ItemCoord itemcoord) {
            this.itemcoord = itemcoord;
        }

        public Float getItemconf() {
            return itemconf;
        }

        public void setItemconf(Float itemconf) {
            this.itemconf = itemconf;
        }

        public String getItemstring() {
            return itemstring;
        }

        public void setItemstring(String itemstring) {
            this.itemstring = itemstring;
        }

        public Object[] getCoords() {
            return coords;
        }

        public void setCoords(Object[] coords) {
            this.coords = coords;
        }

        public Object[] getWords() {
            return words;
        }

        public void setWords(Object[] words) {
            this.words = words;
        }

        public Object[] getCandword() {
            return candword;
        }

        public void setCandword(Object[] candword) {
            this.candword = candword;
        }
    }

    /**
     * 美食图片
     */
    private boolean food;//是否为美食
    private Double food_confidence;

    /**
     * 图片类别/标签
     */
    private List<Tag> tags;

    public class Tag {
        private String tag_name;//标签名称
        private String tag_confidence;

        public String getTag_name() {
            return tag_name;
        }

        public void setTag_name(String tag_name) {
            this.tag_name = tag_name;
        }

        public String getTag_confidence() {
            return tag_confidence;
        }

        public void setTag_confidence(String tag_confidence) {
            this.tag_confidence = tag_confidence;
        }
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isFood() {
        return food;
    }

    public void setFood(boolean food) {
        this.food = food;
    }

    public Double getFood_confidence() {
        return food_confidence;
    }

    public void setFood_confidence(Double food_confidence) {
        this.food_confidence = food_confidence;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer[] getName_confidence_all() {
        return name_confidence_all;
    }

    public void setName_confidence_all(Integer[] name_confidence_all) {
        this.name_confidence_all = name_confidence_all;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer[] getSex_confidence_all() {
        return sex_confidence_all;
    }

    public void setSex_confidence_all(Integer[] sex_confidence_all) {
        this.sex_confidence_all = sex_confidence_all;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public Integer[] getNation_confidence_all() {
        return nation_confidence_all;
    }

    public void setNation_confidence_all(Integer[] nation_confidence_all) {
        this.nation_confidence_all = nation_confidence_all;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public Integer[] getBirth_confidence_all() {
        return birth_confidence_all;
    }

    public void setBirth_confidence_all(Integer[] birth_confidence_all) {
        this.birth_confidence_all = birth_confidence_all;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer[] getAddress_confidence_all() {
        return address_confidence_all;
    }

    public void setAddress_confidence_all(Integer[] address_confidence_all) {
        this.address_confidence_all = address_confidence_all;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer[] getId_confidence_all() {
        return id_confidence_all;
    }

    public void setId_confidence_all(Integer[] id_confidence_all) {
        this.id_confidence_all = id_confidence_all;
    }

    public String getFrontimage() {
        return frontimage;
    }

    public void setFrontimage(String frontimage) {
        this.frontimage = frontimage;
    }

    public Integer[] getFrontimage_confidence_all() {
        return frontimage_confidence_all;
    }

    public void setFrontimage_confidence_all(Integer[] frontimage_confidence_all) {
        this.frontimage_confidence_all = frontimage_confidence_all;
    }

    public Integer getWatermask_status() {
        return watermask_status;
    }

    public void setWatermask_status(Integer watermask_status) {
        this.watermask_status = watermask_status;
    }

    public Integer[] getWatermask_confidence_all() {
        return watermask_confidence_all;
    }

    public void setWatermask_confidence_all(Integer[] watermask_confidence_all) {
        this.watermask_confidence_all = watermask_confidence_all;
    }

    public String getValid_date() {
        return valid_date;
    }

    public void setValid_date(String valid_date) {
        this.valid_date = valid_date;
    }

    public Integer[] getValid_date_confidence_all() {
        return valid_date_confidence_all;
    }

    public void setValid_date_confidence_all(Integer[] valid_date_confidence_all) {
        this.valid_date_confidence_all = valid_date_confidence_all;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Integer[] getAuthority_confidence_all() {
        return authority_confidence_all;
    }

    public void setAuthority_confidence_all(Integer[] authority_confidence_all) {
        this.authority_confidence_all = authority_confidence_all;
    }

    public String getBackimage() {
        return backimage;
    }

    public void setBackimage(String backimage) {
        this.backimage = backimage;
    }

    public Integer[] getBackimage_confidence_all() {
        return backimage_confidence_all;
    }

    public void setBackimage_confidence_all(Integer[] backimage_confidence_all) {
        this.backimage_confidence_all = backimage_confidence_all;
    }

    public Integer[] getDetail_errorcode() {
        return detail_errorcode;
    }

    public void setDetail_errorcode(Integer[] detail_errorcode) {
        this.detail_errorcode = detail_errorcode;
    }

    public String[] getDetail_errormsg() {
        return detail_errormsg;
    }

    public void setDetail_errormsg(String[] detail_errormsg) {
        this.detail_errormsg = detail_errormsg;
    }

    public Integer getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(Integer errorcode) {
        this.errorcode = errorcode;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }
}
