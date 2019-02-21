package com.lkk.everything.core.interceptor.impl;

import com.lkk.everything.core.interceptor.FileInterceptor;

import java.io.File;

public class FilePrintInterceptor implements FileInterceptor {
    @Override
    public void apply(File file) {
        System.out.println(file.getName());
    }
}
