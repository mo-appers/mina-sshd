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
package org.apache.sshd.client.future;

import java.util.concurrent.TimeUnit;

import org.apache.sshd.common.SshException;
import org.apache.sshd.common.future.DefaultSshFuture;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.ValidateUtils;


/**
 * A default implementation of {@link AuthFuture}.
 *
 * @author <a href="mailto:dev@mina.apache.org">Apache MINA SSHD Project</a>
 */
public class DefaultAuthFuture extends DefaultSshFuture<AuthFuture> implements AuthFuture {
    public DefaultAuthFuture(Object lock) {
        super(lock);
    }

    @Override   // TODO for JDK-8 make this a default method
    public void verify() throws SshException {
        verify(Long.MAX_VALUE);
    }

    @Override   // TODO for JDK-8 make this a default method
    public void verify(long timeout, TimeUnit unit) throws SshException {
        verify(unit.toMillis(timeout));        
    }

    @Override
    public void verify(long timeoutMillis) throws SshException {
        try {
            if (!await(timeoutMillis)) {
                throw new SshException("Authentication timeout afer " + timeoutMillis);
            }
        } catch (InterruptedException e) {
            throw new SshException("Authentication interrupted", e);
        }

        if (!isSuccess()) {
            throw new SshException("Authentication failed", getException());
        }
    }

    @Override
    public Throwable getException() {
        Object v = getValue();
        if (v instanceof Throwable) {
            return (Throwable) v;
        } else {
            return null;
        }
    }

    @Override
    public boolean isSuccess() {
        Object v = getValue();
        return (v instanceof Boolean) && ((Boolean) v).booleanValue();
    }

    @Override
    public boolean isFailure() {
        Object v = getValue();
        return (v instanceof Boolean) && (!((Boolean) v).booleanValue());
    }

    @Override
    public void setAuthed(boolean authed) {
        setValue(Boolean.valueOf(authed));
    }

    @Override
    public void setException(Throwable exception) {
        ValidateUtils.checkNotNull(exception, "No exception provided", GenericUtils.EMPTY_OBJECT_ARRAY);
        setValue(exception);
    }
}
