import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
public class main {
	static String os = System.getProperty("os.name", "unknown").toLowerCase(Locale.ROOT);
	static int PORT = 80;
	static String db;
	static String DEFAULT_DOCUMENT = "index.html";
	static HashMap<String, String> content_types = new HashMap<String, String>();
	public static void main(String[] args) {
		try {
			ServerSocket s = new ServerSocket(PORT);
			db = new String(API.readFile(".ct", false, os));
			content_types = TextToHashmap.Convert(db, ",", ":");
			System.out.println("Server Started at port 80");
			while (true) {
				Socket ss = s.accept();
				System.out.println("The client "+ss.getInetAddress().getHostAddress()+" connected");
				t thread = new t(ss);
				thread.start();
			}
		} catch (Exception e) {
			
		}
	}

	public static class t extends Thread {
		static HashMap<String, String> param = new HashMap<String, String>();
		static List<String> headers = new ArrayList<>();
		static String params = "";
		static String[] requestsLines;
		static String[] requestLine;
		static String method;
		static String path;
		static String version;
		static String host;
		public Socket s;
		t(Socket s) {
			this.s = s;
		}
		public static void SendGet(Socket s, byte[] res, String type) {
			try {
				API.Network.write(new DataOutputStream(s.getOutputStream()), res, type);
				param.clear();
				headers.clear();
				requestsLines = null;
				requestLine = null;
				method = null;
				version = null;
				host = null;
				path = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public static void translator(String i) {
			requestsLines = i.split("\r\n");
			requestLine = requestsLines[0].split(" ");
			method = requestLine[0];
			if (requestLine[1].contains("?") && requestLine[1].contains("=")) {
				params = requestLine[1].substring(requestLine[1].indexOf("?") + 1);
				try {
					params = java.net.URLDecoder.decode(params, StandardCharsets.UTF_8.name());
				} catch (UnsupportedEncodingException e) {
					
				}
				param = TextToHashmap.Convert(params, "&", "=");
				path = requestLine[1].substring(0, requestLine[1].indexOf("?"));
			}else {
				path = requestLine[1];
			}
			version = requestLine[2];
			host = requestsLines[1].split(" ")[1];
			for (int h = 2; h < requestsLines.length; h++) {
				String header = requestsLines[h];
				headers.add(header);
			}
		}
		public void run() {
			try {
					String request = API.Network.read(new DataInputStream(s.getInputStream()));
					translator(request);
					if (path.equals("/"))
						path = DEFAULT_DOCUMENT;
					if (method.equals("GET")) {
						byte[] res = null;
						String type = "";
						if(path.contains(".")) {
							
						}else {
							path = path+"/"+DEFAULT_DOCUMENT;
						}
						type = content_types.get(path.substring(path.lastIndexOf(".")));
						if (type.contains("text")) {
							res = API.readFile(path, true, os);
							if (path.equals(DEFAULT_DOCUMENT) || path.equals("/index.html")) {
								//DIV1
							}//DIV2
						} else {
							res = API.readFile(path, true, os);
						}
						SendGet(s, res, type);
				}else {
					SendGet(s, "Type not supported".getBytes(), "text/html");
					}
			} catch (Exception e) {
				SendGet(s,"901 Media Type isn't supported".getBytes(),"text/html");
				this.interrupt();
			}
		}
	}

}
