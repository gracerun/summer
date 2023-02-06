package cn.happyselect.sys.filter;

import org.apache.http.Consts;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SaveResponseWrapper extends HttpServletResponseWrapper {

    private static final int BUFFER_SIZE = 1024;
    private Map<String, String> header = new HashMap<>();
    private final ByteArrayOutputStream os = new ByteArrayOutputStream(BUFFER_SIZE);

    public SaveResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new SaveOutputStream(super.getOutputStream());
    }

    @Override
    public void addHeader(String name, String value) {
        header.put(name, value);
        super.addHeader(name, value);
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public String getBody() {
        return new String(os.toByteArray(), Consts.UTF_8);
    }

    class SaveOutputStream extends ServletOutputStream {

        private final ServletOutputStream delegate;

        public SaveOutputStream(ServletOutputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean isReady() {
            return delegate.isReady();
        }

        @Override
        public void setWriteListener(WriteListener listener) {
            delegate.setWriteListener(listener);
        }

        @Override
        public void write(int b) throws IOException {
            os.write(b);
            delegate.write(b);
        }

    }
}
