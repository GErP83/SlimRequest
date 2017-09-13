package com.gerp83.slimrequest.model;

import java.io.File;

/**
 * http request file class for file upload
 */
public class SlimFile {

    public String paramName;
    public File file;

    public SlimFile() {

    }

    public SlimFile(String paramName, File file) {
        this.paramName = paramName;
        this.file = file;
    }

}