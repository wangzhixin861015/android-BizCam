package com.bcnetech.hyphoto.utils.Image;

import com.bcnetech.bcnetchhttp.bean.request.FileCheckBody;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.hyphoto.utils.StringUtil;

import org.apaches.commons.codec.digest.DigestUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangzhixin on 2018/4/17.
 */

public class FileUpload {

    public static Map<String, Object> fileUpload(String path,String paramsName) {
        int size = 2 * 1024;
        File src = new File(path);
        //获取文件名
        String fileName = src.getName();
        String fileTyle = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        /*if (fileTyle.equals("jpg") || fileTyle.equals("png")) {

        } else*/
        if (fileTyle.equals("mp4")) {
            size = 2 * 1024;
        }

        List<Map<String, Object>> params = new ArrayList<>();
        FileInputStream fis = null;
        int m = 0;
        long l;
        Map<String, Object> mapParam = new HashMap<>();
        try {
            fis = new FileInputStream(src);
            FileChannel fc = fis.getChannel();
            //获取文件的总长度
            l = fc.size();
            m = (int) (l / (size * 1024));

            if((l % (size * 1024)>0)){
                m=m+1;
            }




            mapParam.put("size", src.length() + "");
            try {
                mapParam.put("sha1",  DigestUtils.sha1Hex(fis));
            } catch (IOException e) {
                e.printStackTrace();
            }




            fis.close();
            fc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        InputStream in = null;
        try {
            in = new FileInputStream(src);
            File dirFile = new File(Flag.FENPIAN);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }

            for (int i = 1; i <= m; i++) {
                StringBuffer sb = new StringBuffer();
                sb.append(Flag.FENPIAN);
                sb.append("/");
                sb.append(fileName);
                sb.append("_data");
                sb.append(i);

                //System.out.println(sb.toString());

                File file2 = new File(sb.toString());
                file2.createNewFile();
                //创建写文件的输出流
                OutputStream out = new FileOutputStream(file2);
                int len = -1;
                byte[] bytes = new byte[1024];
                while ((len = in.read(bytes)) != -1) {
                    out.write(bytes, 0, len);
                    if (file2.length() >= size * 1024) {
                        break;
                    }
                }
                out.close();

                FileInputStream fpSha1=new FileInputStream(file2);

                String sha1 = DigestUtils.sha1Hex(fpSha1);
                Map<String, Object> map1 = new HashMap<>();
                map1.put("chunkSize", file2.length() + "");
                map1.put("chunkSha1", sha1);
                params.add(map1);
                fpSha1.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        System.out.println("--- 文件分割完成 ---");


        mapParam.put("chunks", params);
        if(!StringUtil.isBlank(paramsName)){
            mapParam.put("name", paramsName);
        }else {
            mapParam.put("name", fileName);

        }
        return mapParam;


    }


    public static FileCheckBody fileUploadCheck(String path, String paramsName) {
        int size = 2 * 1024;
        File src = new File(path);
        //获取文件名
        String fileName = src.getName();
        String fileTyle = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        /*if (fileTyle.equals("jpg") || fileTyle.equals("png")) {

        } else*/
        if (fileTyle.equals("mp4")) {
            size = 2 * 1024;
        }

//        List<Map<String, Object>> params = new ArrayList<>();
        List<FileCheckBody.Chunks> chunks=new ArrayList<>();

        FileInputStream fis = null;
        int m = 0;
        long l;
//        Map<String, Object> mapParam = new HashMap<>();
        FileCheckBody fileCheckBody=new FileCheckBody();
        try {
            fis = new FileInputStream(src);
            FileChannel fc = fis.getChannel();
            //获取文件的总长度
            l = fc.size();
            m = (int) (l / (size * 1024));

            if((l % (size * 1024)>0)){
                m=m+1;
            }



            fileCheckBody.setSize(src.length()+"");
//            mapPafileCheckBodyram.put("size", src.length() + "");
            try {
                fileCheckBody.setSha1(DigestUtils.sha1Hex(fis));
//                mapParam.put("sha1",  DigestUtils.sha1Hex(fis));
            } catch (IOException e) {
                e.printStackTrace();
            }




            fis.close();
            fc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        InputStream in = null;
        try {
            in = new FileInputStream(src);
            File dirFile = new File(Flag.FENPIAN);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }

            for (int i = 1; i <= m; i++) {
                StringBuffer sb = new StringBuffer();
                sb.append(Flag.FENPIAN);
                sb.append("/");
                sb.append(fileName);
                sb.append("_data");
                sb.append(i);

                //System.out.println(sb.toString());

                File file2 = new File(sb.toString());
                file2.createNewFile();
                //创建写文件的输出流
                OutputStream out = new FileOutputStream(file2);
                int len = -1;
                byte[] bytes = new byte[1024];
                while ((len = in.read(bytes)) != -1) {
                    out.write(bytes, 0, len);
                    if (file2.length() >= size * 1024) {
                        break;
                    }
                }
                out.close();

                FileInputStream fpSha1=new FileInputStream(file2);
                String sha1 = DigestUtils.sha1Hex(fpSha1);

//                Map<String, Object> map1 = new HashMap<>();
//                map1.put("chunkSize", file2.length() + "");
//                map1.put("chunkSha1", sha1);
//                params.add(map1);

                FileCheckBody.Chunks chunk=new FileCheckBody().new Chunks();
                chunk.setChunkSize(file2.length() + "");
                chunk.setChunkSha1(sha1);
                chunks.add(chunk);
                fpSha1.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        System.out.println("--- 文件分割完成 ---");

        fileCheckBody.setChunks(chunks);


//        mapParam.put("chunks", params);
        if(!StringUtil.isBlank(paramsName)){
//            mapParam.put("name", paramsName);
            fileCheckBody.setName(paramsName);
        }else {
//            mapParam.put("name", fileName);
            fileCheckBody.setName(fileName);

        }
        return fileCheckBody;


    }


    public static Map<String, String> fileUploaInfo(String path) {

        Map<String, String> mapParam = new HashMap<>();
        File src = new File(path);
        //获取文件名
        String fileName = src.getName();

        String format=fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
        String contentType = "";
        if(format.equals("jpg")||format.equals("png")||format.equals("jpeg")){
            contentType="1";
            format="image/"+format;
        }else if(format.equals("mp4")){
            contentType="4";
            format="video/"+format;
        }else if(format.equals("txt")){
            contentType="2";
            format="text/plain";
        }else if(format.equals("zip")){
            contentType="7";
            format="25d/25d";
        }

        mapParam.put("fileName",fileName);
        mapParam.put("format",format);
        mapParam.put("contentType",contentType);

        return mapParam;
    }


    public static FileCheckBody fileUploadInfoCheckBody(String path,FileCheckBody fileCheckBody) {

        File src = new File(path);
        //获取文件名
        String fileName = src.getName();

        String format=fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
        String contentType = "";
        if(format.equals("jpg")||format.equals("png")||format.equals("jpeg")){
            contentType="1";
            format="image/"+format;
        }else if(format.equals("mp4")){
            contentType="4";
            format="video/"+format;
        }else if(format.equals("txt")){
            contentType="2";
            format="text/plain";
        }else if(format.equals("zip")){
            contentType="7";
            format="25d/25d";
        }


        fileCheckBody.setFileName(fileName);
        fileCheckBody.setFormat(format);
        fileCheckBody.setContentType(contentType);
        return fileCheckBody;
    }



    public static String createFile(byte[] byteArray,String fileName){

        File dirFile = new File(Flag.FENPIAN);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        StringBuffer sb = new StringBuffer();
        sb.append(Flag.FENPIAN);
        sb.append("/");
        sb.append(fileName);
        sb.append(".txt");

        File file2 = new File(sb.toString());

        try {
            if (!file2.exists()) {
                file2.createNewFile();
            }
            //创建写文件的输出流
            OutputStream out = new FileOutputStream(file2);
            InputStream is = new ByteArrayInputStream(byteArray);
            byte[] buff = new byte[1024];
            int len = 0;
            while((len=is.read(buff))!=-1){
                out.write(buff, 0, len);
            }
            is.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static String getFileHash(String path){
        File src = new File(path);
        FileInputStream fis = null;
        String sha1=null;
        try {
            fis = new FileInputStream(src);


            try {
                sha1 = DigestUtils.sha1Hex(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return sha1;
    }





}
