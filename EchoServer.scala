import java.io._
import java.net._
import java.security.KeyStore;
import javax.net._
import javax.net.ssl._
import javax.security.cert.X509Certificate;

object EchoServer {
  def main(args: Array[String]): Unit = {
    val passphrase = "hailhail".toCharArray()
    val ctx = SSLContext.getInstance("TLS")
    val kmf = KeyManagerFactory.getInstance("SunX509")
    val ks = KeyStore.getInstance("PKCS12")

    ks.load(new FileInputStream("./keystore.p12"), passphrase)
    kmf.init(ks, passphrase)

    val tmf = TrustManagerFactory.getInstance("SunX509")
    val ts = KeyStore.getInstance("JKS")
    ts.load(new FileInputStream("./truststore.p12"), passphrase)
    tmf.init(ts)

    ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null)

    val ssf = ctx.getServerSocketFactory()
    val ss = ssf.createServerSocket(8080)
    ss.asInstanceOf[SSLServerSocket].setNeedClientAuth(true)
    while (true) {
      val socket = ss.accept()
      try {
        val is = socket.getInputStream
        val os = socket.getOutputStream
        val buffer = new Array[Byte](1024)
        var length = is.read(buffer)
        while (length != -1) {
          os.write(buffer, 0, length);
          length = is.read(buffer)
        }
      } finally {
        socket.close()
      }
    }
  }
}
