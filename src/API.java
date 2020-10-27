import java.util.Arrays;
import java.util.zip.GZIPOutputStream;
import java.nio.file.StandardOpenOption;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class API {
	public static byte[] compress(byte[] str) throws Exception {
	    if (str == null || str.length == 0) {
	        return null;
	    }
	    ByteArrayOutputStream obj=new ByteArrayOutputStream();
	    GZIPOutputStream gzip = new GZIPOutputStream(obj);
	    gzip.write(str);
	    gzip.close();	    
	    return obj.toByteArray();
	 }
	public static class Network {
		public static void write(DataOutputStream s, byte[] res, String content) {
			try {
				s.write("HTTP/1.1 200 OK\r\n".getBytes());
				s.write("Server: SimplyJServer\r\n".getBytes());
				s.write("Content-Encoding: gzip\r\n".getBytes());
				s.write(("Content-Length: " + res.length + "\r\n").getBytes());
				s.write("Connection: close\r\n".getBytes());
				if (content.equals("text/html")) {
					s.write(("Content-Type: " + content + ";charset=UTF-8\r\n\r\n").getBytes());
				} else {
					s.write(("Content-Type: " + content + "\r\n\r\n").getBytes());
				}
				byte[] temp;
				int i = 0;
				res = compress(res);
				long First = System.currentTimeMillis();
				if(res.length > 250 * 1000) {
					while (i <= res.length) {
						temp = Arrays.copyOfRange(res, i, i + 1);
						s.write(temp);
						i = i + 1;
					}
				}else {
					s.write(res);
				}
				long Final = System.currentTimeMillis() - First;
				System.out.println("Sent in "+Final+" ms");
				s.flush();
				s.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public static String read(DataInputStream s) {
			StringBuilder result = null;
			try {
				result = new StringBuilder();
				do {
					result.append((char) s.read());
				} while (s.available() > 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result.toString();
		}
	}

	public static byte[] readFile(String filename, boolean ispublic, String os){
		byte[] out = null;
		if (ispublic) {
			if (os.startsWith("win")) {
				filename = ("./public_html/" + filename);
			} else if (os.startsWith("linux")) {
				filename = ("../public_html/" + filename);
			}
		} else {
			if (os.startsWith("linux")) {
				filename = ("../" + filename);
			} else if (os.startsWith("win")) {
				filename = ("./" + filename);
			}
		}
		try {
			File f = new File(filename);
			FileInputStream m = new FileInputStream(f);
			try {
			out = m.readAllBytes();
			}catch(Exception ee) {
				out = "Error".getBytes();
			}
			m.close();
			System.out.println("Reading "+Paths.get(filename));
		} catch (Exception e) {
			System.out.println("Reading "+Paths.get(filename));
			out = "404 not found".getBytes();
		}
		return out;
	}

	public static void writeFile(String filename, boolean ispublic, String os, String content) {
		try {
			if (ispublic) {
				if (os.startsWith("win")) {
					filename = ("./public_html/" + filename);
				} else if (os.startsWith("linux")) {
					filename = ("../public_html/" + filename);
				}
			} else {
				if (os.startsWith("linux")) {
					filename = ("../" + filename);
				} else if (os.startsWith("win")) {
					filename = ("./" + filename);
				}
			}
			Files.write(Paths.get(filename), content.getBytes(), StandardOpenOption.APPEND);
		} catch (Exception e) {

		}
	}
}
