import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;
import java.nio.file.StandardOpenOption;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class API {
	public static byte[] compress(byte[] str) throws Exception {
		if (str == null || str.length == 0) {
			return null;
		}
		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(str);
		gzip.close();
		return obj.toByteArray();
	}

	public static class Network {
		public static void write(DataOutputStream s, byte[] res, String content, String S) {
			try {
				s.write((S + "\r\n").getBytes());
				s.write("Server: SimplyJServer\r\n".getBytes());
				s.write(("Content-Length: " + res.length + "\r\n").getBytes());
				s.write("Connection: Keep-Alive\r\n".getBytes());
				// s.write("Content-Encoding: gzip\r\n".getBytes());
				s.write("Keep-Alive: timeout=5, max=1000\r\n".getBytes());
				if (content.equals("text/html")) {
					s.write(("Content-Type: " + content + ";charset=UTF-8\r\n\r\n").getBytes());
				} else {
					s.write(("Content-Type: " + content + "\r\n\r\n").getBytes());
				}
				// byte[] temp;
				// int i = 0;
				// res = compress(res);
				/*
				 * unComment all ONLY when you want to use GZIP
				 */
				s.write(res);
				s.flush();
				s.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public static ArrayList<Byte> read(DataInputStream s) {
			ArrayList<Byte> result = new ArrayList<Byte>();
			try {
				do {
					result.add(s.readByte());
				} while (s.available() > 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}
	}

	public static byte[] readFile(String filename, boolean ispublic) {
		byte[] out = null;
		try {
			filename = Filenamer(filename, ispublic);
			File f = new File(filename);
			FileInputStream m = new FileInputStream(f);
			try {
				out = m.readAllBytes();
			} catch (Exception ee) {
				out = "404FILENOTFOUND".getBytes();
			}
			m.close();
		} catch (Exception e) {
			out = "404FILENOTFOUND".getBytes();
		}
		return out;
	}

	public static void writeFile(String filename, boolean ispublic, String content) {
		try {
			filename = Filenamer(filename, ispublic);
			Files.write(Paths.get(filename), content.getBytes(), StandardOpenOption.APPEND);
		} catch (Exception e) {

		}
	}

	public static String Filenamer(String filename, boolean ispublic) {
		if (ispublic) {
			filename = filename.replaceAll("../", "");
			filename = ("../public_html/" + filename);
		} else {
			filename = ("../" + filename);
		}
		return filename;
	}

	public static String Pather(String path, String DEFAULT_DOCUMENT) {
		if (path.contains(".")) {

		} else {
			path = path + "/" + DEFAULT_DOCUMENT;
		}
		return path;
	}
}