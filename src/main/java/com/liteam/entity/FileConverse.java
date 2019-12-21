package com.liteam.entity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileConverse {
    private ChannelSftp sftp;
    private Session session;
    private String username;
    private String password;
    private String host;
    private int port;

    private String directory="/root/static/images/";

    private String proto = "http://";

    private String path = "/images/";

    private String randomname;

    public FileConverse(){
        this.username = "root";
        this.password = "wing98sico.";
        this.host = "wingsico.org";
        this.port = 22;
    }

    /**
     * 连接sftp服务器
     *
     * @throws Exception
     */
    public void login(){
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            if (password != null) {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();

            sftp = (ChannelSftp) channel;
        } catch (JSchException e) {
        }
    }

    /**
     * 关闭连接 server
     */
    public void logout(){
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    /**
     *            输入流
     * @throws SftpException
     * @throws Exception
     */
    public void upload(String sftpFileName, InputStream input) throws SftpException{
        try {
            sftp.cd(directory);
        } catch (SftpException e) {
            sftp.mkdir(directory);
            sftp.cd(directory);
        }
        sftp.put(input, sftpFileName);
    }

    /**
     * 上传单个文件
     *
     * @param uploadFile
     *            要上传的文件,包括路径
     * @throws FileNotFoundException
     * @throws SftpException
     * @throws Exception
     */
    public void upload(String uploadFile) throws FileNotFoundException, SftpException{
        File file = new File(uploadFile);
        upload(RandomName(file.getName()), new FileInputStream(file));
        file.delete();
    }

    /**
     * 将byte[]上传到sftp，作为文件。注意:从String生成byte[]是，要指定字符集。
     *
     * @param sftpFileName
     *            文件在sftp端的命名
     * @param byteArr
     *            要上传的字节数组
     * @throws SftpException
     * @throws Exception
     */
    public void upload(String sftpFileName, byte[] byteArr) throws SftpException{
        upload(RandomName(sftpFileName), new ByteArrayInputStream(byteArr));
    }

    /**
     * 将字符串按照指定的字符编码上传到sftp
     *
     * @param sftpFileName
     *            文件在sftp端的命名
     * @param dataStr
     *            待上传的数据
     * @param charsetName
     *            sftp上的文件，按该字符编码保存
     * @throws UnsupportedEncodingException
     * @throws SftpException
     * @throws Exception
     */
    public void upload(String sftpFileName, String dataStr, String charsetName) throws UnsupportedEncodingException, SftpException{
        upload(RandomName(sftpFileName), new ByteArrayInputStream(dataStr.getBytes(charsetName)));
    }

    /**
     * 下载文件
     *
     * @param directory
     *            下载目录
     * @param downloadFile
     *            下载的文件
     * @param saveFile
     *            存在本地的路径
     * @throws SftpException
     * @throws FileNotFoundException
     * @throws Exception
     */
    public void download(String directory, String downloadFile, String saveFile) throws SftpException, FileNotFoundException{
        if (directory != null && !"".equals(directory)) {
            sftp.cd(directory);
        }
        File file = new File(saveFile);
        sftp.get(downloadFile, new FileOutputStream(file));
    }
    /**
     * 下载文件
     * @param directory 下载目录
     * @param downloadFile 下载的文件名
     * @return 字节数组
     * @throws SftpException
     * @throws IOException
     * @throws Exception
     */
    public byte[] download(String directory, String downloadFile) throws SftpException, IOException{
        if (directory != null && !"".equals(directory)) {
            sftp.cd(directory);
        }
        InputStream is = sftp.get(downloadFile);

        byte[] fileData = IOUtils.toByteArray(is);

        return fileData;
    }

    /**
     * 删除文件
     *
     * @param directory
     *            要删除文件所在目录
     * @param deleteFile
     *            要删除的文件
     * @throws SftpException
     * @throws Exception
     */
    public void delete(String directory, String deleteFile) throws SftpException{
        sftp.cd(directory);
        sftp.rm(deleteFile);
    }

    public String RandomName(String filename){
        SimpleDateFormat simpleDateFormat;

        simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        Date date = new Date();

        String str = simpleDateFormat.format(date);

        java.util.Random random = new Random();

        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数
        this.randomname = str+rannum+"_"+filename;
        return  this.randomname;// 当前时间
    }

    public String getRandomname() {
        return randomname;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String url(){
        return this.proto+this.host+this.path+this.randomname;
    }
}
