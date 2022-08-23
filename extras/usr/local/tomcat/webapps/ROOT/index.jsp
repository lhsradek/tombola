<%--
The MIT License (MIT)

Copyright (c) 2022 Radek Kádner

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"><%@ page
  language="java" contentType="text/html; utf-8" pageEncoding="utf-8" session="false"
%><%@ page import="
  java.io.IOException,
  java.net.ConnectException,
  java.net.HttpURLConnection,
  java.net.InetSocketAddress,
  java.net.Socket,
  java.net.URL,
  java.net.UnknownHostException,
  java.security.KeyManagementException,
  java.security.NoSuchAlgorithmException,
  java.security.SecureRandom,
  java.security.cert.X509Certificate,
  java.util.LinkedHashMap,
  java.util.Map,
  java.util.stream.Collectors,
  javax.net.ssl.HostnameVerifier,
  javax.net.ssl.HttpsURLConnection,
  javax.net.ssl.SSLContext,
  javax.net.ssl.SSLSession,
  javax.net.ssl.TrustManager,
  javax.net.ssl.X509TrustManager,
  javax.servlet.ServletContext"
%><%!
public class IndexController {

        private int timeout = 10;
        private boolean isMess = false;
        private final StringBuffer mess = new StringBuffer();

        public IndexController() {
                this.mess.append("class:<strong>IndexController</strong><br/>" + System.lineSeparator());
        }

        private static final String ENV_JAVA_OPTS = "JAVA_OPTS";
        private static final String PROFILES_ACTIVE = "-Dspring.profiles.active";
        private static final String FORMAT_SOCKET = "isSocket(%s) <strong>%s:%d</strong> %s";
        private static final String FORMAT_URL = "isUrl(%s) <strong>%s</strong> %s";
        private static final String FORMAT_STATUS_CODE = "testUrl%s(Status code:<strong>%d</strong>)";
        private static final int STATUS_CODE_LIMIT = 400;

        public boolean isSocket(String serverName, int port) {
                return isSocket(serverName, port, getTimeout());
        }

        public boolean isSocket(String serverName, int port, int timeout) {
                boolean ret = true;
                try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(serverName, port), timeout);
                        socket.close();
                } catch (IllegalArgumentException e) {
                        ret = false;
                        appendMess(String.format(FORMAT_SOCKET, e.getClass().getSimpleName(), serverName, port, e.getMessage()));
                } catch (IOException e) {
                        ret = false;
                        appendMess(String.format(FORMAT_SOCKET, e.getClass().getSimpleName(), serverName, port, e.getMessage()));
                }
                return ret;
        }

        public boolean isUrl(String address) {
                return isUrl(address, getTimeout());
        }

        public boolean isUrl(String address, int timeout) {
                boolean ret = true;
                try {
                        URL url = new URL(address);
                        if ("http".equals(url.getProtocol())) {
                                testUrlHttp(url, timeout);
                        } else if ("https".equals(url.getProtocol())) {
                                testUrlHttps(url, timeout);
                        } else {
                                ret = false;
                        }
                } catch (IllegalArgumentException e) {
                        ret = false;
                        appendMess(String.format(FORMAT_URL, e.getClass().getSimpleName(), address, e.getMessage()));
                } catch (IOException e) {
                        ret = false;
                        appendMess(String.format(FORMAT_URL, e.getClass().getSimpleName(), address, e.getMessage()));
                } catch (KeyManagementException e) {
                        ret = false;
                        appendMess(String.format(FORMAT_URL, e.getClass().getSimpleName(), address, e.getMessage()));
                } catch (NoSuchAlgorithmException e) {
                        ret = false;
                        appendMess(String.format(FORMAT_URL, e.getClass().getSimpleName(), address, e.getMessage()));
                }
                return ret;
        }

        private void testUrlHttp(URL url, int timeout) throws IOException {
                HttpURLConnection.setFollowRedirects(false);
                // http
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setConnectTimeout(timeout);
                int statusCode = http.getResponseCode();
                http.disconnect();
                if (statusCode >= STATUS_CODE_LIMIT) {
                        throw new ConnectException(String.format(FORMAT_STATUS_CODE, ucFirst(url.getProtocol()), statusCode));
                }
        }

       private void testUrlHttps(URL url, int timeout) throws NoSuchAlgorithmException, KeyManagementException, IOException {
                HttpsURLConnection.setFollowRedirects(false);
                // TrustManager[] and SSLContext
                TrustManager[] tm = new TrustManager[] { new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                                return null;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                } };
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, tm, new SecureRandom());
                // https
                HttpsURLConnection http = (HttpsURLConnection) url.openConnection();
                http.setConnectTimeout(timeout);
                http.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                                return true;
                        }
                });
                http.setSSLSocketFactory(sc.getSocketFactory());
                int statusCode = http.getResponseCode();
                http.disconnect();
                if (statusCode >= STATUS_CODE_LIMIT) {
                        throw new ConnectException(String.format(FORMAT_STATUS_CODE, ucFirst(url.getProtocol()), statusCode));
                }
        }

        public String ucFirst(String str) {
                StringBuffer ret = new StringBuffer();
                // Test for StringIndexOutOfBoundsException
                if (str.length() > 0) {
                        ret.append(str.substring(0, 1).toUpperCase());
                }
                if (str.length() > 1) {
                        ret.append(str.substring(1));
                }
                return ret.toString();
        }

        public Map<String, String> getEnv() {
                return System.getenv().entrySet().stream()
                                .sorted(Map.Entry.comparingByKey()).collect(Collectors
                                .toMap(Map.Entry::getKey,
                                                Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        }

        public String getProfile() {
                String ret = "";
                String opts = System.getenv(ENV_JAVA_OPTS);
                if (opts != null) {
                        for (String p : opts.split(" ")) {
                                String[] k = p.split("=");
                                if (k[0] != null && k[1] != null) {
                                        if (k[0].equals(PROFILES_ACTIVE)) {
                                                ret = String.format(" - '%s'", k[1]);
                                                break;
                                        }
                                }
                        }
                }
                return ret;
        }


        public StringBuffer getMess() {
                return mess;
        }

        public void appendMess(String mess) {
                if (isMess()) {
                        this.mess.append(mess + "<br/>" + System.lineSeparator());
                }
        }

        public int getTimeout() {
                return timeout;
        }

        public void setTimeout(int timeout) {
                this.timeout = timeout;
        }

        public boolean isMess() {
                return isMess;
        }

        public void setMess(boolean isMess) {
                this.isMess = isMess;
        }
}
%><%
IndexController index = new IndexController();
// index.setMess(true);
String serverName = pageContext.getRequest().getServerName();
// String serverSoftware = index.ucFirst(pageContext.getServletContext().getServerInfo().split("/")[0]);
// boolean isPostgres = index.isSocket("db", 5432);
// boolean isManager = index.isUrl("https://localhost:8080/manager/");
// boolean isDoc = index.isUrl("https://localhost:8080/docs/");
// StringBuffer env = new StringBuffer();
// index.getEnv().forEach((key, val) -> {
//         env.append(String.format("<strong>%s</strong>:'%s'" + System.lineSeparator(), key, val));
// });
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cs_CZ.UTF-8" lang="cs_CZ.UTF-8">
<head>
  <title><%=serverName%></title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <link rel="stylesheet" href="tomcat/style.css" type="text/css">
  <link rel="shortcut icon" href="tomcat/favicon.png" th:type="image/png"/>
</head>

<body>
  <h1><span><strong><%=serverName%></strong>
	   <a href="https://github.com/lhsradek/tombola" target="_blank"><img src="tomcat/github.png" width="30" height="30"/></a>
	   <a href="https://www.facebook.com/radek.kadner/" target="_blank"><img src="tomcat/facebook.png" width="17" height="30"/></a>
	   <a href="https://www.linkedin.com/in/radekkadner/" target="_blank"><img src="tomcat/in.png" width="30" height="30"/></a>
	   <a href="mailto:radek.kadner@gmail.com"><img src="tomcat/mail.png" width="30" height="30"/></a></span></h1>
  <div class="content">

    <div class="content-columns">
      <div class="content-column-left">

        <h2>Apache Tomcat</h2>
        <h5>Webs servlet/JSP container</h5>
        <p>
          <ul>
              <li><a href="https://<%=serverName%>/tomcat.jsp" target="_blank"><%=serverName%></a></li>
              <li><a href="https://<%=serverName%>/tombola/" target="_blank"><%=serverName%> - tombola</a></li>
              <li><a href="https://<%=serverName%>/tombola-javadoc/" target="_blank"><%=serverName%> - tombola-javadoc</a></li>
              <li><a href="https://<%=serverName%>/info.jsp" target="_blank"><%=serverName%> - jspinfo</a></li>
              <li><a href="https://<%=serverName%>/manager/" target="_blank"><%=serverName%> - manager</a></li>
              <li><a href="https://<%=serverName%>/docs/" target="_blank"><%=serverName%> - documentation</a></li>
	      <!-- <li><a href="https://adminer.<%=serverName%>" target="_blank"><%=serverName%> - adminer</a></li> -->
              <li><a href="https://tomcat.apache.org" target="_blank">tomcat.apache.org</a></li>
          </ul>
        </p>
        <p><img src="tomcat/tomcat.png" width="63" height="40"/></p>
      </div>

      <div class="content-column-right">
        <h2>Documentation</h2>
        <p>
          <ul>
              <li><a href="https://hub.docker.com/_/alpine" target="_blank">hub.docker.com - Official build of Alpine Linux</a></li>
              <li><a href="https://hub.docker.com/_/mariadb" target="_blank">hub.docker.com - Official build of MariaDB</a></li>
              <li><a href="https://hub.docker.com/_/nginx" target="_blank">hub.docker.com - Official build of Nginx</a></li>
              <li><a href="https://hub.docker.com/_/php" target="_blank">hub.docker.com - Official build of PHP</a></li>
              <li><a href="https://hub.docker.com/_/phpmyadmin" target="_blank">hub.docker.com - Official build of phpMyAdmin</a></li>
              <li><a href="https://hub.docker.com/_/postgres" target="_blank">hub.docker.com - Official build of Postgres</a></li>
              <li><a href="https://hub.docker.com/_/redis" target="_blank">hub.docker.com - Official build of Redis</a></li>
              <li><a href="https://hub.docker.com/_/tomcat" target="_blank">hub.docker.com - Official build of Tomcat</a></li>
              <li><a href="https://hub.docker.com/_/ubuntu" target="_blank">hub.docker.com - Official build of Ubuntu</a></li>
              <li><a href="https://hub.docker.com/_/wordpress" target="_blank">hub.docker.com - Official build of Wordpress</a></li>
              <li><a href="https://docs.docker.com" target="_blank">docs.docker.com</a></li>
              <li><a href="https://nginx.org/en/docs/" target="_blank">nginx.org - Documentation</a></li>
              <li><a href="https://wiki.alpinelinux.org" target="_blank">alpinelinux.org - Wiki</a></li>
	  </ul>
        </p>
        <p><img src="tomcat/docker-logo.png" width="53" height="40"/></p>
      </div>

    </div>
  </div>
</body>
</html>
