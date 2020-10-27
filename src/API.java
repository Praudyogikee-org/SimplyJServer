import java.util.Arrays;
import java.nio.file.StandardOpenOption;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class API {
	public static class Network {
		public static void write(DataOutputStream s, byte[] res, String content) {
			try {
				s.write("HTTP/1.1 200 OK\r\n".getBytes());
				s.write("Server: SimplyJServer\r\n".getBytes());
				s.write(("Content-Length: " + res.length + "\r\n").getBytes());
				s.write("Connection: close\r\n".getBytes());
				if (content.equals("text/html")) {
					s.write(("Content-Type: " + content + ";charset=UTF-8\r\n\r\n").getBytes());
				} else {
					s.write(("Content-Type: " + content + "\r\n\r\n").getBytes());
				}
				byte[] temp;
				int i = 0;
				long First = System.currentTimeMillis();
				if(res.length > 15000) {
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

	public static byte[] readFile(String filename, boolean ispublic, String os) throws Exception {
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
			out = Files.readAllBytes(Paths.get(filename));
			System.out.println("Reading "+Paths.get(filename));
		} catch (Exception e) {
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
