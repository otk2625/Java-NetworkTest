package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServer {
	private static final String TAG = "chatServer";
	private ServerSocket serverSocket;
	private Vector<ClientInfo> vc; // ����� Ŭ���̾�Ʈ Ŭ����(����)�� ��� �÷���

	public ChatServer() {
		try {
			vc = new Vector<>();
			serverSocket = new ServerSocket(10000);
			System.out.println(TAG + " : Ŭ���̾�Ʈ ���� �����...");
			// ���� �������� ����
			while (true) {
				Socket socket = serverSocket.accept(); // Ŭ���̾�Ʈ ������
				ClientInfo clientInfo = new ClientInfo(socket);
				System.out.println("���� ����");

				clientInfo.start();
				vc.add(clientInfo);
			}

		} catch (IOException e) {
			System.out.println(TAG + e.getMessage());
		}
	}

	class ClientInfo extends Thread {
		Socket socket;
		BufferedReader reader;
		PrintWriter writer; // BufferedWriter�� �ٸ����� �������� �Լ��� ����, ��ü ����Ⱑ ����

		public ClientInfo(Socket socket) {
			this.socket = socket;
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream(), true);

			} catch (Exception e) {
				System.out.println("���� ���� ����: " + e.getMessage());

			}
		}

		// ���� : Ŭ���̾�Ʈ�� ���� ���� �޽����� ��� Ŭ���̾�Ʈ���� ������
		// PrintWriter , BufferedReader
		@Override
		public void run() {
			try {
				String input = null;
				String[] allProtocol = {};
				while ((input = reader.readLine()) != null) {
					allProtocol = input.split(":");
					
					if (allProtocol[0].equals(Chat.All)) { // ��üä��
						for (int i = 0; i < vc.size(); i++) {
							if (vc.get(i) != this) {
								vc.get(i).writer.println(allProtocol[1]);
							}
						}
						return;
					} 
					
					for (int i = 0; i < vc.size(); i++) {
						if (vc.get(i) != this) {
							vc.get(i).writer.println(input);
						}
					}
				}
			} catch (Exception e) {
				System.out.println(TAG + e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		new ChatServer();

	}
}
