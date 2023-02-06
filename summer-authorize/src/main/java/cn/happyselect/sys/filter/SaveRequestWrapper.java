package cn.happyselect.sys.filter;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class SaveRequestWrapper extends HttpServletRequestWrapper {

    private static final int BUFFER_SIZE = 1024;

    private ByteArrayInputStream is;
    private String body;

    public SaveRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        ContentType contentType = parseContentType();
        if (ContentType.APPLICATION_FORM_URLENCODED.getMimeType().equals(contentType.getMimeType())) {
            ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String[] value = entry.getValue();
                if (!ObjectUtils.isEmpty(value)) {
                    for (int i = 0; i < value.length; i++) {
                        list.add(new BasicNameValuePair(entry.getKey(), value[i]));
                    }
                } else {
                    list.add(new BasicNameValuePair(entry.getKey(), null));
                }
            }
            body = EntityUtils.toString(new UrlEncodedFormEntity(list, contentType.getCharset()), contentType.getCharset());
        } else {
            int contentLength = request.getContentLength();
            ServletInputStream inputStream = request.getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream(contentLength > 0 ? contentLength : BUFFER_SIZE);
            IOUtils.copy(inputStream, os);
            byte[] bytes = os.toByteArray();
            is = new ByteArrayInputStream(bytes);
            body = new String(bytes, contentType.getCharset());
        }

    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new SaveInputStream(super.getInputStream());
    }

    public ContentType parseContentType() {
        if (!StringUtils.hasText(getContentType())) {
            return ContentType.DEFAULT_TEXT.withCharset(Consts.UTF_8);
        }
        ContentType contentType = ContentType.parse(getContentType());

        if (contentType != null) {
            if (contentType.getCharset() == null) {
                contentType = contentType.withCharset(Consts.UTF_8);
            }
        } else {
            contentType = ContentType.DEFAULT_TEXT.withCharset(Consts.UTF_8);
        }
        return contentType;
    }

    public String getBody() {
        return body;
    }

    class SaveInputStream extends ServletInputStream {

        private final ServletInputStream delegate;

        public SaveInputStream(ServletInputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean isFinished() {
            return delegate.isFinished();
        }

        @Override
        public boolean isReady() {
            return delegate.isReady();
        }

        @Override
        public void setReadListener(ReadListener listener) {
            delegate.setReadListener(listener);
        }

        @Override
        public int read() throws IOException {
            if (is != null) {
                return is.read();
            } else {
                return delegate.read();
            }
        }
    }
}
