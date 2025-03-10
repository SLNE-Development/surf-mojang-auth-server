package net.wouto.proxy.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MultiReadHttpServletRequest is a {@link HttpServletRequestWrapper}
 * that that caches the body of the request, such that the body can be read
 * multiple times.
 * MultiReadHttpServletRequest is used by the custom
 * {@link HiddenHttpMethodFilter} provided by
 * {@link HiddenHttpMethodFilterConfig}.
 */
public class MultiReadHttpServletRequest extends HttpServletRequestWrapper {
    private Logger logger = LoggerFactory.getLogger(MultiReadHttpServletRequest.class);
    private ByteArrayOutputStream cachedBytes;

    /**
     * This is the constructor to the MultiReadHttpServletRequest.
     *
     * @param request - HttpServletRequest, which handed over by the Spring
     *                Framework
     */
    public MultiReadHttpServletRequest(HttpServletRequest request) {
        super(request);
        try {
            this.cacheInputStream();
        } catch (IOException ex) {
            this.logger.error("Error while cahcing the input stream. Excpetion = {}", ex);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (cachedBytes == null) {
            this.cacheInputStream();
        }

        return new CachedServletInputStream(cachedBytes.toByteArray());
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    private void cacheInputStream() throws IOException {
        /*
         * Cache the inputstream in order to read it multiple times. For
         * convenience, I use apache.commons IOUtils
         */
        cachedBytes = new ByteArrayOutputStream();
        org.apache.tomcat.util.http.fileupload.IOUtils.copy(super.getInputStream(), cachedBytes);
    }

    /* An inputstream which reads the cached request body */
    private class CachedServletInputStream extends ServletInputStream {
        private ByteArrayInputStream input;

        public CachedServletInputStream(byte[] bytes) {
            /* create a new input stream from the cached request body */
            input = new ByteArrayInputStream(bytes);
        }

        @Override
        public int read() throws IOException {
            return input.read();
        }

        @Override
        public boolean isFinished() {
            return input.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new RuntimeException("Not implemented");
        }
    }
}