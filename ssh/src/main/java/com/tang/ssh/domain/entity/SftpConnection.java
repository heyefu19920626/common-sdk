/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.entity;

import cn.hutool.core.io.FileUtil;
import com.tang.base.exception.BaseErrorCode;
import com.tang.base.exception.BaseException;
import com.tang.ssh.domain.exception.SshErrorCode;
import com.tang.ssh.domain.exception.SshTangException;
import com.tang.base.utils.CloseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.sftp.client.SftpClient;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * sftp连接
 *
 * @author TangAn
 * @version 0.1
 * @since 2023/12/16
 */
@Slf4j
public class SftpConnection implements Closeable {
    private final SftpClient sftpClient;

    private boolean close;

    public SftpConnection(SftpClient sftpClient) {
        this.sftpClient = sftpClient;
    }

    /**
     * 下载文件
     *
     * @param remotePath 远程文件的绝对路径
     * @param localPath  下载到本地的路径
     * @throws SshTangException 下载失败
     */
    public void download(String remotePath, String localPath) throws SshTangException {
        check();
        log.info("start download {} from {}", localPath, remotePath);
        try (InputStream read = sftpClient.read(remotePath);) {
            FileUtil.writeFromStream(read, new File(localPath));
        } catch (IOException e) {
            log.error("download from sftp error.", e);
            throw new SshTangException(SshErrorCode.SFTP_DOWNLOAD_FAIL);
        }
        log.info("finish download {} from {}", localPath, remotePath);
    }

    /**
     * 上传
     *
     * @param localPath  本地文件路径，必须为文件
     * @param remotePath 远程文件的全路径
     * @throws BaseException 上传失败
     */
    public void upload(String localPath, String remotePath) throws BaseException {
        check();
        File file = new File(localPath);
        if (!file.isFile()) {
            throw new BaseException(BaseErrorCode.FILE_FORMAT_ILLEGAL);
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        log.info("upload {} to {}, file size: {}M", localPath, remotePath,
            decimalFormat.format(file.length() / 1024.0 / 1024));
        try (BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
            SftpClient.CloseableHandle handle = sftpClient.open(remotePath, SftpClient.OpenMode.Create,
                SftpClient.OpenMode.Write);
        ) {
            log.info("start upload {} to {}", localPath, remotePath);
            upload(fis, handle);
            log.info("finish upload {} to {}", localPath, remotePath);
        } catch (IOException e) {
            log.error("upload file error.", e);
            throw new SshTangException(SshErrorCode.SFTP_UPLOAD_FAIL);
        }
    }

    private void upload(BufferedInputStream fis, SftpClient.CloseableHandle handle) throws IOException {
        int single = 1024 * 1024 * 4;
        byte[] bytes = new byte[single];
        int len = 0;
        int write = 0;
        while ((len = fis.available()) > 0) {
            if (len >= single) {
                len = single;
            }
            fis.read(bytes, 0, len);
            sftpClient.write(handle, write, bytes, 0, len);
            write += len;
        }
    }

    private void check() throws SshTangException {
        if (isClose()) {
            throw new SshTangException(SshErrorCode.SFTP_DOWNLOAD_FAIL);
        }
    }

    public boolean isClose() {
        return close || sftpClient.isClosing();
    }

    @Override
    public void close() throws IOException {
        close = true;
        CloseUtils.close(sftpClient);
    }
}