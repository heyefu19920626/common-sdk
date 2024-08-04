package com.tang;

import com.sun.tools.attach.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 测试attach
 *
 * @author he
 * @since 2024-08.04-11:10
 */
public class AttachTest {
    private static final String DISPLAYNAME = "com.tang.AgentTest";

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException, InterruptedException {
        List<VirtualMachineDescriptor> allJvm = VirtualMachine.list();
        for (VirtualMachineDescriptor jvm : allJvm) {
            System.out.println(jvm.id() + ":" + jvm.displayName());
        }

        Optional<VirtualMachineDescriptor> targetJvm = allJvm.stream().filter(
            jvm -> DISPLAYNAME.equals(jvm.displayName())).findFirst();
        if (!targetJvm.isPresent()) {
            return;
        }
        VirtualMachine attach = VirtualMachine.attach(targetJvm.get().id());
        attach.loadAgent(
            "/Users/tangan/codes/java/common-sdk/agent/target/agent-1.0-SNAPSHOT-jar-with-dependencies.jar");
        attach.detach();
    }
}
