package chat;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.ArrayList;

import javax.swing.*;

public class ChatClient extends JFrame {
	private static final String TAG = "ChatClient";
	private ChatClient chatClient = this;

	private static final int PORT = 10000;

	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;

	private JButton btnConnect, btnSend;
	private JTextField tfHost, tfChat;
	static private JTextArea taChatList;
	private ScrollPane scrollPane;
	private JPanel topPanel, bottomPanel;
	BufferedWriter bw;

	static ArrayList<String> list = new ArrayList<String>();

	public ChatClient() {
		init();
		setting();
		batch();
		listener();

		setVisible(true);
	}

	private void init() {
		btnConnect = new JButton("connect");
		btnSend = new JButton("send");
		tfHost = new JTextField("127.0.0.1", 20/* 사이즈 */);
		tfChat = new JTextField(20);
		taChatList = new JTextArea(10, 30); // row, column
		scrollPane = new ScrollPane();
		topPanel = new JPanel();
		bottomPanel = new JPanel();

	}

	private void setting() {
		setTitle("채팅 다대다 클라이언트");
		setSize(350, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // 모니터 정중앙 실행
		taChatList.setBackground(Color.orange);
		tfChat.setForeground(Color.BLUE);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	private void batch() {
		topPanel.add(tfHost);
		topPanel.add(btnConnect);
		bottomPanel.add(tfChat);
		bottomPanel.add(btnSend);
		scrollPane.add(taChatList);

		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);

	}

	private void listener() {
		btnConnect.addActionListener(new ActionListener() {
			// while을 돌면서 서버로부터 메시지를 받아서 taChat에 뿌리기
			// reader.readline()
			@Override
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter("D:/workspace/javanetworktest/chatProject/log.txt"));
					String text = taChatList.getText();
					bw.write(text);
					bw.close();
				} catch (IOException e1) {
					System.out.println(TAG + e1.getMessage());
				}

				System.exit(0);
			}
		});
	}

	private void connect() {
		String host = tfHost.getText();
		taChatList.append("[시스템 메시지] 익명님이 입장하셨습니다." + "\n");
		try {
			socket = new Socket(host, PORT);

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true); // true : 자동 flush

			writer.println("SYSTEM:입장");

			ReaderThread rt = new ReaderThread();
			rt.start();

		} catch (Exception e1) {
			System.out.println(TAG + " : 서버 연결 에러 : " + e1.getMessage());
		}
	}

	private void send() {
		String chat = tfChat.getText();
		// 1번 taChatList뿌리기
		taChatList.append("[내 메시지] " + chat + "\n"); // setText는 덧붙여짐,

		// 2번 서버로 전송
		writer.println(chat);
		// 3번 tfChat비우기
		tfChat.setText("");
	}

	class ReaderThread extends Thread {
		@Override
		public void run() {
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String input = null;
				while ((input = reader.readLine()) != null) {

					if (input.equals("SYSTEM:입장")) {
						taChatList.append("[시스템 메시지] 익명님이 입장하셨습니다." + "\n");
						continue;
					}

					taChatList.append("[상대방 메시지] " + input + "\n");
				}
			} catch (Exception e) {
				System.out.println(TAG + e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		new ChatClient();

	}
}
