package com.tang.ssh.domain.utils;

import org.apache.sshd.server.Environment;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.AbstractCommandSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 复制自mina-sshd的单测
 *
 * @author <a href="mailto:dev@mina.apache.org">Apache MINA SSHD Project</a>
 */
public abstract class CommandExecutionHelper extends AbstractCommandSupport {
    /**
     * 命令与回显，可以通过修改这个map来修改命令对应的回显
     */
    public static Map<String, String> commandToResult = new HashMap<>();

    protected CommandExecutionHelper() {
        this(null);
    }

    protected CommandExecutionHelper(String command) {
        super(command, null);
    }

    @Override
    public void run() {
        String command = getCommand();
        try {
            if (command == null) {
                try (BufferedReader r = new BufferedReader(
                    new InputStreamReader(getInputStream(), StandardCharsets.UTF_8))) {
                    for (; ; ) {
                        command = r.readLine();
                        if (command == null) {
                            return;
                        }

                        if (!handleCommandLine(command)) {
                            return;
                        }
                    }
                }
            } else {
                handleCommandLine(command);
            }
        } catch (InterruptedIOException e) {
            // Ignore - signaled end
        } catch (Exception e) {
            String message = "Failed (" + e.getClass()
                .getSimpleName() + ") to handle '" + command + "': " + e.getMessage();
            try {
                OutputStream stderr = getErrorStream();
                stderr.write(message.getBytes(StandardCharsets.US_ASCII));
            } catch (IOException ioe) {
                log.warn("Failed ({}) to write error message={}: {}",
                    e.getClass().getSimpleName(), message, ioe.getMessage());
            } finally {
                onExit(-1, message);
            }
        } finally {
            onExit(0);
        }
    }

    @Override
    public void start(ChannelSession channel, Environment env) throws IOException {
        super.start(channel, env);
        // 写入登录之后的信息
        OutputStream out = getOutputStream();
        out.write("Last login: %s from 127.0.0.1\n$".formatted(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

    /**
     * @param command The command line
     * @return {@code true} if continue accepting command
     * @throws Exception If failed to handle the command line
     */
    protected boolean handleCommandLine(String command) throws Exception {
        OutputStream stdout = getOutputStream();
        String resp;
        if (commandToResult.containsKey(command)) {
            resp = "%s\n%s\n$".formatted(command, commandToResult.get(command));
        } else {
            resp = handleDefaultCommand(command);
        }
        stdout.write(resp.getBytes(StandardCharsets.UTF_8));
        stdout.flush();
        return true;
    }

    private String handleDefaultCommand(String command) {
        String resp;
        if ("pwd".equals(command)) {
            resp = "%s\n%s\n$".formatted(command, "/home/test");
        } else if (command.startsWith("top")) {
            resp = "%s\n%s\n$".formatted(command, "%CPU");
        } else if (command.startsWith("ping")) {
            resp = "%s\n%s\n$".formatted(command, "bytes from");
        } else {
            resp = "%s\n$".formatted(command);
        }
        return resp;
    }
}