package com.bcnetech.hyphoto.utils;

import android.content.Context;

import com.bcnetech.hyphoto.utils.pinyin.CharacterParser;
import com.bcnetech.hyphoto.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FontImageUtil {
    /**
     * 获得随机颜色
     *
     * @return
     */
    private static int[] getRanRGB() {
        int[] colors = new int[3];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = (int) (Math.random() * 256);
        }
        return colors;
    }

    /**
     * 判断是不是深颜色
     *
     * @return
     */
    private static boolean isShenRGB(int[] colors) {
        int grayLevel = (int) (colors[0] * 0.299 + colors[1] * 0.587 + colors[2] * 0.114);
        if (grayLevel >= 192) {
            return true;
        }
        return false;
    }

    /**
     * 生成默认的圆形图片
     */
   /* public Bitmap setDefaultBitmap(int color){
        Bitmap defaultbitmap;
        return defaultbitmap;
    }*/

    /**
     * 根据首字母判断色彩值
     */
    public static int setDefaultColor(Context context,String name) {
        if (name != null) {
            String first = name.charAt(0) + "";
            String d =setPinyin(first);
            int backcolor = 0;
            switch (d) {
                case "1":
                case "a":
                    backcolor = context.getResources().getColor(R.color.letter_a1);
                    break;
                case "2":
                case "b":
                    backcolor = context.getResources().getColor(R.color.letter_b2);
                    break;
                case "3":
                case "c":
                    backcolor = context.getResources().getColor(R.color.letter_c3);
                    break;
                case "4":
                case "d":
                    backcolor = context.getResources().getColor(R.color.letter_d4);
                    break;
                case "5":
                case "e":
                    backcolor = context.getResources().getColor(R.color.letter_e5);
                    break;
                case "6":
                case "f":
                    backcolor = context.getResources().getColor(R.color.letter_f6);
                    break;
                case "7":
                case "g":
                    backcolor = context.getResources().getColor(R.color.letter_g7);
                    break;
                case "8":
                case "h":
                    backcolor = context.getResources().getColor(R.color.letter_h8);
                    break;
                case "9":
                case "i":
                    backcolor = context.getResources().getColor(R.color.letter_i9);
                    break;
                case "0":
                case "j":
                    backcolor = context.getResources().getColor(R.color.letter_j0);
                    break;
                case "k":
                    backcolor = context.getResources().getColor(R.color.letter_k);
                    break;
                case "l":
                    backcolor = context.getResources().getColor(R.color.letter_l);
                    break;
                case "m":
                    backcolor = context.getResources().getColor(R.color.letter_m);
                    break;
                case "n":
                    backcolor = context.getResources().getColor(R.color.letter_n);
                    break;
                case "o":
                    backcolor = context.getResources().getColor(R.color.letter_o);
                    break;
                case "p":
                    backcolor = context.getResources().getColor(R.color.letter_p);
                    break;
                case "q":
                    backcolor = context.getResources().getColor(R.color.letter_q);
                    break;
                case "r":
                    backcolor = context.getResources().getColor(R.color.letter_r);
                    break;
                case "s":
                    backcolor = context.getResources().getColor(R.color.letter_s);
                    break;
                case "t":
                    backcolor = context.getResources().getColor(R.color.letter_t);
                    break;
                case "u":
                    backcolor = context.getResources().getColor(R.color.letter_u);
                    break;
                case "v":
                    backcolor = context.getResources().getColor(R.color.letter_v);
                    break;
                case "w":
                    backcolor = context.getResources().getColor(R.color.letter_w);
                    break;
                case "x":
                    backcolor = context.getResources().getColor(R.color.letter_x);
                    break;
                case "y":
                    backcolor = context.getResources().getColor(R.color.letter_y);
                    break;
                case "z":
                    backcolor = context.getResources().getColor(R.color.letter_z);
                    break;

            }
            return backcolor;
        }
        return 0;
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { // 如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }


    /**
     * 判断是否是Emoji
     *
     * @param codePoint
     *            比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    /**
     * 检测是否包含特殊字符
     * @param nickname
     * @return
     */
    public static boolean ishaveCharacter(String nickname){
        boolean isitok = false;
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(nickname);
        if(m.find()){
            isitok = true;
        }
        return isitok;
    }

    /**
     * 判断拼音首字母
     * @param text
     * @return
     */
    public static String setPinyin(String text){
        CharacterParser characterParser = CharacterParser.getInstance();
        String pinyin = characterParser.getSelling(text);
        String sortString = pinyin.substring(0, 1).toLowerCase();
        return sortString;
    }


    /**
     * 判断是否为中文
     * @param str
     * @return
     */
    public static boolean isChineseChar(String str){
        boolean temp = false;
        Pattern p=Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m=p.matcher(str);
        if(m.find()){
            temp =  true;
        }
        return temp;
    }

    public static boolean isBothLetterAndNum(String str){
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
        for(int i=0 ; i<str.length();i++) {
            if(Character.isDigit(str.charAt(i))){     //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            }
            if(Character.isLetter(str.charAt(i))){   //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
        }
        boolean isRight = isDigit && isLetter;
        return isRight;
    }

}