package com.huang.coolrequestbreak;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sun.tools.attach.*;

import java.io.*;
import java.net.URL;
import java.util.List;

public class BeakCoolRequestTool extends AnAction {

	public static String agentJar = "CoolRequestAgent.jar";

	@Override
	public void actionPerformed(AnActionEvent e) {
		URL resource = getClass().getResource("/lib/" + agentJar);
		File tempFile = createTempFile(resource);

		List<VirtualMachineDescriptor> list = VirtualMachine.list();
		for (VirtualMachineDescriptor descriptor : list) {
			if (!"com.intellij.idea.Main".equals(descriptor.displayName())) {
				continue;
			}
			try {
				VirtualMachine virtualMachine = VirtualMachine.attach(descriptor);
				virtualMachine.loadAgent(tempFile.getAbsolutePath());
			} catch (AttachNotSupportedException | IOException | AgentLoadException |
			         AgentInitializationException ex) {
				throw new RuntimeException(ex);
			}
		}
	}


	public File createTempFile(URL url) {
		File tempFile;
		try {
			tempFile = File.createTempFile(agentJar, "");
			tempFile.deleteOnExit();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try (InputStream inputStream = url.openStream();
		     FileOutputStream outputStream = new FileOutputStream(tempFile)) {
			byte[] buffer = new byte[10240];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return tempFile;
	}
}
