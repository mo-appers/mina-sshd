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
package org.apache.sshd.server.session;

import java.io.IOException;

import org.apache.sshd.common.Service;
import org.apache.sshd.common.ServiceFactory;
import org.apache.sshd.common.SshException;
import org.apache.sshd.common.session.AbstractConnectionService;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.ValidateUtils;

/**
 * Server side <code>ssh-connection</code> service.
 *
 * @author <a href="mailto:dev@mina.apache.org">Apache MINA SSHD Project</a>
 */
public class ServerConnectionService extends AbstractConnectionService {

    public static class Factory implements ServiceFactory {

        @Override
        public String getName() {
            return "ssh-connection";
        }

        @Override
        public Service create(Session session) throws IOException {
            return new ServerConnectionService(session);
        }
    }


    protected ServerConnectionService(Session s) throws SshException {
        super(s);
        
        ValidateUtils.checkTrue(s instanceof ServerSession, "Server side service used on client side", GenericUtils.EMPTY_OBJECT_ARRAY);

        if (!session.isAuthenticated()) {
            throw new SshException("Session is not authenticated");
        }
    }

    @Override
    public String initAgentForward() throws IOException {
        return agentForward.initialize();
    }

    @Override
    public String createX11Display(boolean singleConnection, String authenticationProtocol, String authenticationCookie, int screen) throws IOException {
        return x11Forward.createDisplay(singleConnection, authenticationProtocol, authenticationCookie, screen);
    }

}
