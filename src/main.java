import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.*;
import java.net.ServerSocket;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.*;
public class main {
	
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_BLUE = "\u001B[34m";
	
	static int PORT = 80;
	static boolean verbose = false;
	static String DEFAULT_DOCUMENT = "index.html";
	static String ct = ".ct";

	static String db;
	
	static HashMap<String,String> Args = new HashMap<String,String>();
	static HashMap<String, String> content_types = new HashMap<String, String>();
	
	public static void main(String[] args) {
		if(args.length%2>0 &&args[0].equals("-h")) {
			System.out.println(ANSI_GREEN+"Help Screen\n"
					+ "-p [number]   Port number \"Default is 80\"\n"
					+ "-dd [String]  Default Document Filename \"Default is index.html\"\n"
					+ "-ct [String]  Content Type Filename \"Default is .ct\"\n"
					+ "-v [1/0]      Enable/Disable Verbose \"Default is disabled\"\n");
			System.exit(1);
		}else {
		for(int i = 0;i<args.length;i+=2) {
			Args.put(args[i], args[i+1]);
		}
		Setter();
		}
		ServerSocket s = null;
		v v = new v();
		v.start();
		try {
			s = new ServerSocket(PORT);
			System.out.println(ANSI_BLUE+"Server Started at port "+PORT+ANSI_RESET);
			db = new String(API.readFile(ct, false));
			if(db.equals("404FILENOTFOUND")) {
				System.out.println(ANSI_RED+"A problem happened in reading the content type file"+ANSI_RESET);
				System.exit(1);
			}
			content_types = TextToHashmap.Convert(db, ",", ":");
			while (true) {
				Socket ss = s.accept();
				if(verbose) {
					System.out.println(ANSI_BLUE+"The client "+ss.getInetAddress().getHostAddress()+" connected"+ANSI_RESET);
				}
				t thread = new t(ss);
				thread.start();
			}
		} catch (Exception e) {
			
		}
	}
	public static void Setter() {
		if(Args.containsKey("-p")) {
			PORT = Integer.parseInt(Args.get("-p"));
			System.out.println(ANSI_BLUE+"Custom Port Setting Applied: "+PORT+ANSI_RESET);
		}
		if(Args.containsKey("-ct")) {
			ct = Args.get("-ct");
			System.out.println(ANSI_BLUE+"Custom Content Type File Setting Applied: "+ct+ANSI_RESET);
		}
		if(Args.containsKey("-dd")) {
			DEFAULT_DOCUMENT = Args.get("-dd");
			System.out.println(ANSI_BLUE+"Custom Default Document Setting Applied: "+DEFAULT_DOCUMENT+ANSI_RESET);
		}
		if(Args.containsKey("-v")) {
			if(Args.get("-v").equals("1")) {
				verbose = true;
			}else if(Args.get("-v").equals("0")) {
				verbose = false;
			}
			System.out.println(ANSI_BLUE+"Custom Verbose Setting Applied"+ANSI_RESET);
		}
		System.out.println("---------------------------------");
	}
	public static class v extends Thread {
		public void run() {
			if(verbose) {
			while(true) {
				System.out.println(ANSI_BLUE+"Ram usage: "+(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1000000+" MB"+ANSI_RESET);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					
				}
			}
			}
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
		static String BODY = "";
		public Socket s;
		t(Socket s) {
			this.s = s;
		}
		public static void SendGet(Socket s, byte[] res, String type, String S) {
			try {
				API.Network.write(new DataOutputStream(s.getOutputStream()), res, type, S);
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
		public static void translator(ArrayList<Byte> request) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);
			for (byte element : request) {
			    try {
					out.write(element);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			String o = new String(baos.toByteArray());
			requestsLines = o.split("\r\n");
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
			if(!method.equals("GET")) BODY = headers.get(headers.size()-1);
		}
		public void run() {
			try {
				ArrayList<Byte> request = API.Network.read(new DataInputStream(s.getInputStream()));
				translator(request);
					if(path.contains("//")) path = "";
					if (path.equals("/"))  path = DEFAULT_DOCUMENT;
						byte[] res = null;
						String type = "";
						path = API.Pather(path, DEFAULT_DOCUMENT);
						type = content_types.get(path.substring(path.lastIndexOf(".")));
						res = API.readFile(path, true);
						if(new String(res).equals("404FILENOTFOUND")) {
							SendGet(s, "404 file not found".getBytes(), type, "HTTP/1.1 404 FILE NOT FOUND");
						}else {
							if (type.contains("text")) {
								if (path.equals(DEFAULT_DOCUMENT) || path.equals("/index.html")) {
									
								}
							}
							SendGet(s, res, type, "HTTP/1.1 200 OK");
						}
						System.gc();
			} catch (Exception e) {
				SendGet(s,"901 Media Type isn't supported".getBytes(),"text/html","HTTP/1.1 503 Service Unavailable");
				this.interrupt();
			}
		}
	}

}
