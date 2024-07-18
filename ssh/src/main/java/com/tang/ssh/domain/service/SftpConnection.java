/*
 * Copyright (c) TangAn Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.tang.ssh.domain.service;

import cn.hutool.core.io.FileUtil;
import com.tang.base.exception.BaseErrorCode;
import com.tang.base.exception.BaseException;
import com.tang.base.utils.CloseUtils;
import com.tang.ssh.domain.exception.SshErrorCode;
import com.tang.ssh.domain.exception.SshTangException;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;
import org.apache.sshd.sftp.client.fs.SftpPath;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private final SftpFileSystem fs;

    private boolean close;

    public SftpConnection(SftpFileSystem fs, SftpClient sftpClient) {
        this.fs = fs;
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
        checkSftp();
        if (fs != null) {
            downloadByFileSystem(remotePath, localPath);
            return;
        }
        downloadBySftpClient(remotePath, localPath);
    }

    private void downloadBySftpClient(String remotePath, String localPath) {
        log.info("start download {} from {}", localPath, remotePath);
        try (InputStream read = sftpClient.read(remotePath)) {
            FileUtil.writeFromStream(read, new File(localPath));
        } catch (IOException e) {
            log.error("download from sftp error.", e);
            throw new SshTangException(SshErrorCode.SFTP_DOWNLOAD_FAIL);
        }
        log.info("finish download {} from {}", localPath, remotePath);
    }

    private void downloadByFileSystem(String remotePath, String localPath) {
        log.info("start download {} from {} by fs", localPath, remotePath);
        SftpPath defaultDir = fs.getDefaultDir();
        Path local = Path.of(localPath);
        try {
            if (Files.exists(local)) {
                log.info("{} exits, will delete", localPath);
                Files.delete(local);
            }
            Files.copy(defaultDir.resolve(remotePath), local);
        } catch (IOException e) {
            log.error("download {} from sftp by fs error.", remotePath, e);
            throw new SshTangException(SshErrorCode.SFTP_DOWNLOAD_FAIL);
        }
    }

    /**
     * 上传
     *
     * @param localPath  本地文件路径，必须为文件
     * @param remotePath 远程文件的全路径
     * @throws BaseException 上传失败
     */
    public void upload(String localPath, String remotePath) throws BaseException {
        checkSftp();
        File file = new File(localPath);
        if (!file.isFile()) {
            throw new BaseException(BaseErrorCode.FILE_FORMAT_ILLEGAL);
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        log.info("upload {} to {}, file size: {}M", localPath, remotePath,
            decimalFormat.format(file.length() / 1024.0 / 1024));
        if (fs != null) {
            uploadByFileSystem(localPath, remotePath);
            return;
        }
        uploadBySftpClient(localPath, remotePath, file);
    }

    private void uploadByFileSystem(String localPath, String remotePath) {
        try {
            SftpPath defaultDir = fs.getDefaultDir();
            SftpPath remote = defaultDir.resolve(remotePath);
            preUpload(remotePath, remote);
            log.info("start upload {} to {} by fs", localPath, remotePath);
            Files.copy(Path.of(localPath), remote);
            log.info("finish upload {} to {} by fs", localPath, remotePath);
        } catch (IOException e) {
            log.error("upload file error by fs.", e);
            throw new SshTangException(SshErrorCode.SFTP_UPLOAD_FAIL);
        }
    }

    private void preUpload(String remotePath, SftpPath remote) throws IOException {
        if (Files.exists(remote)) {
            log.info("remote {} exits, will delete", remotePath);
            Files.delete(remote);
        }
        Path parentPath = remote.getParent();
        if (!Files.exists(parentPath)) {
            log.info("remote {} not exits, will create", parentPath);
            Files.createDirectories(parentPath);
        }
    }

    private void uploadBySftpClient(String localPath, String remotePath, File file) {
        try (BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
            SftpClient.CloseableHandle handle = sftpClient.open(remotePath, SftpClient.OpenMode.Create,
                SftpClient.OpenMode.Write)
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
        while ((len = fis.read(bytes, 0, single)) > 0) {
            sftpClient.write(handle, write, bytes, 0, len);
            write += len;
        }
    }

    private void checkSftp() throws SshTangException {
        if (isClose()) {
            throw new SshTangException(SshErrorCode.SFTP_HAS_CLOSE);
        }
    }

    /**
     * sftp连接是否关闭
     *
     * @return true表示sftp已关闭
     */
    public boolean isClose() {
        if (close) {
            return true;
        }
        if (fs != null) {
            return !fs.isOpen();
        }
        return sftpClient.isClosing();
    }

    @Override
    public void close() throws IOException {
        close = true;
        CloseUtils.close(fs);
        CloseUtils.close(sftpClient);
    }
}