/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sshd.git.util;

import java.io.File;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;

import org.apache.sshd.common.keyprovider.AbstractFileKeyPairProvider;
import org.apache.sshd.common.util.SecurityUtils;

public class Utils {

    public static AbstractFileKeyPairProvider createTestHostKeyProvider() {
        return createTestKeyPairProvider("hostkey.pem");
    }

    public static AbstractFileKeyPairProvider createTestKeyPairProvider(String resource) {
        AbstractFileKeyPairProvider provider = SecurityUtils.createFileKeyPairProvider();
        provider.setFiles(Collections.singletonList(getFile(resource)));
        return provider;
    }

    public static int getFreePort() throws Exception {
        ServerSocket s = new ServerSocket(0);
        try {
            return s.getLocalPort();
        } finally {
            s.close();
        }
    }

    private static File getFile(String resource) {
        URL url = Utils.class.getClassLoader().getResource(resource);
        try {
            return new File(url.toURI());
        } catch(URISyntaxException e) {
            return new File(url.getPath());
        }
    }

    public static void deleteRecursive(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                if (children != null) {
                    for (File child : children) {
                        deleteRecursive(child);
                    }
                }
            }
            file.delete();
        }
    }

}
