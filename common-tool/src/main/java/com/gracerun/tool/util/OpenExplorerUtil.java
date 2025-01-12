package com.gracerun.tool.util;

import java.io.IOException;

public class OpenExplorerUtil {

    public static void browse(String url) throws IOException {
        if (java.awt.Desktop.isDesktopSupported()) {
            java.net.URI uri = java.net.URI.create(url);
            java.awt.Desktop dp = java.awt.Desktop.getDesktop();
            dp.browse(uri);
        }
    }

    public static void main(String args[]) throws IOException {

        OpenExplorerUtil.browse("https://www.baidu.com/");

    }
}
