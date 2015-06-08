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

package org.apache.sshd.server;

import java.util.Arrays;
import java.util.List;

import org.apache.sshd.common.BaseBuilder;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.Transformer;
import org.apache.sshd.common.kex.BuiltinDHFactories;
import org.apache.sshd.common.kex.DHFactory;
import org.apache.sshd.common.kex.KeyExchange;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.config.keys.DefaultAuthorizedKeysAuthenticator;
import org.apache.sshd.server.forward.TcpipServerChannel;
import org.apache.sshd.server.global.CancelTcpipForwardHandler;
import org.apache.sshd.server.global.KeepAliveHandler;
import org.apache.sshd.server.global.NoMoreSessionsHandler;
import org.apache.sshd.server.global.TcpipForwardHandler;
import org.apache.sshd.server.kex.DHGEXServer;
import org.apache.sshd.server.kex.DHGServer;

/**
 * SshServer builder
 */
public class ServerBuilder extends BaseBuilder<SshServer, ServerBuilder> {
    public static final Transformer<DHFactory,NamedFactory<KeyExchange>>    DH2KEX = 
            new Transformer<DHFactory, NamedFactory<KeyExchange>>() {
                @Override
                public NamedFactory<KeyExchange> transform(DHFactory factory) {
                    if (factory == null) {
                        return null;
                    } else if (factory.isGroupExchange()) {
                        return DHGEXServer.newFactory(factory);
                    } else {
                        return DHGServer.newFactory(factory);
                    }
                }
            };

    protected PublickeyAuthenticator pubkeyAuthenticator;

    public ServerBuilder() {
        super();
    }

    public ServerBuilder publickeyAuthenticator(PublickeyAuthenticator auth) {
        pubkeyAuthenticator = auth;
        return this;
    }

    @Override
    protected ServerBuilder fillWithDefaultValues() {
        super.fillWithDefaultValues();
        if (keyExchangeFactories == null) {
            keyExchangeFactories = setUpDefaultKeyExchanges(false);
        }
        if (channelFactories == null) {
            channelFactories = Arrays.asList(
                    ChannelSession.ChannelSessionFactory.INSTANCE,
                    TcpipServerChannel.DirectTcpipFactory.INSTANCE);
        }
        if (globalRequestHandlers == null) {
            globalRequestHandlers = Arrays.asList(
                    new KeepAliveHandler(),
                    new NoMoreSessionsHandler(),
                    new TcpipForwardHandler(),
                    new CancelTcpipForwardHandler());
        }
        if (factory == null) {
            factory = SshServer.DEFAULT_SSH_SERVER_FACTORY;
        }
        
        if (pubkeyAuthenticator == null) {
            pubkeyAuthenticator = DefaultAuthorizedKeysAuthenticator.INSTANCE;
        }

        return me();
    }

    @Override
    public SshServer build(boolean isFillWithDefaultValues) {
        SshServer server = super.build(isFillWithDefaultValues);
        server.setPublickeyAuthenticator(pubkeyAuthenticator);
        return server;
    }

    /**
     * @param ignoreUnsupported If {@code true} then all the default
     * key exchanges are included, regardless of whether they are currently
     * supported by the JCE. Otherwise, only the supported ones out of the
     * list are included
     * @return A {@link List} of the default {@link NamedFactory}
     * instances of the {@link KeyExchange}s according to the preference
     * order defined by {@link #DEFAULT_KEX_PREFERENCE}.
     * <B>Note:</B> the list may be filtered to exclude unsupported JCE
     * key exchanges according to the <tt>ignoreUnsupported</tt> parameter
     * @see BuiltinDHFactories#isSupported()
     */
    public static List<NamedFactory<KeyExchange>> setUpDefaultKeyExchanges(boolean ignoreUnsupported) {
        return NamedFactory.Utils.setUpTransformedFactories(ignoreUnsupported, DEFAULT_KEX_PREFERENCE, DH2KEX);
    }

    public static ServerBuilder builder() {
        return new ServerBuilder();
    }
}