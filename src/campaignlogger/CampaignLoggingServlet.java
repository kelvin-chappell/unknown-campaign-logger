package campaignlogger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class CampaignLoggingServlet extends HttpServlet {

    private MessageDigest messageDigest;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url = request.getParameter("url");
        String slot = request.getParameter("slot");
        String content = request.getParameter("content");

        Entity adBlock = new Entity("AdBlock");
        adBlock.setProperty("hash", hash(content));
        adBlock.setProperty("date", new Date());
        adBlock.setProperty("url", url);
        adBlock.setProperty("slot", slot);
        adBlock.setProperty("content", content);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(adBlock);

        response.setContentType("application/javascript");
    }

    private MessageDigest getMessageDigest() {
        if (messageDigest == null) {
            try {
                messageDigest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return messageDigest;
    }

    private String hash(String s) {
        StringBuilder buf = new StringBuilder();
        for (byte b : getMessageDigest().digest(s.getBytes())) {
            buf.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
        }
        return buf.toString();
    }
}
