package catchgossip;

import java.security.cert.X509Certificate;
import javax.net.ssl.*;
import org.jsoup.*;
import org.jsoup.Connection.*; //org.jsoup.Connection.Method
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Catchgossip {

    private static String c; // store check over18
    private static String name = "蔡阿嘎";
    private static int start_pageNum = 33280;
    private static int latest_pageNum = 0;
    private static int All_artical_invNum = 1;
    private static int has_chu = 0;
    private static Document doc_connectBoard = null; // 連線至八卦版主版面
    private static Document doc_connectArticle = null; // 連線至文章內文
    private static Elements e = null; // 存內容_八卦版主版面
    private static Elements div_arrray = null; // 存內容_各文章內文頁面
    private static Element main_content_div = null; // 文章內文存成Element
    private static FileWriter fw = null;
    private static BufferedWriter bufw = null;
    
    
    public static void main(String[] args) throws Exception {
        fw = new FileWriter("Buffered.txt");
        bufw = new BufferedWriter(fw);

        int aim_page_num = start_pageNum; // 在文章列表頁面開始搜尋的頁數
        
        ssl(); // 要先disable ssl

        cookie_set(); // 搞定cookie

        catch_latest_pageNum(); // 取出看版版面之最新頁號

        // 連至八卦版頁面(Gossiping)頁面, 從467頁 -> 至最新. latest_pageNum
        for (; aim_page_num < latest_pageNum; aim_page_num++) {
            doc_connectBoard = null;
            Thread.sleep(50);
            doc_connectBoard = Jsoup
                                .connect("https://www.ptt.cc/bbs/Gossiping/index" + Integer.toString(aim_page_num) + ".html")
                                .cookie("over18", c).get();

            e = doc_connectBoard.select("div[class=r-ent]>div[class=title]>a[href]");

            // for列出, 可列出指定info
            for (int i = 0; i < e.size(); i++) { // 抓標題 e.get(i).text() //抓網址 e.get(i).absUrl("href")
                for (int countindex = 0; countindex <= e.get(i).text().length() - 2; countindex++) {
                    if (name.charAt(0) == e.get(i).text().charAt(countindex)
                            && name.charAt(1) == e.get(i).text().charAt(countindex + 1)
                            && name.charAt(2) == e.get(i).text().charAt(countindex + 2)) {
                        connect_get_article(e.get(i).absUrl("href"));
                    }
                }
                // Thread.sleep(50);
            }

        }
        bufw.close();

        System.out.println("END__ALL");
    }
    
    
    // 要先disable ssl
    private static void ssl() throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    
    // 搞定coockie
    private static void cookie_set() throws Exception {
        Connection.Response res = Jsoup.connect("https://www.ptt.cc/ask/over18").data("yes", "yes").method(Method.POST)
                .execute();
        // System.out.println(res.cookies()); //顯示cookies值
        c = res.cookie("over18");
    }

    
    // 取出看版版面之最新頁號
    private static void catch_latest_pageNum() throws Exception {
        doc_connectBoard = Jsoup.connect("https://www.ptt.cc/bbs/Gossiping/index.html").cookie("over18", c).get();
        
        // 目的取到上一頁頁號(最新-1)
        e = doc_connectBoard
            .select("div[id=action-bar-container]>div[class=action-bar]>div[class=btn-group btn-group-paging]>a"); // [href]

        // 藉由文章列表最新頁面中的上一頁按鈕連結取出上一頁index編號，在+1即是最新頁號
        latest_pageNum = Integer.valueOf(e.get(1).absUrl("href").substring(38, 43)).intValue() + 1; // e.get(1).absUrl("href").substring(38,
                                                                                                    // 43).
        System.out.println("latest_pageNum is: " + latest_pageNum);
    }

    
    // get連線至article內容網頁
    private static void connect_get_article(String article_url) throws Exception {
        doc_connectArticle = null;
        try {
            doc_connectArticle = Jsoup.connect(article_url).cookie("over18", c).get();
            // Thread.sleep(20);
        } catch (org.jsoup.HttpStatusException e) {
            Thread.sleep(50);
        } catch (java.net.SocketTimeoutException e) {
            Thread.sleep(50);
        }

        div_arrray = null;
        // Thread.sleep(50);
        try {
            div_arrray = doc_connectArticle.select("div[id=main-content]"); // "div[class=article-metaline]"
                                                                            // select("div[class=bbs-screen
                                                                            // bbs-content]")
        } catch (java.lang.NullPointerException e) {
            Thread.sleep(50);
        }

        main_content_div = null;
        Thread.sleep(50);

        try {
            main_content_div = div_arrray.get(0); // "div[class=article-metaline]" select("div[class=bbs-screen
                                                  // bbs-content]")
        } catch (java.lang.NullPointerException e) {
            Thread.sleep(50);
        }

        try {
            for (int countindex = 0; countindex <= main_content_div.ownText().length() - 2; countindex++) {
                if (name.charAt(0) == main_content_div.ownText().charAt(countindex)
                        && name.charAt(1) == main_content_div.ownText().charAt(countindex + 1)
                        && name.charAt(2) == main_content_div.ownText().charAt(countindex + 2)) {

                    bufw.write(main_content_div.select("div[class=article-metaline]>span[class=article-meta-value]")
                            .get(1).text());
                    bufw.newLine();
                    bufw.write(main_content_div.ownText());
                    bufw.newLine();
                    bufw.flush();
                    bufw.write(main_content_div.select("div[class=push]>span[class=f3 push-content]").text());
                    bufw.newLine();
                    bufw.newLine();
                    bufw.flush();

                    has_chu++;
                    System.out.println("END_art " + All_artical_invNum + " has_chu: " + has_chu);
                    System.out.println(" ");
                    break;
                }
            }
        } catch (java.lang.NullPointerException e) {
            Thread.sleep(50);
        }

        All_artical_invNum++;
    }

}
